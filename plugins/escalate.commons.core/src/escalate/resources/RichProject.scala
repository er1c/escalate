package escalate
package resources

import org.eclipse.core.resources.IProject
import org.eclipse.core.runtime.{ NullProgressMonitor, IProgressMonitor }

class RichProject(project: IProject) {
  def toggleNature(nature: String, pm: IProgressMonitor = new NullProgressMonitor) =
    Resources toggleNature (project, nature, pm)
}