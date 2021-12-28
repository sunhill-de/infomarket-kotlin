package sunhill.Marketeers

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.DiskDatapool
import sunhill.Items.Disk.*
import sunhill.Items.ItemBase

class DiskMarketeer : PoolMarketeerBase() {

    override fun getRegisteredItemList(): List<ItemBase>
    {
        return listOf<ItemBase>(
            DiskCountItem(),
            DiskNameItem(),
            DiskModellItem(),
            DiskSizeItem(),
            DiskStateItem(),

            PartitionCountItem(),
            PartitionNameItem(),
            PartitionSizeItem(),
            PartitionUsedItem(),
            PartitionAvailItem(),
            PartitionFilesystemItem(),
            PartitionMountpointItem(),
            PartitionReadonlyItem(),
            PartitionRemovableItem(),

            RaidCountItem(),
            RaidNameItem(),
            RaidLevelItem(),
            RaidOnlineItem(),
            RaidDevicesCountItem(),
            RaidDevicesNameItem(),
            RaidDevicesOnlineItem(),
            RaidRecoveredItem(),
            RaidTotalItem(),
            RaidEtaItem(),
            RaidSpeedItem()
        )
    }

    override fun getDatapool(): DataPoolBase
    {
        return DiskDatapool()
    }

}