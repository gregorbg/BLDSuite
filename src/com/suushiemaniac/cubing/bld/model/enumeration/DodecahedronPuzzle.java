package com.suushiemaniac.cubing.bld.model.enumeration;

import com.suushiemaniac.cubing.alglib.lang.MegaminxAlgorithmReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle;
import com.suushiemaniac.cubing.bld.analyze.TwoBldCube;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.MegaminxPuzzle;

import static com.suushiemaniac.cubing.bld.model.enumeration.DodecahedronPieceType.*;

public enum DodecahedronPuzzle implements TwistyPuzzle { //TODO correct puzzle implementations
	KILO(2, new MegaminxPuzzle(), new TwoBldCube(), CORNER),
	MEGA(2, new puzzle.CubePuzzle(2), new TwoBldCube(), CORNER, CENTER, EDGE),
	MASTERKILO(2, new puzzle.CubePuzzle(2), new TwoBldCube(), CORNER, CENTER, EDGE),
	GIGA(2, new puzzle.CubePuzzle(2), new TwoBldCube(), CORNER, CENTER, EDGE);

	public static DodecahedronPuzzle fromSize(int size) {
		for (DodecahedronPuzzle puzzle : values()) {
			if (puzzle.getSize() == size) {
				return puzzle;
			}
		}

		return MEGA;
	}

	private static NotationReader READER_INST = new MegaminxAlgorithmReader();

	private int size;
	private Puzzle scramblingPuzzle;
	private BldPuzzle analyzingPuzzle;
	private PieceType[] types;

	DodecahedronPuzzle(int size, Puzzle scramblingPuzzle, BldPuzzle analyzingPuzzle, PieceType... types) {
		this.size = size;
		this.scramblingPuzzle = scramblingPuzzle;
		this.analyzingPuzzle = analyzingPuzzle;
		this.types = types;
	}

	@Override
	public int getSize() {
		return this.size;
	}

	@Override
	public Puzzle getScramblingPuzzle() {
		return this.scramblingPuzzle;
	}

	@Override
	public BldPuzzle getAnalyzingPuzzle() {
		return this.analyzingPuzzle;
	}

	@Override
	public PieceType[] getPieceTypes() {
		return this.types;
	}

	@Override
	public NotationReader getReader() {
		return READER_INST;
	}
}
