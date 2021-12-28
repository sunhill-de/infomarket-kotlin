package sunhill.Marketeers

import junit.framework.Assert.assertEquals
import org.junit.Test
import sunhill.Items.ItemBase
import sunhill.marketeers.MarketeerBase

class ProxyMarketeerTest {

    class dummyItem: ItemBase("test.item",unit_int = " ", semantic_int = " ", "String", update = "asap") {

        override fun calculateValue(additional: MutableList<String>): Any? {
            return "ABC"
        }
    }

    class testMarketeer: MarketeerBase() {

        override fun searchItem(search: String, userlevel: Int): SearchResult?
        {
            if (search.equals("test.item")) {
                return SearchResult(dummyItem(),mutableListOf())
            } else {
                return null
            }
        }

        /**
         * Just pass the Offering of the target with the prepended prefix
         */
        override fun getOffering(search: String): MutableList<String>
        {
            return mutableListOf("test.item")
        }

    }

    @Test
    fun testDummy()
    {
        val test = dummyItem()
        assertEquals("""{"value":"ABC"}""",test.getValue("test.item"))
    }

    @Test
    fun testProxy()
    {
        val target = testMarketeer()
        val test = ProxyMarketeer("proxy",target)

        assertEquals("""{"value":"ABC"}""", test.getValue("proxy.test.item"))
    }
}