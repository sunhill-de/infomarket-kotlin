package sunhill.marketeers

import sunhill.DataPool.DataPoolBase
import sunhill.Items.ItemBase

abstract class MarketeerBase {

  var data_pool: DataPoolBase? = null

  /**
   * Returns a map string->item where the provided items are given back
   */
  abstract protected fun getRegisteredItemList(): Map<String, out ItemBase>

  abstract protected fun getDatapool(): DataPoolBase

  fun searchItem(search: String,
                 userlevel: Int = 0,
                 callback: (String, DataPoolBase, Int, MutableList<String>, ItemBase)->String): String?
  {
    val offerings = getRegisteredItemList()
    var params = mutableListOf<String>()

    for ((offering,item) in offerings) {
      params.clear()
      if (offerMatches(search,offering,params)) {
        if (data_pool == null) {
          data_pool = getDatapool()
        }
        return item.get(search,data_pool,userlevel,params)
      }
    }
    return null
  }

  /**
   * Returns the answer of an item or null if this marketeer offers no matching item
   */
  fun getOffer(search: String,userlevel: Int = 0): String?
  {
    val callback: (String, DataPoolBase, Int, MutableList<String>, ItemBase) -> String =
      { search: String, data_pool: DataPoolBase, userlevel: Int, params:MutableList<String>, item: ItemBase ->
        item.get( search, data_pool, userlevel, params )}
    return this.searchItem( search, userlevel, callback )
  }

  /**
   * Returns the value of an item or null if this marketeer offers no matching item
   */
  fun getValue(search: String,userlevel: Int = 0): String?
  {
    val callback: (String, DataPoolBase, Int, MutableList<String>, ItemBase) -> String =
      { search: String, data_pool: DataPoolBase, userlevel: Int, params:MutableList<String>, item: ItemBase ->
        item.getValue( search, data_pool, userlevel, params )}
    return this.searchItem( search, userlevel, callback )
  }

  /**
   * Returns true if the given search string matches the offered string. If there are placeholders in the offer
   * return the matching parts of the search string in the array result
   */
  protected fun offerMatches(search: String, offer: String,result: MutableList<String>): Boolean
  {
    val test_parts = search.split('.')
    val offer_parts = offer.split('.')
    var i = 0

    while (true) {
      if ((i == test_parts.count()) && (i == offer_parts.count())) {
        // At this points both strings are equal and we can quit
        return true
      }
      if ((i == test_parts.count()) || (i == offer_parts.count())) {
        // Both string are not the same length, so they doesn't match
        return false
      }
      when (offer_parts[i]) {
        "#" -> {
          if (test_parts[i].toIntOrNull() == null) { // If not an int, then return false (this rule doesn't match)
            return false
          }
          result.add(test_parts[i])
        }
        "?" -> result.add(test_parts[i])
        "*" -> {
          result.add(test_parts.drop(i).joinToString(".")) // Drop the parts until * and return the rest joined by "."
          return true
        }
        else -> if (test_parts[i] != offer_parts[i])
                    return false
      }
      i++
    }
  }
}
