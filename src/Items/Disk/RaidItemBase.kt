package sunhill.Items.Disk

import sunhill.DataPool.DiskDatapool
import sunhill.Items.PoolItemBase

open class RaidItemBase(path: String, unit_int: String, semantic_int: String, type: String, update: String) :
        PoolItemBase(path, unit_int, semantic_int, type, update)
{

    override fun getPermutation(parts: MutableList<String>, index: Int): List<String>? {
        val result = mutableListOf<String>()
        if ((data_pool!! as DiskDatapool).raids!!.count() > 0) {
            if (index == 0) {
                for (i in 0..(data_pool!! as DiskDatapool).raids!!.count()) {
                    result.add(i.toString())
                }
            } else {
                for (i in 0..(data_pool!! as DiskDatapool).raids!![parts[2].toInt()].devices.count()) {
                    result.add(i.toString())
                }
            }
        }
        return result.toList()
    }

}