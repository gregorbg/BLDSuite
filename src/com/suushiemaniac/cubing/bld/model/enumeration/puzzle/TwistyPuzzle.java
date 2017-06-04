package com.suushiemaniac.cubing.bld.model.enumeration.puzzle;

import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import net.gnehzr.tnoodle.scrambles.Puzzle;

import java.util.function.Supplier;

public interface TwistyPuzzle {
	int getSize();

	Puzzle getScramblingPuzzle();

	Supplier<Puzzle> generateScramblingPuzzle();

	BldPuzzle getAnalyzingPuzzle();

	PieceType[] getPieceTypes();

	NotationReader getReader();
}
