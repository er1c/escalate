package escalate.sdt
package ui
package internal.preferences

import escalate.jface.OverlayPreferenceStore
import OverlayPreferenceStore._
import escalate.swt.{ SwtUtils, ScrolledPageContent }
import SwtUtils._
import internal.editors.ScalateSyntaxClass

import PartialFunction.condOpt

import org.eclipse.jface.layout.PixelConverter
import org.eclipse.jface.preference.{ PreferencePage, PreferenceConverter, ColorSelector }
import org.eclipse.jface.viewers.{ Viewer, TreeViewer, StructuredSelection, LabelProvider, ITreeContentProvider, IStructuredSelection, DoubleClickEvent }
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.{ GridLayout, GridData }
import org.eclipse.swt.widgets.{ Scrollable, Link, Label, Control, Composite, Button }
import org.eclipse.swt.SWT
import org.eclipse.ui.dialogs.PreferencesUtil
import org.eclipse.ui.{ IWorkbenchPreferencePage, IWorkbench }

class SspSyntaxColoringPreferencePage extends PreferencePage with IWorkbenchPreferencePage {
  import SyntaxColoringPreferencePage._

  setPreferenceStore(Plugin.getPreferenceStore)
  private val overlayStore = makeOverlayPreferenceStore

  private var colorEditorLabel: Label = _
  private var syntaxForegroundColorEditor: ColorSelector = _
  private var boldCheckBox: Button = _
  private var italicCheckBox: Button = _
  private var underlineCheckBox: Button = _
  private var strikethroughCheckBox: Button = _
  private var treeViewer: TreeViewer = _

  def init(workbench: IWorkbench) {}

  def createContents(parent: Composite): Control = {
    initializeDialogUnits(parent)

    val scrolled = new ScrolledPageContent(parent, SWT.H_SCROLL | SWT.V_SCROLL)
    scrolled.setExpandHorizontal(true)
    scrolled.setExpandVertical(true)

    val control = createSyntaxPage(scrolled)

    scrolled.setContent(control)
    val size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT)
    scrolled.setMinSize(size.x, size.y)

