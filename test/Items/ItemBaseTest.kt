package sunhill.Items

import junit.framework.TestCase.assertEquals
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
            val test = ReadOnlyTestItem()
            val dummy = DummyDatapool()
            val result = test.get("test.request", dummy)
            assertThatJson(result).isObject().containsEntry("request","test.request")
            assertThatJson(result).isObject().containsEntry("human_readable_value","16 minutes 40 seconds")
            assertThatJson(result).isObject().containsEntry("result","OK")
        }

        @Test
        fun testWriteError() {
            var test = ReadOnlyTestItem()
            val dummy = DummyDatapool()
            var result = test.put("test.request", "ABC", datapool = dummy)
            assertThatJson(result).isObject().containsEntry("result","FAILED")
            assertThatJson(result).isObject().containsEntry("error_code","ITEMNOTWRITEABLE")
        }

        @Test
        fun testGetValue() {
            val test = ReadOnlyTestItem()
            val dummy = DummyDatapool()
            val result = test.getValue("test.request", dummy)
            assertEquals("{\"value\":1000}", result)
        }

        @Test
        fun testGetHRValue() {
            val test = ReadOnlyTestItem()
            val dummy = DummyDatapool()
            val result = test.getHRValue("test.request", dummy)
            assertEquals("{\"human_readable_value\":\"16 minutes 40 seconds\"}", result)
        }
    }

    class WriteOnlyItemTest {

        @Test
        fun testReadError() {
            var test = WriteOnlyTestItem()
            val dummy = DummyDatapool()
            var result = test.get("test.request", datapool = dummy)
            assertThatJson(result).isObject().containsEntry("result","FAILED")
            assertThatJson(result).isObject().containsEntry("error_code","ITEMNOTREADABLE")
        }

        @Test
        fun testWrite() {
            val test = WriteOnlyTestItem()
            val dummy = DummyDatapool()
            val result = test.put("test.request", 10, datapool = dummy)
            assertThatJson(result).isObject().containsEntry("result","OK")
        }

    }

    class ReadWriteItemTest {

        @Test
        fun testInsufficientReadRights() {
            var test = ReadWriteTestItem()
            val dummy = DummyDatapool()
            var result = test.get("test.request", datapool = dummy,0, mutableListOf<String>("2"))
            assertThatJson(result).isObject().containsEntry("result","FAILED")
            assertThatJson(result).isObject().containsEntry("error_code","READINGNOTALLOWED")
        }

        @Test
        fun testInsufficientWriteRights() {
            var test = ReadWriteTestItem()
            val dummy = DummyDatapool()
            var result = test.put("test.request", 10, datapool = dummy, 0, mutableListOf<String>("2"))
            assertThatJson(result).isObject().containsEntry("result","FAILED")
            assertThatJson(result).isObject().containsEntry("error_code","WRITINGNOTALLOWED")
        }

        @Test
        fun testSufficientReadRights() {
            var test = ReadWriteTestItem()
            val dummy = DummyDatapool()
            var result = test.get("test.request", datapool = dummy, userlevel=20,mutableListOf<String>("2"))
            assertThatJson(result).isObject().containsEntry("result","OK")
            assertThatJson(result).isObject().containsEntry("human_readable_value","20")
        }

        @Test
        fun testSufficientWriteRights() {
            var test = ReadWriteTestItem()
            val dummy = DummyDatapool()
            var result = test.put("test.request", 10, datapool = dummy, userlevel = 20, mutableListOf<String>("2"))
            assertThatJson(result).isObject().containsEntry("result","OK")
        }

    }

}