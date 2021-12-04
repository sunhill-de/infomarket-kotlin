package sunhill.Marketplace

import sunhill.marketeers.MarketeerBase

abstract class Marketplace {

    abstract fun getMarketeerList(): Array<MarketeerBase>

    fun get(path : String): String
    {
        val marketeers = getMarketeerList()

        for (marketeer in marketeers) {
            var response = marketeer.getOffer(path)
            if (response != null) return response
        }
        return "{\"result\":\"failed\",\"error_code\":\"ITEMNOTFOUND\",\"error_message\":\"The item was not found.\"}"
    }

}