package net.rkr1410.yaklox.parser.expression

import net.rkr1410.yaklox.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows

open class ExpressionTestBase {
    fun expression(src: String): Expression {
        val scanner = Scanner(src)
        val tokens = scanner.scanTokens()
        val parser = Parser(tokens)

        return parser.parse()
    }

    fun assertErrorFor(expr: String, errMsg: String) {
        val errBaos = Yak.setNewErrorByteArrayStream()
        assertThrows<ParseError>("") { expression(expr) }
        val actual = String(errBaos.toByteArray())
        assertTrue(actual.startsWith(errMsg), "Expected $actual to start with $errMsg")
    }

}