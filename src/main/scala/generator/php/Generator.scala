package generator.php
import java.io._
import lexer.CamelCase
import ast._

class Generator {
    def apply(system: System, path: String): Unit = {
        val namespaceWriter = NamespaceWriter(system.name, path, TypeResolver.fromSystem(system))
        system.namespaces.foreach(namespaceWriter(_))
        writeComposer(system, path)
    }

    def writeComposer(system: System, path: String): Unit = {
        val writer = new PrintWriter(new File(path + "/composer.json"))
        writer.write("{\n")
        writer.append("    \"name\": \"\",\n")
        writer.append("    \"description\": \"\",\n")
        writer.append("    \"type\": \"project\",\n")
        writer.append("    \"bin\": [],\n")
        writer.append("    \"authors\": [],\n")
        writer.append("    \"require\": {},\n")
        writer.append("    \"require-dev\": {},\n")
        writer.append("    \"autoload\": {\n")
        writer.append("        \"psr-4\": {\n")
        writer.append("            \"" + system.name.map(_.value).mkString("\\\\") + "\\\\\": \"src\"\n")
        writer.append("        }\n")
        writer.append("    }\n")
        writer.append("}\n")
        writer.close
    }
}