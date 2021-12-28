package sunhill.DataPool

import java.io.File

/**
 * Reads out the file /proc/uptime and provides its content in the field 'uptime' and 'idletime'
 */
class UptimeDatapool : DataPoolBase() {

    private var _uptime: Double? = null

    private var _idletime: Double? = null

    val uptime
        get() = if (_uptime == null) readUptime()
                else _uptime!!

    val idletime
        get() = if (_idletime == null) readIdletime()
                else _idletime!!

    /**
     * Reads the file /proc/uptime, removes the trailing LF
     */
    public fun readUptimeFile(): String
    {
        val f = File("/proc/uptime")
        return f.readText().replace("\n","")
    }

    /**
     * Calls readBothValues and return the uptime
     */
    private fun readUptime(): Double {
        readBothValues()
        return _uptime!!
    }

    /**
     * Calls readBothValues and returns the idletime
     */
    private fun readIdletime(): Double {
        readBothValues()
        return _idletime!!
    }

    /**
     * gets the content of /proc/uptime, splits it in uptime and idletime and sets the private properties
     */
    private fun readBothValues() {
        val parts = readUptimeFile().split(' ')
        _uptime = parts.elementAt(0).toDouble()
        _idletime = parts.elementAt(1).toDouble()
    }

    override fun update() {
        readBothValues()
    }
}