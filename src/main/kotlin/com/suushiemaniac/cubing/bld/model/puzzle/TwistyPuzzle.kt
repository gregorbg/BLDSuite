package com.suushiemaniac.cubing.bld.model.puzzle

import com.suushiemaniac.cubing.alglib.alg.Algorithm
import com.suushiemaniac.cubing.alglib.lang.NotationReader
import com.suushiemaniac.cubing.bld.gsolve.GPuzzle
import com.suushiemaniac.cubing.bld.gsolve.KPuzzle
import net.gnehzr.tnoodle.scrambles.Puzzle
import java.io.File

interface TwistyPuzzle {
    val tPuzzle: Puzzle

    val reader: NotationReader

    val randomScramble: Algorithm
        get() = this.reader.parse(this.tPuzzle.generateScramble())

    val kTag: String

    val kPuzzle: KPuzzle
        get() = KPuzzle(this.reader, KPuzzle.preInstalledConfig(this.kTag))

    fun gPuzzle(bldFile: File) = GPuzzle(this.reader, KPuzzle.preInstalledConfig(this.kTag), KPuzzle.loadCommandMap(bldFile))
    fun gPuzzle(personTag: String) = GPuzzle(this.reader, KPuzzle.preInstalledConfig(this.kTag), GPuzzle.preInstalledConfig(this.kTag, personTag))
}
