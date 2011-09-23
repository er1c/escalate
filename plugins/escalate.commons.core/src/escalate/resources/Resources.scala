package escalate
package resources

import org.eclipse.core.resources.{ IResource, IProject }
import org.eclipse.core.runtime.{ NullProgressMonitor, IProgressMonitor }

object Resources {
  def hasNature(nature: String): PartialFunction[IResource, Boolean] = {
    case r if r.isAccessible && r.getProject != null ⇒ r.getProject hasNature nature
  }
  def hasNature(resource: IResource, nature: String): Boolean = hasNature(nature) lift resource getOrElse false

  def toggleNature(project: IProject, nature: String, pm: IProgressMonitor = new NullProgressMonitor) = {
    val description = project.getDescription
    val (scalateNature, natures) = description.getNatureIds.toList partition (_ == nature)
    description.setNatureIds((if (scalateNature.length > 0) natures else nature :: natures).toArray)
    project.setDescription(description, pm)
  }
}