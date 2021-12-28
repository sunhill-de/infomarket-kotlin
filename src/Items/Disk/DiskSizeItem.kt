package sunhill.Items.Disk

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.DiskDatapool
import sunhill.Items.ItemBase
import sunhill.Items.PoolItemBase

class DiskSizeItem : PoolItemBase("system.disks.#.size"," ", "size", "Integer", "late") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as DiskDatapool).disks!![additional[0].toInt()].size
    }

}