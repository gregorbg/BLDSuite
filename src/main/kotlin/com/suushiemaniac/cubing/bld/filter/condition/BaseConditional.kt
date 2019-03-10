package com.suushiemaniac.cubing.bld.filter.condition

interface BaseConditional<T> {
    fun evaluate(compareTo: T): Boolean
}