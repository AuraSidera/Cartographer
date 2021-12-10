package generator.php
import ast._

object Formatter {
    def constant(isConstant: Boolean) = if (isConstant) " const" else ""

    def static(isStatic: Boolean) = if (isStatic) " static" else ""

    def visibility(access: Access): String = access match {
        case Private() => "private"
        case Protected() => "protected"
        case Public() => "public"
    }

    def name(t: TypeNullable): String = t match {
        case NotNullable(subtype) => guessSimpleName(subtype)
        case Nullable(subtype) => guessSimpleName(subtype)
    }

    def guessSimpleName(t: TypeSimple): String = t match {
        case Void() => "nothing"
        case Bool() => "is_"
        case Int() => "n"
        case Double() => "x"
        case StringType() => "s"
        case Self() => "that"
        case ArrayType() => "list"
        case Callable() => "fn"
        case FullType(name) => name.last.asSnakeCase.value
    }
}