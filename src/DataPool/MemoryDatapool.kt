package sunhill.DataPool

import java.io.File

class MemoryDatapool: DataPoolBase() {

    var _meminfo: MutableMap<String, String>? = null

    fun readMeminfo(): String
    {
        return ""
    }

    override fun update() {
        TODO("Not yet implemented")
    }
}



