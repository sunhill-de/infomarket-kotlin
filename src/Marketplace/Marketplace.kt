package sunhill.Marketplace

import sunhill.marketeers.MarketeerBase

abstract class Marketplace {

    abstract fun getMarketeerList(): Array<MarketeerBase>

    /**
     * Takes an ItemError and wraps it into a JSON String
     */
    protected fun wrapItemErrorToJSON(val input: ItemError): String
    {
        return """{"result":"FAILED","error_code":""""+input.code+"""","error_message":""""+input.message+""""}"""
    }
    
    protected fun wrapItemDataToJSON(val input: ItemData): String
    {
    }
    
    protected fun wrapItemMetaDataToJSON(val input: ItemMetaData): String
    {
    }
    
    protected fun wrapToJSON(val input: Any): String
    {
    }
    
    /**
     * Returns a error as an ItemError
     * @param code String The error code
     * @param message String A more descriptive error message
     * @return ItemError an ItemError object
     */
    protected fun error(code: String, message: String): ItemError
    {
        return ItemError(code,message)
    }

    /**
     * Returns the informations for an item
     * @param path String The path for the requested item
     * @return string: The json representation of the item or an error message
     */
    fun getItemAsJSON(path: String): String
    {
        val marketeers = getMarketeerList()

        for (marketeer in marketeers) {
            var response = marketeer.getItem(path)
            if (response != null) return wrapItemDataToJSON(response)
        }
        return wrapItemErrorToJSON(error("ITEMNOTFOUND","The requested item'"+path+"' was not found."))
    }

    fun getItemRaw(path: String): Any
    {
        val marketeers = getMarketeerList()

        for (marketeer in marketeers) {
            var response = marketeer.getItem(path)
            if (response != null) return response
        }
        return error("ITEMNOTFOUND","The requested item'"+path+"' was not found.")
    }
    
    fun getItem(path : String): String
    {
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
