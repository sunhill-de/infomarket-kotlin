package sunhill.Items.Disk

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.DiskDatapool
import sunhill.Items.ItemBase
import sunhill.Items.PoolItemBase

class PartitionCountItem : PoolItemBase("system.partitions.count"," ", "count", "Integer", "late") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as DiskDatapool).partitions!!.count()
    }

}