package escalate.sdt
package ui
package internal.editors

import internal.preferences.SspSyntaxClasses
import escalate.swt.ColorManager

import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.jface.preference.IPreferenceStore
import org.eclipse.jface.text.contentassist.{ IContentAssistProcessor, ContentAssistant }
import org.eclipse.jface.text.presentation.PresentationReconciler
import org.eclipse.jface.text.reconciler.{ MonoReconciler, IReconcilingStrategyExtension, IReconcilingStrategy, DirtyRegion }
import org.eclipse.jface.text.rules.{ ITokenScanner, Token, SingleLineRule, RuleBasedScanner, RuleBasedPartitionScanner, MultiLineRule, IRule, ICharacterScanner, FastPartitioner, DefaultDamagerRepairer }
import org.eclipse.jface.text.source.{ SourceViewerConfiguration, ISourceViewer }
import org.eclipse.jface.text.{ ITextViewer, IRegion, IDocument }
import org.eclipse.jface.util.PropertyChangeEvent
import org.eclipse.swt.graphics.{ RGB, Color }
import org.eclipse.swt.widgets.Display
import org.eclipse.ui.editors.text.{ TextEditor, FileDocumentProvider }
import org.eclipse.ui.texteditor.ITextEditor

/**
 * Text editor for Scalate SSP templates supporting syntax coloring.
 */
class SspEditor extends TextEditor {
  import SspEditor._

  private lazy val configuration = new Configuration(Plugin.getPreferenceStore, this)

  setSourceViewerConfiguration(configuration)
  setDocumentProvider(documentProvider)

  private object documentProvider extends FileDocumentProvider {
    override def createDocument(element: Any) = {
      val doc = super.createDocument(element)
      if (doc != null) {
        val partitioner = new FastPartitioner(new PartitionScanner, ContentTypes.All.toArray)
        partitioner.connect(doc)
        doc.setDocumentPartitioner(partitioner)
      }
      doc
    }
  }
}

object SspEditor {

  /**
   * Content types for the partitions in a document.
   */
  object ContentTypes {
    val Comment = "__ssp_comment"
    val Variable = "__ssp_variable"
    val Directive = "__ssp_directive"
    val Code = "__ssp_code"
    val VeloCode = "__ssp_velo_code"
    val Expression = "__ssp_expression"
    val DollarExpresssion = "__ssp_dollar_expression"

    val All = List(Comment, Variable, Directive, Code, VeloCode, Expression, DollarExpresssion)
  }

  class Reconciler extends IReconcilingStrategyExtension with IReconcilingStrategy {
    def setProgressMonitor(monitor: IProgressMonitor) = {}
    def setDocument(doc: IDocument) = {}
    def reconcile(dirtyRegion: DirtyRegion, subRegion: IRegion) = {}
    def reconcile(partition: IRegion) = {}
    def initialReconcile() = {}
  }

  case class DefaultScalateScanner(
      colorManager: ColorManager,
      preferenceStore: IPreferenceStore,
      syntaxClass: ScalateSyntaxClass) extends RuleBasedScanner with ScalateScanner {
    setDefaultReturnToken(getToken(syntaxClass))
  }

  class Configuration(store: IPreferenceStore, editor: ITextEditor) extends SourceViewerConfiguration {
    import SspSyntaxClasses._

    private val colorManager = new ColorManager // TODO move to plugin

    private def scanner(syntaxClass: ScalateSyntaxClass) = new DefaultScalateScanner(colorManager, store, syntaxClass)

    val commentScanner = scanner(Comment)
    val variableScanner = scanner(Variable)
    val directiveScanner = scanner(Directive)
    val codeScanner = scanner(Code)
    val expressionScanner = scanner(Expression)
    val dollarExpressionScanner = scanner(DollarExpresssion)
    val veloCodeScanner = scanner(VeloCode)
    val contentScanner = new DefaultContentScanner(colorManager, store)

