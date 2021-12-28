package sunhill.Items

import sunhill.DataPool.DataPoolBase

open class PoolItemBase(path: String,
                        unit_int: String,
                   semantic_int: String,
                   type: String,
                   update: String,
                   readable_to: Int = 0,
                   writeable_to: Int = -1 ): ItemBase(path,unit_int,semantic_int,type,update,readable_to,writeable_to) {

    var data_pool :DataPoolBase? = null

    fun setDataPool(pool: DataPoolBase)
    {
        data_pool = pool
    }

    override fun calculateValue(additional: MutableList<String>): Any? {
        return getValueFromPool(data_pool,additional)
    }

    open fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any?
    {
        return null
    }

    open fun setValueToPool(datapool: DataPoolBase?, value: Any, additional: MutableList<String>)
    {

    }



}