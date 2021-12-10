package ast
import lexer.CamelCase

case class Method(name: CamelCase, parameters: List[TypeNullable], result: TypeNullable) extends FieldDefinition