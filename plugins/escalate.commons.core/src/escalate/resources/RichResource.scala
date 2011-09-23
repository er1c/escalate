package escalate
package resources

import org.eclipse.core.resources.IResource

class RichResource(resource: IResource) {
  def hasNature(nature: String) = Resources hasNature (resource, nature)
  def project = Option(resource.getProject)
}