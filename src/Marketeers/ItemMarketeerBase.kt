package sunhill.Marketeers

import sunhill.DataPool.DataPoolBase
import sunhill.Items.ItemBase
import sunhill.Items.PoolItemBase
import sunhill.marketeers.MarketeerBase

abstract class ItemMarketeerBase: MarketeerBase() {

    /**
     * Returns a map string->item where the provided items are given back
     */
    abstract protected fun getRegisteredItemList(): List<ItemBase>

    override fun searchItem(search: String, userLevel: Int): SearchResult?
    {
        val items = getRegisteredItemList()
        for (item in items) {
            val additional = mutableListOf<String>()
            if (item.matches(search,userLevel,additional)) {
                this.prepareItem(item,additional)
                return SearchResult(item,additional)
            }
        }
        return null
    }

    open fun prepareItem(item: ItemBase,additional: MutableList<String> = mutableListOf<String>())
    {

    }

    override fun getOffering(search: String): MutableList<String>
    {
        val items = getRegisteredItemList()
        val result = mutableListOf<String>()
        for (item in items) {
            prepareItem(item)
            item.addOfferings(search,result)
        }
        return result
    }

}