package escalate
package sdt
package ui
package internal
package actions

import testutils.ContextMenuHelper
import org.scalatest.junit.JUnitSuite
import org.scalatest.WordSpec
import org.scalatest.BeforeAndAfterEach
import org.eclipse.core.resources.IWorkspace
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.resources.IProject
import org.eclipse.core.runtime.NullProgressMonitor
import org.scalatest.matchers.MustMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

object ToggleNatureActionSpec {
  val ProjectExplorerId = "org.eclipse.ui.navigator.ProjectExplorer"
}

@RunWith(classOf[JUnitRunner])
class ToggleNatureActionSpec extends WordSpec
    with MustMatchers
    with BeforeAndAfterEach {

  import ToggleNatureActionSpec._

  var workspace: IWorkspace = _
  var bot: SWTWorkbenchBot = _
  var project: IProject = _

  override def beforeEach() {
    workspace = ResourcesPlugin.getWorkspace()
    bot = new SWTWorkbenchBot()

    project = workspace.getRoot().getProject("TestProject")
    project.create(null)
    project.open(new NullProgressMonitor)
  }

  override def afterEach() {
    if (project != null) {
      project.delete(true, null)
    }
  }

  def addNature() = {
    val description = project.getDescription
    description.setNatureIds((core.NatureId :: description.getNatureIds.toList).toArray)
    project.setDescription(description, new NullProgressMonitor)
  }

  "the ToggleNatureAction" should {

    "add the Scalate nature" in {
      val viewBot = bot.viewById(ProjectExplorerId).bot()
      val tree = viewBot.tree()
      val item = tree.getTreeItem("TestProject").select()
      ContextMenuHelper.clickContextMenu(tree, "Configure", "Add Scalate Nature")
      project.hasNature(core.NatureId) must be === true
    }

    "remove the Scalate nature" in {
      addNature()
      val viewBot = bot.viewById(ProjectExplorerId).bot()
      val tree = viewBot.tree()
      val item = tree.getTreeItem("TestProject").select()
      ContextMenuHelper.clickContextMenu(tree, "Scalate", "Remove Scalate Nature")
      project.hasNature(core.NatureId) must be === false
    }
  }

}