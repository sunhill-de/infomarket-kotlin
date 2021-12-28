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

    class DummyItem1 : ItemBase("test.item"," ", semantic_int = "count", type = "Integer", "asap")
    {
        override fun get(request: String, userlevel: Int, additional: MutableList<String>): String
        {
            return "ABC"
        }
    }

    class DummyItem2: ItemBase("test.?.thing"," ", semantic_int = "count", type = "Integer", "asap")
    {
        override fun get(request: String, userlevel: Int, additional: MutableList<String>): String
        {
            return "DEF"+additional[0]
        }
    }

    class DummyItem3: ItemBase("another.test.item"," ", semantic_int = "count", type = "Integer", "asap")
    {
        override fun get(request: String, userlevel: Int, additional: MutableList<String>): String
        {
            return "GHI"
        }
    }

    class TestMarketeer : ItemMarketeerBase() {

        override fun getRegisteredItemList(): List<ItemBase>
        {
            return listOf(
                DummyItem1(),
                DummyItem2(),
                DummyItem3()
            )
        }

    }


    @Test
    fun testMatchPass1()
    {
        val test = TestMarketeer()
        assertEquals("ABC",test.getItem("test.item"))
    }

    @Test
    fun testMatchPass2()
    {
        val test = TestMarketeer()
        assertEquals("DEFa",test.getItem("test.a.thing"))
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
            var result = mutableListOf<String>()            
            test.getOffering(it.test)
            assertTrue(result.containsAll(it.expect)) // Note: The ordering might be different depending on implementation
        }
    } 
   
    fun OfferingProvider(): Array<OfferingMatches>
    {
        return arrayOf(
            OfferingMatches("test.*",mutableListOf("test.item","test.count","test.a.thing","test.b.thing")),
            OfferingMatches("*",mutableListOf("test.item","test.count","test.a.thing","test.b.thing","another.test.item")),
            OfferingMatches("test.?.item",mutableListOf("test.a.thing","test.b.thing"))
        )
    }    
}
