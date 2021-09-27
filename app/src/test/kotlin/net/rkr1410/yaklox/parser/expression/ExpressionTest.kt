package net.rkr1410.yaklox.parser.expression

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ExpressionTest : ExpressionTestBase() {

    @Test
    @DisplayName("Expect an error for expression consisting of single minus sign")
    fun singleMinusError() {
        assertErrorFor("-", "[1:2] Expected an expression")
    }

    @Test
    @DisplayName("Expect an error for expression with a dangling +")
    fun middleOfExpressionError() {
        assertErrorFor("-(1 + 2 +",  "[1:10] Expected an expression")
    }

    @Test
    @DisplayName("Expect an error for expression without a closing paren")
    fun noClosingParenError() {
        assertErrorFor("-(1 + 2 + 3",  "[1:12] Expected a closing ')'")
    }
}