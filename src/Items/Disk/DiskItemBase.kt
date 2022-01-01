package sunhill.Items.Disk

import sunhill.DataPool.DiskDatapool
import sunhill.Items.PoolItemBase

open class DiskItemBase(path: String, unit_int: String, semantic_int: String, type: String, update: String) :
    PoolItemBase(path, unit_int, semantic_int, type, update)
{

    override fun getPermutation(parts: MutableList<String>, index: Int): List<String>? {
        val result = mutableListOf<String>()
        for (i in 0 .. (data_pool!! as DiskDatapool).disks!!.count()) {
            result.add(i.toString())
        }
        return result.toList()
    }

}