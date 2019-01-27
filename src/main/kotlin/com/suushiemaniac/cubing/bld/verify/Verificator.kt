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
import com.suushiemaniac.cubing.bld.gsolve.GPuzzle
import com.suushiemaniac.cubing.bld.model.cycle.PieceCycle
import com.suushiemaniac.cubing.bld.model.PieceType
import com.suushiemaniac.cubing.bld.model.AlgSource
import com.suushiemaniac.cubing.bld.util.MapUtil.denullify

class Verificator(val analyzer: GPuzzle, val source: AlgSource, val reader: NotationReader) {
    fun verifyAll(type: PieceType): Map<PieceCycle, Map<String, Boolean>> {
        return fullCycles(type).associateWith { this.verifySingleCase(type, it) }
    }

    fun verifySingleCase(type: PieceType, letterPair: PieceCycle): Map<String, Boolean> {
        return this.source.getRawAlgorithms(type, letterPair)
                .associateWith { ParseUtils.isParseable(it, this.reader) && this.analyzer.solves(type, this.reader.parse(it), listOf(letterPair), true) }
    }

    fun attemptFixFor(type: PieceType, letterPair: PieceCycle): Map<String, Algorithm> {
        return this.source.getRawAlgorithms(type, letterPair)
                .filter { ParseUtils.isParseable(it, this.reader) }
                .filter { !this.analyzer.solves(type, this.reader.parse(it), listOf(letterPair), true) }
                .associateWith { this.fixAlgorithm(it, type, letterPair) }
                .denullify()
    }

    fun fixAlgorithm(rawAlg: String, type: PieceType, letterPair: PieceCycle): Algorithm? {
        val alg = this.reader.parse(rawAlg)

        return this.computePossibleReparations(alg)
                .firstOrNull { this.analyzer.solves(type, it, listOf(letterPair), true) }
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

    fun findMatchingSubGroup(type: PieceType, group: SubGroup): Map<PieceCycle, List<String>> {
        return fullCycles(type).associateWith {
            this.source.getRawAlgorithms(type, it)
                    .filter { alg -> ParseUtils.isParseable(alg, this.reader) }
                    .filter { alg -> this.reader.parse(alg).subGroup.sameOrLargerSubGroup(group) }
        }
    }

    fun checkParseable(type: PieceType): Map<PieceCycle, Set<String>> {
        return fullCycles(type).associateWith {
            this.source.getRawAlgorithms(type, it)
                    .filter { alg -> !ParseUtils.isParseable(alg, this.reader) }
                    .toSet()
        }
    }

    companion object {
        fun fullCycles(type: PieceType): List<PieceCycle> {
            return emptyList() // TODO
        }
    }
}