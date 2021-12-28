package sunhill.Items.Disk

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.DiskDatapool
import sunhill.Items.PoolItemBase

class RaidTotalItem : PoolItemBase("system.raid.#.total" ," ", "name", "String", "late") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as DiskDatapool).raids!![additional[0].toInt()].total
    }

    override fun getAllOfferings() : MutableList<String> {
        val result = mutableListOf<String>()
        if ((data_pool as DiskDatapool).raids!!.count() > 0) {
            for (i in 0..(data_pool!! as DiskDatapool).raids!!.count()) {
                result.add("system.raid."+i.toString()+".recovered")
            }
        }
        return result
    }


}