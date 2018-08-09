package com.suushiemaniac.cubing.bld.model.source

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType

import java.net.URI

interface AlgSource {
    val isReadable: Boolean

    val isWritable: Boolean

    val sourceURI: URI

    fun mayRead(): Boolean

    fun getAlgorithms(type: PieceType, letterPair: String): Set<Algorithm>

    fun getRawAlgorithms(type: PieceType, letterPair: String): Set<String>

    fun mayWrite(): Boolean

    fun addAlgorithm(type: PieceType, letterPair: String, algorithm: Algorithm): Boolean

    fun addAlgorithms(type: PieceType, letterPair: String, algorithms: Set<Algorithm>): Boolean

    fun mayUpdate(): Boolean

    fun updateAlgorithm(type: PieceType, oldAlg: Algorithm, newAlg: Algorithm): Boolean

    fun mayDelete(): Boolean

    fun deleteAlgorithm(type: PieceType, algorithm: Algorithm): Boolean

    fun deleteAlgorithms(type: PieceType, letterPair: String): Boolean
}
