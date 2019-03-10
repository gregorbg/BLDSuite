package com.suushiemaniac.cubing.bld.filter.condition

data class BooleanCondition(private var value: Boolean, val isImportant: Boolean) : BaseConditional<Boolean> {
    val positive: Boolean
        get() = !this.isImportant || this.value

    val negative: Boolean
        get() = this.isImportant && this.value

    fun evaluatePositive(compareTo: Boolean): Boolean {
        return !this.isImportant || this.value == compareTo
    }

    fun evaluateNegative(compareTo: Boolean): Boolean {
        return this.isImportant && this.value == compareTo
    }

    override fun evaluate(compareTo: Boolean): Boolean {
        return this.evaluatePositive(compareTo)
    }

    fun define(value: Boolean) {
        this.value = value
    }

    companion object {
        fun YES() = BooleanCondition(true, true)

        fun NO(): BooleanCondition = BooleanCondition(false, true)

        fun UNIMPORTANT() = BooleanCondition(false, false)
        fun MAYBE() = BooleanCondition.UNIMPORTANT()

        fun STRICT(value: Boolean, isStrict: Boolean = false) = if (value) if (isStrict) YES() else MAYBE() else NO()
    }
}
