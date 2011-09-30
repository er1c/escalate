package escalate.sdt
package ui
package internal.editors

import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.jface.text.contentassist.{ IContentAssistProcessor, ContentAssistant }
import org.eclipse.jface.text.presentation.PresentationReconciler
import org.eclipse.jface.text.reconciler.{ MonoReconciler, IReconcilingStrategyExtension, IReconcilingStrategy, DirtyRegion }
import org.eclipse.jface.text.rules.{ Token, SingleLineRule, RuleBasedScanner, RuleBasedPartitionScanner, MultiLineRule, IRule, ICharacterScanner, FastPartitioner, DefaultDamagerRepairer }
import org.eclipse.jface.text.source.{ SourceViewerConfiguration, ISourceViewer }
import org.eclipse.jface.text.{ TextAttribute, ITextViewer, IRegion, IDocument }
import org.eclipse.swt.graphics.{ RGB, Color }
import org.eclipse.swt.widgets.Display
import org.eclipse.ui.editors.text.{ TextEditor, FileDocumentProvider }

/**
 * Text editor for Scalate SSP templates supporting syntax coloring.
 */
class SspEditor extends TextEditor {
  import SspEditor._

  setSourceViewerConfiguration(configuration)
  setDocumentProvider(documentProvider)

  private object configuration extends SourceViewerConfiguration {
    override def getPresentationReconciler(sourceViewer: ISourceViewer) = SspPresentationReconciler()
    override def getReconciler(sourceViewer: ISourceViewer) = new MonoReconciler(reconciler, false)

    override def getConfiguredContentTypes(sourceViewer: ISourceViewer) =
      (IDocument.DEFAULT_CONTENT_TYPE :: ContentTypes.All).toArray

    override def getContentAssistant(sv: ISourceViewer) = {
      val ca = new ContentAssistant
      val pr = new DefaultCompletionProcessor
      ca.setContentAssistProcessor(pr, IDocument.DEFAULT_CONTENT_TYPE)
      ca.setInformationControlCreator(getInformationControlCreator(sv))
      ca
    }
  }

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

  private object reconciler extends IReconcilingStrategyExtension with IReconcilingStrategy {
    def setProgressMonitor(monitor: IProgressMonitor) = {}
    def setDocument(doc: IDocument) = {}
    def reconcile(dirtyRegion: DirtyRegion, subRegion: IRegion) = {}
    def reconcile(partition: IRegion) = {}
    def initialReconcile() = {}
  }
}

object SspEditor {

  object Colors {
    val LimeGreen = fromRGB((50, 205, 50))
    val Gray = fromRGB((190, 190, 190))

    def fromRGB(rgb: (Int, Int, Int)): Color = fromRGB(new RGB(rgb._1, rgb._2, rgb._3))
    def fromRGB(rgb: RGB): Color = new Color(Display.getCurrent, rgb)
  }

  /**
   * Content types for the partitions in a document.
   */
  object ContentTypes {
    val Comment = "__comment"
    val Variable = "__variable"
    val Directive = "__directive"
    val Code = "__code"
    val VeloCode = "__velo_code"
    val Expresssion = "__expression"
    val DollarExpresssion = "__dollar_expression"

    val All = List(Comment, Variable, Directive, Code, VeloCode, Expresssion, DollarExpresssion)
  }
  
  object SspPresentationReconciler {

    def damagerRepairer(reconciler: PresentationReconciler,
      token: String, color: Color,
      scanner: RuleBasedScanner = new RuleBasedScanner) = {
      scanner.setDefaultReturnToken(new Token(new TextAttribute(color)))
      val dr = new DefaultDamagerRepairer(scanner)
      reconciler.setDamager(dr, token)
      reconciler.setRepairer(dr, token)
    }

    def apply() = {
      val reconciler = new PresentationReconciler
      damagerRepairer(reconciler, token = IDocument.DEFAULT_CONTENT_TYPE,
        color = new Color(Display.getCurrent, new RGB(0, 0, 0)),
        scanner = new DefaultContentScanner)
      damagerRepairer(reconciler, token = ContentTypes.Comment, color = Colors.Gray)
      damagerRepairer(reconciler, token = ContentTypes.Variable, color = Colors.LimeGreen)
      damagerRepairer(reconciler, token = ContentTypes.Directive, color = Colors.LimeGreen)
      damagerRepairer(reconciler, token = ContentTypes.Expresssion, color = Colors.LimeGreen)
      damagerRepairer(reconciler, token = ContentTypes.Code, color = Colors.LimeGreen)
      damagerRepairer(reconciler, token = ContentTypes.DollarExpresssion, color = Colors.LimeGreen)
      damagerRepairer(reconciler, token = ContentTypes.VeloCode, color = Colors.LimeGreen)
      reconciler
    }
  }

  /**
   * Rule-based partition scanner for SSP documents.
   */
  class PartitionScanner extends RuleBasedPartitionScanner {
    setPredicateRules(Array(
      new MultiLineRule("<%--", "--%>", new Token(ContentTypes.Comment)),
      new MultiLineRule("<%@", "%>", new Token(ContentTypes.Variable)),
      new MultiLineRule("<%=", "%>", new Token(ContentTypes.Expresssion)),
      new MultiLineRule("<%", "%>", new Token(ContentTypes.Code)),
      new MultiLineRule("${", "}", new Token(ContentTypes.DollarExpresssion)),
      new MultiLineRule("#{", "}#", new Token(ContentTypes.VeloCode)),
      new DirectiveRule("if"),
      new DirectiveRule("elseif"),
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

  class DefaultContentScanner extends RuleBasedScanner {
    setRules(Array(
      new Rules.SimpleStartRule('#', List("end", "else", "otherwise"),
        new Token(new TextAttribute(Colors.LimeGreen)))))
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