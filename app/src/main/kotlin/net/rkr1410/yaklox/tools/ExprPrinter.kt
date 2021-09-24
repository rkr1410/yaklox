package net.rkr1410.yaklox.tools

import net.rkr1410.yaklox.Expression

class ExprPrinter : Expression.Visitor<String> {

    fun visit(expr: Expression) = expr.accept(this)

    private fun p(sym: String, vararg exprs: Expression): String {
        return "($sym ${exprs.joinToString(" ", transform = { e -> e.accept(this)})})"
    }

    private fun g(vararg exprs: Expression): String {
        return "[grp ${exprs.joinToString(" ", transform = { e -> e.accept(this)})}]"
    }

    override fun visitBinaryExpression(expression: Expression.Binary) =
        p(expression.operator.lexeme, expression.left, expression.right)

    override fun visitGroupingExpression(expression: Expression.Grouping) =
        g(expression.expr)

    override fun visitLiteralExpression(expression: Expression.Literal) =
        expression.value?.toString() ?: "nil"

    override fun visitUnaryExpression(expression: Expression.Unary) =
        "${expression.operator.lexeme}${expression.right.accept(this)}"
}