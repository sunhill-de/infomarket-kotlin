package sunhill.Items.Disk

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.DiskDatapool
import sunhill.Items.ItemBase
import sunhill.Items.PoolItemBase

class DiskModellItem : PoolItemBase("system.disks.#.model",
                            " ",
                        "name",
                        "String",
                        "late") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as DiskDatapool).disks!![additional[0].toInt()].model
    }

    override fun getAllOfferings() : MutableList<String> {
        val result = mutableListOf<String>()
        for (i in 0..(data_pool!! as DiskDatapool).disks!!.count()) {
            result.add("system.disks."+i.toString()+".model")
        }
        return result
    }

}