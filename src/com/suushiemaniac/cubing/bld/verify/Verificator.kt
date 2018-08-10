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
import com.suushiemaniac.cubing.bld.model.cycle.PieceCycle
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType
import com.suushiemaniac.cubing.bld.model.source.AlgSource
import com.suushiemaniac.cubing.bld.util.BruteForceUtil
import com.suushiemaniac.cubing.bld.util.BruteForceUtil.permuteStr
import com.suushiemaniac.cubing.bld.util.MapUtil.denullify
import com.suushiemaniac.cubing.bld.util.SpeffzUtil.toSpeffz
import com.suushiemaniac.cubing.bld.util.SpeffzUtil.toThreeCycle

class Verificator(val reader: NotationReader, val source: AlgSource, val model: BldPuzzle) {
    fun verifyAll(type: PieceType): Map<String, Map<String, Boolean>> {
        return FULL_LETTER_PAIRS
                .map { it to this.verifySingleCase(type, it.toThreeCycle()) } // FIXME
                .toMap()
    }

    fun verifySingleCase(type: PieceType, letterPair: PieceCycle): Map<String, Boolean> {
        return this.source.getRawAlgorithms(type, letterPair)
                .map { it to (ParseUtils.isParseable(it, this.reader) && this.model.solves(type, this.reader.parse(it), letterPair.toSpeffz(), true)) }
                .toMap()
    }

    fun attemptFixFor(type: PieceType, letterPair: PieceCycle): Map<String, Algorithm> {
        return this.source.getRawAlgorithms(type, letterPair)
                .filter { ParseUtils.isParseable(it, this.reader) }
                .filter { !this.model.solves(type, this.reader.parse(it), letterPair.toSpeffz(), true) }
                .map { it to this.fixAlgorithm(it, type, letterPair) }
                .toMap().denullify()
    }

    fun fixAlgorithm(rawAlg: String, type: PieceType, letterPair: PieceCycle): Algorithm? {
        val alg = this.reader.parse(rawAlg)

        return this.computePossibleReparations(alg)
                .firstOrNull { this.model.solves(type, it, letterPair.toSpeffz(), true) }
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
                            val nextReparation = preReparation.toMutableList()
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
                .filter { it != original.modifier }
                .map { CubicMove( original.plane, it, original.depth) }
        else emptyList()
    }

    fun findMatchingSubGroup(type: PieceType, group: SubGroup): Map<String, List<String>> {
        return FULL_LETTER_PAIRS.map {
            it to this.source.getRawAlgorithms(type, it.toThreeCycle()) // FIXME
                    .filter { alg -> ParseUtils.isParseable(alg, this.reader) }
                    .filter { alg -> this.reader.parse(alg).subGroup.sameOrLargerSubGroup(group) }
        }.toMap()
    }

    fun checkParseable(type: PieceType): Map<String, Set<String>> {
        return FULL_LETTER_PAIRS.map {
            it to this.source.getRawAlgorithms(type, it.toThreeCycle()) // FIXME
                    .filter { alg -> !ParseUtils.isParseable(alg, this.reader) }
                    .toSet()
        }.toMap()
    }

    companion object {
        val FULL_LETTER_PAIRS = BruteForceUtil.ALPHABET.permuteStr(2, false, false)
    }
}