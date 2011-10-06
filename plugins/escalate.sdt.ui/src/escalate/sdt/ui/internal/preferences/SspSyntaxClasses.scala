package escalate.sdt
package ui
package internal.preferences

import internal.editors.ScalateSyntaxClass

object SspSyntaxClasses {
  val Comment = ScalateSyntaxClass("Comment", "ssp.syntaxColoring.comment")
  val Default = ScalateSyntaxClass("Others", "ssp.syntaxColoring.default")
  val Variable = ScalateSyntaxClass("Variable", "ssp.syntaxColoring.variable")
  val Directive = ScalateSyntaxClass("Directive", "ssp.syntaxColoring.directive")
  val Code = ScalateSyntaxClass("Code", "ssp.syntaxColoring.code")
  val VeloCode = ScalateSyntaxClass("Velocity-style Code", "ssp.syntaxColoring.veloCode")
  val Expression = ScalateSyntaxClass("Expression", "ssp.syntaxColoring.expression")
  val DollarExpresssion = ScalateSyntaxClass("Dollar Expression", "ssp.syntaxColoring.dollarExpression")

  val All = List(Comment, Variable, Directive, Code, VeloCode, Expression, DollarExpresssion, Default)
}