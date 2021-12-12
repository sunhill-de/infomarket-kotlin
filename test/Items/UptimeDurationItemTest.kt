package sunhill.Items

import org.junit.Test
import io.mockk.every
import io.mockk.spyk
import sunhill.DataPool.UptimeDatapool
import sunhill.Items.Uptime.*
import net.javacrumbs.jsonunit.assertj.assertThatJson

class UptimeDurationItemTest {

    @Test
    fun testUptime() {
        val pool: UptimeDatapool = spyk<UptimeDatapool>()

        every { pool.readUptimeFile() } returns "5340.99 15710.18"

        val test: UptimeDurationItem = UptimeDurationItem()

        val result: String = test.get("test.request", pool)
        assertThatJson(result).isObject().containsEntry("request","test.request")
        assertThatJson(result).isObject().containsEntry("type","Float")
        assertThatJson(result).isObject().containsEntry("result","OK")
        assertThatJson(result).isObject().containsEntry("update","asap")
        assertThatJson(result).isObject().containsEntry("human_readable_value","1 hour 29 minutes")
    }

}