package net.rkr1410.yaklox

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("LocalVariableName")
class EnvironmentTest {
    @Test
    fun `can access variable created in same block`() {
        /*
        int i;
         */
        val topLevel = Environment()
        val _var = createVar("i")

        topLevel.declare(_var)

        assertNull(topLevel.get(_var))
    }

    @Test
    fun `can access variable declared in parent block`() {
        /*
        int i = 5;
        {
            print i;
        }
         */
        val topLevel = Environment()
        val child = Environment(topLevel)
        val _var = createVar("i")

        topLevel.declare(_var, 5)

        assertEquals(5, child.get(_var))
    }

    @Test
    fun `can shadow parent variable in child block`() {
        /*
        {
            var test1 = 5;
            {
                var test1 = 444;
            }
        }
         */
        val topLevel = Environment()
        val child = Environment(topLevel)
        val _var = createVar("test1")

        topLevel.declare(_var, 5)
        assertEquals(5, child.get(_var))

        child.declare(_var, 444)
        assertEquals(444, child.get(_var))
    }

    @Test
    fun `cannot access undefined variable`() {
        /*
            print test; // undefined error
         */
        val topLevel = Environment()

        val err = assertThrows<RuntimeError> { topLevel.get(createVar("someVariable")) }
        val expectedError = "Variable someVariable is undefined"
        assertTrue((err.message ?: "").contains(expectedError), "Expected \"${err.message}\" to contain \"$expectedError\"")
    }

    @Test
    fun `cannot access child variable in parent block`() {
        /*
        print test; // undefined error
        {
            var test = 4;
        }
         */
        val topLevel = Environment()
        val child = Environment(topLevel)
        val _var = createVar("test")

        child.declare(_var, 5)

        val err = assertThrows<RuntimeError> { topLevel.get(_var) }
        val expectedError = "Variable test is undefined"
        assertTrue((err.message ?: "").contains(expectedError), "Expected \"${err.message}\" to contain \"$expectedError\"")
    }

    @Test
    fun `cannot redeclare variable in same block`() {
        /*
        {
            var test = 4;
            var test = 2; // redeclaration error
        }
         */
        val topLevel = Environment()
        val child = Environment(topLevel)
        val _var = createVar("test")

        child.declare(_var, 5)

        val err = assertThrows<RuntimeError> { child.declare(_var, 4) }
        val expectedError = "Variable redeclaration not supported"
        assertTrue((err.message ?: "").contains(expectedError), "Expected \"${err.message}\" to contain \"$expectedError\"")
    }

    private fun createVar(name: String) = Token(TokenType.IDENTIFIER, name, Token.NoLiteral, 1, 1)
}