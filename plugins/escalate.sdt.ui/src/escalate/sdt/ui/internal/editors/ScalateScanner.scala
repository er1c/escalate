package escalate.sdt
package ui
package internal.editors

import escalate.swt.ColorManager

import org.eclipse.jface.text.rules.{ ITokenScanner, Token }
import org.eclipse.jface.preference.IPreferenceStore
import org.eclipse.jface.util.PropertyChangeEvent

trait ScalateScanner extends ITokenScanner {

  protected def colorManager: ColorManager
  protected def preferenceStore: IPreferenceStore

  private var tokens: Map[ScalateSyntaxClass, Token] = Map()

  protected def getToken(syntaxClass: ScalateSyntaxClass): Token =
    tokens.getOrElse(syntaxClass, createToken(syntaxClass))

  private def createToken(syntaxClass: ScalateSyntaxClass) = {
    val token = new Token(getTextAttribute(syntaxClass))
    tokens = tokens + (syntaxClass -> token)
    token
  }

  def adaptToPreferenceChange(event: PropertyChangeEvent) =
    for ((syntaxClass, token) ← tokens)
      token.setData(getTextAttribute(syntaxClass))

  private def getTextAttribute(syntaxClass: ScalateSyntaxClass) =
    syntaxClass.getTextAttribute(colorManager, preferenceStore)

}