package net.rkr1410.yaklox.tools

import java.io.File

internal class AstClassGen(private val outputDir: String, private val baseName: String) {
    fun generateAst(serializedTypes: List<String>) {
        val typedefs = serializedTypes.map(TypeDef::of)
        val baseClassDefinition= """
                |// autogenerated
                |package net.rkr1410.yaklox
                
                |abstract class $baseName {
                |
                     |${each("" to "\n", typedefs.map(::astType))}
                |}
            """.trimMargin()

        File("$outputDir/$baseName.kt").printWriter().use { it.println(baseClassDefinition) }
    }

    private fun tab2() = "    ".repeat(2)
    private fun each(preAndPostfix: Pair<String?, String>, items: List<String>) =
        items.joinToString(preAndPostfix.second) { "${preAndPostfix.first?:""}$it" }.trimEnd()

    private fun astType(td: TypeDef) =
        """
            |    data class ${td.className}(
                    |${each("${tab2()}val " to ",\n", td.properties)}
            |    ): $baseName() 
            |
        """.trimMargin()
}

class TypeDef(val className: String, val properties: List<String>) {
    companion object {
        fun of(serialized: String): TypeDef {
            val className = serialized.split("=")[0].trim()
            val properties = serialized.split("=")[1].split(", ").map(String::trim).toList()
            return TypeDef(className, properties)
        }
    }
}

fun main() {
    val dir = "app/src/main/kotlin/net/rkr1410/yaklox/"
    //val dir = "/Users/pafau/tmp/"
    AstClassGen(dir, "Statement").generateAst(listOf(
        "Expr        = expr: Expression",
        "Print       = expr: Expression",
        "Var         = name: Token, initializer: Expression?",
        "Block       = statements: List<Statement>",
        "If          = condition: Expression, thenBranch: Statement, elseBranch: Statement?",
        "While       = condition: Expression, statement: Statement",
        "FlowChange  = flowToken: Token"
    ))
    AstClassGen(dir, "Expression").generateAst(listOf(
        "Assign   = name: Token, value: Expression",
        "Ternary  = condition: Expression, ifBranch: Expression, elseBranch: Expression",
        "Binary   = left: Expression, operator: Token, right: Expression",
        "Grouping = expr: Expression",
        "Literal  = value: Any?",
        "Unary    = operator: Token, right: Expression",
        "Variable = name: Token",
        "Logical  = left: Expression, operator: Token, right: Expression",
    ))

}