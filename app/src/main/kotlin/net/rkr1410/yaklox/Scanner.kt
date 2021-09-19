package net.rkr1410.yaklox

import net.rkr1410.yaklox.TokenType.*

class Scanner(val source: String) {
    companion object {
        val keywords = mapOf(
            "and" to AND,
            "class" to CLASS,
            "else" to ELSE,
            "false" to FALSE,
            "for" to FOR,
            "fun" to FUN,
            "if" to IF,
            "nil" to NIL,
            "or" to OR,
            "return" to RETURN,
            "super" to SUPER,
            "this" to THIS,
            "true" to TRUE,
            "var" to VAR,
            "while" to WHILE,
        )

        fun tokenTypeForLiteral(literal: String) = keywords.getOrDefault(literal, IDENTIFIER)
    }

    fun scanTokens(): List<TokenType> {
        return listOf(tokenTypeForLiteral("abc"), tokenTypeForLiteral("super"))
    }
}