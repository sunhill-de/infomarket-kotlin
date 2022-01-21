package sunhill.marketeers

import sunhill.Items.AbstractItemBase
import sunhill.Items.ItemData

abstract class MarketeerBase {

  data class SearchResult(val item: AbstractItemBase, val additional: MutableList<String>)

  /**
   * Searches this marketeer if it provides the item "search" with the userLevel
   * If it finds the item then it returns its answer otherwise it returns null
   */
  abstract fun searchItem(search: String,
                          userLevel: Int): SearchResult?

  /**
   * Returns the answer of an item or null if this marketeer offers no matching item
   */
  fun getItem(search: String,userlevel: Int = 0): ItemData
  {
    val result = searchItem(search,userlevel)
    if (result == null) {
      return null
    } else {
      return result.item.getItem(search,userlevel = userlevel,additional = result.additional)
    }
  }

  /**
   * Returns the value of an item or null if this marketeer offers no matching item
   */
  fun getValue(search: String,userlevel: Int = 0): Any?
  {
    val result = searchItem(search,userlevel)
    if (result == null) {
      return null
    } else {
      return result.item.getValue(search,userlevel = userlevel,additional = result.additional)
    }
  }

  /**
   * Returns the value of an item or null if this marketeer offers no matching item
   */
  fun getHRValue(search: String,userlevel: Int = 0): String?
  {
    val result = searchItem(search,userlevel)
    if (result == null) {
      return null
    } else {
      return result.item.getHRValue(search,userlevel = userlevel,additional = result.additional)
    }
  }

  /**
   * Returns all items this marketeer provides
   */
  abstract fun getOffering(search: String): MutableList<String>

}
