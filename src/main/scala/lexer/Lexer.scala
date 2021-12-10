package lexer

object Lexer {
    def apply(input: String): List[Symbol] = {      
        val special = List("/" , ":" , "<" , "+" , "#" , "-", "?", "=>")
        val keywords = List(
            "base" , "namespace" , "class" , "abstract" , "interface", "trait",
            "static" , "const" , "get" , "set" , 
            "void" , "bool", "int" , "double" , "string" , "array" , "callable" , "self"
        ) ++ special
        special
        .fold(input)((carry, symbol) => carry.replace(symbol, " " + symbol + " "))
        .split(" ")
        .filter(!_.isEmpty())
        .map(_ match {
            case keyword if keywords.contains(keyword) => Keyword(keyword)
            case identifier if identifier.charAt(0).isUpper => CamelCase(identifier)
            case identifier if identifier.charAt(0).isLetter => SnakeCase(identifier)
            case _ => Unknown()
        }).toList ++ List(End())
    }
}