package com.suushiemaniac.cubing.bld.filter.condition

data class BooleanCondition(private var value: Boolean, val isImportant: Boolean) : BaseConditional<Boolean> {
    val positive: Boolean
        get() = !this.isImportant || this.value

    val negative: Boolean
        get() = this.isImportant && this.value

    fun evaluatePositive(compareTo: Boolean) = !this.isImportant || this.value == compareTo
    fun evaluateNegative(compareTo: Boolean) = this.isImportant && this.value == compareTo

    override fun evaluate(compareTo: Boolean) = this.evaluatePositive(compareTo)

    fun define(value: Boolean) {
        this.value = value
    }

    companion object {
        fun YES() = BooleanCondition(value = true, isImportant = true)

        fun NO(): BooleanCondition = BooleanCondition(value = false, isImportant = true)

        fun UNIMPORTANT() = BooleanCondition(value = false, isImportant = false)
        fun MAYBE() = UNIMPORTANT()

        fun STRICT(value: Boolean, isStrict: Boolean = false) = if (value) if (isStrict) YES() else MAYBE() else NO()
    }
}
