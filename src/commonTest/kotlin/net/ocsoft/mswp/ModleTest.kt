
package net.ocsoft.mswp

import kotlin.test.Test
import kotlin.test.assertNotNull

class ModelTest {
    @Test fun testModelHasMethodTostr() {
        val classUnderTest = Model()
        assertNotNull(classUnderTest.toString(), "app should have a greeting")
    }
}
