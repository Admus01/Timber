package com.darkn0va.timber.api

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.time.Duration.Companion.seconds

class APITest {

    @Test
    fun testAPI() = runTest(timeout = 10.seconds) {
        val expected = "Hello World"
        val result = com.darkn0va.timber.api.testAPI(ktorHttpClient)
        assertEquals(expected, result)
    }
}