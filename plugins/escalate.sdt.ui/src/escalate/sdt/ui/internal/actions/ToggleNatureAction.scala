package escalate
package sdt
package ui
package internal
package actions

import collection.JavaConversions._

import core.EScalateCore
import resources.Resources._
import runtime.Adapters._

import org.eclipse.core.runtime.IAdaptable
import org.eclipse.core.resources.IProject
import org.eclipse.jface.action.IAction
import org.eclipse.jface.viewers.{ ISelection, IStructuredSelection }
import org.eclipse.ui.{ IObjectActionDelegate, IWorkbenchPart }

/**
 * Action for adding/removing the Scala nature from a project.
 */
class ToggleNatureAction extends IObjectActionDelegate {
  private var selection: ISelection = _

  def run(action: IAction) = projects foreach (_.toggleNature(EScalateCore.NatureId))

  protected def projects: List[IProject] = selection match {
    case s: IStructuredSelection ⇒ s.toList.toList collect {
      case p: IProject ⇒ p
      case ProjectAdapter(p) ⇒ p
    }
    case _ ⇒ Nil
  }

  def selectionChanged(a: IAction, s: ISelection) = selection = s
  def setActivePart(a: IAction, p: IWorkbenchPart) {}
}