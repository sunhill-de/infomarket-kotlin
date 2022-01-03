package sunhill.Marketplace

import sunhill.marketeers.MarketeerBase

abstract class Marketplace {

    abstract fun getMarketeerList(): Array<MarketeerBase>

    fun get(path : String): String
    {
        val marketeers = getMarketeerList()

        for (marketeer in marketeers) {
            var response = marketeer.getItem(path)
            if (response != null) return response
        }
        return "{\"result\":\"failed\",\"error_code\":\"ITEMNOTFOUND\",\"error_message\":\"The item was not found.\"}"
    }

    fun getValue(path : String): String
    {
        val marketeers = getMarketeerList()

        for (marketeer in marketeers) {
            var response = marketeer.getValue(path)
            if (response != null) return response
        }
        return "{\"result\":\"failed\",\"error_code\":\"ITEMNOTFOUND\",\"error_message\":\"The item was not found.\"}"
    }

    fun getHRValue(path : String): String
    {
        val marketeers = getMarketeerList()

        for (marketeer in marketeers) {
            var response = marketeer.getHRValue(path)
            if (response != null) return response
        }
        return "{\"result\":\"failed\",\"error_code\":\"ITEMNOTFOUND\",\"error_message\":\"The item was not found.\"}"
    }

    fun getOffering(path: String): String
    {
        val result = mutableListOf<String>();
        val marketeers = getMarketeerList()

        for (marketeer in marketeers) {
            result.addAll(marketeer.getOffering(path))
        }
        val unique = result.distinct()
        var returning = """{"offering":["""
        for ((index,line) in unique.withIndex()) {
            returning += (if (index == 0) "" else ",")+"\""+line+"\"\n"
        }
        return returning+"]}"
    }

}