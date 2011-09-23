package escalate
package runtime

import org.eclipse.core.runtime.IAdaptable
import org.eclipse.core.resources.IProject

object Adapters {
  class AdapterFor[T](t: Class[T]) {
    def unapply(a: IAdaptable) = Option(a.getAdapter(t).asInstanceOf[T])
  }

  object ProjectAdapter extends AdapterFor[IProject](classOf[IProject])
}