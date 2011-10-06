package escalate.sdt
package ui
package internal.preferences

import internal.editors.ScalateSyntaxClass
import ScalateSyntaxClass._

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer
import org.eclipse.jface.resource.StringConverter
import org.eclipse.swt.graphics.{ RGB, Color }
import org.eclipse.swt.widgets.Display

object ColorPreferenceInitializer {
  object Colors {
    val LimeGreen = new RGB(50, 205, 50)
    val Gray = new RGB(190, 190, 190)
    val DodgerBlue = new RGB(30, 144, 255)
  }
}

class ColorPreferenceInitializer extends AbstractPreferenceInitializer {
  import ColorPreferenceInitializer._

  def initializeDefaultPreferences() {
    val preferenceStore = EScalateUi.plugin.getPreferenceStore

      def initialize(syntaxClass: ScalateSyntaxClass, rgb: RGB, bold: Boolean = false, italic: Boolean = false,
        strikethrough: Boolean = false, underline: Boolean = false) =
        {
          val baseName = syntaxClass.baseName
          preferenceStore.setDefault(baseName + ColorSuffix, StringConverter.asString(rgb))
          preferenceStore.setDefault(baseName + BoldSuffix, bold)
          preferenceStore.setDefault(baseName + ItalicSuffix, italic)
          preferenceStore.setDefault(baseName + StrikethroughSuffix, strikethrough)
          preferenceStore.setDefault(baseName + UnderlineSuffix, underline)
        }

    import SspSyntaxClasses._

    initialize(Comment, Colors.Gray, italic = true)
    initialize(Directive, Colors.LimeGreen)
    initialize(Variable, Colors.DodgerBlue)
    initialize(Code, Colors.LimeGreen)
    initialize(VeloCode, Colors.LimeGreen)
    initialize(Expression, Colors.LimeGreen, bold = true)
    initialize(DollarExpresssion, Colors.LimeGreen, bold = true)
    initialize(Default, new RGB(0, 0, 0))
  }

}