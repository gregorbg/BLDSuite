package com.suushiemaniac.cubing.bld.model.enumeration.puzzle;

import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.analyze.*;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import com.suushiemaniac.cubing.bld.util.ClosureUtil;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.*;

import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType.*;

public enum CubicPuzzle implements TwistyPuzzle {
	TWO(2, TwoByTwoCubePuzzle::new, CORNER),
	THREE(3, ThreeByThreeCubePuzzle::new, CORNER, CENTER, EDGE),
	THREE_BLD(NoInspectionThreeByThreeCubePuzzle::new, THREE),
	THREE_FMC(ThreeByThreeCubeFewestMovesPuzzle::new, THREE),
	FOUR(4, FourByFourCubePuzzle::new, CORNER, XCENTER, WING),
	FOUR_RAND(FourByFourRandomTurnsCubePuzzle::new, FOUR),
	FOUR_BLD(NoInspectionFourByFourCubePuzzle::new, FOUR),
	FIVE(5, CubePuzzle::new, CORNER, CENTER, EDGE, XCENTER, TCENTER, WING),
	FIVE_BLD(NoInspectionFiveByFiveCubePuzzle::new, FIVE),
	SIX(6, CubePuzzle::new, CORNER, XCENTER, WING, INNERXCENTER, INNERWING, LEFTOBLIQUE, RIGHTOBLIQUE),
	SEVEN(7, CubePuzzle::new, CORNER, CENTER, EDGE, XCENTER, TCENTER, WING, INNERXCENTER, INNERWING, LEFTOBLIQUE, RIGHTOBLIQUE, INNERTCENTER);

	public static CubicPuzzle fromSize(int size, String flag) {
		for (CubicPuzzle puzzle : values()) {
			if (puzzle.getSize() == size) {
				if (flag.length() <= 0) {
					if (!puzzle.name().contains("_")) {
						return puzzle;
					}
				} else if (puzzle.name().toUpperCase().endsWith(flag.toUpperCase())) {
					return puzzle;
				}
			}
		}

		if (size >= 2 && size <= 7) {
			return fromSize(size);
		}

		return THREE;
	}

	public static CubicPuzzle fromSize(int size) {
		return fromSize(size, "");
	}

	private static NotationReader READER_INST = new CubicAlgorithmReader();

	private int size;
	private Supplier<Puzzle> scramblingPuzzleGen;
	private PieceType[] types;

	private BldPuzzle analyzingPuzzle;

	CubicPuzzle(int size, Supplier<Puzzle> scramblingPuzzleGen, PieceType... types) {
		this.size = size;
		this.scramblingPuzzleGen = scramblingPuzzleGen;
		this.types = types;

		this.analyzingPuzzle = new BldCube(this);
	}

	CubicPuzzle(int size, Function<Integer, Puzzle> scramblingPuzzleGen, PieceType... types) {
		this(size, () -> scramblingPuzzleGen.apply(size), types);
	}

	CubicPuzzle(Supplier<Puzzle> scramblingPuzzleGen, CubicPuzzle parent) {
		this(parent.getSize(), scramblingPuzzleGen, parent.getPieceTypes());
	}

	@Override
	public int getSize() {
		return this.size;
	}

	@Override
	public Supplier<Puzzle> supplyScramblingPuzzle() {
		return this.scramblingPuzzleGen;
	}

	@Override
	public BldPuzzle getAnalyzingPuzzle() {
		return this.analyzingPuzzle.clone();
	}

	@Override
	public PieceType[] getPieceTypes() {
		return this.types;
	}

	@Override
	public NotationReader getReader() {
		return READER_INST;
	}

	@Override
	public String toString() {
		return String.join("x", Collections.nCopies(3, String.valueOf(this.size)));
	}
}
