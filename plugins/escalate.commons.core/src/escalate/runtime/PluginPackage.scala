package escalate
package runtime

import resources.Resources

trait PluginPackage {
  implicit def meta: ActivatorMeta

  object Log extends Logger {
    val meta = PluginPackage.this.meta
  }
}