package sunhill.Marketeers

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.UptimeDatapool
import sunhill.Items.Uptime.*
import sunhill.Items.ItemBase
import sunhill.marketeers.MarketeerBase

class UptimeMarketeer : MarketeerBase() {

    override fun getRegisteredItemList(): Map<String, out ItemBase>
    {
        return mapOf(
            "system.uptime.duration" to UptimeDurationItem(),
            "system.uptime.seconds" to UptimeSecondsItem(),
            "system.idletime.duration" to IdletimeDurationItem(),
            "system.idletime.seconds" to IdletimeSecondsItem()
        )
    }

    override fun getDatapool(): DataPoolBase
    {
        return UptimeDatapool()
    }
}