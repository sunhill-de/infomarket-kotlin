package sunhill.Marketplace

import sunhill.Items.ItemData
import sunhill.Items.ItemError
import sunhill.Items.ItemMetaData
import sunhill.marketeers.MarketeerBase

abstract class Marketplace {

    abstract fun getMarketeerList(): Array<MarketeerBase>

    // ******************************* Answer helpers **********************************
    private fun wrapToJSON(key: String, value: String, addComma: Boolean=true): String
    {
        return "\""+key+"\":\""+value+"\""+(if(addComma) {","} else {""})
    }

    /**
     * Takes an ItemError and wraps it into a JSON String
     */
    private fun wrapToJSON(input: ItemError): String
    {
        return "{"+
                wrapToJSON("result","FAILED")+
                wrapToJSON("error_code",input.code)+
                wrapToJSON("error_message",input.message,false)+
                "}"
    }
    
     /**
     * Takes an ItemData and wraps it into a JSON String
     */
    private fun wrapToJSON(input: ItemData): String
    {
        return "{"+
                wrapJSONPair("result","OK")+
                wrapJSONPair("path", input.path)+
                wrapJSONPair("unit_int",input.unit_int)+
                wrapJSONPair("unit",input.unit)+
                wrapJSONPair("semantic_int",input.semantic_int)+
                wrapJSONPair("semantic",input.semantic)+
                wrapJSONPair("type",input.type)+
                wrapJSONPair("update",input.update)+
                wrapJSONPair("stamp",input.stamp.toString())+
                wrapJSONPair("value",input.value!!.toString())+
                wrapJSONPair("human_readable_value",input.human_readable_value)+
                wrapJSONPair("request",input.request)+"}"
    }
    
    /**
     * Takes an ItemMetaData and wraps it into a JSON String
     */
    protected fun wrapToJSON(input: ItemMetaData): String
    {
        return "{"+
                wrapJSONPair("result","OK")+
                wrapJSONPair("path", input.path)+
                wrapJSONPair("unit_int",input.unit_int)+
                wrapJSONPair("unit",input.unit)+
                wrapJSONPair("semantic_int",input.semantic_int)+
                wrapJSONPair("semantic",input.semantic)+
                wrapJSONPair("type",input.type)+
                wrapJSONPair("update",input.update)+"}"
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
        return wrapToJSON(getItem(path))
    }

    /**
     * Tries to find the item given by path. If it finds some then return a ItemData object, if not
     * return a ItemError object
     */
    fun getItem(path : String): Any
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
    fun getValue(path : String): Any?
    {
        val item = getItem(path)
        return if (item is ItemData) {
            item.value
        } else {
           null
        }
    }

    /**
     * Returns the value from an item as a json string (or an error message)
     */
    fun getValueAsJSON(path: String): String
    {
        val item = getItem(path)
        return when (item) {
            is ItemError -> wrapToJSON(item)
            is ItemData -> "{"+wrapToJSON("value",item.value.toString())
            else -> wrapToJSON(error("UNEXPECTEDRESULT","Unexpected result from getItem()"))
        }
    }

    /**
     * Returns the human readable value for an item
     * @param path String The path for the requested item
     * @return string: The json representation of the human readable value of the item or an error message
     */
    fun getHumanReadableValue(path : String): String?
    {
        val item = getItem(path)
        return if (item is ItemData) {
            item.human_readable_value
        } else {
            null
        }
    }

    /**
     * Returns the human readable value from an item as a json string (or an error message)
     */
    fun getHumanReadableValueAsJSON(path: String): String
    {
        val item = getItem(path)
        return when (item) {
            is ItemError -> wrapToJSON(item)
            is ItemData -> "{"+wrapToJSON("value",item.human_readable_value)
            else -> wrapToJSON(error("UNEXPECTEDRESULT","Unexpected result from getItem()"))
        }
    }


    /**
     * Returns all items that this marketplace is able to offer that meet the given condition
     * @param path String A possible filter for the offering (default "*" means no filter)
     * @return String The json answer (array) of all offerings
     */
    fun getOffering(path: String = "*"): List<String>
    {
        val result = mutableListOf<String>()
        val marketeers = getMarketeerList()

        for (marketeer in marketeers) {
            result.addAll(marketeer.getOffering(path))
        }
        val unique = result.distinct()
        return result.toList()
    }

    /**
     * Returns all items that this marketplace is able to offer that meer the given condtion an wraps it into json
     * @return String
     */
    fun getOfferingAsJSON(path: String = "*"): String
    {
        val offering = getOffering()
        var result = "{["
        var first = true
        for (entry in offering) {
            result += (if (first) {""} else {","})+"\""+offering+"\""
            first = false
        }
        return result + "]}"
    }

    /**
     * Returns all items that meet the given condition
     * @param path String A possible filter for the offering (default "*" means no filter)
     * @return List if Items
     */
    fun getAllItems(path: String = "*"): List<ItemData>
    {
        val items = getOffering(path)
        val temp = mutableListOf<ItemData>()
        for (item_name in items) {
            val item = getItem(item_name)
            if (item is ItemData) {
                temp.add(item)
            }
        }
        return temp.toList()
    }

    /**
     * Returns all items that this marketplace is able to offer that meer the given condtion an wraps it into json
     * @return String
     */
    fun geAllItemsAsJSON(path: String = "*"): String
    {
        val offering = getAllItems(path)
        var result = "{["
        var first = true
        for (entry in offering) {
            result += (if (first) {""} else {","})+wrapItemDataToJSON(entry)
            first = false
        }
        return result + "]}"
    }

    /**
     * Returns all item values that meet the given condition
     * @param path String A possible filter for the offering (default "*" means no filter)
     * @return String The json answer (array) of all item values
     */
    fun getAllValues(path: String): Map<String, Any>
    {
        val items = getOffering(path)
        val temp = mutableMapOf<String,Any>()
        for (item_name in items) {
            val item = getItem(item_name)
            if (item is ItemData) {
                temp.put(item.path,item.value!!)
            }
        }
        return temp.toMap()
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
