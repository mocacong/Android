package com.example.mocacong

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.util.Scanner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    var arr = ArrayList<String>()

    @Before
    fun init(){
        arr.add("tlswltn9")
        arr.add("qqqqqqqqqq1111111111")
        arr.add("tlswltfajfklj10")
        arr.add("tlswltn;1")
        arr.add("tlswltnADF")
    }
    fun passwordRegex(password: String): Boolean {
        return password.matches(Regex("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,20}$"))
    }

    @Test
    fun test(){
        assertEquals(true, passwordRegex(arr[0]))
        assertEquals(true, passwordRegex(arr[1]))
        assertEquals(true, passwordRegex(arr[2]))
        assertEquals(false, passwordRegex(arr[3]))
        assertEquals(false, passwordRegex(arr[4]))

    }

}