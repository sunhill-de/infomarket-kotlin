package sunhill.Items

import org.junit.Test
import io.mockk.every
import io.mockk.spyk
import junit.framework.TestCase.assertEquals
import sunhill.DataPool.UptimeDatapool
import sunhill.Items.Uptime.*
import net.javacrumbs.jsonunit.assertj.assertThatJson

class UptimeDurationItemTest {

    @Test
    fun testUptime() {
        val pool: UptimeDatapool = spyk<UptimeDatapool>()

        every { pool.readUptimeFile() } returns "5340.99 15710.18"

        val test: UptimeDurationItem = UptimeDurationItem()
        test.setDataPool(pool)
        val result = test.getItem("test.request")!!
        assertEquals(result.request,"test.request")
        assertEquals(result.type,"Float")
        assertEquals(result.update,"asap")
        assertEquals(result.human_readable_value,"1 hour 29 minutes")
    }

}