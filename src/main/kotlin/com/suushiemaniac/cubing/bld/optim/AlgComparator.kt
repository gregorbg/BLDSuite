package com.suushiemaniac.cubing.bld.optim

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.bld.optim.AlgComparator.score

object AlgComparator : Comparator<Algorithm> by Comparator.comparingDouble<Algorithm>(::score) {
    fun score(alg: Algorithm): Double {
        return (2 * this.lengthScore(alg)
                + this.rotationScore(alg)
                + this.subGroupScore(alg)) / 4.toDouble()
    }

    fun lengthScore(alg: Algorithm): Int {
        return -2 * alg.moveLength() + 26
    }

    fun rotationScore(alg: Algorithm): Int {
        return -5 * alg.rotationGroup.size + 10
    }

    fun subGroupScore(alg: Algorithm): Int {
        return -5 * alg.subGroup.size + 25
    }
}