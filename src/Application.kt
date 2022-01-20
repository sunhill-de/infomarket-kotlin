package sunhill

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
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
            call.respondText(MyMarketplace.getItemAsJSON(call.parameters["path"]!!), contentType = io.ktor.http.ContentType.Application.Json)
        }

        get ("/value/{path}") {
            call.respondText(MyMarketplace.getValueAsJSON(call.parameters["path"]!!), contentType = io.ktor.http.ContentType.Application.Json)
        }

        get ("/hrvalue/{path}") {
            call.respondText(MyMarketplace.getHumanReadableValueAsJSON(call.parameters["path"]!!), contentType = io.ktor.http.ContentType.Application.Json)
        }

        get ("/offering/{path}") {
            call.respondText(MyMarketplace.getOfferingAsJSON(call.parameters["path"]!!), contentType = io.ktor.http.ContentType.Application.Json)
        }

        get ("/allitems/{path}") {
            call.respondText(MyMarketplace.getAllItemsAsJSON(call.parameters["path"]!!), contentType = io.ktor.http.ContentType.Application.Json)
        }

        get ("/allvalues/{path}") {
            call.respondText(MyMarketplace.getAllValues(call.parameters["path"]!!), contentType = io.ktor.http.ContentType.Application.Json)
        }

        get ("/allhrvalues/{path}") {
            call.respondText(MyMarketplace.getAllHRValues(call.parameters["path"]!!), contentType = io.ktor.http.ContentType.Application.Json)
        }

        get ("{...}") {
            call.response.status(HttpStatusCode.NotFound)
            call.respondText("{\"result\":\"FAILED\", \"error_code\":\"UNKNOWNREQUEST\",\"error_message\":\"Unknown request\"}", contentType = ContentType.Application.Json)
        }

    }
}