    override def getPresentationReconciler(sourceViewer: ISourceViewer) = {
      val reconciler = new PresentationReconciler
      damagerRepairer(reconciler, contentScanner, IDocument.DEFAULT_CONTENT_TYPE)
      damagerRepairer(reconciler, commentScanner, ContentTypes.Comment)
      damagerRepairer(reconciler, variableScanner, ContentTypes.Variable)
      damagerRepairer(reconciler, directiveScanner, ContentTypes.Directive)
      damagerRepairer(reconciler, expressionScanner, ContentTypes.Expression)
      damagerRepairer(reconciler, codeScanner, ContentTypes.Code)
      damagerRepairer(reconciler, dollarExpressionScanner, ContentTypes.DollarExpresssion)
      damagerRepairer(reconciler, veloCodeScanner, ContentTypes.VeloCode)
      reconciler
    }

    def damagerRepairer(reconciler: PresentationReconciler,
      scanner: RuleBasedScanner, contentType: String) = {
      val dr = new DefaultDamagerRepairer(scanner)
      reconciler.setDamager(dr, contentType)
      reconciler.setRepairer(dr, contentType)
    }

    override def getReconciler(sourceViewer: ISourceViewer) = new MonoReconciler(new Reconciler, false)

    override def getConfiguredContentTypes(sourceViewer: ISourceViewer) =
      (IDocument.DEFAULT_CONTENT_TYPE :: ContentTypes.All).toArray

    override def getContentAssistant(sv: ISourceViewer) = {
      val ca = new ContentAssistant
      val pr = new DefaultCompletionProcessor
      ca.setContentAssistProcessor(pr, IDocument.DEFAULT_CONTENT_TYPE)
      ca.setInformationControlCreator(getInformationControlCreator(sv))
      ca
    }

    def handlePropertyChangeEvent(event: PropertyChangeEvent) {
      codeScanner.adaptToPreferenceChange(event)
      variableScanner.adaptToPreferenceChange(event)
      directiveScanner.adaptToPreferenceChange(event)
      commentScanner.adaptToPreferenceChange(event)
      dollarExpressionScanner.adaptToPreferenceChange(event)
      expressionScanner.adaptToPreferenceChange(event)
      veloCodeScanner.adaptToPreferenceChange(event)
      contentScanner.adaptToPreferenceChange(event)
    }
  }

  /**
   * Rule-based partition scanner for SSP documents.
   */
  class PartitionScanner extends RuleBasedPartitionScanner {
    setPredicateRules(Array(
      new MultiLineRule("<%--", "--%>", new Token(ContentTypes.Comment)),
      new MultiLineRule("<%@", "%>", new Token(ContentTypes.Variable)),
      new MultiLineRule("<%=", "%>", new Token(ContentTypes.Expression)),
      new MultiLineRule("<%", "%>", new Token(ContentTypes.Code)),
      new MultiLineRule("${", "}", new Token(ContentTypes.DollarExpresssion)),
      new MultiLineRule("#{", "}#", new Token(ContentTypes.VeloCode)),
      new DirectiveRule("if"),
      new DirectiveRule("elseif"), // FIXME not recognized
      new DirectiveRule("for"),
      new DirectiveRule("import"),
      new DirectiveRule("match"),
      new DirectiveRule("case"),
      new DirectiveRule("do"),
      new DirectiveRule("set")))
  }

  // FIXME whitespace is allowed
  class DirectiveRule(name: String)
    extends SingleLineRule("#" + name + "(", ")", new Token(ContentTypes.Directive))

  class DefaultContentScanner(val colorManager: ColorManager, val preferenceStore: IPreferenceStore) extends RuleBasedScanner with ScalateScanner {
    setRules(Array(
      new Rules.SimpleStartRule('#', List("end", "else", "otherwise"), getToken(SspSyntaxClasses.Directive))))
  }

  /**
   * Completion processor for partitions with default content type in SSP templates.
   */
  class DefaultCompletionProcessor extends IContentAssistProcessor {
    def computeCompletionProposals(viewer: ITextViewer, offset: Int) = null
    def getErrorMessage = null
    def computeContextInformation(viewer: ITextViewer, offset: Int) = null
    def getCompletionProposalAutoActivationCharacters = null
    def getContextInformationAutoActivationCharacters = null
    def getContextInformationValidator = null
  }
}