package sunhill.Items.Disk

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.DiskDatapool
import sunhill.Items.PoolItemBase

class RaidDevicesNameItem : PoolItemBase("system.raid.#.devices.#.name"," ", "count", "Integer", "late") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as DiskDatapool).raids!![additional[0].toInt()].devices[additional[1].toInt()]
    }

    override fun getAllOfferings() : MutableList<String> {
        val result = mutableListOf<String>()
        if ((data_pool as DiskDatapool).raids!!.count() > 0) {
            for (i in 0..(data_pool!! as DiskDatapool).raids!!.count()) {
                for (j in 0..(data_pool!! as DiskDatapool).raids!![i].devices.count()) {
                    result.add("system.raid." + i.toString() + ".devices." + j.toString() + ".name")
                }
            }
        }
        return result
    }

}