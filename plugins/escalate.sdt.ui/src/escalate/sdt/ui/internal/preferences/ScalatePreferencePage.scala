package escalate.sdt
package ui
package internal.preferences

import org.eclipse.jface.preference.PreferencePage
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.SWT
import org.eclipse.ui.{ IWorkbenchPreferencePage, IWorkbench }

class ScalatePreferencePage extends PreferencePage with IWorkbenchPreferencePage {
  def init(workbench: IWorkbench) = {}
  def createContents(parent: Composite) = new Composite(parent, SWT.NONE)
}