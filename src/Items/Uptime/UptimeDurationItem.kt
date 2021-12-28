package sunhill.Items.Uptime

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.UptimeDatapool
import sunhill.Items.ItemBase
import sunhill.Items.PoolItemBase

class UptimeDurationItem() : PoolItemBase("system.uptime.duration","d", "uptime", "Float", "asap") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as UptimeDatapool).uptime
    }

}