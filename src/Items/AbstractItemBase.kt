package sunhill.Items

import kotlin.math.roundToInt

data class ItemError(val code: String, val message: String) {}

data class ItemData(val path: String, val unit_int: String, val unit: String, val semantic_int: String, val semantic: String,
                    val type: String, val update: String, val stamp: Long, val value: Any?, val human_readable_value: String,
                    val request: String) {}

data class ItemMetaData(val path: String, val unit_int: String, val unit: String, val semantic_int: String, val semantic: String,
                        val type: String, val update: String) {}

/**
 * The AbstractItemBase is the base class for items.
 */
abstract class AbstractItemBase(path : String) {

    private var _path: String

    private var _error_code: String? = null

    private var _error_message: String? = null

    init {
        this._path = path
    }

    // ************************* Translation methods **********************************
    /**
     * Translates the internal unit to the displayable unit
     * This is a helper function for the constructor
     */
    protected  fun translateUnit(unit_int: String): String
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

    /**
     * Translates the semantic meaning to a more verbous
     * This is a helper function for the constructor
     */
    protected fun translateSemantic(semantic_int: String): String
    {
        return when (semantic_int) {
            "temp" -> { "Temperature" }
            "air_temp" ->{ "Air temperature" }
            "uptime" -> {  "Uptime" }
            "number" -> { "Number" }
            "capacity" -> { "Capacity" }
            "name" -> { "Name" }
            else -> { "" }
        }
    }

    // ************************* Item getters ***************************************
    /**
     * Returns the (default)path to this item
     * @return String The path to this item
     */
    fun getPath(additional: MutableList<String> = mutableListOf()): String
    {
        return _path
    }

    /**
     * Returns the result of the query (OK or FAILED)
     */
    fun getResult(): String
    {
        return if (hasError()) {
            "OK"
        } else {
            "FAILED"
        }
    }

    /**
     * Returns the internal representation of the unit
     * @return String
     */
    abstract fun getUnitInt(additional: MutableList<String> = mutableListOf()): String

    /**
     * Translates the internal unit to a human readable one
     */
    fun getUnit(additional: MutableList<String> = mutableListOf()): String
    {
        return translateUnit(getUnitInt(additional))
    }

    /**
     * Returns the internal representation of the semantic value
     */
    abstract fun getSemanticInt(additional: MutableList<String> = mutableListOf()): String

    /**
     * Translates the internal semantic meaning to a human readable one
     */
    fun getSemantic(additional: MutableList<String> = mutableListOf()): String
    {
        return translateSemantic(getSemanticInt(additional))
    }

    /**
     * Returns the data type of the item
     */
    abstract fun getType(additional: MutableList<String> = mutableListOf()): String

    /**
     * Returns the suggested update frequency
     */
    abstract fun getUpdate(additional: MutableList<String> = mutableListOf()): String

    /**
     * Returns the current timestamp
     */
    fun getStamp(): Long
    {
        return System.currentTimeMillis()
    }
    // ************************ Rights management ************************************

    abstract protected fun getReadableTo(additional: MutableList<String> = mutableListOf()): Int

    /**
     * Returns if the item is readable a all (independent of the given userlevel)
     */
    fun isReadableAtAll(additional: MutableList<String> = mutableListOf()): Boolean
    {
        return (getReadableTo(additional) !== -1)
    }

    /**
     * Returns if the item is readable to the given user. It does not check, if the item is readable at all
     */
    private fun isReadableToUser(userlevel: Int = 0,additional: MutableList<String> = mutableListOf()): Boolean
    {
        return (userlevel >=getReadableTo(additional))
    }

    /**
     * Returns it this item is readable at all and readable to the given userlevel
     */
    fun isReadable(userlevel: Int = 0,additional: MutableList<String> = mutableListOf()): Boolean
    {
        return (isReadableAtAll(additional) &&  isReadableToUser(userlevel,additional))
    }

    abstract protected fun getWriteableTo(additional: MutableList<String> = mutableListOf()): Int

    /**
     * Returns if the item is writable a all (independent of the given userlevel)
     */
    fun isWritableAtAll(additional: MutableList<String> = mutableListOf()): Boolean
    {
        return (getWriteableTo(additional) !== -1)
    }

