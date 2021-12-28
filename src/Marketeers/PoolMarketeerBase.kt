package sunhill.Marketeers

import sunhill.DataPool.DataPoolBase
import sunhill.Items.ItemBase
import sunhill.marketeers.MarketeerBase

abstract class PoolMarketeerBase: ItemMarketeerBase() {

    var data_pool: DataPoolBase? = null

    abstract protected fun getDatapool(): DataPoolBase

    /**
     * Updates the data in the pool
     */
    protected fun updatePool()
    {
        if (data_pool == null) {
            data_pool = getDatapool()
        }
        data_pool!!.update()
    }
}