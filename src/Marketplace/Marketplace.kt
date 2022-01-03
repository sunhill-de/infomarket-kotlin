package sunhill.Marketplace

import sunhill.marketeers.MarketeerBase

abstract class Marketplace {

    abstract fun getMarketeerList(): Array<MarketeerBase>

    /**
     * Returns a error as a json string
     * @param code String The error code
     * @param message String A more descriptive error message
     * @return String The json error message
     */
    protected fun error(code: String, message: String): String
    {
        return """{"result":"failed","error_code":""""+code+"""","error_message":""""+message+""""}"""
    }

    /**
     * Returns the informations for an item
     * @param path String The path for the requested item
     * @return string: The json representation of the item or an error message
     */
    fun getItem(path : String): String
    {
        val marketeers = getMarketeerList()

        for (marketeer in marketeers) {
            var response = marketeer.getItem(path)
            if (response != null) return response
        }
        return error("ITEMNOTFOUND","The requested item'"+path+"' was not found.")
    }

    /**
     * Returns the value for an item
     * @param path String The path for the requested item
     * @return string: The json representation of the value of the item or an error message
     */
    fun getValue(path : String): String
    {
        val marketeers = getMarketeerList()

        for (marketeer in marketeers) {
            var response = marketeer.getValue(path)
            if (response != null) return response
        }
        return error("ITEMNOTFOUND","The requested item'"+path+"' was not found.")
    }

    /**
     * Returns the human readable value for an item
     * @param path String The path for the requested item
     * @return string: The json representation of the human readable value of the item or an error message
     */
    fun getHRValue(path : String): String
    {
        val marketeers = getMarketeerList()

        for (marketeer in marketeers) {
            var response = marketeer.getHRValue(path)
            if (response != null) return response
        }
        return error("ITEMNOTFOUND","The requested item'"+path+"' was not found.")
    }

    /**
     * Returns all items that this marketplace is able to offer that meet the given condition
     * @param path String A possible filter for the offering (default "*" means no filter)
     * @return String The json answer (array) of all offerings
     */
    fun getOffering(path: String = "*"): String
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

    /**
     * Returns all items that meet the given condition
     * @param path String A possible filter for the offering (default "*" means no filter)
     * @return String The json answer (array) of all items
     */
    fun getAllItems(path: String): String
    {
        return """{"items":["""+
               """]}"""
    }

    /**
     * Returns all item values that meet the given condition
     * @param path String A possible filter for the offering (default "*" means no filter)
     * @return String The json answer (array) of all item values
     */
    fun getAllValues(path: String): String
    {
        return """{"items":["""+
                """]}"""
    }

    /**
     * Returns all item human readable values that meet the given condition
     * @param path String A possible filter for the offering (default "*" means no filter)
     * @return String The json answer (array) of all items human readable values
     */
    fun getAllHRValues(path: String): String
    {
        return """{"items":["""+
                """]}"""
    }

    fun getItemsByList(list: String): String
    {
        return """{"items":["""+
                """]}"""
    }

    fun getItemsByList(list: List<String>): String
    {
        return """{"items":["""+
                """]}"""
    }

    fun getValuesByList(list: String): String
    {
        return """{"items":["""+
                """]}"""
    }

    fun getValuesByList(list: List<String>): String
    {
        return """{"items":["""+
                """]}"""
    }

    fun getHRValuesByList(list: String): String
    {
        return """{"items":["""+
                """]}"""
    }

    fun getHRValuesByList(list: List<String>): String
    {
        return """{"items":["""+
                """]}"""
    }
}