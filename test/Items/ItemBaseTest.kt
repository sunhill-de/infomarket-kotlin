package sunhill.Items

import junit.framework.TestCase.assertEquals
import org.junit.Test
import net.javacrumbs.jsonunit.assertj.assertThatJson
import sunhill.DataPool.DataPoolBase
import sunhill.Marketeers.MarketeerBaseTest

class ReadOnlyTestItem : ItemBase("test.request","d","uptime","Integer","asap") {

    override fun calculateValue(additional: MutableList<String>): Any
    {
        return 1000
    }

}

class WriteOnlyTestItem : ItemBase("test.request","d","uptime","Integer","asap", readable_to = -1, writeable_to = 0) {

    override fun calculateValue(additional: MutableList<String>): Any? {
        return "ABC"
    }


}

class ReadWriteTestItem : ItemBase("test.request"," ","count","Integer","asap", readable_to = 10, writeable_to = 10) {

    override fun calculateValue(additional: MutableList<String>): Any
    {
        return (additional[0].toInt())*10
    }

}

class IndexedTestItem : ItemBase("test.#.request"," ","count","Integer","asap") {

    override fun calculateValue(additional: MutableList<String>): Any
    {
        return (additional[0].toInt())*10
    }

    override fun getPermutation(parts: MutableList<String>,index: Int): List<String>?
    {
        return listOf("0","1")
    }

}

class MultiIndexedTestItem : ItemBase("test.#.request.#.item"," ","count","Integer","asap") {

    override fun calculateValue(additional: MutableList<String>): Any
    {
        return additional[0].toInt()*additional[1].toInt()
    }

    override fun getPermutation(parts: MutableList<String>,index: Int): List<String>?
    {
        if (index == 0) {
            return listOf("0","1")
        } else if (parts[1].equals("0")) {
            return listOf("0","1")
        } else {
            return listOf("0","1","2")
        }
    }
}

class ItemBaseTest {

    class ReadOnlyItemTest {

        @Test
        fun testSimpleGet() {
            val test = ReadOnlyTestItem()
            val result = test.get("test.request")
            assertThatJson(result).isObject().containsEntry("request","test.request")
            assertThatJson(result).isObject().containsEntry("human_readable_value","16 minutes 40 seconds")
            assertThatJson(result).isObject().containsEntry("result","OK")
        }

        @Test
        fun testWriteError() {
            var test = ReadOnlyTestItem()
            var result = test.put("test.request", "ABC")
            assertThatJson(result).isObject().containsEntry("result","FAILED")
            assertThatJson(result).isObject().containsEntry("error_code","ITEMNOTWRITEABLE")
        }

        @Test
        fun testGetValue() {
            val test = ReadOnlyTestItem()
            val result = test.getValue("test.request")
            assertEquals("{\"value\":1000}", result)
        }

        @Test
        fun testGetHRValue() {
            val test = ReadOnlyTestItem()
            val result = test.getHRValue("test.request")
            assertEquals("{\"human_readable_value\":\"16 minutes 40 seconds\"}", result)
        }

        @Test
        fun testOfferMatches()
        {
            val list = OfferMatchesProvider()
            list.forEach {
                val test = ItemBase(it.provider," ", " ", "Integer", "asap")
                var variables = mutableListOf<String>()
                assertEquals(it.match, test.matches(it.test,0,variables))
                if (it.match) {
                    assertEquals(variables, it.variables)
                }
            }
        }

        data class OfferMatches(val test: String, val provider: String, val variables: MutableList<String>, val match: Boolean)

