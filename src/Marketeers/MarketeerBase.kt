package sunhill.marketeers

abstract class MarketeerBase {

  /**
   * Returns a map string->item where the provided items are given back
   */
  abstract protected fun getRegisteredItemList(): Map

  /**
   * Returns the answer of an item or null if this marketeer offers no matching item
   */
  fun getOffer(search: String): String?
  {
  }
  
}
