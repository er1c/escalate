package escalate.sdt
package ui
package internal.preferences

import internal.editors.SspEditor
import SspEditor.PartitionScanner

import org.eclipse.jface.preference.IPreferenceStore
import org.eclipse.jface.resource.JFaceResources
import org.eclipse.jface.text.rules.FastPartitioner
import org.eclipse.jface.text.source.SourceViewer
import org.eclipse.jface.text.Document
import org.eclipse.jface.util.{ PropertyChangeEvent, IPropertyChangeListener }
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.SWT
import org.eclipse.ui.editors.text.EditorsUI
import org.eclipse.ui.texteditor.ChainedPreferenceStore

object ScalatePreviewerFactory {

  def createSspPreviewer(parent: Composite, store: IPreferenceStore, initialText: String): SourceViewer = {
    val preferenceStore = new ChainedPreferenceStore(Array(store, EditorsUI.getPreferenceStore))
    val previewViewer = new SourceViewer(parent, null, null, false, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER)
    val font = JFaceResources.getFont("org.eclipse.ui.ide.textEditorFont")
    previewViewer.getTextWidget.setFont(font)
    previewViewer.setEditable(false)

    val configuration = new SspEditor.Configuration(store, null)
    previewViewer.configure(configuration)

    val document = new Document
    document.set(initialText)
    val partitioner = new FastPartitioner(new PartitionScanner, SspEditor.ContentTypes.All.toArray)
    partitioner.connect(document)
    document.setDocumentPartitioner(partitioner)
    previewViewer.setDocument(document)

    preferenceStore.addPropertyChangeListener(new IPropertyChangeListener {
      def propertyChange(event: PropertyChangeEvent) {
        configuration.handlePropertyChangeEvent(event)
        previewViewer.invalidateTextPresentation()
      }
    })
    previewViewer
  }

}