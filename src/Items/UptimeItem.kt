package sunhill.Items

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.UptimeDatapool

class UptimeItem() : ItemBase("d", "uptime", "Float") {

    override fun getValueFromPool(datapool: DataPoolBase): Any {
        return (datapool as UptimeDatapool).uptime
    }

}