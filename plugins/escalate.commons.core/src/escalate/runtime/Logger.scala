package escalate
package runtime

import org.eclipse.core.runtime.{ Status, IStatus, ILog }

trait Logger {
  def meta: ActivatorMeta

  def apply(severity: Int, msg: String, exception: Throwable): Unit = meta.plugin.getLog.log(new Status(severity, meta.id, msg, exception))

  def log(severity: Int, exception: Throwable): Unit = this(severity, "", exception)
  def log(severity: Int, msg: String): Unit = this(severity, msg, null)

  def info(msg: String): Unit = this(IStatus.INFO, msg, null)
  def info(msg: String, exception: Throwable): Unit = this(IStatus.INFO, msg, exception)
  def info(exception: Throwable): Unit = this(IStatus.INFO, "", exception)

  def warn(msg: String): Unit = this(IStatus.WARNING, msg, null)
  def warn(msg: String, exception: Throwable): Unit = this(IStatus.WARNING, msg, exception)
  def warn(exception: Throwable): Unit = this(IStatus.WARNING, "", exception)

  def error(msg: String): Unit = this(IStatus.ERROR, msg, null)
  def error(msg: String, exception: Throwable): Unit = this(IStatus.ERROR, msg, exception)
  def error(exception: Throwable): Unit = this(IStatus.ERROR, "", exception)
}