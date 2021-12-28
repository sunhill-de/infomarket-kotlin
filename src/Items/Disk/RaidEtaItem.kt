package sunhill.Items.Disk

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.DiskDatapool
import sunhill.Items.PoolItemBase

class RaidEtaItem : PoolItemBase("system.raid.#.eta"," ", "name", "String", "late") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as DiskDatapool).raids!![additional[0].toInt()].eta
    }

    override fun getAllOfferings() : MutableList<String> {
        val result = mutableListOf<String>()
        if ((data_pool as DiskDatapool).raids!!.count() > 0) {
            for (i in 0..(data_pool!! as DiskDatapool).raids!!.count()) {
                result.add("system.raid."+i.toString()+".eta")
            }
        }
        return result
    }


}