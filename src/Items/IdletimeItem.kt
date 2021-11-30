package sunhill.Items

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.UptimeDatapool

class IdletimeItem : ItemBase("d", "uptime", "Float") {

    override fun getValueFromPool(datapool: DataPoolBase): Any {
        return (datapool as UptimeDatapool).idletime
    }

}