package com.suushiemaniac.cubing.bld.model.enumeration;

import com.suushiemaniac.cubing.bld.analyze.cube.BldPuzzle;
import net.gnehzr.tnoodle.scrambles.Puzzle;

public interface TwistyPuzzle {
	int getSize();

	Puzzle getScramblingPuzzle();

	BldPuzzle getAnalyzingPuzzle();

	PieceType[] getPieceTypes();
}
