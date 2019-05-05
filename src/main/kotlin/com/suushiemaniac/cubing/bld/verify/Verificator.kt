package com.suushiemaniac.cubing.bld.verify

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg
import com.suushiemaniac.cubing.alglib.alg.SubGroup
import com.suushiemaniac.cubing.alglib.alg.commutator.CombinedAlg
import com.suushiemaniac.cubing.alglib.move.CubicMove
import com.suushiemaniac.cubing.alglib.move.Move
import com.suushiemaniac.cubing.alglib.move.modifier.CubicModifier
import com.suushiemaniac.cubing.alglib.util.ParseUtils
import com.suushiemaniac.cubing.bld.util.CollectionUtil.headOrNullWithTail
import com.suushiemaniac.cubing.bld.gsolve.GPuzzle
import com.suushiemaniac.cubing.bld.model.PieceType
import com.suushiemaniac.cubing.bld.model.AlgSource
import com.suushiemaniac.cubing.bld.util.PieceCycle
import com.suushiemaniac.cubing.bld.util.BruteForceUtil.fullCycles

class Verificator(val analyzer: GPuzzle, val source: AlgSource) {
    val reader = this.analyzer.reader

    fun verifyAll(type: PieceType): Map<PieceCycle, Map<String, Boolean>> {
        return type.fullCycles().associateWith { this.verifySingleCase(type, it) }
    }

    fun verifySingleCase(type: PieceType, letterPair: PieceCycle): Map<String, Boolean> {
        return this.source.getRawAlgorithms(type, letterPair)
                .associateWith { ParseUtils.isParseable(it, this.reader) && this.analyzer.solves(type, this.reader.parse(it), listOf(letterPair), true) }
    }

    fun attemptFixFor(type: PieceType, letterPair: PieceCycle): Map<String, Algorithm?> {
        return this.source.getRawAlgorithms(type, letterPair)
                .filter { ParseUtils.isParseable(it, this.reader) }
                .filter { !this.analyzer.solves(type, this.reader.parse(it), listOf(letterPair), true) }
                .associateWith { this.fixAlgorithm(it, type, letterPair) }
    }

    fun fixAlgorithm(rawAlg: String, type: PieceType, letterPair: PieceCycle): Algorithm? {
        val alg = this.reader.parse(rawAlg)

        return computePossibleReparations(alg)
                .firstOrNull { this.analyzer.solves(type, it, listOf(letterPair), true) }
    }

    fun findMatchingSubGroup(type: PieceType, group: SubGroup): Map<PieceCycle, List<String>> {
        return type.fullCycles().associateWith {
            this.source.getRawAlgorithms(type, it)
                    .filter { alg -> ParseUtils.isParseable(alg, this.reader) }
                    .filter { alg -> this.reader.parse(alg).subGroup.sameOrLargerSubGroup(group) }
        }
    }

    fun checkParseable(type: PieceType): Map<PieceCycle, Set<String>> {
        return type.fullCycles().associateWith {
            this.source.getRawAlgorithms(type, it)
                    .filter { alg -> !ParseUtils.isParseable(alg, this.reader) }
                    .toSet()
        }
    }

    companion object {
        fun computePossibleReparations(alg: Algorithm): List<Algorithm> {
            when (alg) {
                is SimpleAlg -> {
                    val (headMove, tailAlg) = alg.headOrNullWithTail()

                    return if (headMove == null) {
                        emptyList()
                    } else {
                        val headAlgs = computePossibleAlternatives(headMove).map { SimpleAlg(listOf(it)) }

                        if (tailAlg.isEmpty()) {
                            headAlgs
                        } else {
                            headAlgs.flatMap {
                                computePossibleReparations(SimpleAlg(tailAlg)).map { rAlg ->
                                    it + rAlg
                                }
                            }
                        }
                    }
                }
                is CombinedAlg -> {
                    val firstPartReparations = computePossibleReparations(alg.first)
                    val secondPartReparations = computePossibleReparations(alg.second)

                    val reparations = firstPartReparations.map { alg(it, alg.second) } +
                            secondPartReparations.map { alg(alg.first, it) }

                    return reparations + reparations.map { it.inverse() }
                }
                else -> return emptyList()
            }
        }

        fun computePossibleAlternatives(original: Move): List<Move> {
            return if (original is CubicMove)
                CubicModifier.values().map { CubicMove( original.plane, it, original.depth, original.fromDepth) }
            else emptyList()
        }
    }
}