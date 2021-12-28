package sunhill.Items.Uptime

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.UptimeDatapool
import sunhill.Items.ItemBase
import sunhill.Items.PoolItemBase

class IdletimeDurationItem : PoolItemBase("system.idletime.seconds",
    "d",
    "uptime",
    "Float",
    "asap") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as UptimeDatapool).idletime
    }

}