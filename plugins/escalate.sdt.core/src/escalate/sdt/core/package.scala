package escalate
package sdt

package object core extends runtime.PluginPackage {
  type PluginType = CorePlugin
  val id = "escalate.sdt.core"

  val NatureId = core.internal.ScalateNature.Id
}