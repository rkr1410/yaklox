package net.rkr1410.yaklox

import net.rkr1410.yaklox.TokenType.*

class Interpreter {

    fun interpret(program: List<Statement>) {
        try {
            execute(program, Environment())
        } catch (re: RuntimeError) {
            Yak.reportRuntimeError(re)
        }
    }

    fun execute(program: List<Statement>, env: Environment) {
        for (stmt in program) {
            when (stmt) {
                is Statement.Print -> println(evaluate(stmt.expr, env))
                is Statement.Expr  -> evaluate(stmt.expr, env)
                is Statement.Var   -> declareVariable(stmt.name, stmt.initializer, env)
                is Statement.Block -> executeBlock(stmt.statements, env)
                else               -> throw RuntimeException("Unknown statement type ${stmt.javaClass}")
            }
        }
    }

    private fun executeBlock(statements: List<Statement>, env: Environment) {
        val blockEnv = Environment(env)
        execute(statements, blockEnv)
    }

    private fun declareVariable(name: Token, initializer: Expression?, env: Environment) {
        val initialValue = if (initializer != null) evaluate(initializer, env) else null
        env.declare(name, initialValue)
    }

    private fun evaluate(expr: Expression, env: Environment): Any? {
        return when (expr) {
            is Expression.Binary   -> evalBinary(expr, env)
            is Expression.Grouping -> evalGrouping(expr, env)
            is Expression.Unary    -> evalUnary(expr, env)
            is Expression.Literal  -> evalLiteral(expr)
            is Expression.Ternary  -> evalTernary(expr, env)
            is Expression.Variable -> evalVariable(expr, env)
            is Expression.Assign   -> evalAssignment(expr, env)
            else                   -> throw RuntimeException("Unknown expression type ${expr.javaClass}")
        }
    }

    private fun evalAssignment(expr: Expression.Assign, env: Environment) =
        env.assign(expr.name, evaluate(expr.value, env))

    private fun evalVariable(expr: Expression.Variable, env: Environment) = env.get(expr.name)

    private fun evalLiteral(expr: Expression.Literal) = expr.value

    private fun evalGrouping(expr: Expression.Grouping, env: Environment) = evaluate(expr.expr, env)

    private fun evalUnary(expr: Expression.Unary, env: Environment): Any? {
        val value = evaluate(expr.right, env)
        return when (expr.operator.type) {
            MINUS -> when (value) {
                is Double -> -value
                else      -> throw error("Unary - (minus) not supported for operand type: ${value.typeOrNil()}")
            }
            BANG  -> !value.isTruthy()
            else -> null
        }
    }

    private fun evalBinary(expr: Expression.Binary, env: Environment): Any {
        val operator = expr.operator.type
        val left = evaluate(expr.left, env)
        val right = evaluate(expr.right, env)

        if (expr.operator.type == EQUAL_EQUAL) return left == right
        if (expr.operator.type == BANG_EQUAL) return left != right

        if ((left is Double) && (right is Double)) {
            return when (operator) {
                GREATER       -> left >  right
                GREATER_EQUAL -> left >= right
                LESS          -> left <  right
                LESS_EQUAL    -> left <= right
                MINUS         -> left -  right
                PLUS          -> left +  right
                SLASH         -> left /  right
                STAR          -> left *  right
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

    private fun evalTernary(expr: Expression.Ternary, env: Environment): Any? {
        val testResult = evaluate(expr.condition, env)
        return if (testResult.isTruthy()) evaluate(expr.ifBranch, env) else evaluate(expr.elseBranch, env)
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