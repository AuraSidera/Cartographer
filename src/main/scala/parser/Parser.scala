package parser
import lexer._
import ast._

object Parser {
    def apply(symbols: Symbols): System = {
        try {
            system(symbols)._1
        }
        catch {
            case e: ParsingException => {
                println("Unexpected symbol: ")
                println(e.symbols)
                e.printStackTrace
                System(Nil, Nil)
            }
        }
    }

    def system(symbols: Symbols): (System, Symbols) = {
        val continue = List("namespace", "class", "abstract", "interface", "trait")
        symbols match {
            case Keyword("base")::CamelCase(identifier)::rest => {
                val (namespace_path, rest2) = namespacePath(rest)
                val (classes, rest3) = root(rest2)
                val (namespace_list, rest4) = namespaceList(rest3)
                (System(CamelCase(identifier)::namespace_path, Namespace(Nil, classes)::namespace_list), rest3)
            }
            case Keyword(keyword)::_ if continue.contains(keyword) => {
                val (classes, rest) = root(symbols)
                val (namespace_list, rest2) = namespaceList(rest)
                (System(Nil, Namespace(Nil, classes)::namespace_list), rest2)
            }
            case End()::_ => {
                val (classes, rest) = root(symbols)
                val (namespace_list, rest2) = namespaceList(rest)
                (System(Nil, Namespace(Nil, classes)::namespace_list), rest2)
            }
            case _ => throw new ParsingException(symbols)
        }
    }

    def namespacePath(symbols: Symbols): (List[CamelCase], Symbols) = {
        val stop = List(
            "namespace", "class", "abstract", "interface", "trait", "<", "static",
            "const", "+", "#", "-", "get", "set", "?", "void", "bool", "int", "double",
            "string", "array", "callable", "=>"
        )
        symbols match {
            case Keyword(keyword)::_ if stop.contains(keyword) => (Nil, symbols)
            case End()::_ => (Nil, symbols)
            case CamelCase(_)::_ => (Nil, symbols)
            case Keyword("/")::CamelCase(identifier)::rest => {
                val (namespace_path, rest2) = namespacePath(rest)
                (CamelCase(identifier)::namespace_path, rest2)
            }
            case _ => throw new ParsingException(symbols)
        }
    }

    def root(symbols: Symbols): (List[BaseClass], Symbols) = {
        val continue = List("class", "abstract", "interface", "trait")
        symbols match {
            case Keyword("namespace")::_ => (Nil, symbols)
            case End()::_ => (Nil, symbols)
            case Keyword(keyword)::_ if continue.contains(keyword) => {
                val (c, rest) = classParser(symbols)
                val (classes, rest2) = classList(rest)
                (c::classes, rest2)
            }
            case _ => throw new ParsingException(symbols)
        }
    }

    def namespaceList(symbols: Symbols): (List[Namespace], Symbols) = symbols match {
        case End()::_ => (Nil, symbols)
        case Keyword("namespace")::CamelCase(identifier)::rest => {
            val (namespace_path, rest2) = namespacePath(rest)
            val (classes, rest3) = classList(rest2)
            val (namespaces, rest4) = namespaceList(rest3)
            (Namespace(CamelCase(identifier)::namespace_path, classes)::namespaces, rest4)
        }
        case _ => throw new ParsingException(symbols)
    }

    def classList(symbols: Symbols): (List[BaseClass], Symbols) = symbols match {
        case Keyword("namespace")::_ => (Nil, symbols)
        case End()::_ => (Nil, symbols)
        case Keyword("class")::_ => {
            val (c, rest) = classParser(symbols)
            val (classes, rest2) = classList(rest)
            (c::classes, rest2)
        }
        case Keyword("abstract")::_ => {
            val (c, rest) = classParser(symbols)
            val (classes, rest2) = classList(rest)
            (c::classes, rest2)
        }
        case Keyword("interface")::_ => {
            val (c, rest) = classParser(symbols)
            val (classes, rest2) = classList(rest)
            (c::classes, rest2)
        }
        case Keyword("trait")::_ => {
            val (c, rest) = classParser(symbols)
            val (classes, rest2) = classList(rest)
            (c::classes, rest2)
        }
        case _ => throw new ParsingException(symbols)
    }

