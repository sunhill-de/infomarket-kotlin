package sunhill.Items.Disk

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.DiskDatapool
import sunhill.Items.ItemBase

class DiskStateItem : ItemBase(" ", "state", "String", "late") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as DiskDatapool).disks!![additional[0].toInt()].state
    }

}