package sunhill.Items

import kotlin.math.roundToInt

/**
 * The base class for items. Every item has to define a constructor that gives some essential properties of this
 * item (like type, semantic_type, etc.)
 * public functions:
 * - JSONGetItem - Return the complete JSON Answer for the given request
 * - JSONGetValue - Returns only the JSON answer with the value for the given request
 * - JSONGetHRValue - Return only the JSON answer with the human readable value for the given request
 * - JSONGetOffering - Returns a JSON answer for what this items offers for requests
 */
open class ItemBase(path: String,
                    unit_int: String,
                    semantic_int: String,
                    type: String,
                    update: String,
                    readable_to: Int = 0,
                    writeable_to: Int = -1 ) {

    private var _path: String

    private var _unit_int: String

    private var _unit: String;

    private var _semantic_int: String

    private var _semantic: String

    private var _type: String

    private var _update: String

    private var _readable_to: Int

    private var _writeable_to: Int

    private var _error_code: String? = null

    private var _error_message: String? = null

    init {
        this._path = path
        this._unit_int = unit_int
        this._unit = translateUnit(unit_int)
        this._semantic_int = semantic_int
        this._semantic = translateSemantic(semantic_int)
        this._type = type
        this._update = update
        this._readable_to = readable_to
        this._writeable_to = writeable_to
    }

    /**
     * Returns the (default)path to this item
     * @return String The path to this item
     */
    fun getPath(): String
    {
      return _path
    }
    
    /**
     * Returns the internal representation of the unit of this item
     */
    fun getUnitInt(): String
    {
      return _unit_int
    }
    
    /**
     * Returns the unit of this item (or " " if no unit)
     */
    fun getUnit(): String
    {
      return _unit
    }
    
    fun getSemantic(): String
    {
      return _semantic
    }
    
    fun getSemanticInt(): String
    {
      return _semantic_int
    }
    
    fun getType(): String
    {
      return _type
    }
    
    fun getUpdate(): String
    {
      return _update
    }
    
    /**
     * Returns if this item has an error or not
     */
    fun hasError(): Boolean
    {
      return _error_code !== null
    }
    
    /**
     * Returns the error code (or raises an exception if no error is defined)
     */
    fun getErrorCode(): String
    {
      return _error_code!!
    }
  
    /**
     * Returns the error message (or raises an exception if no error is defined)
     */
    fun getErrorMessage(): String
    {
      return _error_message!!
    }
    /**
     * Translates the internal unit to the displayable unit
     */
    private fun translateUnit(unit_int: String): String
    {
        return when (unit_int) {
            "s","m" -> { unit_int }
            "C" -> { "°C" }
            "p" -> { "mmHg" }
            "c" -> { "cm" }
            "l" -> { "lx" }
            "M" -> { "MB" }
            "G" -> { "GB" }
            "T" -> { "TB" }
            "P" -> { "%" }
            else -> { "" }
        }
    }

    private fun translateSemantic(semantic_int: String): String
    {
        return when (semantic_int) {
            "temp" -> { "Temperature" }
            "air_temp" ->{ "Air temperature" }
            "uptime" -> {  "Uptime" }
            "number" -> { "Number" }
            "capacity" -> { "Capacity" }
            else -> { "" }
        }
    }

    /**
     * Creates an error message for return of the getXXXX methods
     */
    private fun error(error_id: String, error_message: String): String
    {
        return "{\"result\":\"FAILED\",\"error_code\":\"$error_id\",\"error_message\":\"$error_message\"}"
    }

    /**
     * Indicates an error condition
     */
    protected fun setError(error_id: String, error_message: String)
    {
        _error_code = error_id
        _error_message = error_message
    }

    /**
     * Return the complete descriptor for this item with all constant and variable (meta)data
     */
    fun JSONGetItem(request: String, userlevel: Int = 0, additional: MutableList<String> = mutableListOf()): String
    {
        if (_error_code !== null) {
            return error(_error_code!!,_error_message!!)
        } else if (this._readable_to == -1)
            return this.error("ITEMNOTREADABLE","The item is not readable.")
        else if (userlevel < this._readable_to)
            return this.error("READINGNOTALLOWED", "You are not allowed to read this item.")
        else if (request.contains(".count")) {
            return """{"result":"OK","""+getJSONRequest(request)+
                   """"unit_int":" ","unit":"","semantic_int":"number","semantic":"number","type":"Integer""""+
                   getJSONUpdate()+getJSONStamp()+getJSONCount(request)+getJSONHRCount(request)+"}"
        } else
            return  "{"+
                getJSONResult()+
                getJSONRequest(request)+
                getJSONUnitInt()+
                getJSONUnit()+
                getJSONSemanticInt()+
                getJSONSemantic()+
                getJSONType()+
                getJSONUpdate()+
                getJSONStamp()+
                getJSONValue(additional)+
                getJSONHumanReadableValue(additional)+
                "}"
    }

    /**
     * Returns only the current value as a json string
     */
    fun JSONGetValue(request: String, userlevel: Int = 0, additional: MutableList<String> = mutableListOf()): String
    {
        if (_error_code !== null) {
            return error(_error_code!!,_error_message!!)
        } else if (this._readable_to == -1)
            return this.error("ITEMNOTREADABLE","The item is not readable.")
        else if (userlevel < this._readable_to)
            return this.error("READINGNOTALLOWED", "You are not allowed to read this item.")
        else
            return  "{"+getJSONValue(additional, "")+"}"
    }

    /**
     * Returns only the current human readable value as a json string
     */
    fun JSONGetHRValue(request: String, userlevel: Int = 0, additional: MutableList<String> = mutableListOf()): String
    {
        if (_error_code !== null) {
            return error(_error_code!!,_error_message!!)
        } else if (this._readable_to == -1)
            return this.error("ITEMNOTREADABLE","The item is not readable.")
        else if (userlevel < this._readable_to)
            return this.error("READINGNOTALLOWED", "You are not allowed to read this item.")
        else
            return  "{"+getJSONHumanReadableValue(additional)+"}"
    }

    fun JSONGetOffering(request: String, userlevel: Int = 0): String
    {
        val offering =  mutableListOf<String>()
        addOfferings(request,offering)
        var result = """{"result":"OK","offering":["""
        for (i in 0..offering.count()) {
            result += ((if (i>0) "," else ""))+"\""+offering[i]+"\""
        }
        return result+"]}"
    }

    fun put(request: String,
            value: Any,
            userlevel: Int = 0,
            additional: MutableList<String> = mutableListOf()): String
    {
        if (_error_code !== null) {
            return error(_error_code!!,_error_message!!)
        } else return if (this._writeable_to == -1)
            this.error("ITEMNOTWRITEABLE", "The item is not writeable.")
        else  if (userlevel < this._writeable_to)
            this.error("WRITINGNOTALLOWED", "You are not allowed to write this item.")
        else {
            "{"+
                    getJSONResult()+
                    getJSONRequest(request)+
                    "}"
        }
    }

    private fun getJSONPart(key: String, value: String, addComma: Boolean = true): String
    {
        return "\""+key+"\":\""+value+"\""+(if (addComma) "," else "")
    }

    private fun getJSONResult(): String
    {
        return getJSONPart("result",getResult())
    }

    /**
     * Returns the result of the query (OK or FAILED)
     */
    fun getResult(): String
    {
        return if (_error_code == null) {
            "OK"
        } else {
            "FAILED"
        }
    }

    private fun getJSONRequest(request: String): String
    {
        return getJSONPart("request", request )
    }

    private fun getJSONUnitInt(): String
    {
        return getJSONPart("unit_int", getUnitInt() )
    }

    /**
     * Returns the internal unit for this item
     */
    fun getUnitInt(): String
    {
        return _unit_int
    }

    private fun getJSONUnit(): String
    {
        return getJSONPart("unit", getUnit() )
    }

    fun getUnit(): String
    {
        return _unit
    }

    private fun getJSONSemanticInt(): String
    {
        return getJSONPart("semantic_int", getSemanticInt() )
    }

    fun getSemanticInt(): String
    {
        return _semantic_int
    }

    private fun getJSONSemantic(): String
    {
        return getJSONPart("semantic", getSemantic())
    }

    fun getSemantic(): String
    {
        return _semantic
    }

    private fun getJSONType(): String
    {
        return getJSONPart("type",getType() )
    }

    /**
     * Returns the data type of this item (Integer, Float, String, etc)
     */
    fun getType(): String
    {
        return _type
    }

    private fun getJSONUpdate(): String
    {
        return getJSONPart("update", getUpdate() )
    }

    /**
     * Returns the suggested update frequency
     */
    fun getUpdate(): String
    {
        return _update
    }

    private fun getJSONStamp(): String
    {
        return getJSONPart("stamp",getStamp() )
    }

    /**
     * Returns the current timestamp
     */
    fun getStamp(): String
    {
        return System.currentTimeMillis().toString()
    }

    /**
     * Returns the value of the item with the given additional informations
     * Must't return null. So if not overwritten it raises an error
     */
    protected open fun calculateValue(additional: MutableList<String>): Any?
    {
        return null
    }

    /**
     * Gets the item value and does some checks
     */
    fun getValue(additional: MutableList<String>): Any
    {
        val value = calculateValue(additional)
        if (value == null) {
            setError("NOVALUE","calculateValue() returns no value")
            return 0
        } else {
            return value
        }
    }

    private fun getJSONValue(additional: MutableList<String>, tail: String=","): String
    {
        val value: Any = getValue(additional)

        return "\"value\":"+(when (this._type) {
            "Integer", "Float" -> { value.toString() }
            else -> { "\""+value.toString()+"\""}})+tail
    }

    fun getCount(request: String): Int
    {
        val request_parts = request.split(".")
        val path_parts = _path.split(".")
        var index = 0
        for (i in 0..request_parts.count()) {
            if (path_parts[i].equals("*") || path_parts[i].equals("#") || path_parts[i].equals("?")) {
                index++
            }
        }
        if (index == 0) {
            setError("NOWILDCARD","The item provides no wildcards.")
        }
        val list = getPermutation(request_parts.toMutableList(),index-1)
        if (list == null) {
            setError("GETPERMUTATIONRETURNSNULL",
                "The method getPermutation return null instead of expected permutation")
            return 0
        } else {
            return list.count()
        }
    }

    private fun getJSONCount(request: String, tail: String=","): String
    {
        return """"value":"""+getCount(request).toString()+tail
    }

    private fun getJSONHRCount(request: String): String
    {
        return """"human_readable_value":""""+getCount(request).toString()+"\","
    }

    private fun getJSONHumanReadableValue(additional: MutableList<String>): String
    {
        val value: Any = calculateValue(additional)!!

        return getJSONPart("human_readable_value", when (this._unit_int) {
            "d" -> getDuration(value)
            "K" -> getCapacity(value)
            " " -> value.toString()
            else -> value.toString() + " " + this._unit
        }, false)
    }

    private fun getDuration(duration:Any): String
    {
        var timespan : Int = duration.toString().toDouble().roundToInt()

        val seconds = timespan%60
        val sec_str = if (seconds == 1) "1 second" else ""+seconds+" seconds"
        timespan /= 60

        val minutes = timespan%60
        val min_str = if (minutes == 1) "1 minute" else ""+minutes+" minutes"
        timespan /= 60

        val hours = timespan%24
        val hour_str = if (hours == 1) "1 hour" else ""+hours+" hours"
        timespan /= 24

        val days = timespan%365
        val day_str = if (days == 1) "1 day" else ""+days+" days"

        val years = timespan / 365
        val year_str = if (years == 1) "1 year" else ""+years+" years"

        return if (years > 0) year_str + " " + day_str
        else if (days > 0) day_str + " " + hour_str
        else if (hours > 0) hour_str + " " + min_str
        else if (minutes > 0) min_str + " " + sec_str
        else sec_str
    }

    fun getCapacity(capacity: Any): String
    {
        var size : Int = capacity.toString().toInt()
        return ""+size+" Bytes"
    }

    fun matches(search: String, userLevel: Int, additional: MutableList<String>): Boolean
    {
        val test_parts = search.split('.')
        val offer_parts = _path.split('.')
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
                    if (test_parts[i].equals("count")) {
                        additional.add("count")
                        return true // Stop searching here, because we request a count
                    }
                    if (test_parts[i].toIntOrNull() == null) { // If not an int, then return false (this rule doesn't match)
                        return false
                    }
                    additional.add(test_parts[i])
                }
                "?" -> {
                    additional.add(test_parts[i])
                    if (test_parts[i].equals("count")) {
                        additional.add("count")
                        return true // Stop searching here, because we request a count
                    }
                }
                "*" -> {
                    additional.add(test_parts.drop(i).joinToString(".")) // Drop the parts until * and return the rest joined by "."
                    return true
                }
                else -> if (test_parts[i] != offer_parts[i])
                    return false
            }
            i++
        }
    }

    /**
     * This method has to be overwritten for items that support variables
     */
    open fun getAllOfferings(): MutableList<String>
    {
        val result =  mutableListOf<String>()
        val parts = _path.split(".").toMutableList()
        handlePermutations(parts,0, result)
        return result
    }

    fun handlePermutations(parts: MutableList<String>,index: Int,result: MutableList<String>)
    {
        var i = 0;
        while (i<parts.count()) {
            if (parts[i].equals("#") || parts[i].equals("?") || (parts[i].equals("*"))) {

                // Item pathes mustn't start with a wildcard
                if (i == 0) {
                    setError("MUSTNTSTARTWITHPERMUTATION",
                    "An item path must't start with a permutation")
                    return
                }

                // Every permutation provides a count element
                var count_str : String = ""
                for (j in 0..i-1) {
                    count_str += parts[j]+"."
                }
                result.add(count_str+"count")

                // Return all permutations
                var list = getPermutation(parts,index)
                if (list !== null) {
                    val backup = parts[i]
                    for (entry in list) {
                        parts[i] = entry
                        handlePermutations(parts,index+1,result)
                    }
                    parts[i] = backup
                    return
                } else {
                    setError("GETPERMUTATIONRETURNSNULL",
                        "The method getPermutation return null instead of expected permutation")
                }
            }
            i++;
        }
        result.add(parts.joinToString(separator = "."))
    }

    open fun getPermutation(parts: MutableList<String>,index: Int): List<String>?
    {
        return null
    }

    fun addOfferings(search: String, result: MutableList<String>) {
        val offerings = getAllOfferings()
        for (offer in offerings) {
            if (offeringMatches(offer,search)) {
                result.add(offer)
            }
        }
    }

    private fun offeringMatches(offer: String,search: String): Boolean
    {
        val offer_parts = offer.split(".")
        val search_parts = search.split(".")
        var i = 0
        while (true) {
            if ((i > offer_parts.count()) && (i > search_parts.count())) {
                return true
            }
            if ((i > offer_parts.count()) || (i > search_parts.count())) {
                return false
            }
            if (search_parts[i].equals("*")) {
                return true
            }
            if (!search_parts[i].equals(offer_parts[i])) {
                return false
            }
            i++;
        }
    }

}
