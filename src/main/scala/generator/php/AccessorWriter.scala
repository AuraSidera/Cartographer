package generator.php
import java.io._
import ast._

case class AccessorWriter(
    writer: PrintWriter,
    typeFormatter: TypeFormatter
) {
    def apply(attribute: Field): Unit = {
        val Field(isStatic, isConstant, _, Attribute(name, t, hasGet, hasSet)) = attribute
        if (isConstant || isStatic) {
            return
        }
        if (hasGet) {
            writer.append("    public function get" + name.asCamelCase.value + "(): " + typeFormatter(t) + " {\n")
            writer.append("       return $this->" + name.value + ";\n")
            writer.append("    }\n\n")
        }
        if (hasSet) {
            writer.append("    public function set" + name.asCamelCase.value + "(" + typeFormatter(t) + " $" + name.value + "): self {\n")
            writer.append("       $this->" + name.value + " = $" + name.value + ";\n")
            writer.append("       return $this;\n")
            writer.append("    }\n\n")
        }
    }
}