    def classParser(symbols: Symbols): (BaseClass, Symbols) = symbols match {
        case Keyword("class")::CamelCase(identifier)::rest => {
            val (extend, rest2) = maybeExtend(rest)
            val (implement, rest3) = maybeImplement(rest2)
            val (fields, rest4) = fieldList(rest3)
            (Class(CamelCase(identifier), extend, implement, fields), rest4)
        }
        case Keyword("abstract")::Keyword("class")::CamelCase(identifier)::rest => {
            val (extend, rest2) = maybeExtend(rest)
            val (implement, rest3) = maybeImplement(rest2)
            val (fields, rest4) = fieldList(rest3)
            (AbstractClass(CamelCase(identifier), extend, implement, fields), rest4)
        }
        case Keyword("interface")::CamelCase(identifier)::rest => {
            val (extend, rest2) = maybeExtend(rest)
            val (implement, rest3) = maybeImplement(rest2)
            val (fields, rest4) = fieldList(rest3)
            (Interface(CamelCase(identifier), extend, implement, fields), rest4)
        }
        case Keyword("trait")::CamelCase(identifier):: rest => {
            val (extend, rest2) = maybeExtend(rest)
            val (implement, rest3) = maybeImplement(rest2)
            val (fields, rest4) = fieldList(rest3)
            (Trait(CamelCase(identifier), extend, implement, fields), rest4)
        }
        case _ => throw new ParsingException(symbols)
    }

    def maybeExtend(symbols: Symbols): (Option[Fqn], Symbols) = {
        val next = List(
            "namespace", "class", "abstract", "interface", "trait", "<",
            "static", "const", "+", "#", "-"
        )
        symbols match {
            case Keyword(keyword)::_ if next.contains(keyword) => (None, symbols)
            case End()::_ => (None, symbols)
            case Keyword(":")::CamelCase(identifier)::rest => {
                val (namespace_path, rest2) = namespacePath(rest)
                (Some(CamelCase(identifier)::namespace_path), rest2)
            }
            case _ => throw new ParsingException(symbols)
        }
    }

    def maybeImplement(symbols: Symbols): (List[Fqn], Symbols) = {
        val next = List(
            "namespace", "class", "abstract", "interface", "trait",
            "static", "const", "+", "#", "-"
        )
        symbols match {
            case Keyword(keyword)::_ if next.contains(keyword) => (Nil, symbols)
            case End()::_ => (Nil, symbols)
            case Keyword("<")::CamelCase(identifier)::rest => {
                val (namespace_path, rest2) = namespacePath(rest)
                val (implements, rest3) = implementList(rest2)
                ((CamelCase(identifier)::namespace_path)::implements, rest3)
            }
            case _ => throw new ParsingException(symbols)
        }
    }

    def implementList(symbols: Symbols): (List[Fqn], Symbols) = {
        val next = List(
            "namespace", "class", "abstract", "interface", "trait",
            "static", "const", "+", "#", "-"
        )
        symbols match {
            case Keyword(keyword)::_ if next.contains(keyword) => (Nil, symbols)
            case End()::_ => (Nil, symbols)
            case CamelCase(identifier)::rest => {
                val (namespace_path, rest2) = namespacePath(rest)
                val (implements, rest3) = implementList(rest2)
                ((CamelCase(identifier)::namespace_path)::implements, rest3)
            }
            case _ => throw new ParsingException(symbols)
        }
    }

    def fieldList(symbols: Symbols): (List[Field], Symbols) = {
        val stop = List("namespace", "class", "abstract", "interface", "trait")
        val continue = List("static", "const", "+", "#", "-")
        symbols match {
            case Keyword(keyword)::_ if stop.contains(keyword) => (Nil, symbols)
            case End()::_ => (Nil, symbols)
            case Keyword(keyword)::_ if continue.contains(keyword) => {
                val (static, rest) = maybeStatic(symbols)
                val (constant, rest2) = maybeConstant(rest)
                val (access_type, rest3) = access(rest2)
                val (definition, rest4) = fieldDefinition(rest3)
                val (fields, rest5) = fieldList(rest4)
                (Field(static, constant, access_type, definition)::fields, rest5)
            }
            case _ => throw new ParsingException(symbols)
        }
    }

    def maybeStatic(symbols: Symbols): (Boolean, Symbols) = {
        val stop = List("const", "+", "#", "-")
        symbols match {
            case Keyword(keyword)::_ if stop.contains(keyword) => (false, symbols)
            case Keyword("static")::rest => (true, rest)
            case _ => throw new ParsingException(symbols)
        }
    }

