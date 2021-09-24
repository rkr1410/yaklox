package net.rkr1410.yaklox.parser.expression

import net.rkr1410.yaklox.Expression
import net.rkr1410.yaklox.Parser
import net.rkr1410.yaklox.Scanner

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
}