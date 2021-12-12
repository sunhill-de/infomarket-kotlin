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
            "system.disks.#.model" to DiskModellItem(),
            "system.disks.#.size" to DiskSizeItem(),
            "system.disks.#.state" to DiskStateItem(),

            "system.partitions.count" to PartitionCountItem(),
            "system.partitions.#.name" to PartitionNameItem(),
            "system.partitions.#.size" to PartitionSizeItem(),
            "system.partitions.#.used" to PartitionUsedItem(),
            "system.partitions.#.avail" to PartitionAvailItem(),
            "system.partitions.#.filesystem" to PartitionFilesystemItem(),
            "system.partitions.#.mountpoint" to PartitionMountpointItem(),
            "system.partitions.#.readonly" to PartitionReadonlyItem(),
            "system.partitions.#.removable" to PartitionRemovableItem(),

            "system.raid.count" to RaidCountItem(),
            "system.raid.#.name" to RaidNameItem(),
            "system.raid.#.level" to RaidLevelItem(),
            "system.raid.#.online" to RaidOnlineItem(),
            "system.raid.#.devices.count" to RaidDevicesCountItem(),
            "system.raid.#.devices.#.name" to RaidDevicesNameItem(),
            "system.raid.#.devices.#.online" to RaidDevicesOnlineItem(),
            "system.raid.#.recovered" to RaidRecoveredItem(),
            "system.raid.#.total" to RaidTotalItem(),
            "system.raid.#.eta" to RaidEtaItem(),
            "system.raid.#.speed" to RaidSpeedItem()
        )
    }

    override fun getDatapool(): DataPoolBase
    {
        return DiskDatapool()
    }

}