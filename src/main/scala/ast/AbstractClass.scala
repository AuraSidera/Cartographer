package ast

import lexer.CamelCase

case class AbstractClass(
    override val name: CamelCase,
    extend: Option[Fqn],
    implement: List[Fqn],
    override val fields: List[Field]
) extends BaseClass