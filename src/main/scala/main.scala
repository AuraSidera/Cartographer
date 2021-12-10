import System.exit
import scala.io.Source
import lexer._
import parser._
import ast._
import type_tree._
import generator.php.Generator

import sys.process._
import generator.php.TypeResolver

object Main extends App {
    if (args.length < 2) {
        println("Usage: " + args(0) + " <path to file> <output dir>\n")
        exit(-1)
    }

    val source = args(0)
    val dest = args(1)
    val system = Parser(Lexer(readFile(source)))
    
    val phpGenerator = new Generator();
    phpGenerator(system, dest)
    
    val typeTree = TreeBuilder(system)
    val charter = new TypeChart
    charter(typeTree, dest + "/type-chart.dot")

    def readFile(path: String): String = {
        val bufferedSource = Source.fromFile(path)
        try {
            bufferedSource.getLines.mkString(" ")
        }
        finally {
            bufferedSource.close
        }
    }
}