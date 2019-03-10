package com.suushiemaniac.cubing.bld.filter.condition

data class IntegerCondition(private var min: Int, private var max: Int) : BaseConditional<Int> {
    val isPrecise: Boolean
        get() = this.min == this.max

    val interval: Int
        get() = this.max - this.min

    val average: Float
        get() = (this.min + this.max) / 2f

    fun getMin() = this.min
    fun getMax() = this.max

    fun capMin(minCap: Int) {
        if (this.min < minCap) this.setMin(minCap)
    }

    fun capMax(maxCap: Int) {
        if (this.max > maxCap) this.setMax(maxCap)
    }

    override fun evaluate(compareTo: Int): Boolean {
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

    override fun toString() = this.toString(":")
    fun toString(delim: String) = "${this.min}$delim${this.max}"

    companion object {
        fun EXACT(point: Int) = IntegerCondition(point, point)
        fun POINT(point: Int) = IntegerCondition.EXACT(point)

        fun PLUSMINUS(point: Int, plusMinus: Int) = IntegerCondition(point - plusMinus, point + plusMinus)

        fun INTERVAL(min: Int, max: Int) = IntegerCondition(min, max)
        fun BETWEEN(min: Int, max: Int) = IntegerCondition.INTERVAL(min, max)

        fun MINIMUM(min: Int) = IntegerCondition(min, Integer.MAX_VALUE)
        fun MIN(min: Int) = IntegerCondition.MINIMUM(min)

        fun MAXIMUM(max: Int) = IntegerCondition(Integer.MIN_VALUE, max)
        fun MAX(max: Int) = IntegerCondition.MAXIMUM(max)

        fun ANY() = IntegerCondition(Integer.MIN_VALUE, Integer.MAX_VALUE)

        fun NONE() = IntegerCondition.EXACT(0)

        fun STRICT_MAX(point: Int, isStrict: Boolean = false) = if (isStrict) EXACT(point) else MAX(point)
        fun STRICT_MIN(point: Int, isStrict: Boolean = false) = if (isStrict) EXACT(point) else MIN(point)
    }
}
