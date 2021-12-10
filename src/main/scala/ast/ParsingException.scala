package ast
import lexer.Symbol

final case class ParsingException(
    val symbols: List[Symbol],
    private val message: String = "Unexpected symbol",
    private val cause: Throwable = None.orNull
) extends Exception(message, cause)