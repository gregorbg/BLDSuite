package com.suushiemaniac.cubing.bld.util

import com.suushiemaniac.cubing.bld.model.enumeration.piece.LetterPairImage
import com.suushiemaniac.cubing.bld.model.source.AlgSource

object MemoUtil {
    fun genMemoTree(pairs: String, source: AlgSource): List<String> {
        val treeList = mutableListOf("")
        val posHistory = mutableListOf<LetterPairImage>()

        for (pair in pairs.split("(?<=\\G.{2})".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            if (pair.length == 1) {
                val oldMemoStrings = treeList.toMutableList()

                for (oldMemo in oldMemoStrings) {
                    treeList.add(oldMemo + (if (oldMemo.isNotEmpty()) " // " else "") + "Parity: " + pair)
                }

                treeList.removeAll(oldMemoStrings)
            } else if (pair.length == 2) {
                val partOfSpeech = MemoUtil.classifyNextPOS(posHistory)

                val words = source.getRawAlgorithms(partOfSpeech, pair)
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
