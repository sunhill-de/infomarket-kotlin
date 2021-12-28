package sunhill.Items.Disk

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.DiskDatapool
import sunhill.Items.PoolItemBase

class PartitionMountpointItem : PoolItemBase("system.partitions.#.mountpoint"," ", "name", "String", "late") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as DiskDatapool).partitions!![additional[0].toInt()].mountpoint
    }

}