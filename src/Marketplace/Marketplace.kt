package sunhill.Marketplace

import sunhill.Items.ItemData
import sunhill.Items.ItemError
import sunhill.Items.ItemMetaData
import sunhill.marketeers.MarketeerBase

abstract class Marketplace {

    abstract fun getMarketeerList(): Array<MarketeerBase>

    // ******************************* Answer helpers **********************************
    /**
     * For the cases when the type is not defined at compile time
     */
    private fun wrapToJSON(value: Any?): String
    {
        return when (value) {
            is String -> "\""+value+"\""
            is ItemData -> wrapToJSON(value)
            is ItemError -> wrapToJSON(value)
            is ItemMetaData -> wrapToJSON(value)
            else -> wrapToJSON(error("UNEXPECTEDPARAM","wrapToJSON was passed an unexpected param"))
        }

    }

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
                wrapToJSON("result","OK")+
                wrapToJSON("path", input.path)+
                wrapToJSON("unit_int",input.unit_int)+
                wrapToJSON("unit",input.unit)+
                wrapToJSON("semantic_int",input.semantic_int)+
                wrapToJSON("semantic",input.semantic)+
                wrapToJSON("type",input.type)+
                wrapToJSON("update",input.update)+
                wrapToJSON("stamp",input.stamp.toString())+
                wrapToJSON("value",input.value!!.toString())+
                wrapToJSON("human_readable_value",input.human_readable_value)+
                wrapToJSON("request",input.request)+"}"
    }
    
    /**
     * Takes an ItemMetaData and wraps it into a JSON String
     */
    protected fun wrapToJSON(input: ItemMetaData): String
    {
        return "{"+
                wrapToJSON("result","OK")+
                wrapToJSON("path", input.path)+
                wrapToJSON("unit_int",input.unit_int)+
                wrapToJSON("unit",input.unit)+
                wrapToJSON("semantic_int",input.semantic_int)+
                wrapToJSON("semantic",input.semantic)+
                wrapToJSON("type",input.type)+
                wrapToJSON("update",input.update)+"}"
    }

    private fun wrapToJSON(input: List<String>): String
    {
        var result = "{["
        var first = true
        for (entry in input) {
            result += (if (first) {""} else {","})+"\""+entry+"\""
            first = false
        }
        return result + "]}"
    }

    private fun wrapToJSON(input: List<ItemData>,getter: (item: ItemData) -> String): String
    {
        var result = "{["
        var first = true
        for (entry in input) {
            result += (if (first) {""} else {","})+"\""+getter(entry)+"\""
            first = false
        }
        return result + "]}"
    }

    private fun wrapToJSON(input: Map<String, Any>): String
    {
        var result = "{["
        var first = true
        input.forEach {
            key,value -> result +=  (if (first) {""} else {","})+wrapToJSON(key,value.toString())
        }
        return result + "]}"
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
        return wrapToJSON(item)
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
        return result.distinct().toList()
    }

    /**
     * Returns all items that this marketplace is able to offer that meet the given condition and wraps it into json
     * @return String
     */
    fun getOfferingAsJSON(path: String = "*"): String
    {
        return wrapToJSON(getOffering(path))
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
     * Returns all items that this marketplace is able to offer that meet the given condition and wraps it into json
     * @return String
     */
    fun getAllItemsAsJSON(path: String = "*"): String
    {
        return wrapToJSON(getAllItems(path)) { it -> wrapToJSON(it) }
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
                temp[item.path] = item.value!!
            }
        }
        return temp.toMap()
    }

    /**
     * Returns all item human readable values that meet the given condition
     * @param path String A possible filter for the offering (default "*" means no filter)
     * @return String The json answer (array) of all items human readable values
     */
    fun getAllValuesAsJSON(path: String): String
    {
        return wrapToJSON(getAllValues(path))
    }

    /**
     * Returns all item values that meet the given condition
     * @param path String A possible filter for the offering (default "*" means no filter)
     * @return String The json answer (array) of all item values
     */
    fun getAllHumanReadableValues(path: String): Map<String, Any>
    {
        val items = getOffering(path)
        val temp = mutableMapOf<String,Any>()
        for (item_name in items) {
            val item = getItem(item_name)
            if (item is ItemData) {
                temp[item.path] = item.human_readable_value
            }
        }
        return temp.toMap()
    }

    /**
     * Returns all item human readable values that meet the given condition
     * @param path String A possible filter for the offering (default "*" means no filter)
     * @return String The json answer (array) of all items human readable values
     */
    fun getAllHumanReadableValuesAsJSON(path: String): String
    {
        return wrapToJSON(getAllHumanReadableValues(path))
    }

    /**
     * Takes a json encoded list and converts it to a List of Strings
     */
    private fun parseJSONtoList(input: String): List<String>
    {
        return listOf<String>()
    }

    /**
     * Takes a json encoded list and returns the items that are in this list
     */
    fun getItemsByList(list: String): List<ItemData>
    {
        return getItemsByList(parseJSONtoList(list))
    }

    /**
     * Takes a list of strings and return the items that are in this list as a list of items
     */
    fun getItemsByList(list: List<String>): List<ItemData>
    {
        val result = mutableListOf<ItemData>()
        list.forEach {
            item -> run {
                val found = getItem(item)
                if (found is ItemData) {
                    result.add(found)
                }
            }
        }
        return result.toList()
    }

    /**
     * Takes a list of string and returns the items that are in this list as a json list
     */
    fun getItemsByListAsJSON(list: List<String>): String
    {
        return wrapToJSON(getItemsByList(list))
    }

    /**
     * Takes a json encoded list of strings and returns the items that are in this list as a json list
     */
    fun getItemsByListAsJSON(list: String): String
    {
        return wrapToJSON(getItemsByList(list))
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
