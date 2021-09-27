package net.rkr1410.yaklox

import net.rkr1410.yaklox.TokenType.*

/*
    expression  → equality ;
    ternary     → equality ? equality : equality ;
    equality    → comparison ( ( "!=" | "==" ) comparison )* ;
    comparison  → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    term        → factor ( ( "-" | "+" ) factor )* ;
    factor      → unary ( ( "/" | "*" ) unary )* ;
    unary       → ( ("!" | "-") unary ) | primary ;
    primary     → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
 */

class Parser(private val tokens: List<Token>) {
    private var current = 0

    fun parse(): Expression {
        return expression()
    }

    private fun expression(): Expression = ternary()

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

    private fun equality()   = binary(::comparison, EQUAL_EQUAL, BANG_EQUAL)
    private fun comparison() = binary(::term,       GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)
    private fun term()       = binary(::factor,     PLUS, MINUS)
    private fun factor()     = binary(::unary,      SLASH, STAR)

    private fun binary(higherPrecedence: () -> Expression, vararg operators: TokenType): Expression {
        var expr = higherPrecedence()

        while (advanceIf(*operators)) {
            val operator = previous()
            val right = higherPrecedence()
            expr = Expression.Binary(expr, operator, right)
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

        if (advanceIf(LEFT_PAREN)) {
            val expr = expression()
            require("Expected a closing ')'", RIGHT_PAREN)
            return Expression.Grouping(expr)
        }

        throw error("Expected an expression")
    }

    private fun advanceIf(vararg types: TokenType) = !isEof() and (peek().type in types).whenTrueAlso(::advance)
    private fun advance() { if (!isEof()) current++ }
    private fun require(errMsg: String, vararg types: TokenType) = advanceIf(*types).whenFalseAlso { throw error(errMsg) }
    private fun isEof() = peek().type == EOF
    private fun peek() = tokens[current]
    private fun previous() = tokens[current - 1]
    private fun error(message: String): ParseError {
        Yak.reportError(peek(), message)
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
