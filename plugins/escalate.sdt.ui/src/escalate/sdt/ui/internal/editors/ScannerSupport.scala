package escalate.sdt
package ui
package internal.editors

import org.eclipse.jface.text.rules.ICharacterScanner

trait ScannerSupport {
  def read(scanner: ICharacterScanner, max: Int) = Stream.continually(scanner.read()).zipWithIndex.takeWhile(i ⇒ i._2 < max).map(i ⇒ i._1.asInstanceOf[Char])
  def unread(scanner: ICharacterScanner, count: Int) = for (i ← 0 to count) scanner.unread()
  def charArray2String(chars: Array[Char]) = chars.foldLeft("") { (s, c) ⇒ s + c.toString }

  implicit def richCharacterScanner(scanner: ICharacterScanner) = new ScannerSupport.RichCharacterScanner(scanner)
}

object ScannerSupport extends ScannerSupport {
  class RichCharacterScanner(scanner: ICharacterScanner) {
    def read(max: Int) = ScannerSupport.this.read(scanner, max)
    def unread(count: Int) = ScannerSupport.this.unread(scanner, count)
  }
}