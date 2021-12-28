package sunhill.Marketeers

import sunhill.DataPool.DataPoolBase
import sunhill.Items.ItemBase
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
                return SearchResult(item,additional)
            }
        }
        return null
    }

    override fun getOffering(search: String): MutableList<String>
    {
        val items = getRegisteredItemList()
        val result = mutableListOf<String>()
        for (item in items) {
            item.addOfferings(search,result)
        }
        return result
    }

}