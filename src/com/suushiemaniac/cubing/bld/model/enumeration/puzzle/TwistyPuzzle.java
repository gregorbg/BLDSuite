package com.suushiemaniac.cubing.bld.model.enumeration.puzzle;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import net.gnehzr.tnoodle.scrambles.Puzzle;

import java.util.function.Supplier;

public interface TwistyPuzzle {
	int getSize();

	Supplier<Puzzle> supplyScramblingPuzzle();

	BldPuzzle getAnalyzingPuzzle();

	PieceType[] getPieceTypes();

	NotationReader getReader();

	default Puzzle getScramblingPuzzle() {
		return this.supplyScramblingPuzzle().get();
	}

	default Algorithm generateRandomScramble() {
		return this.getReader().parse(this.getScramblingPuzzle().generateScramble());
	}

	default BldPuzzle getScrambleAnalysis(Algorithm scramble) {
		BldPuzzle analysis = this.getAnalyzingPuzzle();
		analysis.parseScramble(scramble);

		return analysis;
	}

	default BldPuzzle getScrambleAnalysis() {
		return this.getScrambleAnalysis(this.generateRandomScramble());
	}
}
