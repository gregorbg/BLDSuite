package com.suushiemaniac.cubing.bld.model.enumeration.puzzle;

import com.suushiemaniac.cubing.alglib.lang.MegaminxAlgorithmReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle;
import com.suushiemaniac.cubing.bld.analyze.TwoBldCube;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.CubePuzzle;
import puzzle.MegaminxPuzzle;

import java.util.function.Supplier;

import static com.suushiemaniac.cubing.bld.model.enumeration.piece.DodecahedronPieceType.*;

public enum DodecahedronPuzzle implements TwistyPuzzle { //TODO correct puzzle implementations
	KILO(2, MegaminxPuzzle::new, new TwoBldCube(), CORNER),
	MEGA(2, () -> new CubePuzzle(2), new TwoBldCube(), CORNER, CENTER, EDGE),
	MASTERKILO(2, () -> new CubePuzzle(2), new TwoBldCube(), CORNER, CENTER, EDGE),
	GIGA(2, () -> new CubePuzzle(2), new TwoBldCube(), CORNER, CENTER, EDGE);

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
	private Supplier<Puzzle> scramblingPuzzleGen;
	private BldPuzzle analyzingPuzzle;
	private PieceType[] types;

	DodecahedronPuzzle(int size, Supplier<Puzzle> scramblingPuzzleGen, BldPuzzle analyzingPuzzle, PieceType... types) {
		this.size = size;
		this.scramblingPuzzleGen = scramblingPuzzleGen;
		this.analyzingPuzzle = analyzingPuzzle;
		this.types = types;
	}

	@Override
	public int getSize() {
		return this.size;
	}

	@Override
	public Puzzle getScramblingPuzzle() {
		return this.scramblingPuzzleGen.get();
	}

	@Override
	public Supplier<Puzzle> generateScramblingPuzzle() {
		return this.scramblingPuzzleGen;
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
