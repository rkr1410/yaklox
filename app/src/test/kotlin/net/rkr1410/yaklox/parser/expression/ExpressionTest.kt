package net.rkr1410.yaklox.parser.expression

import net.rkr1410.yaklox.ParseError
import net.rkr1410.yaklox.Yak
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertContains

class ExpressionTest : ExpressionTestBase() {

    @Test
    @DisplayName("Expect an error for expression consisting of single minus sign")
    fun singleMinusError() {
        val errBaos = Yak.setNewErrorByteArrayStream()
        assertThrows<ParseError>("") { expression("-") }
        val error = "Expected an expression"
        assertContains(String(errBaos.toByteArray()), error)
    }

    @Test
    @DisplayName("Expect an error for expression with a dangling +")
    fun middleOfExpressionError() {
        val errBaos = Yak.setNewErrorByteArrayStream()
        assertThrows<ParseError>("") { expression("-(1 + 2 +") }
        val error = "Expected an expression"
        assertContains(String(errBaos.toByteArray()), error)
    }

    @Test
    @DisplayName("Expect an error for expression without a closing paren")
    fun noClosingParenError() {
        val errBaos = Yak.setNewErrorByteArrayStream()
        assertThrows<ParseError>("") { expression("-(1 + 2 + 3") }
        val error = "Expected a closing ')'"
        assertContains(String(errBaos.toByteArray()), error)
    }
}