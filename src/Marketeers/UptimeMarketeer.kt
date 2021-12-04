package sunhill.Marketeers

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.UptimeDatapool
import sunhill.Items.ItemBase
import sunhill.Items.UptimeDurationItem
import sunhill.Items.UptimeSecondsItem
import sunhill.marketeers.MarketeerBase

class UptimeMarketeer : MarketeerBase() {

    override fun getRegisteredItemList(): Map<String, out ItemBase>
    {
        return mapOf(
            "system.uptime.duration" to UptimeDurationItem(),
            "system.uptime.seconds" to UptimeSecondsItem()
        )
    }

    override fun getDatapool(): DataPoolBase
    {
        return UptimeDatapool()
    }
}