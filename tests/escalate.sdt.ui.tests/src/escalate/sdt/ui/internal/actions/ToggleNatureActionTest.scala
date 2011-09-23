package escalate
package sdt
package ui
package internal
package actions

import core.EScalateCore

import org.eclipse.core.resources.{ ResourcesPlugin, IWorkspace }
import org.junit.{ Test, Before, After }
import org.junit.Assert._
import org.eclipse.core.resources.IProject
import org.eclipse.core.runtime.CoreException
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot
import org.eclipse.swtbot.swt.finder.SWTBot

import escalate.sdt.ui.testutils.ContextMenuHelper

object ToggleNatureActionTest {
  val ProjectExplorerId = "org.eclipse.ui.navigator.ProjectExplorer"
}

class ToggleNatureActionTest {
  import ToggleNatureActionTest._

  var workspace: IWorkspace = _
  var project: IProject = _
  var bot: SWTWorkbenchBot = _

  @Before
  def setUp = {
    workspace = ResourcesPlugin.getWorkspace()
    createProject()
    bot = new SWTWorkbenchBot()
  }

  @After
  def tearDown = {
    if (project != null) {
      project.delete(true, null)
    }
  }

  def createProject() = {
    project = workspace.getRoot().getProject("TestProject")
    project.create(null)
    project.open(new NullProgressMonitor)
  }

  def addNature() = {
    val description = project.getDescription
    description.setNatureIds((EScalateCore.NatureId :: description.getNatureIds.toList).toArray)
    project.setDescription(description, new NullProgressMonitor)
  }

  @Test
  def testToggleWithoutNature = {
    val viewBot = bot.viewById(ProjectExplorerId).bot()
    val tree = viewBot.tree()
    val item = tree.getTreeItem("TestProject").select()
    ContextMenuHelper.clickContextMenu(tree, "Configure", "Add Scalate Nature")
    assertTrue(project.hasNature(EScalateCore.NatureId))
  }

  @Test
  def testToggleWithNature = {
    addNature()
    val viewBot = bot.viewById(ProjectExplorerId).bot()
    val tree = viewBot.tree()
    val item = tree.getTreeItem("TestProject").select()
    ContextMenuHelper.clickContextMenu(tree, "Scalate", "Remove Scalate Nature")
    assertFalse(project.hasNature(EScalateCore.NatureId))
  }
}