package sunhill.Marketeers

import sunhill.DataPool.DataPoolBase
import sunhill.Items.ItemBase
import sunhill.Items.PoolItemBase
import sunhill.marketeers.MarketeerBase

abstract class PoolMarketeerBase: ItemMarketeerBase() {

    var data_pool: DataPoolBase? = null

    abstract protected fun getDatapool(): DataPoolBase

    /**
     * Updates the data in the pool
     */
    protected fun updatePool()
    {
        retrievePool()!!.update()
    }

    protected fun retrievePool(): DataPoolBase?
    {
        if (data_pool == null) {
            data_pool = getDatapool()
        }
        return data_pool
    }

    override fun prepareItem(item: ItemBase,additional: MutableList<String>)
    {
        if (item is PoolItemBase){
            item.setDataPool(retrievePool()!!)
        }
    }


}