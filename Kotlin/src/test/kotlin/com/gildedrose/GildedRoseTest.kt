package com.gildedrose

import com.gildedrose.GildedRoseTest.ItemBuilder.Companion.DEFAULT_QUALITY
import com.gildedrose.GildedRoseTest.ItemBuilder.Companion.tickets
import com.gildedrose.GildedRoseTest.ItemBuilder.Companion.cheese
import com.gildedrose.GildedRoseTest.ItemBuilder.Companion.regular
import com.gildedrose.GildedRoseTest.ItemBuilder.Companion.sulfuras
import org.junit.Assert.*
import org.junit.Test

class GildedRoseTest {

    sealed class ItemBuilder(private var name: String, private var sellIn: Int = DEFAULT_SELLIN, private var quality: Int = DEFAULT_QUALITY) {

        companion object {
            const val SULFURAS = "Sulfuras, Hand of Ragnaros"
            const val TICKET = "Backstage passes to a TAFKAL80ETC concert"
            const val CHEESE = "Aged Brie"
            const val REGULAR = "foo"

            const val DEFAULT_SELLIN = 10
            const val DEFAULT_QUALITY = 10

            val sulfuras = Sulfuras()
            val cheese = Cheese()
            val tickets = Tickets()
            val regular = Regular()
        }

        class Sulfuras : ItemBuilder(SULFURAS, 0, 80)
        class Tickets : ItemBuilder(TICKET)
        class Cheese : ItemBuilder(CHEESE)
        class Regular : ItemBuilder(REGULAR)

        fun item() = build()
        fun outdated() = build().apply { sellIn = 0 }
        fun highQuality() = build().apply { quality = 50 }
        fun noQuality() = build().apply { quality = 0 }
        fun quality(input: Int) = build().apply { quality = input }
        fun sellIn(input: Int) = build().apply { sellIn = input }

        private fun build() = Item(name, sellIn, quality)
    }

    // tests regarding quality

    @Test
    fun ticketsShouldNotExceed50InQuality() {
        checkItemQuality(50, tickets.highQuality())
    }

    @Test
    fun ticketsShouldDropToZeroAfterSellInReached() {
        checkItemQuality(0, tickets.outdated())
    }

    @Test
    fun ticketsShouldIncrease_Stage3() {
        checkItemQuality(DEFAULT_QUALITY + 3, tickets.sellIn(5))
    }

    @Test
    fun ticketsShouldIncrease_Stage3HighQualityKeeps50() {
        checkItemQuality(50, tickets.sellIn(5).apply { quality = 50 })
    }

    @Test
    fun ticketsShouldIncrease_Stage2() {
        checkItemQuality(DEFAULT_QUALITY + 2, tickets.item())
    }

    @Test
    fun ticketsShouldIncrease_Stage2HighQualityKeeps50() {
        checkItemQuality(50, tickets.item().apply { quality = 50 })
    }

    @Test
    fun ticketsShouldIncrease_Stage1() {
        checkItemQuality(DEFAULT_QUALITY + 1, tickets.sellIn(11))
    }

    // cheese

    @Test
    fun cheeseShouldNotExceed50InQualityOutdated() {
        checkItemQuality(50, cheese.highQuality().apply { sellIn = -1 })
    }

    @Test
    fun cheeseShouldNotExceed50InQuality2() {
        checkItemQuality(50, cheese.quality(49))
    }

    @Test
    fun cheeseShouldNotExceed50InQuality2Outdated() {
        checkItemQuality(50, cheese.quality(48).apply { sellIn = -1 })
    }

    @Test
    fun cheeseShouldNotExceed50InQuality3Outdated() {
        checkItemQuality(50, cheese.quality(49).apply { sellIn = -1 })
    }


    @Test
    fun regularShouldKeepZeroQualityEvenWhenOutdated() {
        checkItemQuality(0, regular.noQuality().apply { sellIn = 0 })
    }

    @Test
    fun regularShouldKeepZeroQuality() {
        checkItemQuality(0, regular.noQuality())
    }

    @Test
    fun regularShouldGiveDecreasedQuality() {
        checkItemQuality(DEFAULT_QUALITY - 1, regular.item())
    }

    @Test
    fun regularShouldGiveDecreasedQualityEvenWhenOutdated() {
        checkItemQuality(DEFAULT_QUALITY - 2, regular.outdated())
    }

    @Test
    fun sulfurasShouldNotIncrease() {
        checkItemQuality(80, sulfuras.item().apply { sellIn = -1 })
    }

    // tests regarding sellIn

    @Test
    fun shouldGiveDecreasedSellIn() {
        val item = regular.item()
        val oldSellIn = item.sellIn
        GildedRose(arrayOf(item)).updateQuality()
        assertEquals(oldSellIn - 1, item.sellIn)
    }

    @Test
    fun shouldGiveNoDecreasedSellInfForSulfuras() {
        val item = sulfuras.item()
        val oldSellIn = item.sellIn
        GildedRose(arrayOf(item)).updateQuality()
        assertEquals(oldSellIn, item.sellIn)
    }


    // helpers

    private fun checkItemQuality(expectedQuality: Int, item: Item) {
        val items = arrayOf(item)
        GildedRose(items).updateQuality()
        assertEquals(expectedQuality, item.quality)
    }

}


