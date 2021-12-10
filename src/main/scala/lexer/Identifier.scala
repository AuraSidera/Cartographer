package lexer

trait Identifier extends Symbol {
    def split: List[String]

    def asCamelCase: CamelCase = {
        val head::tail = split
        CamelCase(split.map(_.capitalize).mkString)
    }

    def asSnakeCase: SnakeCase = SnakeCase(split.map(_.toLowerCase).mkString("_"))
}