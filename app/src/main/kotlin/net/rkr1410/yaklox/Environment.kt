package net.rkr1410.yaklox

class Environment(private val parent: Environment? = null) {
    private val stuff = mutableMapOf<String, Any?>()

    fun get(name: Token): Any? {
        if (stuff.containsKey(name.lexeme)) return stuff[name.lexeme]
        return parent?.get(name) ?: throw RuntimeError(name, "Variable ${name.lexeme} is undefined")
    }

    fun declare(name: Token, value: Any? = null) {
        if (stuff.containsKey(name.lexeme)) throw RuntimeError(name, "Variable redeclaration not supported")
        stuff[name.lexeme] = value
    }

    fun assign(name: Token, value: Any?): Any? {
        if (stuff.containsKey(name.lexeme)) {
            stuff[name.lexeme] = value
            return value
        }
        parent?.assign(name, value) ?: throw RuntimeError(name, "Variable ${name.lexeme} is undefined")
        return value
    }
}