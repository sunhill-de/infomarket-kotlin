package sunhill.Items.Uptime

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.UptimeDatapool
import sunhill.Items.ItemBase

class UptimeSecondsItem :  ItemBase("s", "uptime", "Float", update = "asap" ) {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as UptimeDatapool).uptime
    }
}