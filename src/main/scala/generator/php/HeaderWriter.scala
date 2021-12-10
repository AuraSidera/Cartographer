package generator.php
import java.io._
import lexer.CamelCase
import ast._

case class HeaderWriter(
    writer: PrintWriter,
    typeFormatter: TypeFormatter
) {
    def apply(namespace: List[CamelCase], c: BaseClass): Unit = {
        val (prefix, name, extend, implement, fields) = c match {
            case Class(CamelCase(name), extend, implement, fields) => ("class", name, extend, implement, fields)
            case AbstractClass(CamelCase(name), extend, implement, fields) => ("abstract class", name, extend, implement, fields)
            case Interface(CamelCase(name), extend, implement, fields) => ("interface", name, extend, implement, fields)
            case Trait(CamelCase(name), extend, implement, fields) => ("trait", name, extend, implement, fields)
        }
        writer.write("<?php\n")
        writer.append("namespace " + namespace.map(_.value).mkString("\\") + ";\n")
        writer.append(prefix + " " + name)
        extend match {
            case None => 
            case Some(baseClass) => writer.append(" extends " + typeFormatter.formatFqn(baseClass))
        }
        if (!implement.isEmpty) {
            writer.append(" implements " + implement.map(typeFormatter.formatFqn(_)).mkString(", "))
        }
        writer.append(" {\n")
    }
}