    /**
     * Returns if the item is writable to the given user. It does not check, if the item is writable at all
     */
    private fun isWritableToUser(userlevel: Int = 0,additional: MutableList<String> = mutableListOf()): Boolean
    {
        return (userlevel >= getWriteableTo(additional))
    }

    /**
     * Returns if the item is writable at all and writable to the given user
     */
    fun isWritable(userlevel: Int = 0,additional: MutableList<String> = mutableListOf()): Boolean
    {
        return (isWritableAtAll(additional) && isWritableToUser(userlevel,additional))
    }

    // ************************* Error handling *****************************************
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
     * Indicates an error condition
     */
    protected fun setError(error_id: String, error_message: String)
    {
        _error_code = error_id
        _error_message = error_message
    }

    /**
     * Returns an error data object or null if there is no error
     */
    fun getError(): ItemError?
    {
        return if (hasError()) { ItemError(getErrorCode(),getErrorMessage()) } else { null }
    }

    // ***************************** Value management *************************************
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

    fun setValue()
    {

    }

    /**
     * If a wildcard item is called with the count parameter, this method returns the count
     * @return Int if -1 then this permutation is uncountable otherwise the count of items in this permutation
     * Remark: A uncountable permutation means that the number can't be calculated
     */
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
            return -1
        } else {
            return list.count()
        }
    }

    /**
     * Returns the list of permutations for the given wildcard or null if it's not possible to calculate the permutations
     */
    open fun getPermutation(parts: MutableList<String>,index: Int): List<String>?
    {
        return null
    }

    // ****************************** Match methods ***************************************
    /**
     * Determines if the given search item matches to this item. If yes it return true and puts
     * the variables in additional otherwise it returns false
     */
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

    // ***************************** The main methods *************************************
    fun getItem(request: String,userlevel: Int = 0,additional: MutableList<String> = mutableListOf()): ItemData?
    {
        if (!isReadableAtAll(additional)) {
            setError("ITEMNOTREADABLE","The item is not readable.")
        } else if (!isReadableToUser(userlevel,additional)) {
            setError("ITEMNOTREADABLETOUSER", "The item is not readable to this user.")
        }
        // Don't put this in the return statement below, because there might be errors in creating the itemData
        val result = ItemData(
            unit=getUnit(additional),
            path=getPath(additional),
            unit_int=getUnitInt(additional),
            semantic=getSemantic(additional),
            semantic_int=getSemanticInt(additional),
            type=getType(additional),
            update=getUpdate(additional),
            stamp=getStamp(),
            value=getValue(additional),
            human_readable_value = getHumanReadableValue(additional),
            request = request
        )
        return if (hasError())  {
            null
        } else {
            result
        }
    }

    fun getItemMetadata(request: String, userlevel: Int = 0, additional: MutableList<String> = mutableListOf()): ItemMetaData?
    {
        val result = ItemMetaData(
            unit=getUnit(),
            path=getPath(),
            unit_int=getUnitInt(),
            semantic=getSemantic(),
            semantic_int=getSemanticInt(),
            type=getType(),
            update=getUpdate(),
        )
        return if (hasError()) {
            null
        } else {
            result
        }
    }

    fun setItem(request: String, value: Any, userlevel: Int = 0, additional: MutableList<String> = mutableListOf()): ItemError?
    {
        if (!isWritableAtAll(additional)) {
            setError("ITEMNOTWRITEABLE","The item is not writeable.")
        } else if (!isWritableToUser(userlevel,additional)) {
            setError("ITEMNOTWRITEABLETOUSER", "The item is not writeable to this user.")
        }
        return null
    }

    // ******************************** Human readable value ***************************************
    protected fun getHumanReadableValue(additional: MutableList<String>): String
    {
        val value = calculateValue(additional)
        if (value == null) {
            return ""
        }
        return when (this.getUnitInt()) {
            "d" -> getDuration(value)
            "K" -> getCapacity(value)
            " " -> value.toString()
            else -> value.toString() + " " + this.getUnit()
        }
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

    private fun getCapacity(capacity: Any): String
    {
        var size : Int = capacity.toString().toInt()
        return ""+size+" Bytes"
    }

}