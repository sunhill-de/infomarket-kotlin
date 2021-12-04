package sunhill.Items

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.UptimeDatapool

class UptimeDurationItem() : ItemBase("d", "uptime", "Float", "asap") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as UptimeDatapool).uptime
    }

}