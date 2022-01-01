package sunhill.Items.Disk

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.DiskDatapool
import sunhill.Items.ItemBase
import sunhill.Items.Disk.DiskItemBase

class DiskModellItem : DiskItemBase("system.disks.#.model",
                            " ",
                        "name",
                        "String",
                        "late") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as DiskDatapool).disks!![additional[0].toInt()].model
    }


}