package type_tree
import java.io._

class TypeChart {
    def apply(tree: Tree, path: String): Unit = {
        val pw = new PrintWriter(new File(path))
        pw.write(toCode(tree))
        pw.close
    }

    def toCode(tree: Tree): String = {
        "strict digraph {\n" +
        "\tnode [shape=box]" +
        nodes(tree) +
        edges(tree) + "\n}"
    }

    def nodes(tree: Tree): String = tree.nodes.filterNot(_.name.isEmpty).map(node => "\n\t" + node.identifier + "[label=" + node.name + style(node.nodeType) + "]").mkString

    def edges(tree: Tree): String = tree.edges.filterNot{case (from, to) => from.name.isEmpty || to.name.isEmpty}.map{case (from, to) => "\n\t" + from.identifier + " -> " + to.identifier}.mkString

    def style(nodeType: NodeType): String = " color=\"" + color(nodeType) + "\" shape=\"" + shape(nodeType) + "\" fontname=\"" + font(nodeType) + "\" fontcolor=\"" + color(nodeType) + "\""

    def shape(nodeType: NodeType): String = nodeType match {
        case Namespace() => "tab"
        case Class() => "box"
        case AbstractClass() => "box"
        case Interface() => "box"
        case Trait() => "box"
    }

    def color(nodeType: NodeType): String = nodeType match {
        case Namespace() => "#666666"
        case Class() => "#333333"
        case AbstractClass() => "#555555"
        case Interface() => "#555555"
        case Trait() => "#555555"
    }

    def font(nodeType: NodeType): String = nodeType match {
        case Namespace() => "Helvetica"
        case Class() => "Helvetica"
        case AbstractClass() => "Helvetica italic"
        case Interface() => "Helvetica italic"
        case Trait() => "Helvetica italic"
    }
}