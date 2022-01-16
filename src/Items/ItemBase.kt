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
                    writeable_to: Int = -1 ) : AbstractItemBase(path) {
    

    private var _unit_int: String

    private var _semantic_int: String

    private var _type: String

    private var _update: String

    private var _readable_to: Int

    private var _writeable_to: Int

    /**
     * Constructor
     */
    init {
        this._unit_int = unit_int
        this._semantic_int = semantic_int
        this._type = type
        this._update = update
        this._readable_to = readable_to
        this._writeable_to = writeable_to
    }

    override fun getReadableTo(additional: MutableList<String>): Int {
        return _readable_to
    }

    override fun getWriteableTo(additional: MutableList<String>): Int {
        return _writeable_to
    }

    /**
     * Returns the internal unit for this item
     */
    override fun getUnitInt(additional: MutableList<String>): String
    {
        return _unit_int
    }

    override fun getSemanticInt(additional: MutableList<String>): String
    {
        return _semantic_int
    }

    /**
     * Returns the data type of this item (Integer, Float, String, etc)
     */
    override fun getType(additional: MutableList<String>): String
    {
        return _type
    }

    /**
     * Returns the suggested update frequency
     */
    override fun getUpdate(additional: MutableList<String>): String
    {
        return _update
    }

}
