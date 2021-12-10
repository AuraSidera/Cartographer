package ast

case class System(name: Fqn, namespaces: List[Namespace]) extends Node