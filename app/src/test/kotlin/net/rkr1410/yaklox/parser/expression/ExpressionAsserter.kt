package net.rkr1410.yaklox.parser.expression

import net.rkr1410.yaklox.Expression
import net.rkr1410.yaklox.TokenType
import net.rkr1410.yaklox.tools.ExprPrinter
import kotlin.test.assertEquals
import kotlin.test.assertIs

abstract class ExpressionAsserter {
    protected val exprPrinter = ExprPrinter()
    abstract fun assert()
}

class IsLiteral(
    private val expression: Expression,
    private val value: Any?
) : ExpressionAsserter() {
    override fun assert() {
        assertIs<Expression.Literal>(expression,"Expected ${expression.javaClass.simpleName} ${exprPrinter.visit(expression)} to be a Literal\n")
        assertEquals(value, expression.value)
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

    fun hasOperandWhich(operandAsserter: (Expression) -> ExpressionAsserter): IsUnary {
        operandAsserter((expression as Expression.Unary).right).assert()
        return this
    }
}

class IsGrouping(
    private val expression: Expression
) : ExpressionAsserter() {
    override fun assert() {
        assertIs<Expression.Grouping>(expression,"Expected ${expression.javaClass.simpleName} ${exprPrinter.visit(expression)} to be a Grouping\n")
    }

    fun groupsExpressionWhich(groupedExprAsserter: (Expression) -> ExpressionAsserter): IsGrouping {
        groupedExprAsserter((expression as Expression.Grouping).expr).assert()
        return this
    }
}

class IsBinary(
    private val expression: Expression,
    private val operator: TokenType
) : ExpressionAsserter() {
    override fun assert() {
        assertIs<Expression.Binary>(expression, "Expected ${expression.javaClass.simpleName} ${exprPrinter.visit(expression)} to be a Binary expression\n")
        assertEquals(operator, expression.operator.type)
    }

    fun hasLeftHandSideWhich(lhsAsserter: (Expression) -> ExpressionAsserter): IsBinary {
        lhsAsserter((expression as Expression.Binary).left).assert()
        return this
    }

    fun hasRightHandSideWhich(rhsAsserter: (Expression) -> ExpressionAsserter): IsBinary {
        rhsAsserter((expression as Expression.Binary).right).assert()
        return this
    }
}