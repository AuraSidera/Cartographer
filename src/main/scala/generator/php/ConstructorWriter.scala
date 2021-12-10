package generator.php
import java.io._
import ast._

case class ConstructorWriter(
    writer: PrintWriter,
    typeFormatter: TypeFormatter
) {
    def apply(attributes: List[Field]): Unit = {
        val params = attributes
            .map{ case Field(_, _, _, d) => d }
            .map{ case Attribute(name, t, _, _) => (name, t) }
        writer.append(
            "    public function __construct(" +
            params.map{ case (name, t) => typeFormatter(t) + " $" + name.value }.mkString(", ") + ") {\n"
        )
        params.foreach{ case (name, _) =>
            writer.append("        $this->" + name.value + " = $" + name.value + ";\n")
        }
        writer.append("    }\n\n")
    }
}