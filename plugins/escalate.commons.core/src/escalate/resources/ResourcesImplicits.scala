package escalate
package resources

import org.eclipse.core.resources.{ IResource, IProject }

trait ResourcesImplicits {
  implicit def resource2richResource(r: IResource) = new RichResource(r)
  implicit def project2richProject(p: IProject) = new RichProject(p)
}

object ResourcesImplicits extends ResourcesImplicits