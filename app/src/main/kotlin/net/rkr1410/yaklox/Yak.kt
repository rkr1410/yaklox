package net.rkr1410.yaklox

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.system.exitProcess

class Yak {
    companion object {
        var err: PrintStream = System.err

        /* KOTLIN
           Apparently @JvmStatic is needed by Java if I want to call this prefixed just by Yak,
           otherwise need Yak.Companion (when unnamed, the companion object goes by default 'Companion' name) */
        @JvmStatic
        fun main(args: Array<String>) {
            val argCount = args.size
            when {
                argCount  > 1 -> printUsageAndExit()
                argCount == 1 -> runScript(args[0])
                else          -> runPrompt()
            }
        }

        fun runCode(code: String) {
            println("Source:\n$code")
            val scanner = Scanner(code)
            val tokens = scanner.scanTokens()
            println(tokens)
        }

        private fun runScript(scriptName: String) {

        }

        private fun runPrompt() {

        }

        private fun printUsageAndExit() {
            println("Usage yak [script]")
            exitProcess(EX_USAGE)
        }

        fun report(line: Int, position: Int?, message: String) {
            err.println("[$line:$position] $message")
        }

        fun setNewErrorByteArrayStream(): ByteArrayOutputStream {
            val baos = ByteArrayOutputStream()
            val newPrintStream = PrintStream(baos)
            this.err = newPrintStream
            return baos
        }
    }
}

/* KOTLIN
   Ok, so there's main both here and in the Yak companion object. That other main is what gets
   executed when running net.rkr1410.yaklox.Yak, and this one when running net.rkr1410.yaklox.YakKt,
   which seems to explain stuff like this:
   https://stackoverflow.com/questions/68216963/spring-boot-kotlin-gradle-error-main-method-not-found-in-class */
fun main(args: Array<String>) {
    //Yak.main(args)
//    Yak.runCode("{(*")
    //Yak.runCode("//asd\n")
    //Yak.runCode("\"a\nz")
    //Yak.runCode("1.")
    //Yak.runCode("fafa r")
}