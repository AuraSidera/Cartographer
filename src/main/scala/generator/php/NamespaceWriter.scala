package generator.php
import java.io.File
import lexer.CamelCase
import ast.Namespace

case class NamespaceWriter(
    base: List[CamelCase],
    path: String,
    typeResolver: TypeResolver
) {
    def apply(namespace: Namespace): Unit = {
        val currentNamespace = base ++ namespace.name
        val currentPath = path + "/src/" + namespace.name.map(_.value).mkString("/")
        val classWriter = new ClassWriter(currentNamespace, currentPath, typeResolver)
        createDirectory(currentPath)
        namespace.classes.foreach(classWriter(_))
    }

    def createDirectory(path: String): Unit = {
        val dir = new File(path)
        if (!dir.mkdirs) {
            throw new Exception("Cannot create directory in \"" + path + "\".")
        }
    }
}