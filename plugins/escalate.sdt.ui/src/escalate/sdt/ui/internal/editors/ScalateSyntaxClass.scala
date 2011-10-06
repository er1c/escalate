package escalate.sdt
package ui
package internal.editors

import escalate.swt.ColorManager

import org.eclipse.swt.graphics.RGB
import org.eclipse.swt.SWT
import org.eclipse.jface.text._
import org.eclipse.jface.preference.{ PreferenceConverter, IPreferenceStore }

case class ScalateSyntaxClass(displayName: String, baseName: String) {

  import ScalateSyntaxClass._

  def colourKey = baseName + ColorSuffix
  def boldKey = baseName + BoldSuffix
  def italicKey = baseName + ItalicSuffix
  def underlineKey = baseName + UnderlineSuffix
  def strikethroughKey = baseName + StrikethroughSuffix

  def getTextAttribute(colorManager: ColorManager, preferenceStore: IPreferenceStore): TextAttribute = {
    val colour = colorManager.getColor(PreferenceConverter.getColor(preferenceStore, colourKey))
    val style = makeStyle(preferenceStore.getBoolean(boldKey), preferenceStore.getBoolean(italicKey),
      preferenceStore.getBoolean(strikethroughKey), preferenceStore.getBoolean(underlineKey))
    val backgroundColour = null
    new TextAttribute(colour, backgroundColour, style)
  }

  private def makeStyle(bold: Boolean, italic: Boolean, strikethrough: Boolean, underline: Boolean): Int = {
    var style = SWT.NORMAL
    if (bold) style |= SWT.BOLD
    if (italic) style |= SWT.ITALIC
    if (strikethrough) style |= TextAttribute.STRIKETHROUGH
    if (underline) style |= TextAttribute.UNDERLINE
    style
  }
}

object ScalateSyntaxClass {
  val ColorSuffix = ".colour"
  val BoldSuffix = ".bold"
  val ItalicSuffix = ".italic"
  val StrikethroughSuffix = ".strikethrough"
  val UnderlineSuffix = ".underline"
}