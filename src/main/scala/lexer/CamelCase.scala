package lexer

case class CamelCase(value: String) extends Identifier {
    def split: List[String] = value.split("(?=\\p{Lu})").toList
}