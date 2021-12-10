package generator.php
import java.io._
import lexer.SnakeCase
import lexer.CamelCase
import ast._

case class ClassWriter(
    namespace: List[CamelCase],
    parentPath: String,
    typeResolver: TypeResolver
) {
    def apply(c: BaseClass): Unit = {
        val typeFormatter = TypeFormatter(namespace, typeResolver)
        val writer = new PrintWriter(new File(parentPath + "/" + c.name.value + ".php"))
        val headerWriter = HeaderWriter(writer, typeFormatter)
        val constructorWriter = ConstructorWriter(writer, typeFormatter)
        val attributeWriter = AttributeWriter(writer, typeFormatter)
        val accessorWriter = AccessorWriter(writer, typeFormatter)
        val methodWriter = MethodWriter(writer, typeFormatter)

        var (attributes, methods) = c.fields.partition{
            _.definition match {
                case Attribute(_, _, _, _) => true
                case Method(_, _, _) => false
            }
        }
        
        headerWriter(namespace, c)
        attributes.foreach(attributeWriter(_))
        writer.append("\n")
        constructorWriter(attributes)
        attributes.foreach(accessorWriter(_))
        methods.foreach(methodWriter(_))
        writer.append("}\n")
        writer.close
    }
}