package sunhill.Marketeers

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.UptimeDatapool
import sunhill.Items.Uptime.*
import sunhill.Items.ItemBase
import sunhill.marketeers.MarketeerBase

class UptimeMarketeer : PoolMarketeerBase() {

    override fun getRegisteredItemList(): List<ItemBase>
    {
        return listOf<ItemBase>(
             UptimeDurationItem(),
             UptimeSecondsItem(),
             IdletimeDurationItem(),
             IdletimeSecondsItem()
        )
    }

    override fun getDatapool(): DataPoolBase
    {
        return UptimeDatapool()
    }
}