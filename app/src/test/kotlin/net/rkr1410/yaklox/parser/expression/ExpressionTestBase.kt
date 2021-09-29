package net.rkr1410.yaklox.parser.expression

import net.rkr1410.yaklox.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows

open class ExpressionTestBase {
    fun expression(src: String): Expression {
        val expr = parse(src)?.first() as Statement.Expr

        return expr.expr
    }

    private fun parse(src: String): List<Statement>? {
        val scanner = Scanner("$src;")
        val tokens = scanner.scanTokens()
        val parser = Parser(tokens)
        return parser.parse()
    }

    fun assertErrorFor(expr: String, errMsg: String) {
        val errBaos = Yak.setNewErrorByteArrayStream()
        parse(expr)
        val actual = String(errBaos.toByteArray())
        assertTrue(actual.startsWith(errMsg), "Expected $actual to start with $errMsg")
    }

}