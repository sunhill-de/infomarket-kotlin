package sunhill.Marketeers

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.DiskDatapool
import sunhill.Items.Disk.*
import sunhill.marketeers.MarketeerBase
import sunhill.Items.ItemBase

class DiskMarketeer : MarketeerBase() {

    override fun getRegisteredItemList(): Map<String, out ItemBase>
    {
        return mapOf(
            "system.disks.count" to DiskCountItem(),
            "system.disks.#.name" to DiskNameItem(),
            "system.partitions.count" to PartitionCountItem(),
            "system.raid.count" to RaidCountItem()
        )
    }

    override fun getDatapool(): DataPoolBase
    {
        return DiskDatapool()
    }

}