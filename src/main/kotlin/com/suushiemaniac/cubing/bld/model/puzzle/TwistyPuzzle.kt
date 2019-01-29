package com.suushiemaniac.cubing.bld.model.puzzle

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.bld.gsolve.GPuzzle
import com.suushiemaniac.cubing.bld.gsolve.KPuzzle
import net.gnehzr.tnoodle.scrambles.Puzzle
import java.io.File

interface TwistyPuzzle {
    val kPuzzle: KPuzzle

    val tPuzzle: Puzzle

    val reader: NotationReader

    val randomScramble: Algorithm
        get() = reader.parse(tPuzzle.generateScramble())

    fun gPuzzle(bldFile: File) = GPuzzle(reader, kPuzzle.commandMap, bldFile)
}
