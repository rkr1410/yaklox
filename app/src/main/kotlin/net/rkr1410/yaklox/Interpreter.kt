package net.rkr1410.yaklox

import net.rkr1410.yaklox.TokenType.*

class Interpreter {

    fun interpret(program: List<Statement>) {
        try {
            execute(program)
        } catch (re: RuntimeError) {
            Yak.reportRuntimeError(re)
        }
    }

    fun execute(program: List<Statement>) {
        for (stmt in program) {
            when (stmt) {
                is Statement.Print -> println(evaluate(stmt.expr))
                is Statement.Expr  -> evaluate(stmt.expr)
            }
        }
    }

    private fun evaluate(expr: Expression): Any? {
        return when (expr) {
            is Expression.Binary   -> evalBinary(expr)
            is Expression.Grouping -> evalGrouping(expr)
            is Expression.Unary    -> evalUnary(expr)
            is Expression.Literal  -> evalLiteral(expr)
            is Expression.Ternary  -> evalTernary(expr)
            else -> null
        }
    }

    private fun evalLiteral(expr: Expression.Literal) = expr.value

    private fun evalGrouping(expr: Expression.Grouping) = evaluate(expr.expr)

    private fun evalUnary(expr: Expression.Unary): Any? {
        val value = evaluate(expr.right)
        return when (expr.operator.type) {
            MINUS -> when (value) {
                is Double -> -value
                else      -> null  //todo error
            }
            BANG  -> !value.isTruthy()
            else -> null
        }
    }

    private fun evalBinary(expr: Expression.Binary): Any? {
        val operator = expr.operator.type
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        if (expr.operator.type == EQUAL_EQUAL) return left == right
        if (expr.operator.type == BANG_EQUAL) return left != right

        if ((left is Double) && (right is Double)) {
            return when (operator) {
                GREATER       -> left > right
                GREATER_EQUAL -> left >= right
                LESS          -> left < right
                LESS_EQUAL    -> left <= right
                MINUS         -> left - right
                PLUS          -> left + right
                SLASH         -> left / right
                STAR          -> left * right
                else          -> throw error(expr.operator, "Binary operator ${expr.operator.lexeme}" +
                        " not implemented for types: Double, Double")
            }
        }

        val bothStrings = { (left is String) and (right is String) } //todo can we figure out a way for eitherIsString? What about chains?
        if (operator == PLUS && bothStrings()) return left.toStringOrNil() + right.toStringOrNil()

        //todo string multiplication?
        //val stringAndDouble = { ((left is String) and (right is Double)) || ((left is Double) and (right is String)) }

        throw error(expr.operator, "Binary operator ${expr.operator.lexeme}" +
                " not implemented for types: ${left.typeOrNil()}, ${right.typeOrNil()}")
    }

    private fun evalTernary(expr: Expression.Ternary): Any? {
        val testResult = evaluate(expr.condition)
        return if (testResult.isTruthy()) evaluate(expr.ifBranch) else evaluate(expr.elseBranch)
    }

    private fun Any?.toStringOrNil() = this?.toString() ?: "nil"
    private fun Any?.typeOrNil() = this?.javaClass?.toString()?.substringAfterLast(".") ?: "nil"
    private fun Any?.isTruthy(): Boolean {
        if (this is Boolean) return this
        return this != null
    }
    private fun error(token: Token, message: String) = RuntimeError(token, message)
}

class RuntimeError(val token: Token, message: String) : RuntimeException(message)