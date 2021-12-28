package sunhill.Items

import sunhill.DataPool.DataPoolBase
import kotlin.math.roundToInt

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

    private fun error(error_id: String, error_message: String): String
    {
        return "{\"result\":\"FAILED\",\"error_code\":\"$error_id\",\"error_message\":\"$error_message\"}"
    }

    /**
     * Return the complete descriptor for this item with all constant and variable (meta)data
     */
    open fun get(request: String, userlevel: Int = 0, additional: MutableList<String> = mutableListOf()): String
    {
        if (this._readable_to == -1)
            return this.error("ITEMNOTREADABLE","The item is not readable.")
        else if (userlevel < this._readable_to)
            return this.error("READINGNOTALLOWED", "You are not allowed to read this item.")
        else
            return  "{"+
                getResult()+
                getRequest(request)+
                getUnitInt()+
                getUnit()+
                getSemanticInt()+
                getSemantic()+
                getType()+
                getUpdate()+
                getStamp()+
                getValueJSON(additional)+
                getHumanReadableValue(additional)+
                "}"
    }

    /**
     * Returns only the current value as a json string
     */
    open fun getValue(request: String, userlevel: Int = 0, additional: MutableList<String> = mutableListOf()): String
    {
        if (this._readable_to == -1)
            return this.error("ITEMNOTREADABLE","The item is not readable.")
        else if (userlevel < this._readable_to)
            return this.error("READINGNOTALLOWED", "You are not allowed to read this item.")
        else
            return  "{"+getValueJSON(additional, "")+"}"
    }

    /**
     * Returns only the current human readable value as a json string
     */
    open fun getHRValue(request: String, userlevel: Int = 0, additional: MutableList<String> = mutableListOf()): String
    {
        if (this._readable_to == -1)
            return this.error("ITEMNOTREADABLE","The item is not readable.")
        else if (userlevel < this._readable_to)
            return this.error("READINGNOTALLOWED", "You are not allowed to read this item.")
        else
            return  "{"+getHumanReadableValue(additional)+"}"
    }

    fun put(request: String,
            value: Any,
            userlevel: Int = 0,
            additional: MutableList<String> = mutableListOf()): String
    {
        return if (this._writeable_to == -1)
            this.error("ITEMNOTWRITEABLE", "The item is not writeable.")
        else  if (userlevel < this._writeable_to)
            this.error("WRITINGNOTALLOWED", "You are not allowed to write this item.")
        else {
            "{"+
                    getResult()+
                    getRequest(request)+
                    "}"
        }
    }

    private fun getJSONPart(key: String, value: String, addComma: Boolean = true): String
    {
        return "\""+key+"\":\""+value+"\""+(if (addComma) "," else "")
    }

    private fun getResult(): String
    {
        return getJSONPart("result","OK")
    }

    private fun getRequest(request: String): String
    {
        return getJSONPart("request", request )
    }

    private fun getUnitInt(): String
    {
        return getJSONPart("unit_int", this._unit_int )
    }

    private fun getUnit(): String
    {
        return getJSONPart("unit", this._unit )
    }

    private fun getSemanticInt(): String
    {
        return getJSONPart("semantic_int", this._semantic_int )
    }

    private fun getSemantic(): String
    {
        return getJSONPart("semantic", "")
    }

    private fun getType(): String
    {
        return getJSONPart("type",_type)
    }

    private fun getUpdate(): String
    {
        return getJSONPart("update", _update)
    }

    private fun getStamp(): String
    {
        return getJSONPart("stamp",System.currentTimeMillis().toString())
    }

    open fun calculateValue(additional: MutableList<String>): Any?
    {
        return null
    }

    private fun getValueJSON(additional: MutableList<String>, tail: String=","): String
    {
        val value: Any = calculateValue(additional)!!

        return "\"value\":"+(when (this._type) {
            "Integer", "Float" -> { value.toString() }
            else -> { "\""+value.toString()+"\""}})+tail
    }

    private fun getHumanReadableValue(additional: MutableList<String>): String
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
                    if (test_parts[i].toIntOrNull() == null) { // If not an int, then return false (this rule doesn't match)
                        return false
                    }
                    additional.add(test_parts[i])
                }
                "?" -> additional.add(test_parts[i])
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
        return mutableListOf<String>(this._path)
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