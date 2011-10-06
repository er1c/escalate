package escalate.swt

import org.eclipse.jface.resource.StringConverter
import org.eclipse.jface.text.source.ISharedTextColors
import org.eclipse.swt.graphics.{ RGB, Color }
import org.eclipse.swt.widgets.Display

// TODO cache colors
class ColorManager extends ISharedTextColors {
  def getColor(key: String): Color = getColor(StringConverter.asRGB(key))
  def getColor(rgb: RGB): Color = new Color(Display.getCurrent(), rgb)
  def dispose = {}
}