package lexer

case class SnakeCase(value: String) extends Identifier {
    def split = value.split("_").toList
}