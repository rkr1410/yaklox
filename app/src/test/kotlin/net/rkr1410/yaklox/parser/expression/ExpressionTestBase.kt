package net.rkr1410.yaklox.parser.expression

import net.rkr1410.yaklox.Expression
import net.rkr1410.yaklox.Parser
import net.rkr1410.yaklox.Scanner

open class ExpressionTestBase {
    fun expression(src: String): Expression {
        val scanner = Scanner(src)
        val tokens = scanner.scanTokens()
        val parser = Parser(tokens)

        return parser.parse()
    }
}