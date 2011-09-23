package escalate
package sdt
package ui

import runtime.Activator

import org.eclipse.core._
import runtime.Plugin
import org.osgi._
import framework.BundleContext
import org.eclipse.ui.plugin.AbstractUIPlugin

class EScalateUi extends AbstractUIPlugin with Activator {
  val meta = EScalateUi
}

object EScalateUi extends Activator.Meta {
  type PluginType = AbstractUIPlugin
  def id = "escalate.sdt.ui"
}