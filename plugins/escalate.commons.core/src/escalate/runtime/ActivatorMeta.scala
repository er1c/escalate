package escalate
package runtime

import org.eclipse.core.runtime.Plugin

trait ActivatorMeta {
  type PluginType <: Plugin

  def id: String
  def plugin: PluginType
}