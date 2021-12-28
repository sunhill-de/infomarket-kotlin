package sunhill.Items.Disk

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.DiskDatapool
import sunhill.Items.PoolItemBase

class PartitionSizeItem : PoolItemBase("system.partitions.#.size"," ", "size", "Integer", "late") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as DiskDatapool).partitions!![additional[0].toInt()].size
    }

    override fun getAllOfferings() : MutableList<String> {
        val result = mutableListOf<String>()
        for (i in 0..(data_pool!! as DiskDatapool).partitions!!.count()) {
            result.add("system.partitions."+i.toString()+".size")
        }
        return result
    }

}