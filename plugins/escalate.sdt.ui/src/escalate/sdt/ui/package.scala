package escalate
package sdt

package object ui extends runtime.PluginPackage with resources.ResourcesImplicits {
  type PluginType = UiPlugin
  def id = "escalate.sdt.ui"
}