package sunhill.Items.Disk

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.DiskDatapool
import sunhill.Items.PoolItemBase

class RaidCountItem : PoolItemBase("system.raid.count"," ", "count", "Integer", "late") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as DiskDatapool).raids!!.count()
    }

}