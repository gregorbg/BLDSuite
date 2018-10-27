package com.suushiemaniac.cubing.bld.util

import com.suushiemaniac.cubing.bld.model.cycle.ParityCycle
import com.suushiemaniac.cubing.bld.model.cycle.PieceCycle
import com.suushiemaniac.cubing.bld.model.cycle.ThreeCycle
import com.suushiemaniac.cubing.bld.model.enumeration.piece.LetterPairImage
import com.suushiemaniac.cubing.bld.model.source.AlgSource

object MemoUtil {
    fun genMemoTree(cycles: List<PieceCycle>, source: AlgSource): List<String> {
        val treeList = mutableListOf("")
        val posHistory = mutableListOf<LetterPairImage>()

        for (cycle in cycles) {
            if (cycle is ParityCycle) {
                val oldMemoStrings = treeList.toMutableList()

                for (oldMemo in oldMemoStrings) {
                    treeList.add(oldMemo + (if (oldMemo.isNotEmpty()) " // " else "") + "Parity: " + cycle.target)
                }

                treeList.removeAll(oldMemoStrings)
            } else if (cycle is ThreeCycle) {
                val partOfSpeech = MemoUtil.classifyNextPOS(posHistory)

                val words = source.getRawAlgorithms(partOfSpeech, cycle)
                val oldMemoStrings = treeList.toMutableList()

                for (oldMemo in oldMemoStrings) {
                    for (word in words) {
                        treeList.add(oldMemo + (if (oldMemo.isNotEmpty()) " // " else "") + word)
                    }
                }

                treeList.removeAll(oldMemoStrings)
                posHistory.add(partOfSpeech)
            }
        }

        return treeList
    }

    fun classifyNextPOS(history: List<LetterPairImage>): LetterPairImage {
        return LetterPairImage.NOUN // TODO improve!
    }
}
