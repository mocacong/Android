package com.konkuk.mocacong

import androidx.test.rule.ActivityTestRule
import com.konkuk.mocacong.presentation.main.MainActivity
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
//@RunWith(AndroidJUnit4::class)
//class ExampleInstrumentedTest {
//    @Test
//    fun useAppContext() {
//        // Context of the app under test.
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//        assertEquals("com.example.mocacong", appContext.packageName)
//    }
//}

class ExampleInstrumentedTest {
    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun test_ui_fragment_review() {

    }
}