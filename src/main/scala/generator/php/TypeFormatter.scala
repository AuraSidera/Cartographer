package generator.php
import lexer.CamelCase
import ast._

case class TypeFormatter(
    currentNamespace: List[CamelCase],
    typeResolver: TypeResolver
) {
    def apply(t: TypeNullable): String = formatNullable(t)

    def formatNullable(t: TypeNullable): String = t match {
        case NotNullable(subtype) => formatSimple(subtype)
        case Nullable(subtype) => "?" + formatSimple(subtype)
    }

    def formatSimple(t: TypeSimple): String = t match {
        case Void() => "void"
        case Bool() => "bool"
        case Int() => "int"
        case Double() => "double"
        case StringType() => "string"
        case Self() => "self"
        case ArrayType() => "array"
        case Callable() => "callable"
        case FullType(name) => formatFqn(name)
    }

    def formatFqn(name: List[CamelCase]): String = {
        "\\" + typeResolver(currentNamespace, name).map(_.value).mkString("\\")
    }
}