        fun OfferMatchesProvider(): Array<OfferMatches>
        {
            return arrayOf(
                OfferMatches("this.is.a.test", "this.is.a.test", mutableListOf<String>(), true),
                OfferMatches(
                    "this.is.a.test",
                    "this.is.another.test",
                    mutableListOf<String>(),
                    false
                ),
                OfferMatches("this.is.a.test", "this.is.a", mutableListOf<String>(), false),
                OfferMatches("this.is.a", "this.is.a.test", mutableListOf<String>(), false),
                OfferMatches("this.is.a.test", "this.is.?.test", mutableListOf<String>("a"), true),
                OfferMatches("this.is.a.test", "this.is.?.testing", mutableListOf<String>(), false),
                OfferMatches("this.is.a.test", "this.is.#.test", mutableListOf<String>(), false),
                OfferMatches("this.is.1.test", "this.is.#.test", mutableListOf<String>("1"), true),
                OfferMatches(
                    "this.is.a.test",
                    "this.?.a.?",
                    mutableListOf<String>("is", "test"),
                    true
                ),
                OfferMatches("this.is.a", "this.is.?.test", mutableListOf<String>(), false),
                OfferMatches("this.is.a.test", "this.is.*", mutableListOf<String>("a.test"), true),
                OfferMatches("another.test.item", "another.test.item", mutableListOf<String>(), true), // Reduntant rest!
            )
        }

        @Test
        fun testAddOfferings()
        {
            val test = ReadOnlyTestItem()
            val result = mutableListOf<String>();
            test.addOfferings("*",result)
            assertEquals(mutableListOf<String>("test.request"),result)
        }

        @Test
        fun testAddOfferings2()
        {
            val test = ReadOnlyTestItem()
            val result = mutableListOf<String>();
            test.addOfferings("test.*",result)
            assertEquals(mutableListOf<String>("test.request"),result)
        }

        @Test
        fun testAddOfferings3()
        {
            val test = ReadOnlyTestItem()
            val result = mutableListOf<String>();
            test.addOfferings("something.*",result)
            assertEquals(mutableListOf<String>(),result)
        }

        @Test
        fun testAddOfferings4()
        {
            val test = IndexedTestItem()
            val result = mutableListOf<String>();
            test.addOfferings("*",result)
            assertEquals(mutableListOf<String>("test.0.request","test.1.request"),result)
        }

        @Test
        fun testAddOfferings5()
        {
            val test = MultiIndexedTestItem()
            val result = mutableListOf<String>();
            test.addOfferings("*",result)
            assertEquals(mutableListOf<String>(
                "test.0.request.0.item",
                "test.0.request.1.item",
                "test.1.request.0.item",
                "test.1.request.1.item",
                "test.1.request.2.item",
            ),result)
        }

    }

    class WriteOnlyItemTest {

        @Test
        fun testReadError() {
            var test = WriteOnlyTestItem()
            var result = test.get("test.request")
            assertThatJson(result).isObject().containsEntry("result","FAILED")
            assertThatJson(result).isObject().containsEntry("error_code","ITEMNOTREADABLE")
        }

        @Test
        fun testWrite() {
            val test = WriteOnlyTestItem()
            val result = test.put("test.request", 10)
            assertThatJson(result).isObject().containsEntry("result","OK")
        }

    }

    class ReadWriteItemTest {

        @Test
        fun testInsufficientReadRights() {
            var test = ReadWriteTestItem()
            var result = test.get("test.request", 0, mutableListOf<String>("2"))
            assertThatJson(result).isObject().containsEntry("result","FAILED")
            assertThatJson(result).isObject().containsEntry("error_code","READINGNOTALLOWED")
        }

        @Test
        fun testInsufficientWriteRights() {
            var test = ReadWriteTestItem()
            var result = test.put("test.request", 10, 0, mutableListOf<String>("2"))
            assertThatJson(result).isObject().containsEntry("result","FAILED")
            assertThatJson(result).isObject().containsEntry("error_code","WRITINGNOTALLOWED")
        }

        @Test
        fun testSufficientReadRights() {
            var test = ReadWriteTestItem()
            var result = test.get("test.request",  userlevel=20,mutableListOf<String>("2"))
            assertThatJson(result).isObject().containsEntry("result","OK")
            assertThatJson(result).isObject().containsEntry("human_readable_value","20")
        }

        @Test
        fun testSufficientWriteRights() {
            var test = ReadWriteTestItem()
            var result = test.put("test.request", 10, userlevel = 20, mutableListOf<String>("2"))
            assertThatJson(result).isObject().containsEntry("result","OK")
        }

    }

}