package sunhill.Marketeers

import io.mockk.every
import io.mockk.spyk
import junit.framework.Assert.assertNull
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.UptimeDatapool
import sunhill.Items.ItemBase
import sunhill.marketeers.MarketeerBase

class MarketeerBaseTest {

    class DummyItem1 : ItemBase("test.item"," ", semantic_int = "name", type = "String", "asap")
    {
        override fun calculateValue(additional: MutableList<String>): Any?
        {
            return "ABC"
        }
    }

    class DummyItem2: ItemBase("test.?.thing"," ", semantic_int = "name", type = "String", "asap")
    {
        override fun calculateValue(additional: MutableList<String>): Any?
        {
            return "DEF"+additional[0]
        }

        override fun getPermutation(parts: MutableList<String>,index: Int): List<String>?
        {
            return listOf("a","b")
        }

    }

    class DummyItem3: ItemBase("another.test.item"," ", semantic_int = "name", type = "String", "asap")
    {
        override fun calculateValue(additional: MutableList<String>): Any?
        {
            return "GHI"
        }

    }

    class DummyItem4: ItemBase("we.test.?.another.?.item"," ", semantic_int = "name", type = "String", "asap")
    {
        override fun calculateValue(additional: MutableList<String>): Any?
        {
            return "JKL"+additional[0]+"M"
        }

        override fun getPermutation(parts: MutableList<String>,index: Int): List<String>? {
            if (index == 0) {
                return listOf("A", "B")
            } else if (parts[2].equals("A")) {
                return listOf("0", "1")
            } else {
                return listOf("0", "1", "2")
            }
        }

    }

    class TestMarketeer : ItemMarketeerBase() {

        override fun getRegisteredItemList(): List<ItemBase>
        {
            return listOf(
                DummyItem1(),
                DummyItem2(),
                DummyItem3(),
                DummyItem4()
            )
        }

    }


    @Test
    fun testMatchPass1()
    {
        val test = TestMarketeer()
        assertEquals("""{"value":"ABC"}""",test.getValue("test.item"))
    }

    @Test
    fun testMatchPass2()
    {
        val test = TestMarketeer()
        assertEquals("""{"value":"DEFa"}""",test.getValue("test.a.thing"))
    }

    @Test
    fun testMatchFail()
    {
        val test = TestMarketeer()
        assertNull(test.getItem("non.existing.path"))
    }

    data class OfferingMatches(val test: String, val expect: MutableList<String>)

    @Test
    fun testGetOffering()
    {
        val list = OfferingProvider()
        val test = TestMarketeer()
        list.forEach {
            var result = test.getOffering(it.test)
            assertTrue(result.containsAll(it.expect)) // Note: The ordering might be different depending on implementation
        }
    } 
   
    fun OfferingProvider(): Array<OfferingMatches>
    {
        return arrayOf(
            OfferingMatches("test.*",mutableListOf("test.item","test.count","test.a.thing","test.b.thing")),
            OfferingMatches("*",mutableListOf(
                "test.item",
                "test.count",
                "test.a.thing",
                "test.b.thing",
                "another.test.item",
                "we.test.count",
                "we.test.A.another.count",
                "we.test.A.another.0.item",
                "we.test.A.another.1.item",
                "we.test.B.another.count",
                "we.test.B.another.0.item",
                "we.test.B.another.1.item",
                "we.test.B.another.2.item",
            )),
            OfferingMatches("test.*.item",mutableListOf("test.a.thing","test.b.thing"))
        )
    }

}
