package com.sugarmanz.npm

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class PackageTest {

    @Test
    fun `generic test`() {
        println(Name("@player/core").scope)
        println(Name("@player").scope)
        println(Package(Name("@player/core")).name?.scope)
    }

    @Test fun `name should contain at least one character`() {
        assertEquals("Name must not be empty", assertThrows<IllegalArgumentException> {
            Name("")
        }.message)
    }

    @Test fun `name shouldn't have allow more than 214 characters`() {
        assertEquals("Name must not be empty", assertThrows<IllegalArgumentException> {
            Name("")
        }.message)
    }

    @Test fun `valid name with scope doesn't trigger validation`() {
        val raw = "@scope/package"
        val (scope, `package`) = raw.split("/")
        val name = assertDoesNotThrow {
            Name(raw)
        }
        assertEquals(`package`, name.`package`)
        assertEquals(scope, name.scope)
    }

    @Test fun `valid name without scope doesn't trigger validation`() {
        val raw = "package"
        val name = assertDoesNotThrow {
            Name(raw)
        }
        assertEquals(raw, name.`package`)
        assertEquals(raw, name)
    }

}