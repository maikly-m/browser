package com.example.mylibrary

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test_format_time() {
        formatTime(1000).let {
            println("it= $it")
        }
        formatTime(10000).let {
            println("it= $it")
        }
        formatTime(100000).let {
            println("it= $it")
        }
        formatTime(1000000).let {
            println("it= $it")
        }
        formatTime(10000000).let {
            println("it= $it")
        }
    }
}