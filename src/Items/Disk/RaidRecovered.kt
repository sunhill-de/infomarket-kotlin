package sunhill.Items.Disk

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.DiskDatapool
import sunhill.Items.ItemBase

class RaidRecoveredItem : ItemBase(" ", "size", "Integer", "asap") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as DiskDatapool).raids!![additional[0].toInt()].recovered
    }

}