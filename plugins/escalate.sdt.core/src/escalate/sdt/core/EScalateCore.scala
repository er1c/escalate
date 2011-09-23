package escalate
package sdt
package core

import runtime.Activator
import internal.ScalateNature

import org.eclipse.core.runtime.Plugin

class EScalateCore extends Plugin with Activator {
  val meta = EScalateCore
}

object EScalateCore extends Activator.Meta {
  type PluginType = Plugin
  def id = "escalate.sdt.core"

  val NatureId = ScalateNature.Id
}