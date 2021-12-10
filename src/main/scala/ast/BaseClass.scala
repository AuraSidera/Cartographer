package ast
import lexer.CamelCase

trait BaseClass extends Node {
    def name: CamelCase = this match {
        case Class(name, _, _, _) => name
        case AbstractClass(name, _, _, _) => name
        case Interface(name, _, _, _) => name
        case Trait(name, _, _, _) => name
    }

    def fields: List[Field] = this match {
        case Class(_, _, _, fields) => fields
        case AbstractClass(_, _, _, fields) => fields
        case Interface(_, _, _, fields) => fields
        case Trait(_, _, _, fields) => fields
    }
}