package net.rkr1410.yaklox.parser.expression

import net.rkr1410.yaklox.Expression
import net.rkr1410.yaklox.TokenType
import net.rkr1410.yaklox.tools.ExprPrinter
import kotlin.test.assertEquals
import kotlin.test.assertIs

abstract class ExpressionAsserter {
    private val exprPrinter = ExprPrinter()
    private val asserters = mutableListOf<() -> ExpressionAsserter>()
    fun addAsserter(anAssert: () -> ExpressionAsserter) = asserters.add(anAssert)
    open fun assert() {
        for (asserter in asserters) asserter().assert()
    }
    fun shouldHaveBeen(expr: Expression, type: String) =
        "Expected ${expr.javaClass.simpleName} ${exprPrinter.stringify(expr)} to be a ${type}\n"
}

class IsLiteral(
    private val expr: Expression,
    private val value: Any?
) : ExpressionAsserter() {
    override fun assert() {
        assertIs<Expression.Literal>(expr, shouldHaveBeen(expr, "Literal"))
        assertEquals(value, expr.value)
    }
}

class IsUnary(
    private val expr: Expression,
    private val operator: TokenType
) : ExpressionAsserter() {
    override fun assert() {
        assertIs<Expression.Unary>(expr, shouldHaveBeen(expr, "Unary"))
        assertEquals(operator, expr.operator.type)
        super.assert()
    }

    fun hasOperandWhich(operandAsserter: (Expression) -> ExpressionAsserter): IsUnary {
        addAsserter { operandAsserter((expr as Expression.Unary).right) }
        return this
    }
}

class IsGrouping(
    private val expr: Expression
) : ExpressionAsserter() {
    override fun assert() {
        assertIs<Expression.Grouping>(expr, shouldHaveBeen(expr, "Grouping"))
        super.assert()
    }

    fun groupsExpressionWhich(groupedExprAsserter: (Expression) -> ExpressionAsserter): IsGrouping {
        addAsserter { groupedExprAsserter((expr as Expression.Grouping).expr) }
        return this
    }
}

class IsBinary(
    private val expr: Expression,
    private val operator: TokenType
) : ExpressionAsserter() {
    override fun assert() {
        assertIs<Expression.Binary>(expr, shouldHaveBeen(expr, "Binary expression"))
        assertEquals(operator, expr.operator.type)
        super.assert()
    }

    fun hasLeftHandSideWhich(lhsAsserter: (Expression) -> ExpressionAsserter): IsBinary {
        addAsserter { lhsAsserter((expr as Expression.Binary).left) }
        return this
    }

    fun hasRightHandSideWhich(rhsAsserter: (Expression) -> ExpressionAsserter): IsBinary {
        addAsserter { rhsAsserter((expr as Expression.Binary).right) }
        return this
    }
}

class IsTernary(
    private val expression: Expression
) : ExpressionAsserter() {
    override fun assert() {
        assertIs<Expression.Ternary>(expression, shouldHaveBeen(expression, "Ternary expression"))
        super.assert()
    }

    fun hasConditionWhich(conditionBranchAsserter: (Expression) -> ExpressionAsserter): IsTernary {
        addAsserter { conditionBranchAsserter((expression as Expression.Ternary).condition) }
        return this
    }

    fun hasTrueBranchWhich(ifBranchAsserter: (Expression) -> ExpressionAsserter): IsTernary {
        addAsserter { ifBranchAsserter((expression as Expression.Ternary).ifBranch) }
        return this
    }

    fun hasFalseBranchWhich(falseBranchAsserter: (Expression) -> ExpressionAsserter): IsTernary {
        addAsserter { falseBranchAsserter((expression as Expression.Ternary).elseBranch) }
        return this
    }

}