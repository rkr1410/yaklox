package net.rkr1410.yaklox

import net.rkr1410.yaklox.Expression.Binary
import net.rkr1410.yaklox.Expression.Logical
import net.rkr1410.yaklox.TokenType.*

/*

    program        → declaration* EOF ;
    declaration    → varDecl | statement ;
    varDecl        → "var" IDENTIFIER ( '=' expression )? ";" ;
    statement      → exprStmt | ifStmt | printStmt | block | whileStmt | flowChangeStmt;
    flowChangeStmt → BREAK | CONTINUE ;
    whileStmt      → "while" "(" expression ")" statement
    ifStmt         → "if" "(" expression ")" statement ( "else" statement )?
    exprStmt       → expression ";" ;
    printStmt      → "print" expression ";" ;
    block          → "{" declaration* "}" ;
    expression     → assignment ;
    assignment     → IDENTIFIER "=" assignment | logic_or ;
    logic_or       → logic_and ( "or" logic_and )*;
    logic_and      → ternary ( "and" ternary )* ;
    ternary        → equality ? equality : equality ;
    equality       → comparison ( ( "!=" | "==" ) comparison )* ;
    comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    term           → factor ( ( "-" | "+" ) factor )* ;
    factor         → unary ( ( "/" | "*" ) unary )* ;
    unary          → ( ("!" | "-") unary ) | primary ;
    primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDENTIFIER;
 */

class Parser(private val tokens: List<Token>) {
    private var current = 0

    fun parse(): List<Statement>? {
        val statements = mutableListOf<Statement>()
        while (!isEof()) {
            try {
                val statement = declaration()
                statements.add(statement)
            } catch (e: Exception) {
                synchronize()
                return null
            }
        }
        return statements
    }

    private fun declaration(): Statement {
        if (advanceIf(VAR)) return varDeclaration()

        return statement()
    }

    private fun varDeclaration(): Statement {
        require("Missing variable name", IDENTIFIER)

        val varName = previous()
        var initializer: Expression? = null
        if (advanceIf(EQUAL)) {
            initializer = expression()
        }
        require("Expected ;", SEMICOLON)

        return Statement.Var(varName, initializer)
    }

    private fun statement(): Statement {
        if (advanceIf(PRINT)) return printStatement()
        if (advanceIf(LEFT_BRACE)) return block()
        if (advanceIf(IF)) return ifStatement()
        if (advanceIf(WHILE)) return whileStatement()
        if (advanceIf(BREAK, CONTINUE)) return breakStatement()

        return expressionStatement()
    }

    private fun breakStatement(): Statement.FlowChange {
        val fcToken = previous()
        require("Expected ;", SEMICOLON)

        return Statement.FlowChange(fcToken)
    }

    private fun whileStatement(): Statement.While {
        if (!advanceIf(LEFT_PAREN)) throw error("'while' condition must be parenthesized")
        val condition = expression()
        if (!advanceIf(RIGHT_PAREN)) throw error("Closing paren missing")

        val stmt = statement()

        return Statement.While(condition, stmt)
    }

    private fun ifStatement(): Statement.If {
        if (!advanceIf(LEFT_PAREN)) throw error("'if' condition must be parenthesized")
        val condition = expression()
        if (!advanceIf(RIGHT_PAREN)) throw error("Closing paren missing")

        val thenBranch = statement()
        var elseBranch: Statement? = null
        if (advanceIf(ELSE)) elseBranch = statement()

        return Statement.If(condition, thenBranch, elseBranch)
    }

    private fun printStatement(): Statement.Print {
        val value = expression()
        require("Expected ;", SEMICOLON)

        return Statement.Print(value)
    }

    private fun block(): Statement.Block {
        val statements = mutableListOf<Statement>()

        while (!isEof() and (peek().type != RIGHT_BRACE)) {
            statements.add(declaration())
        }
        require("Unexpected EOF (unclosed block)", RIGHT_BRACE)
        return Statement.Block(statements)
    }

    private fun expressionStatement(): Statement.Expr {
        val value = expression()
        require("Expected ;", SEMICOLON)

        return Statement.Expr(value)
    }

    private fun expression(): Expression = assignment()

    private fun assignment(): Expression {
        var expr = logicOr()

        if (advanceIf(EQUAL)) {
            val assignmentExpr = assignment()
            if (expr is Expression.Variable) expr = Expression.Assign(expr.name, assignmentExpr)
            else throw error("Invalid assignment target")
        }

        return expr
    }

    private fun logicOr()  = binary(::Logical, ::logicAnd, OR)
    private fun logicAnd() = binary(::Logical, ::ternary, AND)

    private fun ternary(): Expression {
        var expr = equality()

        if (advanceIf(QUESTION_MARK)) {
            val ifBranch = equality()
            require("Expected :", COLON)
            val elseBranch = equality()
            expr = Expression.Ternary(expr, ifBranch, elseBranch)
        }

        return expr
    }

    private fun equality()   = binary(::Binary, ::comparison, EQUAL_EQUAL, BANG_EQUAL)
    private fun comparison() = binary(::Binary, ::term,       GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)
    private fun term()       = binary(::Binary, ::factor,     PLUS, MINUS)
    private fun factor()     = binary(::Binary, ::unary,      SLASH, STAR)

    private fun binary(
        creatpr: (Expression, Token, Expression) -> Expression,
        higherPrecedence: () -> Expression,
        vararg operators: TokenType
    ): Expression {
        var expr = higherPrecedence()

        while (advanceIf(*operators)) {
            val operator = previous()
            val right = higherPrecedence()
            expr = creatpr(expr, operator, right)
        }
        return expr
    }

    private fun unary(): Expression  {
        if (advanceIf(BANG, MINUS)) {
            val operator = previous()
            val right = unary()
            return Expression.Unary(operator, right)
        }

        return primary()
    }

    private fun primary(): Expression {
        if (advanceIf(NIL, FALSE, TRUE, NUMBER, STRING))
            return Expression.Literal(previous().literal)

        if (advanceIf(IDENTIFIER)) return Expression.Variable(previous())

        if (advanceIf(LEFT_PAREN)) {
            val expr = expression()
            require("Expected a closing ')'", RIGHT_PAREN)
            return Expression.Grouping(expr)
        }

        `check if there's a binary operator missing left-hand side`()
        throw error("Expected an expression")
    }


    private fun `check if there's a binary operator missing left-hand side`() {
        if (advanceIf(EQUAL_EQUAL, BANG_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL, PLUS, SLASH, STAR, OR, AND)) {
            val operator = previous()
            throw error("Missing left-hand side", operator)
        }
    }

    private fun advanceIf(vararg types: TokenType) = !isEof() and (peek().type in types).whenTrueAlso(::advance)
    private fun advance() { if (!isEof()) current++ }
    private fun require(errMsg: String, vararg types: TokenType) = advanceIf(*types).whenFalseAlso { throw error(errMsg) }
    private fun isEof() = peek().type == EOF
    private fun peek() = tokens[current]
    private fun previous() = tokens[current - 1]
    private fun error(message: String, token: Token = peek()): RuntimeException {
        Yak.reportError(token, message)
        return ParseError()
    }

    private fun Boolean.whenTrueAlso(block: () -> Unit) = also { if (it) block() }
    private fun Boolean.whenFalseAlso(block: () -> Unit) = also { if (!it) block() }

    fun synchronize() {
        advance()
        while (!isEof()) {
            if (previous().type == SEMICOLON) return
            if (peek().type in synchronizables) return
            advance()
        }
    }

    companion object {
        val synchronizables = listOf(CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN)
    }
}

class ParseError : RuntimeException()