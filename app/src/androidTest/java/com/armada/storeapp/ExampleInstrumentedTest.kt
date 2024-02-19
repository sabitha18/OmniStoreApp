package com.armada.storeapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented layout_customer_saved_cards.xml, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under layout_customer_saved_cards.xml.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.armada.storeapp", appContext.packageName)
    }
}