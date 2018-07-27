package com.suushiemaniac.cubing.bld.optim

import com.suushiemaniac.cubing.alglib.alg.Algorithm

class AlgComparator protected constructor() : Comparator<Algorithm> {
    override fun compare(alg: Algorithm, otherAlg: Algorithm): Int {
        return java.lang.Float.compare(this.score(otherAlg), this.score(alg))
    }

    fun score(alg: Algorithm): Float {
        return (2 * this.lengthScore(alg)
                + this.rotationScore(alg)
                + this.subGroupScore(alg)) / 4f
    }

    protected fun lengthScore(alg: Algorithm): Int {
        return -2 * alg.moveLength() + 26
    }

    protected fun rotationScore(alg: Algorithm): Int {
        return -5 * alg.rotationGroup.size() + 10
    }

    protected fun subGroupScore(alg: Algorithm): Int {
        return -5 * alg.subGroup.size() + 25
    }

    companion object {
        val SINGLETON = AlgComparator()

        fun scoreAlg(alg: Algorithm): Float {
            return SINGLETON.score(alg)
        }
    }
}