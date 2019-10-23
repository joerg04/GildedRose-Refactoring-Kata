package com.gildedrose

class GildedRose(private var items: Array<Item>) {

    companion object {
        const val SULFURAS = "Sulfuras, Hand of Ragnaros"
        const val CHEESE = "Aged Brie"
        const val TICKETS = "Backstage passes to a TAFKAL80ETC concert"
    }

    fun updateQuality() = items.map(::intoWrapper).forEach(::updateItemQuality)

    private fun updateItemQuality(item: ItemWrapper) {

        item.updateSellIn()
        item.updateQuality()

        if (item.isOutdated()) {
            item.updateQualityAfterOutdated()
        }
    }

    private fun intoWrapper(item: Item) = when (item.name) {
        CHEESE -> Cheese(item)
        TICKETS -> Tickets(item)
        SULFURAS -> Sulfuras(item)
        else -> Regular(item)
    }
}

interface ItemWrapper {
    val internal: Item

    fun updateSellIn()

    fun isOutdated(): Boolean

    fun updateQuality()

    fun updateQualityAfterOutdated()
}

abstract class BaseItemWrapper(override val internal: Item) : ItemWrapper {

    companion object {
        const val MAX_QUALITY = 50
        const val MIN_QUALITY = 0
        const val DELTA_QUALITY = 1
    }

    override fun updateSellIn() {
        internal.sellIn -= 1
    }

    final override fun isOutdated() = internal.sellIn < 0

    override fun updateQuality() { // nOP
    }

    override fun updateQualityAfterOutdated() { // nOP
    }

    protected fun increaseQuality() {
        if (internal.quality < MAX_QUALITY) {
            internal.quality += DELTA_QUALITY
        }
    }

    protected fun decreaseQuality() {
        if (internal.quality > MIN_QUALITY) {
            internal.quality -= DELTA_QUALITY
        }
    }
}

class Sulfuras(override val internal: Item) : BaseItemWrapper(internal) {
    override fun updateSellIn() { /* nOP is legendary item */
    }
}

class Regular(override val internal: Item) : BaseItemWrapper(internal) {
    override fun updateQuality() = decreaseQuality()
    override fun updateQualityAfterOutdated() = decreaseQuality()
}

class Cheese(override val internal: Item) : BaseItemWrapper(internal) {
    override fun updateQuality() = increaseQuality()
    override fun updateQualityAfterOutdated() = increaseQuality()
}

class Tickets(override val internal: Item) : BaseItemWrapper(internal) {
    override fun updateQuality() {
        var sellInGaps = internal.sellIn
        while (sellInGaps < 15) {
            increaseQuality()
            sellInGaps += 5
        }
    }

    override fun updateQualityAfterOutdated() {
        internal.quality = MIN_QUALITY
    }
}


