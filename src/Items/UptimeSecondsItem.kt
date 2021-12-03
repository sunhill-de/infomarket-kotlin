package sunhill.Items

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.UptimeDatapool

class UptimeSecondsItem :  ItemBase("s", "uptime", "Float", update = "asap" ) {

    override fun getValueFromPool(datapool: DataPoolBase, additional: Array<out String>): Any {
        return (datapool as UptimeDatapool).uptime
    }
}