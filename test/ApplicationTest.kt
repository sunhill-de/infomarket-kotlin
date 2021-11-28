package sunhill

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlin.test.*
import io.ktor.server.testing.*

/**
 * Tests some of the standard responses
 */
class ApplicationTest {
    @Test
    fun testStatus() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/status").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("{\"result\":\"OK\", \"status\":\"working\"}", response.content)
            }
        }
    }

    @Test
    fun test404() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/notexisting").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertEquals("{\"result\":\"FAILED\", \"error_code\":\"UNKNOWNREQUEST\",\"error_message\":\"Unknown request\"}", response.content)
            }
        }
    }
}
