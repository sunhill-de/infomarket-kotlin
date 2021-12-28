package sunhill.Marketeers

import sunhill.marketeers.MarketeerBase

class ProxyMarketeer(val prefix: String, val target: MarketeerBase) : MarketeerBase() {

    /**
     * If the search starts with the given prefix it tries to pass this request to the proxy
     */
    override fun searchItem(search: String, userlevel: Int): SearchResult?
    {
        if (search.startsWith(prefix)) {
            return target.searchItem(search.substring(prefix.length+1), userlevel)
        } else {
            return null
        }
    }

    /**
     * Just pass the Offering of the target with the prepended prefix
     */
    override fun getOffering(search: String): MutableList<String>
    {
        val result = target.getOffering(search)
        result.forEach({it->prefix+"."+it}) // Prepend our prefix
        return result
    }

}