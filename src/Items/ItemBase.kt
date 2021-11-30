package sunhill.Items

import sunhill.DataPool.DataPoolBase
import kotlin.math.roundToInt

abstract class ItemBase(unit_int: String, semantic_int: String, type: String ) {

    private var _unit_int: String

    private var _unit: String;

    private var _semantic_int: String

    private var _semantic: String

    private var _type: String

    init {
        this._unit_int = unit_int
        this._unit = translateUnit(unit_int)
        this._semantic_int = semantic_int
        this._semantic = translateSemantic(semantic_int)
        this._type = type
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
            "temp" -> { translate("Temperature") }
            "air_temp" ->{ translate("Air temperature") }
            "uptime" -> { translate( "Uptime") }
            "number" -> { translate("Number") }
            "capacity" -> { translate("Capacity") }
            else -> { "" }
        }
    }

    public fun get(request: String, datapool: DataPoolBase): String
    {
        return  "{"+
                getResult()+
                getRequest(request)+
                getUnitInt()+
                getUnit()+
                getSemanticInt()+
                getSemantic()+
                getValue(datapool)+
                getHumanReadableValue(datapool)+
                "}"
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

    }

    fun translate(item: String, lan: String = "de"): String
    {
        when (lan) {
            "en" -> {
                return item
            }
            "de" -> {
                return translate_de(item)
            }
            else -> {
                return item
            }
        }
    }

    private fun translate_de(item : String): String
    {
        when (item) {
            "second" -> { return "Sekunde" }
            "seconds" -> { return "Sekunden" }
            "minute" -> { return "Minute" }
            "minutes" -> { return "Minuten" }
            "hour" -> { return "Stunde" }
            "hours" -> { return "Stunden" }
            "day" -> { return "Tag" }
            "days" -> { return "Tage" }
            "year" -> { return "Jahr" }
            "years" -> { return "Jahre" }
            "Temperature" -> { return "Temperatur" }
            "Air temperature" -> { return "Lufttemperatur" }
            "Uptime" -> { return "Laufzeit" }
            "Number" -> { return "Nummer" }
            "Capacity" -> { return "Kapazität" }
            else -> { return item }
        }
    }

    internal abstract fun getValueFromPool(datapool: DataPoolBase): Any

    private fun getValue(datapool: DataPoolBase): String
    {
        val value: Any = getValueFromPool(datapool)

        return "\"value\":"+(when (this._type) {
            "Integer", "Float" -> { value.toString() }
            else -> { "\""+value.toString()+"\""}})+","
    }

    private fun getHumanReadableValue(datapool: DataPoolBase): String
    {
        val value: Any = getValueFromPool(datapool)

        return getJSONPart("human_readable_value", when (this._unit) {
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
        val sec_str = if (seconds == 1) "1 "+translate("second") else ""+seconds+" "+translate("seconds")
        timespan /= 60

        val minutes = timespan%60
        val min_str = if (minutes == 1) "1 "+translate("minute") else ""+minutes+" "+translate("minutes")
        timespan /= 60

        val hours = timespan%24
        val hour_str = if (hours == 1) "1 "+translate("hour") else ""+hours+" "+translate("hours")
        timespan /= 24

        val days = timespan%365
        val day_str = if (days == 1) "1 "+translate("day") else ""+days+" "+translate("days")

        val years = timespan / 365
        val year_str = if (years == 1) "1 "+translate("year") else ""+years+" "+translate("years")

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
}