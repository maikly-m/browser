package com.example.mylibrary

import android.graphics.Rect
import androidx.core.graphics.contains
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

    @Test
    fun test_01(){
        val rect = Rect()
        loop@for (j in 0..10){
            println(":::: ${j}")
            rect.let {
                kotlin.run {
                    return
                }

            }
        }

        arrayListOf<Int>().apply {
            add(1)
            add(12)
            add(14)
        }.forEach {
            println(":::: ${it}")
            rect.let {


            }
        }
        println("----")
    }
}