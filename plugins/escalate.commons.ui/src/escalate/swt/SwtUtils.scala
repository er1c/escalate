package escalate.swt

import org.eclipse.jface.util.{ PropertyChangeEvent, IPropertyChangeListener }
import org.eclipse.jface.viewers.{ SelectionChangedEvent, ISelectionChangedListener, IDoubleClickListener, DoubleClickEvent }
import org.eclipse.swt.events.{ VerifyListener, VerifyEvent, SelectionEvent, SelectionAdapter, ModifyListener, ModifyEvent, KeyEvent, KeyAdapter, FocusEvent, FocusAdapter }
import org.eclipse.swt.widgets.{ Display, Control }

object SwtUtils {

  /** Run `f` on the UI thread.  */
  def asyncExec(f: ⇒ Unit) {
    Display.getDefault asyncExec new Runnable {
      override def run() { f }
    }
  }

  implicit def fnToModifyListener(f: ModifyEvent ⇒ Unit): ModifyListener = new ModifyListener {
    def modifyText(e: ModifyEvent) = f(e)
  }

  implicit def fnToValListener(f: VerifyEvent ⇒ Unit) = new VerifyListener {
    def verifyText(e: VerifyEvent) = f(e)
  }

  implicit def fnToSelectionAdapter(p: SelectionEvent ⇒ Any): SelectionAdapter =
    new SelectionAdapter() {
      override def widgetSelected(e: SelectionEvent) { p(e) }
    }

  implicit def byNameToSelectionAdapter(p: ⇒ Any): SelectionAdapter =
    new SelectionAdapter() {
      override def widgetSelected(e: SelectionEvent) { p }
    }

  implicit def fnToPropertyChangeListener(p: PropertyChangeEvent ⇒ Any): IPropertyChangeListener =
    new IPropertyChangeListener() {
      def propertyChange(e: PropertyChangeEvent) { p(e) }
    }

  implicit def byNameToSelectionChangedListener(p: ⇒ Any): ISelectionChangedListener =
    new ISelectionChangedListener {
      def selectionChanged(event: SelectionChangedEvent) { p }
    }

  implicit def fnToDoubleClickListener(p: DoubleClickEvent ⇒ Any): IDoubleClickListener =
    new IDoubleClickListener {
      def doubleClick(event: DoubleClickEvent) { p(event) }
    }

  implicit def control2PimpedControl(control: Control): PimpedControl = new PimpedControl(control)

  class PimpedControl(control: Control) {

    def onKeyReleased(p: KeyEvent ⇒ Any ⇒ KeyEvent ⇒ Any) {
      control.addKeyListener(new KeyAdapter {
        override def keyReleased(e: KeyEvent) { p(e) }
      })
    }

    def onKeyReleased(p: ⇒ Any) {
      control.addKeyListener(new KeyAdapter {
        override def keyReleased(e: KeyEvent) { p }
      })
    }

    def onFocusLost(p: ⇒ Any) {
      control.addFocusListener(new FocusAdapter {
        override def focusLost(e: FocusEvent) { p }
      })
    }

  }

}