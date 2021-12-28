package sunhill

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import sunhill.Marketeers.DiskMarketeer
import sunhill.Marketeers.UptimeMarketeer
import sunhill.Marketplace.Marketplace
import sunhill.marketeers.MarketeerBase

object MyMarketplace : Marketplace() {

    override fun getMarketeerList(): Array<MarketeerBase>
    {
        return arrayOf(
            UptimeMarketeer(),
            DiskMarketeer()
        )
    }
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    routing {
        get( "/status" ) {
            call.respondText("{\"result\":\"OK\", \"status\":\"working\"}", contentType = ContentType.Application.Json)
        }

        get ("/item/{path}") {
            call.respondText(MyMarketplace.get(call.parameters["path"]!!), contentType = io.ktor.http.ContentType.Application.Json)
        }

        get ("/value/{path}") {
            call.respondText(MyMarketplace.getValue(call.parameters["path"]!!), contentType = io.ktor.http.ContentType.Application.Json)
        }

        get ("/hrvalue/{path}") {
            call.respondText(MyMarketplace.getHRValue(call.parameters["path"]!!), contentType = io.ktor.http.ContentType.Application.Json)
        }

        get ("/offering/{path}") {
            call.respondText(MyMarketplace.getOffering(call.parameters["path"]!!), contentType = io.ktor.http.ContentType.Application.Json)
        }
        get ("{...}") {
            call.response.status(HttpStatusCode.NotFound)
            call.respondText("{\"result\":\"FAILED\", \"error_code\":\"UNKNOWNREQUEST\",\"error_message\":\"Unknown request\"}", contentType = ContentType.Application.Json)
        }

    }
}

