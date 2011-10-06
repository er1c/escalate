package escalate.swt

import org.eclipse.swt.widgets.{ Control, Composite }
import org.eclipse.swt.SWT
import org.eclipse.ui.forms.widgets.SharedScrolledComposite

class ScrolledPageContent(parent: Composite, style: Int) extends SharedScrolledComposite(parent, style) {
  setFont(parent.getFont)
  setExpandHorizontal(true)
  setExpandVertical(true)

  {
    val body = new Composite(this, SWT.NONE)
    body.setFont(parent.getFont)
    setContent(body)
  }

  def this(parent: Composite) = this(parent, SWT.V_SCROLL | SWT.H_SCROLL)

  def adaptChild(childControl: Control) = null
  def body = getContent.asInstanceOf[Composite]

}