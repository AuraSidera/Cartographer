package ast

case class Field(static: Boolean, constant: Boolean, access: Access, definition: FieldDefinition) extends Node