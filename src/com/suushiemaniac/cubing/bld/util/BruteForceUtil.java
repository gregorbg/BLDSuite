package com.suushiemaniac.cubing.bld.util;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.CubicPuzzle;

import java.util.*;
import java.util.stream.Collectors;

public class BruteForceUtil {
    public static String[] ALPHABET = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public static String[] genBlockString(String[] alphabet, int length, boolean inclusive, boolean mayRepeat) {
        ArrayList<String> moveList = new ArrayList<>();
        if (length < 1) return new String[0];
        else if (length == 1) return alphabet;
        else {
            if (inclusive)
                for (int i = 1; i < length; i++)
                    Collections.addAll(moveList, genBlockString(alphabet, i, false, mayRepeat));
            for (String genBlockMove : genBlockString(alphabet, length - 1, false, mayRepeat))
                for (String blockMove : alphabet)
                    if (mayRepeat || !genBlockMove.contains(blockMove))
                        moveList.add(genBlockMove + blockMove);
        }
        return moveList.toArray(new String[moveList.size()]);
    }

    public static String[] genBlockString(String[] alphabet, int length, boolean mayRepeat) {
        return genBlockString(alphabet, length, false, mayRepeat);
    }

	public static <T> List<List<T>> permute(Collection<T> alphabet, int length, boolean inclusive, boolean mayRepeat) {
		ArrayList<List<T>> moveList = new ArrayList<>();

		if (length < 1) {
			return new ArrayList<>();
		} else if (length == 1) {
			return alphabet.stream()
					.map(Collections::singletonList)
					.map(ArrayList::new)
					.collect(Collectors.toList());
		} else {
			if (inclusive) {
				for (int i = 1; i < length; i++) {
					moveList.addAll(permute(alphabet, i, false, mayRepeat));
				}
			}

			for (List<T> prevPermute : permute(alphabet, length - 1, false, mayRepeat)) {
				for (T blockObj : alphabet) {
					if (mayRepeat || !prevPermute.contains(blockObj)) {
						List<T> nextPermute = new ArrayList<>(prevPermute);
						nextPermute.add(blockObj);

						moveList.add(nextPermute);
					}
				}
			}
		}

		return moveList;
	}

    public static void bruteForceAlg(String lpCase, PieceType type, String[] alphabet, int prune) {
    	int len = 1;

		NotationReader reader = new CubicAlgorithmReader();
		BldPuzzle analyze = CubicPuzzle.THREE_BLD.getAnalyzingPuzzle();

    	while (len < prune) {
			System.out.println("Trying length " + len + "…");
			String[] moves = genBlockString(alphabet, len, true);

    		for (String alg: moves) {
				Algorithm current = reader.parse(alg);

				if (analyze.solves(type, current, lpCase, false)) {
					System.out.println(current.toFormatString());
				}
			}

			len++;
		}
	}

	public static void bruteForceAlg(String lpCase, PieceType type, String[] alphabet) {
    	bruteForceAlg(lpCase, type, alphabet, 21);
	}
}