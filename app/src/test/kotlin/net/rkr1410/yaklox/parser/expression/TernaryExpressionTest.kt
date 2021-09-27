package net.rkr1410.yaklox.parser.expression

import net.rkr1410.yaklox.TokenType.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TernaryExpressionTest : ExpressionTestBase() {
    @Test
    fun testValidTernaryParsesSuccessfully() {
        val expr = expression("2 == 2 ? 3 : 4")

        IsTernary(expr)
            .hasConditionWhich { cond -> IsBinary(cond, EQUAL_EQUAL) }
            .hasTrueBranchWhich { trueExpr -> IsLiteral(trueExpr, 3.0) }
            .hasFalseBranchWhich { falseExpr -> IsLiteral(falseExpr, 4.0) }
            .assert()
    }

    @Test
    @DisplayName("A question mark by itself os an error")
    fun questionMarkIsError() {
        assertErrorFor("?", "[1:1] Expected an expression")
    }

    @Test
    @DisplayName("An expression with just question mark is an error")
    fun exprPlusQuestionMarkIsAnError() {
        assertErrorFor("1 ?", "[1:4] Expected an expression")
    }

    @Test
    @DisplayName("A ternary operator must have a colon")
    fun noColonIsAnError() {
        assertErrorFor("1 ? 2", "[1:6] Expected :")
    }

    @Test
    @DisplayName("A ternary operator must have a 'false' branch")
    fun noFalseBranchIsAnError() {
        assertErrorFor("1 ? 2 :", "[1:8] Expected an expression")
    }
}