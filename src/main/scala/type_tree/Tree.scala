package type_tree

case class Tree(
    val name: String,
    val nodeType: NodeType,
    var parent: Option[Node] = None,
    var children: List[Node] = Nil,
) {
    def identifier: String = path.map(_.name).mkString("_")

    def path: List[Node] = pathReversed.reverse

    def pathReversed: List[Node] = parent match {
        case None => List(this)
        case Some(node) => this::node.pathReversed
    }

    def depth: Int = pathReversed.length - 1

    def siblings: List[Node] = parent match {
        case None => Nil
        case Some(node) => node.children
    }

    def nodes: List[Node] = children match {
        case Nil => List(this)
        case child::Nil => this::child.nodes
        case children => this::children.map(_.nodes).reduce((a, b) => a ++ b)
    }

    def edges: List[(Node, Node)] = children match {
        case Nil => Nil
        case child::Nil => (this, child)::child.edges
        case children => children.map(child => (this, child)::child.edges).reduce((a, b) => a ++ b)
    }

    def depthFirstSearch(f: Tree => Boolean): Option[Tree] = {
        if (f(this)) {
            return Some(this)
        }
        children.foreach{child =>
            child.depthFirstSearch(f) match {
                case Some(node) => return Some(node)
                case _ =>
            }
        }
        None
    }

    override def toString: String = children.foldLeft("|  " * depth + "+" + name + "\n")((carry, node) => carry + node.toString)
}