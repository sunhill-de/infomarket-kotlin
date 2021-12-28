package sunhill.Items.Uptime

import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.UptimeDatapool
import sunhill.Items.ItemBase
import sunhill.Items.PoolItemBase

class IdletimeSecondsItem : PoolItemBase("system.idletime.seconds","s", "uptime", "Float", "asap") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any {
        return (datapool as UptimeDatapool).idletime
    }

}