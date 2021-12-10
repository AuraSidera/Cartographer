package generator.php
import java.io._
import ast.Field
import ast.Attribute

case class AttributeWriter(
    writer: PrintWriter,
    typeFormatter: TypeFormatter
) {
    def apply(field: Field): Unit = {
        val Attribute(name, t, _, _) = field.definition
        writer.append("    " +
            Formatter.visibility(field.access) +
            Formatter.static(field.static) +
            Formatter.constant(field.constant) +
            " $" + name.asSnakeCase.value + ";\n"
        )
    }
}