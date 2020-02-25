package com.suushiemaniac.cubing.bld.model.puzzle

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.bld.gsolve.GPuzzle
import com.suushiemaniac.cubing.bld.gsolve.KPuzzle
import com.suushiemaniac.cubing.bld.model.puzzledef.CommandMap
import com.suushiemaniac.cubing.bld.model.puzzledef.GCommands
import org.worldcubeassociation.tnoodle.scrambles.Puzzle
import java.io.File

interface TwistyPuzzle {
    val tPuzzleSupply: () -> Puzzle
    val tPuzzle: Puzzle

    val antlrReaderSupply: () -> NotationReader
    val antlrReader: NotationReader

    val randomScramble: Algorithm
        get() = this.antlrReader.parse(this.tPuzzle.generateScramble())

    val kTag: String

    val kPuzzle: KPuzzle
        get() = KPuzzle(KPuzzle.preInstalledConfig(this.kTag, this.antlrReader))

    fun gPuzzle(personTag: String) = GPuzzle(GPuzzle.preInstalledConfig(this.kTag, personTag, this.antlrReader))
}
