package ast
import lexer.SnakeCase

case class Attribute(name: SnakeCase, t: TypeNullable, get: Boolean, set: Boolean) extends FieldDefinition