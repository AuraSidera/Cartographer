package generator.php
import ast.System
import type_tree.Tree
import type_tree.TreeBuilder
import lexer.CamelCase

case class TypeResolver(val typeTree: Tree) {
    def apply(currentNamespace: List[CamelCase], target: List[CamelCase]): List[CamelCase] = searchAmongChildren(currentNamespace, target) match {
        case Some(name) => name
        case None => searchAmongAncestors(currentNamespace, target)
    }

    def searchAmongChildren(currentNamespace: List[CamelCase], target: List[CamelCase]): Option[List[CamelCase]] = {
        typeTree.depthFirstSearch(_.path.map(_.name) == (currentNamespace ++ target).map(_.value)) match {
            case None => None
            case Some(node) => Some(node.path.map(node => CamelCase(node.name)))
        }
    }

    def searchAmongAncestors(currentNamespace: List[CamelCase], target: List[CamelCase]):  List[CamelCase] = currentNamespace match {
        case Nil => target
        case namespace => {
            val (last::first) = namespace.reverse
            searchAmongChildren(first.reverse, target) match {
                case Some(result) => result
                case None => searchAmongAncestors(first.reverse, target)
            }
        }
    }
}

object TypeResolver {
    def fromSystem(root: System): TypeResolver = TypeResolver(TreeBuilder(root))
}