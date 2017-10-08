package com.suushiemaniac.cubing.bld.util;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle;
import com.suushiemaniac.cubing.bld.analyze.ThreeBldCube;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;

import java.util.ArrayList;
import java.util.Collections;

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

    public static void bruteForceAlg(String lpCase, PieceType type, String[] alphabet, int prune) {
    	int len = 1;

		NotationReader reader = new CubicAlgorithmReader();
		BldPuzzle analyze = new ThreeBldCube();

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