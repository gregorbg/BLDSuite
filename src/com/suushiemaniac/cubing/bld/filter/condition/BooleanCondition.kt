package com.suushiemaniac.cubing.bld.filter.condition

data class BooleanCondition(var value: Boolean, var isImportant: Boolean) {
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

    companion object {
        fun YES(): BooleanCondition {
            return BooleanCondition(true, true)
        }

        fun NO(): BooleanCondition {
            return BooleanCondition(false, true)
        }

        fun UNIMPORTANT(): BooleanCondition {
            return BooleanCondition(false, false)
        }

        fun MAYBE(): BooleanCondition {
            return BooleanCondition.UNIMPORTANT()
        }
    }
}
