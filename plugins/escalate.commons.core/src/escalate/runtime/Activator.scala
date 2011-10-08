package escalate
package runtime

import org.eclipse.core.runtime.Plugin
import org.osgi.framework.BundleContext

object Activator {
  trait Meta extends ActivatorMeta {
    private[Activator] var instance: Plugin = _
    def plugin: PluginType = instance.asInstanceOf[PluginType]
    def id: String
  }
}

trait Activator extends Plugin {
  import Activator._

  def meta: Meta

  abstract override def start(c: BundleContext) {
    super.start(c)
    meta.instance = this
  }

  override def stop(c: BundleContext) {
    super.stop(c)
    meta.instance = null
  }
}