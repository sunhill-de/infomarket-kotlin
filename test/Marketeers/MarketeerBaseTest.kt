package sunhill.Marketeers

import io.mockk.every
import io.mockk.spyk
import junit.framework.Assert.assertNull
import junit.framework.TestCase.assertEquals
import org.junit.Test
import sunhill.DataPool.DataPoolBase
import sunhill.DataPool.UptimeDatapool
import sunhill.Items.ItemBase
import sunhill.marketeers.MarketeerBase

class DummyItem1 : ItemBase(" ", semantic_int = "count", type = "Integer", "asap")
{
    override fun get(request: String, datapool: DataPoolBase?, userlevel: Int, additional: MutableList<String>): String
    {
        return "ABC"
    }
}

class DummyItem2: ItemBase(" ", semantic_int = "count", type = "Integer", "asap")
{
    override fun get(request: String, datapool: DataPoolBase?, userlevel: Int, additional: MutableList<String>): String
    {
        return "DEF"+additional[0]
    }
}

class TestMarketeer : MarketeerBase() {

    override fun getRegisteredItemList(): Map<String, out ItemBase>
    {
        return mapOf(
            "test.item" to DummyItem1(),
            "test.?.thing" to DummyItem2()
        )
    }

    override fun getDatapool(): DataPoolBase {
        return DataPoolBase()
    }

    fun testMatch(test: String, offer: String, variables :MutableList<String>): Boolean
    {
        var found_vars = mutableListOf<String>()
        val result = offerMatches(test,offer,found_vars)
        if (result) {
            assertEquals(variables,found_vars)
        }
        return result
    }
}

data class OfferMatches(val test: String, val provider: String, val variables: MutableList<String>, val match: Boolean)

class MarketeerBaseTest {

    @Test
    fun testOfferMatches()
    {
        val list = OfferMatchesProvider()
        val test = TestMarketeer()
        list.forEach {
            assertEquals(it.match, test.testMatch(it.test,it.provider,it.variables))
        }
    }

    fun OfferMatchesProvider(): Array<OfferMatches>
    {
        return arrayOf(
            OfferMatches("this.is.a.test","this.is.a.test", mutableListOf<String>(),true),
            OfferMatches("this.is.a.test","this.is.another.test", mutableListOf<String>(),false),
            OfferMatches("this.is.a.test","this.is.a", mutableListOf<String>(),false),
            OfferMatches("this.is.a","this.is.a.test", mutableListOf<String>(),false),
            OfferMatches("this.is.a.test","this.is.?.test", mutableListOf<String>("a"),true),
            OfferMatches("this.is.a.test","this.is.?.testing", mutableListOf<String>(),false),
            OfferMatches("this.is.a.test","this.is.#.test", mutableListOf<String>(),false),
            OfferMatches("this.is.1.test","this.is.#.test", mutableListOf<String>("1"),true),
            OfferMatches("this.is.a.test","this.?.a.?", mutableListOf<String>("is","test"),true),
            OfferMatches("this.is.a","this.is.?.test",mutableListOf<String>(),false),
            OfferMatches("this.is.a.test","this.is.*",mutableListOf<String>("a.test"),true),
        )
    }

    @Test
    fun testMatchPass1()
    {
        val test = TestMarketeer()
        assertEquals("ABC",test.getOffer("test.item"))
    }

    @Test
    fun testMatchPass2()
    {
        val test = TestMarketeer()
        assertEquals("DEFa",test.getOffer("test.a.thing"))
    }

    @Test
    fun testMatchFail()
    {
        val test = TestMarketeer()
        assertNull(test.getOffer("non.existing.path"))
    }

}