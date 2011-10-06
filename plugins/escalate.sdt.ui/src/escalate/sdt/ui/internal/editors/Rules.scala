package escalate.sdt
package ui
package internal.editors

import org.eclipse.jface.text.rules._

object Rules extends ScannerSupport {

  class SimpleStartRule(start: Char, words: List[String], token: IToken) extends IRule {
    def evaluate(scanner: ICharacterScanner) = {
      var c = scanner.read()
      if (c == start) {
        val next = charArray2String(scanner.read(9).toArray)
        val d = words find (next.startsWith(_))
        if (d.isDefined) {
          scanner unread (9 - d.get.length())
          token
        } else {
          scanner unread (next.length)
          Token.UNDEFINED
        }
      } else {
        scanner.unread()
        Token.UNDEFINED
      }
    }
  }
}