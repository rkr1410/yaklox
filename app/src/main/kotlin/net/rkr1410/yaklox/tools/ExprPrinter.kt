package net.rkr1410.yaklox.tools

import net.rkr1410.yaklox.Expression

class ExprPrinter {

    fun stringify(expr: Expression): String {
        return when (expr) {
            is Expression.Binary -> p(expr.operator.lexeme, expr.left, expr.right)
            is Expression.Grouping -> g(expr.expr)
            is Expression.Unary -> "${expr.operator.lexeme}${stringify(expr.right)}"
            is Expression.Literal -> expr.value?.toString() ?: "nil"
            else -> "Unimplemented for ${expr.javaClass.simpleName}"
        }
    }

    private fun p(sym: String, vararg exprs: Expression): String {
        return "($sym ${exprs.joinToString(" ", transform = ::stringify)})"
    }

    private fun g(vararg exprs: Expression): String {
        return "[grp ${exprs.joinToString(" ", transform = ::stringify)}]"
    }
}