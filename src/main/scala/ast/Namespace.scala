package ast

case class Namespace(name: Fqn, classes: List[BaseClass]) extends Node