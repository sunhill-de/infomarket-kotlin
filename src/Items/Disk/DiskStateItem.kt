package sunhill.Items.Disk

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.DiskDatapool
import sunhill.Items.ItemBase
import sunhill.Items.PoolItemBase

class DiskStateItem : PoolItemBase("system.disks.#.state"," ", "state", "String", "late") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as DiskDatapool).disks!![additional[0].toInt()].state
    }

    override fun getAllOfferings() : MutableList<String> {
        val result = mutableListOf<String>()
        for (i in 0..(data_pool!! as DiskDatapool).disks!!.count()) {
            result.add("system.disks."+i.toString()+".size")
        }
        return result
    }


}