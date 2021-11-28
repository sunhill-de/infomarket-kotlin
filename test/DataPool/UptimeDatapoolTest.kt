package sunhill.DataPool

import org.junit.Test
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.spyk
import io.mockk.verify
import junit.framework.TestCase.assertEquals

class UptimeDatapoolTest {


    @Test
    fun testUptime() {
        val test: UptimeDatapool = spyk<UptimeDatapool>()

        every { test.readUptimeFile() } returns "5340.99 15710.18"

        assertEquals(5340.99, test.uptime)
        assertEquals(15710.18, test.idletime)
    }
}