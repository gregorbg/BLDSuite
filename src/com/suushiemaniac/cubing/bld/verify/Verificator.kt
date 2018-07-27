package com.suushiemaniac.cubing.bld.verify

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg
import com.suushiemaniac.cubing.alglib.alg.SubGroup
import com.suushiemaniac.cubing.alglib.alg.commutator.Commutator
import com.suushiemaniac.cubing.alglib.alg.commutator.PureComm
import com.suushiemaniac.cubing.alglib.alg.commutator.SetupComm
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.alglib.move.CubicMove
import com.suushiemaniac.cubing.alglib.move.Move
import com.suushiemaniac.cubing.alglib.move.modifier.CubicModifier
import com.suushiemaniac.cubing.alglib.util.ParseUtils
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import com.suushiemaniac.cubing.bld.model.source.AlgSource
import com.suushiemaniac.cubing.bld.util.BruteForceUtil
import com.suushiemaniac.cubing.bld.util.BruteForceUtil.permute

class Verificator(val reader: NotationReader, val source: AlgSource, val model: BldPuzzle) {
    fun verifyAll(type: PieceType): Map<String, Map<String, Boolean>> {
        return FULL_LETTER_PAIRS
                .map { it.joinToString("") }
                .map { it to this.verifySingleCase(type, it) }
                .toMap()
    }

    fun verifySingleCase(type: PieceType, letterPair: String): Map<String, Boolean> {
        return this.source.getRawAlgorithms(type, letterPair)
                .map { it to (ParseUtils.isParseable(it, this.reader) && this.model.solves(type, this.reader.parse(it), letterPair, true)) }
                .toMap()
    }

    fun attemptFixFor(type: PieceType, letterPair: String): Map<String, String> {
        return this.source.getRawAlgorithms(type, letterPair)
                .filter { ParseUtils.isParseable(it, this.reader) }
                .filter { !this.model.solves(type, this.reader.parse(it), letterPair, true) }
                .map { it to this.fixAlgorithm(it, type, letterPair) }
                .toMap()
    }

    fun fixAlgorithm(rawAlg: String, type: PieceType, letterPair: String): String {
        val alg = this.reader.parse(rawAlg)

        val reparations = this.computePossibleReparations(alg)

        for (repairedAlg in reparations) {
            if (this.model.solves(type, repairedAlg, letterPair, true)) {
                return repairedAlg.toFormatString()
            }
        }

        return rawAlg
    }

    protected fun computePossibleReparations(alg: Algorithm): List<Algorithm> {
        when (alg) {
            is SimpleAlg -> {
                var reparations = listOf<List<Move>>(listOf())

                for (i in 0 until alg.algLength()) {
                    val currentReparations = mutableListOf<List<Move>>()
                    val alternatives = this.computePossibleAlternatives(alg.nMove(i))

                    for (preReparation in reparations) {
                        for (alt in alternatives) {
                            val nextReparation = ArrayList(preReparation)
                            nextReparation.add(alt)

                            currentReparations.add(nextReparation)
                        }
                    }

                    reparations = currentReparations
                }

                return reparations.map(::SimpleAlg)
            }
            is Commutator -> {
                val firstPart: Algorithm
                val secondPart: Algorithm

                val generator: (Algorithm, Algorithm) -> Algorithm

                val reparations = mutableListOf<Algorithm>()

                when (alg) {
                    is PureComm -> {
                        firstPart = alg.partA
                        secondPart = alg.partB

                        generator = ::PureComm
                    }
                    is SetupComm -> {

                        firstPart = alg.setup
                        secondPart = alg.inner

                        generator = ::SetupComm
                    }
                    else -> return emptyList()
                }

                val firstPartReparations = this.computePossibleReparations(firstPart)
                val secondPartReparations = this.computePossibleReparations(secondPart)

                reparations += firstPartReparations.map { generator(it, secondPart) }
                reparations += secondPartReparations.map { generator(firstPart, it) }

                if (alg is PureComm) {
                    reparations += firstPartReparations.map { generator(secondPart, it) }
                    reparations += secondPartReparations.map { generator(it, firstPart) }
                }

                return reparations
            }
            else -> return emptyList()
        }
    }

    fun computePossibleAlternatives(original: Move): List<Move> {
        return if (original is CubicMove)
            CubicModifier.values()
                //.filter(it != move.getModifier())
                .map { CubicMove( original.plane, it, original.depth) }
        else emptyList()
    }

    fun findMatchingSubGroup(type: PieceType, group: SubGroup): Map<String, List<String>> {
        val sameGroupMap = mutableMapOf<String, MutableList<String>>()

        for (possPairList in FULL_LETTER_PAIRS) {
            val possPair = possPairList.joinToString("")
            val accu = mutableListOf<String>()

            val algStringList = this.source.getRawAlgorithms(type, possPair)

            if (algStringList != null) {
                for (alg in algStringList) {
                    if (ParseUtils.isParseable(alg, this.reader) && this.reader.parse(alg).subGroup.sameOrLargerSubGroup(group)) {
                        accu.add(alg)
                    }
                }
            }

            sameGroupMap[possPair] = accu
        }

        return sameGroupMap
    }

    fun checkParseable(type: PieceType): Map<String, Set<String>> {
        val unparseableMap = mutableMapOf<String, Set<String>>()

        for (possPairList in FULL_LETTER_PAIRS) {
            val possPair = possPairList.joinToString("")
            val algStringList = this.source.getRawAlgorithms(type, possPair)

            if (algStringList != null) {
                val unparseable = algStringList.filter { !ParseUtils.isParseable(it, this.reader) }
                unparseableMap[possPair] = unparseable.toSet()
            }
        }

        return unparseableMap
    }

    companion object {
        var FULL_LETTER_PAIRS = BruteForceUtil.ALPHABET.permute(2, false, false)
    }
}