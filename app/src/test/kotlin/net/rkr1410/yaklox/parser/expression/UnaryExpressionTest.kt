package net.rkr1410.yaklox.parser.expression

import net.rkr1410.yaklox.TokenType.BANG
import net.rkr1410.yaklox.TokenType.MINUS
import org.junit.jupiter.api.Test

class UnaryExpressionTest : ExpressionTestBase() {

    @Test
    fun testNegativeNumber() {
        val expr = expression("-1")

        IsUnary(expr, MINUS).hasOperandWhich { operand ->
            IsLiteral(operand, 1.0)
        }.assert()
    }

    @Test
    fun testDoubleNegativeNumber() {
        val expr = expression("--1")

        IsUnary(expr, MINUS).hasOperandWhich { secondMinus ->
            IsUnary(secondMinus, MINUS).hasOperandWhich { literal ->
                IsLiteral(literal, 1.0)
            }
        }.assert()
    }

    @Test
    fun testNegativeTrue() {
        val expr = expression("!true")

        IsUnary(expr, BANG)
            .hasOperandWhich { operand -> IsLiteral(operand, true) }
            .assert()
    }

    @Test
    fun testDoubleNegativeFalse() {
        val expr = expression("!!false")

        IsUnary(expr, BANG).hasOperandWhich { secondBang ->
            IsUnary(secondBang, BANG).hasOperandWhich { literal ->
                IsLiteral(literal, false)
            }
        }.assert()
    }

    @Test
    fun testNegativeGroup() {
        val expr = expression("-(-123)")

        IsUnary(expr, MINUS).hasOperandWhich { operand ->
            IsGrouping(operand).groupsExpressionWhich { grouping ->
                IsUnary(grouping, MINUS).hasOperandWhich { literal ->
                    IsLiteral(literal, 123.0)
                }
            }
        }.assert()
    }
}

