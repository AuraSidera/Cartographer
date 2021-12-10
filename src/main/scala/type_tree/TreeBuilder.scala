package type_tree
import ast.System
import lexer.CamelCase

object TreeBuilder {
    def apply(system: System): Tree = {
        val System(baseNamespace, namespaces) = system
        val rootName = baseNamespace match {
            case Nil => ""
            case CamelCase(head)::_ => head
        }
        val root = Tree(rootName, Namespace())

        namespaces.foreach(namespace => {
            val ast.Namespace(name, classes) = namespace
            val path = (baseNamespace ++ name).map(_.value)
            val node = ensurePath(root, path)
            classes.foreach(c => {
                val child = c match {
                    case ast.Class(CamelCase(className), _, _, _) => Tree(className, Class(), Some(node))
                    case ast.AbstractClass(CamelCase(className), _, _, _) => Tree(className, AbstractClass(), Some(node))
                    case ast.Interface(CamelCase(className), _, _, _) => Tree(className, Interface(), Some(node))
                    case ast.Trait(CamelCase(className), _, _, _) => Tree(className, Trait(), Some(node))
                }
                node.children = child::node.children
            })
        })
        root
    }

    def ensurePath(node: Tree, path: List[String]): Node = path match {
        case Nil => node
        case node.name::rest => ensurePath(node, rest)
        case name::rest => node.children.find(_.name == name) match {
            case Some(child) => ensurePath(child, rest)
            case None => {
                val child = Tree(name, Namespace(), Some(node))
                node.children = child::node.children
                ensurePath(child, rest)
            }
        }
    }
}