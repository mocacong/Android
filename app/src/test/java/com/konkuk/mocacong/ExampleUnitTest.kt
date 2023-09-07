package com.konkuk.mocacong

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

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
    fun `비밀번호가 8자 이상 20자 이하가 아닌 경우 예외를 반환한다`(){
        assertEquals(true, passwordRegex(arr[0]))
        assertEquals(true, passwordRegex(arr[1]))
        assertEquals(true, passwordRegex(arr[2]))
        assertEquals(false, passwordRegex(arr[3]))
        assertEquals(false, passwordRegex(arr[4]))

    }


    //테스트 코드 함수 기능 열심히 쪼개자!
    @Test
    fun `비밀번호에 소문자, 숫자 둘 다 포함돼있지 않은 경우 예외를 반환한다`() {

    }

}