package com.suushiemaniac.cubing.bld.model.source

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.bld.model.cycle.PieceCycle
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType

import java.net.URI

interface AlgSource {
    val isReadable: Boolean

    val isWritable: Boolean

    val sourceURI: URI

    fun mayRead(): Boolean

    fun getAlgorithms(type: PieceType, case: PieceCycle): Set<Algorithm> {
        return this.getRawAlgorithms(type, case).map(type.reader::parse).toSet()
    }

    fun getRawAlgorithms(type: PieceType, case: PieceCycle): Set<String>

    fun mayWrite(): Boolean

    fun addAlgorithm(type: PieceType, case: PieceCycle, algorithm: Algorithm): Boolean

    fun addAlgorithms(type: PieceType, case: PieceCycle, algorithms: Set<Algorithm>): Boolean {
        return algorithms.fold(true) { acc, alg -> acc && this.addAlgorithm(type, case, alg) }
    }

    fun mayUpdate(): Boolean

    fun updateAlgorithm(type: PieceType, oldAlg: Algorithm, newAlg: Algorithm): Boolean

    fun mayDelete(): Boolean

    fun deleteAlgorithm(type: PieceType, algorithm: Algorithm): Boolean

    fun deleteAlgorithms(type: PieceType, case: PieceCycle): Boolean
}
