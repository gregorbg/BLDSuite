package com.suushiemaniac.cubing.bld.model

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.bld.model.cycle.PieceCycle

import java.net.URI

interface AlgSource {
    val isReadable: Boolean

    val isWritable: Boolean

    val uri: URI

    fun mayRead(): Boolean

    fun mayWrite(): Boolean

    fun mayUpdate(): Boolean

    fun mayDelete(): Boolean

    fun getAlgorithms(type: PieceType, reader: NotationReader, case: PieceCycle): Set<Algorithm> {
        return this.getRawAlgorithms(type, case).map(reader::parse).toSet()
    }

    fun getRawAlgorithms(type: PieceType, case: PieceCycle): Set<String>

    fun addAlgorithm(type: PieceType, case: PieceCycle, algorithm: Algorithm): Boolean

    fun addAlgorithms(type: PieceType, case: PieceCycle, algorithms: Set<Algorithm>): Boolean {
        return algorithms.all { this.addAlgorithm(type, case, it) }
    }

    fun updateAlgorithm(type: PieceType, oldAlg: Algorithm, newAlg: Algorithm): Boolean

    fun deleteAlgorithm(type: PieceType, algorithm: Algorithm): Boolean

    fun deleteAlgorithms(type: PieceType, case: PieceCycle): Boolean
}
