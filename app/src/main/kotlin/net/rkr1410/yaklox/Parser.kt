package net.rkr1410.yaklox

import net.rkr1410.yaklox.TokenType.*

/*
    expression  → equality ;
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

    private fun expression(): Expression = equality()

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

    private fun advanceIf(vararg types: TokenType) = !isEof() and (peek().type in types).whenTrueAlso { current++ }
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
}

class ParseError : RuntimeException()