package escalate
package sdt
package core
package internal

import org.eclipse.core.resources.{ IProjectNature, IProject }

class ScalateNature extends IProjectNature {
  var project: IProject = _

  def getProject = project
  def setProject(p: IProject) = project = p

  def configure {}

  def deconfigure {}
}

object ScalateNature {
  val Id = "escalate.sdt.scalateNature"
}