    scrolled
  }

  def makeOverlayPreferenceStore = {
    import OverlayPreferenceStore._
    val keys = SspSyntaxClasses.All.flatMap { syntaxClass ⇒
      List(
        new OverlayKey(TypeDescriptor.String, syntaxClass.colourKey),
        new OverlayKey(TypeDescriptor.Boolean, syntaxClass.boldKey),
        new OverlayKey(TypeDescriptor.Boolean, syntaxClass.italicKey),
        new OverlayKey(TypeDescriptor.Boolean, syntaxClass.strikethroughKey),
        new OverlayKey(TypeDescriptor.Boolean, syntaxClass.underlineKey))
    }
    new OverlayPreferenceStore(getPreferenceStore, keys)
  }

  // TODO editors are not updated
  override def performOk() = {
    super.performOk()
    overlayStore.propagate
    Plugin.savePluginPreferences
    true
  }

  override def dispose() {
    overlayStore.stop()
    super.dispose()
  }

  override def performDefaults() {
    super.performDefaults()
    overlayStore.loadDefaults()
    handleSyntaxColorListSelection()
  }

  object TreeContentAndLabelProvider extends LabelProvider with ITreeContentProvider {

    case class Category(name: String, children: List[ScalateSyntaxClass])

    val othersCategory = Category("Others", SspSyntaxClasses.All)

    val categories = List(othersCategory)

    def getElements(inputElement: AnyRef) = categories.toArray

    def getChildren(parentElement: AnyRef) = parentElement match {
      case Category(_, children) ⇒ children.toArray
      case _ ⇒ Array()
    }

    def getParent(element: AnyRef): Category = {
      for {
        category ← categories
        child ← category.children
        if (child == element)
      } return category
      null
    }

    def hasChildren(element: AnyRef) = getChildren(element).nonEmpty

    def inputChanged(viewer: Viewer, oldInput: AnyRef, newInput: AnyRef) {}

    override def getText(element: AnyRef) = element match {
      case Category(name, _) ⇒ name
      case ScalateSyntaxClass(displayName, _) ⇒ displayName
    }
  }

  def createTreeViewer(editorComposite: Composite) {
    treeViewer = new TreeViewer(editorComposite, SWT.SINGLE | SWT.BORDER)

    treeViewer.setContentProvider(TreeContentAndLabelProvider)
    treeViewer.setLabelProvider(TreeContentAndLabelProvider)

    // scrollbars and tree indentation guess
    val widthHint = SspSyntaxClasses.All.map { syntaxClass ⇒ convertWidthInCharsToPixels(syntaxClass.displayName.length) }.max +
      Option(treeViewer.getControl.asInstanceOf[Scrollable].getVerticalBar).map { _.getSize.x * 3 }.getOrElse(0)

    treeViewer.getControl.setLayoutData(gridData(
      horizontalAlignment = SWT.BEGINNING,
      verticalAlignment = SWT.BEGINNING,
      grabExcessHorizontalSpace = false,
      grabExcessVerticalSpace = true,
      widthHint = widthHint,
      heightHint = convertHeightInCharsToPixels(9)))

    treeViewer.addDoubleClickListener { event: DoubleClickEvent ⇒
      val element = event.getSelection.asInstanceOf[IStructuredSelection].getFirstElement
      if (treeViewer.isExpandable(element))
        treeViewer.setExpandedState(element, !treeViewer.getExpandedState(element))
    }

    treeViewer.addSelectionChangedListener {
      handleSyntaxColorListSelection()
    }

    treeViewer.setInput(new Object)
  }

  private def gridLayout(marginHeight: Int = 5, marginWidth: Int = 5, numColumns: Int = 1): GridLayout = {
    val layout = new GridLayout
    layout.marginHeight = marginHeight
    layout.marginWidth = marginWidth
    layout.numColumns = numColumns
    layout
  }

  private def gridData(
    horizontalAlignment: Int = SWT.BEGINNING,
    verticalAlignment: Int = SWT.CENTER, //
    grabExcessHorizontalSpace: Boolean = false,
    grabExcessVerticalSpace: Boolean = false,
    widthHint: Int = SWT.DEFAULT,
    heightHint: Int = SWT.DEFAULT,
    horizontalSpan: Int = 1,
    horizontalIndent: Int = 0): GridData =
    {
      val gridData = new GridData(horizontalAlignment, verticalAlignment, grabExcessHorizontalSpace,
        grabExcessVerticalSpace)
      gridData.widthHint = widthHint
      gridData.heightHint = heightHint
      gridData.horizontalSpan = horizontalSpan
      gridData.horizontalIndent = horizontalIndent
      gridData
    }

  def createSyntaxPage(parent: Composite): Control = {
    overlayStore.load()
    overlayStore.start()

    val outerComposite = new Composite(parent, SWT.NONE)
    outerComposite.setLayout(gridLayout(marginHeight = 0, marginWidth = 0))

    val link = new Link(outerComposite, SWT.NONE)
    link.setText(PreferencesMessages.ScalateEditorColoringConfigurationBlock_link)
    link.addSelectionListener { e: SelectionEvent ⇒
      PreferencesUtil.createPreferenceDialogOn(parent.getShell, e.text, null, null)
    }
    link.setLayoutData(gridData(
      horizontalAlignment = SWT.FILL,
      verticalAlignment = SWT.BEGINNING,
      grabExcessHorizontalSpace = true,
      grabExcessVerticalSpace = false,
      widthHint = 150,
      horizontalSpan = 2))

    val filler = new Label(outerComposite, SWT.LEFT)
    filler.setLayoutData(gridData(
      horizontalAlignment = SWT.FILL,
      horizontalSpan = 1,
      heightHint = new PixelConverter(outerComposite).convertHeightInCharsToPixels(1) / 2))

    var elementLabel = new Label(outerComposite, SWT.LEFT)
    elementLabel.setText(PreferencesMessages.ScalateEditorPreferencePage_coloring_element)
    elementLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL))

    val elementEditorComposite = new Composite(outerComposite, SWT.NONE)
    elementEditorComposite.setLayout(gridLayout(marginHeight = 0, marginWidth = 0, numColumns = 2))
    elementEditorComposite.setLayoutData(gridData(
      horizontalAlignment = SWT.FILL, verticalAlignment = SWT.BEGINNING, grabExcessHorizontalSpace = true,
      grabExcessVerticalSpace = false))

    createTreeViewer(elementEditorComposite)

    val stylesComposite = new Composite(elementEditorComposite, SWT.NONE)
    stylesComposite.setLayout(gridLayout(marginHeight = 0, marginWidth = 0, numColumns = 2))
    stylesComposite.setLayoutData(new GridData(GridData.FILL_BOTH))

    colorEditorLabel = new Label(stylesComposite, SWT.LEFT)
    colorEditorLabel.setText(PreferencesMessages.ScalateEditorPreferencePage_color)

    colorEditorLabel.setLayoutData(gridData(horizontalAlignment = GridData.BEGINNING, horizontalIndent = 20))

    syntaxForegroundColorEditor = new ColorSelector(stylesComposite)
    val foregroundColorButton = syntaxForegroundColorEditor.getButton
    foregroundColorButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING))

    boldCheckBox = new Button(stylesComposite, SWT.CHECK)
    boldCheckBox.setText(PreferencesMessages.ScalateEditorPreferencePage_bold)

    boldCheckBox.setLayoutData(gridData(
      horizontalAlignment = GridData.BEGINNING, horizontalIndent = 20, horizontalSpan = 2))

    italicCheckBox = new Button(stylesComposite, SWT.CHECK)
    italicCheckBox.setText(PreferencesMessages.ScalateEditorPreferencePage_italic)
    italicCheckBox.setLayoutData(gridData(
      horizontalAlignment = GridData.BEGINNING, horizontalIndent = 20, horizontalSpan = 2))

    strikethroughCheckBox = new Button(stylesComposite, SWT.CHECK)
    strikethroughCheckBox.setText(PreferencesMessages.ScalateEditorPreferencePage_strikethrough)
    strikethroughCheckBox.setLayoutData(gridData(
      horizontalAlignment = GridData.BEGINNING, horizontalIndent = 20, horizontalSpan = 2))

    underlineCheckBox = new Button(stylesComposite, SWT.CHECK)
    underlineCheckBox.setText(PreferencesMessages.ScalateEditorPreferencePage_underline)
    underlineCheckBox.setLayoutData(
      gridData(horizontalAlignment = GridData.BEGINNING, horizontalIndent = 20, horizontalSpan = 2))

    val previewLabel = new Label(outerComposite, SWT.LEFT)
    previewLabel.setText(PreferencesMessages.ScalateEditorPreferencePage_preview)
    previewLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL))

    val previewer = createPreviewer(outerComposite)
    previewer.setLayoutData(gridData(
      horizontalAlignment = GridData.FILL,
      verticalAlignment = GridData.FILL,
      grabExcessHorizontalSpace = true,
      grabExcessVerticalSpace = true,
      widthHint = convertWidthInCharsToPixels(20),
      heightHint = convertHeightInCharsToPixels(5)))

    foregroundColorButton.addSelectionListener {
      for (syntaxClass ← selectedSyntaxClass)
        PreferenceConverter.setValue(overlayStore, syntaxClass.colourKey, syntaxForegroundColorEditor.getColorValue)
    }
    boldCheckBox.addSelectionListener {
      for (syntaxClass ← selectedSyntaxClass)
        overlayStore.setValue(syntaxClass.boldKey, boldCheckBox.getSelection)
    }
    italicCheckBox.addSelectionListener {
      for (syntaxClass ← selectedSyntaxClass)
        overlayStore.setValue(syntaxClass.italicKey, italicCheckBox.getSelection)
    }
    underlineCheckBox.addSelectionListener {
      for (syntaxClass ← selectedSyntaxClass)
        overlayStore.setValue(syntaxClass.underlineKey, underlineCheckBox.getSelection)
    }
    strikethroughCheckBox.addSelectionListener {
      for (syntaxClass ← selectedSyntaxClass)
        overlayStore.setValue(syntaxClass.strikethroughKey, strikethroughCheckBox.getSelection)
    }

    treeViewer.setSelection(new StructuredSelection(TreeContentAndLabelProvider.othersCategory))

    outerComposite.layout(false)
    outerComposite
  }

  private def createPreviewer(parent: Composite): Control =
    ScalatePreviewerFactory.createSspPreviewer(parent, overlayStore, previewText).getControl

  private def selectedSyntaxClass: Option[ScalateSyntaxClass] =
    condOpt(treeViewer.getSelection.asInstanceOf[IStructuredSelection].getFirstElement) {
      case syntaxClass: ScalateSyntaxClass ⇒ syntaxClass
    }

  private def massSetEnablement(enabled: Boolean) =
    List(syntaxForegroundColorEditor.getButton, colorEditorLabel, boldCheckBox, italicCheckBox,
      strikethroughCheckBox, underlineCheckBox) foreach { _.setEnabled(enabled) }

  private def handleSyntaxColorListSelection() = selectedSyntaxClass match {
    case None ⇒
      massSetEnablement(false)
    case Some(syntaxClass) ⇒
      val rgb = PreferenceConverter.getColor(overlayStore, syntaxClass.colourKey)
      syntaxForegroundColorEditor.setColorValue(rgb)
      boldCheckBox.setSelection(overlayStore.getBoolean(syntaxClass.boldKey))
      italicCheckBox.setSelection(overlayStore.getBoolean(syntaxClass.italicKey))
      strikethroughCheckBox.setSelection(overlayStore.getBoolean(syntaxClass.strikethroughKey))
      underlineCheckBox.setSelection(overlayStore.getBoolean(syntaxClass.underlineKey))
      massSetEnablement(true)
  }

}

object SyntaxColoringPreferencePage {

  val previewText = """<%@ val foo: MyType %>
<%-- this is a comment --%>
#{
  import java.util.Date
  val now = new Date 
}#
Hello the time is ${now}
<p>
  <%= List("hi", "there", "reader!").mkString(" ") %> 
</p>
<%
  var foo = "hello"
  foo += " there"
%>
<p>
#if(n == "James")
  Hey James
#else
  Yo Hiram
#end
</p>"""

}