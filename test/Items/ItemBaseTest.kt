package sunhill.Items

import junit.framework.Assert.assertNull
import junit.framework.TestCase.*
import org.junit.Test
import net.javacrumbs.jsonunit.assertj.assertThatJson

class ItemBaseTest {

    class ReadOnlyTestItem : ItemBase("test.request","d","uptime","Integer","asap") {

        override fun calculateValue(additional: MutableList<String>): Any
        {
            return 1000
        }

        fun errorCondition() {
            setError("TESTERROR","This is a test error")
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

    class ReadOnlyItemTest {

        @Test
        fun testProvidesItemPass()
        {
            val test = ReadOnlyTestItem()
            assertTrue(test.providesItem("test.request"))
        }

        @Test
        fun testProvidesItemFail()
        {
            val test = ReadOnlyTestItem()
            assertFalse(test.providesItem("not.existing.request"))
        }

        @Test
        fun testSimpleGet() {
            val test = ReadOnlyTestItem()
            val result = test.getItem("test.request")!!
            assertEquals("test.request",result.request)
            assertEquals("16 minutes 40 seconds",result.human_readable_value)
        }

        @Test
        fun testError()
        {
            val test = ReadOnlyTestItem()
            test.errorCondition()
            assertNull(test.getItem("test.request"))
            assertEquals("TESTERROR",test.getErrorCode())
            assertEquals("This is a test error",test.getErrorMessage())
            assertEquals("TESTERROR",test.getError()!!.code)
        }

        @Test
        fun testWriteError() {
            var test = ReadOnlyTestItem()
            test.setItem("test.request", "ABC")

            assertEquals("ITEMNOTWRITEABLE",  test.getErrorCode())
        }

        @Test
        fun testGetItemValue() {
            val test = ReadOnlyTestItem()
            val result = test.getItemValue("test.request")
            assertEquals(1000, result)
        }

        @Test
        fun testGetHRValue() {
            val test = ReadOnlyTestItem()
            val result = test.getItemHumanReadableValue("test.request")
            assertEquals("16 minutes 40 seconds", result)
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
            assertEquals(mutableListOf<String>("test.count","test.0.request","test.1.request"),result)
        }

        @Test
        fun testGetCountSimple()
        {
            val test = IndexedTestItem()
            assertEquals(2,test.getItemValue("test.count"))
        }

        @Test
        fun testAddOfferings5()
        {
            val test = MultiIndexedTestItem()
            val result = mutableListOf<String>();
            test.addOfferings("*",result)
            assertEquals(mutableListOf<String>(
                "test.count",
                "test.0.request.count",
                "test.0.request.0.item",
                "test.0.request.1.item",
                "test.1.request.count",
                "test.1.request.0.item",
                "test.1.request.1.item",
                "test.1.request.2.item",
            ),result)
        }

        @Test
        fun testGetCountMultiindex1()
        {
            val test = MultiIndexedTestItem()
            assertEquals(2,test.getItemValue("test.count"))
        }

        @Test
        fun testGetCountMultiindex2()
        {
            val test = MultiIndexedTestItem()
            assertEquals(2,test.getItemValue("test.0.request.count"))
            assertEquals(3,test.getItemValue("test.1.request.count"))
        }

    }

    class WriteOnlyItemTest {

        @Test
        fun testReadError() {
            var test = WriteOnlyTestItem()
            var result = test.getItem("test.request")
            assertNull(result)
            assertEquals("ITEMNOTREADABLE",test.getErrorCode())
        }

        @Test
        fun testWrite() {
            val test = WriteOnlyTestItem()
            val result = test.setItem("test.request", 10)
            assertNull(result)
        }

    }

    class ReadWriteItemTest {

        @Test
        fun testInsufficientReadRights() {
            var test = ReadWriteTestItem()
            var result = test.getItem("test.request", 0)
            assertNull(result)
            assertEquals("ITEMNOTREADABLETOUSER",test.getErrorCode())
        }

        @Test
        fun testInsufficientWriteRights() {
            var test = ReadWriteTestItem()
            var result = test.setItem("test.request", 10, 0)
            assertFalse(result == null)
            if (result is ItemError) {
                assertEquals(result.code,"WRITINGNOTALLOWED")
            }
        }

        @Test
        fun testSufficientReadRights() {
            var test = ReadWriteTestItem()
            var result = test.getItem("test.request",  userlevel=20)
            if (result is ItemData) {
                assertEquals(result.human_readable_value, "20")
            }
        }

        @Test
        fun testSufficientWriteRights() {
            var test = ReadWriteTestItem()
            var result = test.setItem("test.request", 10, userlevel = 20)
            assertTrue(result == null)
        }

    }

}
