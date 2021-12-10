package generator.php
import java.io._
import ast._

case class MethodWriter(
    writer: PrintWriter,
    typeFormatter: TypeFormatter
) {
    def apply(field: Field): Unit = {
        val Method(name, parameters, result) = field.definition
        writer.append("    " +
            Formatter.visibility(field.access) +
            Formatter.static(field.static) +
            " function " + name.asCamelCase.value +
            "(" + parameters.map(t => typeFormatter(t) + " $" + Formatter.name(t)).mkString(", ") + ")" +
            formatResult(result) +
            " {\n"
        )
        writer.append("    }\n\n")
    }

    def formatResult(t: TypeNullable): String = t match {
        case NotNullable(Void()) => ""
        case _ => ": " + typeFormatter(t)
    }
}