    def maybeConstant(symbols: Symbols): (Boolean, Symbols) = {
        val stop = List("+", "#", "-")
        symbols match {
            case Keyword(keyword)::_ if stop.contains(keyword) => (false, symbols)
            case Keyword("const")::rest => (true, rest)
            case _ => throw new ParsingException(symbols)
        }
    }

    def access(symbols: Symbols): (Access, Symbols) = symbols match {
        case Keyword("+")::rest => (Public(), rest)
        case Keyword("#")::rest => (Protected(), rest)
        case Keyword("-")::rest => (Private(), rest)
        case _ => throw new ParsingException(symbols)
    }

    def fieldDefinition(symbols: Symbols): (FieldDefinition, Symbols) = symbols match {
        case CamelCase(identifier)::Keyword(":")::rest => {
            val (types, Keyword("=>")::rest2) = typeList(rest)
            val (type_nullable, rest3) = typeNullable(rest2)
            (Method(CamelCase(identifier), types, type_nullable), rest3)
        }
        case SnakeCase(identifier)::Keyword(":")::rest => {
            val (type_nullable, rest2) = typeNullable(rest)
            val (get, rest3) = maybeGet(rest2)
            val (set, rest4) = maybeSet(rest3)
            (Attribute(SnakeCase(identifier), type_nullable, get, set), rest4)
        }
        case _ => throw new ParsingException(symbols)
    }

    def maybeGet(symbols: Symbols): (Boolean, Symbols) = {
        val stop = List(
            "namespace", "class", "abstract", "interface", "trait",
            "static", "const", "+", "#", "-", "set"
        )
        symbols match {
            case Keyword(keyword)::_ if stop.contains(keyword) => (false, symbols)
            case End()::_ => (false, symbols)
            case Keyword("get")::rest => (true, rest)
            case _ => throw new ParsingException(symbols)
        }
    }

    def maybeSet(symbols: Symbols): (Boolean, Symbols) = {
        val stop = List(
            "namespace", "class", "abstract", "interface", "trait",
            "static", "const", "+", "#", "-"
        )
        symbols match {
            case Keyword(keyword)::_ if stop.contains(keyword) => (false, symbols)
            case End()::_ => (false, symbols)
            case Keyword("set")::rest => (true, rest)
            case _ => throw new ParsingException(symbols)
        }
    }

    def typeNullable(symbols: Symbols): (TypeNullable, Symbols) = {
        val continue = List("void", "bool", "int", "double", "string", "array", "callable", "self")
        symbols match {
            case Keyword("?")::rest => {
                val (t, rest2) = typeSimple(rest)
                (Nullable(t), rest2)
            }
            case Keyword(keyword)::_ if continue.contains(keyword) => {
                val (t, rest) = typeSimple(symbols)
                (NotNullable(t), rest)
            }
            case CamelCase(identifier)::_ => {
                val (t, rest) = typeSimple(symbols)
                (NotNullable(t), rest)
            }
            case _ => throw new ParsingException(symbols)
        }
    }

    def typeSimple(symbols: Symbols): (TypeSimple, Symbols) = symbols match {
        case Keyword("void")::rest => (Void(), rest)
        case Keyword("bool")::rest => (Bool(), rest)
        case Keyword("int")::rest => (Int(), rest)
        case Keyword("double")::rest => (Double(), rest)
        case Keyword("string")::rest => (StringType(), rest)
        case Keyword("array")::rest => (ArrayType(), rest)
        case Keyword("callable")::rest => (Callable(), rest)
        case Keyword("self")::rest => (Self(), rest)
        case CamelCase(identifier)::rest => {
            val (namespace_path, rest2) = namespacePath(rest)
            (FullType(CamelCase(identifier)::namespace_path), rest2)
        }
        case _ => throw new ParsingException(symbols)
    }

    def typeList(symbols: Symbols): (List[TypeNullable], Symbols) = {
        val continue = List("?", "void", "bool", "int", "double", "string", "array", "callable", "self")
        symbols match {
            case Keyword("=>")::_ => (Nil, symbols)
            case Keyword(keyword)::_ if continue.contains(keyword) => {
                val (type_nullable, rest) = typeNullable(symbols)
                val (types, rest2) = typeList(rest)
                (type_nullable::types, rest2)
            }
            case CamelCase(_)::_ => {
                val (type_nullable, rest) = typeNullable(symbols)
                val (types, rest2) = typeList(rest)
                (type_nullable::types, rest2)
            }
            case _ => throw new ParsingException(symbols)
        }
    }
}