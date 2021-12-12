package sunhill.Items.Disk

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.DiskDatapool
import sunhill.Items.ItemBase

class PartitionFilesystemItem : ItemBase(" ", "name", "String", "late") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as DiskDatapool).partitions!![additional[0].toInt()].filesystem
    }

}