package sunhill.Items.Disk

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.DiskDatapool
import sunhill.Items.PoolItemBase

class DiskCountItem : PoolItemBase("system.disk.count"," ", "count", "Integer", "late") {

    override fun calculateValue(additional: MutableList<String>): Any?
    {
        return (data_pool as DiskDatapool).disks!!.count()
    }

}