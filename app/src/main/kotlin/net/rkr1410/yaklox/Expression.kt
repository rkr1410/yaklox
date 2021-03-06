// autogenerated
package net.rkr1410.yaklox
                
abstract class Expression {

    data class Assign(
        val name: Token,
        val value: Expression
    ): Expression() 

    data class Ternary(
        val condition: Expression,
        val ifBranch: Expression,
        val elseBranch: Expression
    ): Expression() 

    data class Binary(
        val left: Expression,
        val operator: Token,
        val right: Expression
    ): Expression() 

    data class Grouping(
        val expr: Expression
    ): Expression() 

    data class Literal(
        val value: Any?
    ): Expression() 

    data class Unary(
        val operator: Token,
        val right: Expression
    ): Expression() 

    data class Variable(
        val name: Token
    ): Expression() 

    data class Logical(
        val left: Expression,
        val operator: Token,
        val right: Expression
    ): Expression()
}
