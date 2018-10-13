package com.suushiemaniac.cubing.bld.filter.condition

data class IntCondition(private var min: Int, private var max: Int) {
    val isPrecise: Boolean
        get() = this.min == this.max

    val interval: Int
        get() = this.max - this.min

    val average: Float
        get() = (this.min + this.max) / 2f

    fun getMin(): Int {
        return this.min
    }

    fun getMax(): Int {
        return this.max
    }

    fun capMin(minCap: Int) {
        if (this.min < minCap) this.setMin(minCap)
    }

    fun capMax(maxCap: Int) {
        if (this.max > maxCap) this.setMax(maxCap)
    }

    fun evaluate(compareTo: Int): Boolean {
        return this.min <= compareTo && compareTo <= this.max
    }

    fun setMax(max: Int) {
        this.max = max
        if (this.min > max) this.min = max
    }

    fun setMin(min: Int) {
        this.min = min
        if (this.max < min) this.max = min
    }

    override fun toString(): String {
        return this.toString(":")
    }

    fun toString(delim: String): String {
        return this.min.toString() + delim + this.max.toString()
    }

    companion object {
        fun EXACT(point: Int): IntCondition {
            return IntCondition(point, point)
        }

        fun POINT(point: Int): IntCondition {
            return IntCondition.EXACT(point)
        }

        fun PLUSMINUS(point: Int, plusMinus: Int): IntCondition {
            return IntCondition(point - plusMinus, point + plusMinus)
        }

        fun INTERVAL(min: Int, max: Int): IntCondition {
            return IntCondition(min, max)
        }

        fun BETWEEN(min: Int, max: Int): IntCondition {
            return IntCondition.INTERVAL(min, max)
        }

        fun MINIMUM(min: Int): IntCondition {
            return IntCondition(min, Integer.MAX_VALUE)
        }

        fun MIN(min: Int): IntCondition {
            return IntCondition.MINIMUM(min)
        }

        fun MAXIMUM(max: Int): IntCondition {
            return IntCondition(Integer.MIN_VALUE, max)
        }

        fun MAX(max: Int): IntCondition {
            return IntCondition.MAXIMUM(max)
        }

        fun ANY(): IntCondition {
            return IntCondition(Integer.MIN_VALUE, Integer.MAX_VALUE)
        }

        fun NONE(): IntCondition {
            return IntCondition.EXACT(0)
        }
    }
}
