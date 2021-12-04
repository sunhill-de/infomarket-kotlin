package sunhill.Items

import org.junit.Test
import net.javacrumbs.jsonunit.assertj.assertThatJson
import sunhill.DataPool.DataPoolBase

class DummyDatapool : DataPoolBase() {

}

class ReadOnlyTestItem : ItemBase("d","uptime","Integer","asap") {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any
    {
        return 1000
    }

}

class WriteOnlyTestItem : ItemBase("d","uptime","Integer","asap", readable_to = -1, writeable_to = 0) {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any
    {
        return "ABC"
    }


}

class ReadWriteTestItem : ItemBase(" ","count","Integer","asap", readable_to = 10, writeable_to = 10) {

    override fun getValueFromPool(datapool: DataPoolBase?, additional: MutableList<String>): Any
    {
        return (additional[0].toInt())*10
    }


}


class ItemBaseTest {

    class ReadOnlyItemTest {

        @Test
        fun testSimpleGet() {
            var test : ReadOnlyTestItem = ReadOnlyTestItem()
            val dummy : DummyDatapool = DummyDatapool()
            val result = test.get("test.request", dummy)
            assertThatJson(result).isObject().containsEntry("request","test.request")
            assertThatJson(result).isObject().containsEntry("human_readable_value","16 minutes 40 seconds")
            assertThatJson(result).isObject().containsEntry("result","OK")
        }

        @Test
        fun testWriteError() {
            var test : ReadOnlyTestItem = ReadOnlyTestItem()
            val dummy : DummyDatapool = DummyDatapool()
            var result = test.put("test.request", "ABC", datapool = dummy)
            assertThatJson(result).isObject().containsEntry("result","FAILED")
            assertThatJson(result).isObject().containsEntry("error_code","ITEMNOTWRITEABLE")
        }
    }

    class WriteOnlyItemTest {

        @Test
        fun testReadError() {
            var test : WriteOnlyTestItem = WriteOnlyTestItem()
            val dummy : DummyDatapool = DummyDatapool()
            var result = test.get("test.request", datapool = dummy)
            assertThatJson(result).isObject().containsEntry("result","FAILED")
            assertThatJson(result).isObject().containsEntry("error_code","ITEMNOTREADABLE")
        }

        @Test
        fun testWrite() {
            var test : WriteOnlyTestItem = WriteOnlyTestItem()
            val dummy : DummyDatapool = DummyDatapool()
            var result = test.put("test.request", 10, datapool = dummy)
            assertThatJson(result).isObject().containsEntry("result","OK")
        }

    }

    class ReadWriteItemTest {

        @Test
        fun testInsufficientReadRights() {
            var test : ReadWriteTestItem = ReadWriteTestItem()
            val dummy : DummyDatapool = DummyDatapool()
            var result = test.get("test.request", datapool = dummy,0, mutableListOf<String>("2"))
            assertThatJson(result).isObject().containsEntry("result","FAILED")
            assertThatJson(result).isObject().containsEntry("error_code","READINGNOTALLOWED")
        }

        @Test
        fun testInsufficientWriteRights() {
            var test : ReadWriteTestItem = ReadWriteTestItem()
            val dummy : DummyDatapool = DummyDatapool()
            var result = test.put("test.request", 10, datapool = dummy, 0, mutableListOf<String>("2"))
            assertThatJson(result).isObject().containsEntry("result","FAILED")
            assertThatJson(result).isObject().containsEntry("error_code","WRITINGNOTALLOWED")
        }

        @Test
        fun testSufficientReadRights() {
            var test : ReadWriteTestItem = ReadWriteTestItem()
            val dummy : DummyDatapool = DummyDatapool()
            var result = test.get("test.request", datapool = dummy, userlevel=20,mutableListOf<String>("2"))
            assertThatJson(result).isObject().containsEntry("result","OK")
            assertThatJson(result).isObject().containsEntry("human_readable_value","20")
        }

        @Test
        fun testSufficientWriteRights() {
            var test : ReadWriteTestItem = ReadWriteTestItem()
            val dummy : DummyDatapool = DummyDatapool()
            var result = test.put("test.request", 10, datapool = dummy, userlevel = 20, mutableListOf<String>("2"))
            assertThatJson(result).isObject().containsEntry("result","OK")
        }

    }

}