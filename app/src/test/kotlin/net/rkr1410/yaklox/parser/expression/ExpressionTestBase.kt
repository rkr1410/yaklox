package net.rkr1410.yaklox.parser.expression

import net.rkr1410.yaklox.Expression
import net.rkr1410.yaklox.Parser
import net.rkr1410.yaklox.Scanner
import net.rkr1410.yaklox.TokenType
import net.rkr1410.yaklox.tools.ExprPrinter
import kotlin.test.assertEquals
import kotlin.test.assertIs

open class ExpressionTestBase {

    val literalValueVisitor : Expression.Visitor<Any?> = object : Expression.Visitor<Any?> {
        override fun visitBinaryExpression(expression: Expression.Binary): Any? = null
        override fun visitGroupingExpression(expression: Expression.Grouping): Any? = null
        override fun visitUnaryExpression(expression: Expression.Unary): Any? = null
        override fun visitLiteralExpression(expression: Expression.Literal): Any? = expression.value
    }

    fun expression(src: String): Expression {
        val scanner = Scanner(src)
        val tokens = scanner.scanTokens()
        val parser = Parser(tokens)

        return parser.parse()
    }

    abstract class ExpressionAsserter() {
        val exprPrinter = ExprPrinter()
        abstract fun assert()
    }

    class IsLiteral(
        private val expression: Expression,
        private val value: Any?
    ) : ExpressionAsserter() {
        override fun assert() {
            assertIs<Expression.Literal>(expression,"Expected ${expression.javaClass.simpleName} ${exprPrinter.visit(expression)} to be a Literal\n")
            assertEquals(value, (expression as Expression.Literal).value)
        }
    }

    class IsUnary(
        private val expression: Expression,
        private val operator: TokenType
    ) : ExpressionAsserter() {
        override fun assert() {
            assertIs<Expression.Unary>(expression,"Expected ${expression.javaClass.simpleName} ${exprPrinter.visit(expression)} to be Unary\n")
            assertEquals(operator, expression.operator.type)
        }

        fun hasOperandWhich(operandAsserter: ((Expression) -> ExpressionAsserter)): IsUnary {
            assertIs<Expression.Unary>(expression,"Expected ${expression.javaClass.simpleName} ${exprPrinter.visit(expression)} to be Unary\n")
            operandAsserter(expression.right).assert()
            return this
        }
    }

    class IsGrouping(
        private val expression: Expression
    ) : ExpressionAsserter() {
        override fun assert() {
            assertIs<Expression.Grouping>(expression,"Expected ${expression.javaClass.simpleName} ${exprPrinter.visit(expression)} to be a Grouping\n")
        }

        fun groupsExpressionWhich(groupedExprAsserter: ((Expression) -> ExpressionAsserter)): IsGrouping {
            assertIs<Expression.Grouping>(expression,"Expected ${expression.javaClass.simpleName} ${exprPrinter.visit(expression)} to be a Grouping\n")
            groupedExprAsserter(expression.expr).assert()
            return this
        }
    }

    class IsBinary(
        private val expression: Expression,
        private val operator: TokenType
    ) : ExpressionTestBase.ExpressionAsserter() {
        override fun assert() {
            assertIs<Expression.Binary>(expression)
            assertEquals(operator, expression.operator.type)
        }

        fun hasLeftHandSideWhich(lhsAsserter: ((Expression) -> ExpressionAsserter)): IsBinary {
            assertIs<Expression.Binary>(expression,"Expected ${expression.javaClass.simpleName} ${exprPrinter.visit(expression)} to be a Binary expression\n")
            lhsAsserter(expression.left).assert()
            return this
        }

        fun hasRightHandSideWhich(rhsAsserter: ((Expression) -> ExpressionAsserter)): IsBinary {
            assertIs<Expression.Binary>(expression,"Expected ${expression.javaClass.simpleName} ${exprPrinter.visit(expression)} to be a Binary expression\n")
            rhsAsserter(expression.right).assert()
            return this
        }
    }
}