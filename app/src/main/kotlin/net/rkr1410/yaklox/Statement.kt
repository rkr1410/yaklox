// autogenerated
package net.rkr1410.yaklox
                
abstract class Statement {

    data class Expr(
        val expr: Expression
    ): Statement() 

    data class Print(
        val expr: Expression
    ): Statement() 

    data class Var(
        val name: Token,
        val initializer: Expression?
    ): Statement() 

    data class Block(
        val statements: List<Statement>
    ): Statement() 

    data class If(
        val condition: Expression,
        val thenBranch: Statement,
        val elseBranch: Statement?
    ): Statement() 

    data class While(
        val condition: Expression,
        val statement: Statement
    ): Statement() 

    data class FlowChange(
        val flowToken: Token
    ): Statement()
}
