package com.suushiemaniac.cubing.bld.model.puzzle

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.bld.gsolve.GPuzzle
import com.suushiemaniac.cubing.bld.gsolve.KPuzzle
import com.suushiemaniac.cubing.bld.model.puzzledef.GCommands
import com.suushiemaniac.cubing.bld.model.puzzledef.KCommands
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
        get() = KPuzzle(this.antlrReader, KPuzzle.preInstalledConfig(this.kTag))

    fun gPuzzle(bldFile: File) = GPuzzle(this.antlrReader, KPuzzle.preInstalledConfig(this.kTag), GCommands.parse(KCommands.loadFile(bldFile)))
    fun gPuzzle(personTag: String) = GPuzzle(this.antlrReader, KPuzzle.preInstalledConfig(this.kTag), GPuzzle.preInstalledConfig(this.kTag, personTag))
}
