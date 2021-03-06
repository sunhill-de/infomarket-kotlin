package sunhill.Items.Disk

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.DiskDatapool
import sunhill.Items.PoolItemBase

class RaidOnlineItem : RaidItemBase("system.raid.#.online"," ", "name", "String", "late") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as DiskDatapool).raids!![additional[0].toInt()].online
    }

}