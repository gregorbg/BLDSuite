package com.suushiemaniac.cubing.bld.util;

import com.suushiemaniac.cubing.bld.model.enumeration.piece.LetterPairImage;
import com.suushiemaniac.cubing.bld.model.source.AlgSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class MemoUtil {
	public static List<String> genMemoTree(String pairs, AlgSource source) {
		List<String> treeList = new ArrayList<>(Collections.singletonList(""));
		List<LetterPairImage> posHistory = new ArrayList<>();

		for (String pair : pairs.split("(?<=\\G.{2})")) {
			if (pair.length() == 1) {
				List<String> oldMemoStrings = new ArrayList<>(treeList);

				for (String oldMemo : oldMemoStrings) {
					treeList.add(oldMemo + (oldMemo.length() > 0 ? " // " : "") + "Parity: " + pair);
				}

				treeList.removeAll(oldMemoStrings);
			} else if (pair.length() == 2) {
				LetterPairImage partOfSpeech = MemoUtil.classifyNextPOS(posHistory);

				List<String> words = new ArrayList<>(source.getRawAlgorithms(partOfSpeech, pair));
				List<String> oldMemoStrings = new ArrayList<>(treeList);

				for (String oldMemo : oldMemoStrings) {
					for (String word : words) {
						treeList.add(oldMemo + (oldMemo.length() > 0 ? " // " : "") + word);
					}
				}

				treeList.removeAll(oldMemoStrings);
				posHistory.add(partOfSpeech);
			}
		}

		return treeList;
	}

	public static LetterPairImage classifyNextPOS(List<LetterPairImage> history) {
		return LetterPairImage.NOUN; // TODO improve!
	}
}
