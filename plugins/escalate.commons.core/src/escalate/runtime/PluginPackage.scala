package escalate
package runtime

import resources.Resources

trait PluginPackage extends Activator.Meta {
  object Log extends Logger {
    val meta = PluginPackage.this
  }
  def Id = id
  def Plugin = plugin
}