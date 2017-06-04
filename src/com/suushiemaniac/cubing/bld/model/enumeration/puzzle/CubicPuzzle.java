package com.suushiemaniac.cubing.bld.model.enumeration.puzzle;

import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.analyze.*;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.*;

import java.util.Collections;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType.*;

public enum CubicPuzzle implements TwistyPuzzle {
	TWO(2, TwoByTwoCubePuzzle::new, new TwoBldCube(), CORNER),
	THREE(3, ThreeByThreeCubePuzzle::new, new ThreeBldCube(), CORNER, CENTER, EDGE),
	THREE_BLD(3, NoInspectionThreeByThreeCubePuzzle::new, new ThreeBldCube(), CORNER, CENTER, EDGE),
	THREE_FMC(3, ThreeByThreeCubeFewestMovesPuzzle::new, new ThreeBldCube(), CORNER, CENTER, EDGE),
	FOUR(4, FourByFourCubePuzzle::new, new FourBldCube(), CORNER, XCENTER, WING),
	FOUR_RAND(4, FourByFourRandomTurnsCubePuzzle::new, new FourBldCube(), CORNER, XCENTER, WING),
	FOUR_BLD(4, NoInspectionFourByFourCubePuzzle::new, new FourBldCube(), CORNER, XCENTER, WING),
	FIVE(5, () -> new CubePuzzle(5), new FiveBldCube(), CORNER, CENTER, EDGE, XCENTER, TCENTER, WING),
	FIVE_BLD(5, NoInspectionFiveByFiveCubePuzzle::new, new FiveBldCube(), CORNER, CENTER, EDGE, TCENTER, WING),
	SIX(6, () -> new CubePuzzle(6), new SixBldCube(), CORNER, XCENTER, WING, INNERXCENTER, INNERWING, LEFTOBLIQUE, RIGHTOBLIQUE),
	SEVEN(7, () -> new CubePuzzle(7), new SevenBldCube(), CORNER, CENTER, EDGE, XCENTER, TCENTER, WING, INNERXCENTER, INNERWING, LEFTOBLIQUE, RIGHTOBLIQUE, INNERTCENTER);

	public static CubicPuzzle fromSize(int size, String flag) {
		for (CubicPuzzle puzzle : values()) {
			if (puzzle.getSize() == size) {
				Matcher puzzleMatcher = Pattern.compile("[A-Z]+_([A-Z]+)").matcher(puzzle.name());
				String puzzleFlag = "";

				if (puzzleMatcher.find()) {
					puzzleFlag = puzzleMatcher.group(1);
				}

				if (puzzleFlag.equals(flag)) {
					return puzzle;
				}
			}
		}

		return THREE;
	}

	public static CubicPuzzle fromSize(int size) {
		return fromSize(size, "");
	}

	private static NotationReader READER_INST = new CubicAlgorithmReader();

	private int size;
	private Supplier<Puzzle> scramblingPuzzleGen;
	private BldPuzzle analyzingPuzzle;
	private PieceType[] types;

	CubicPuzzle(int size, Supplier<Puzzle> scramblingPuzzleGen, BldPuzzle analyzingPuzzle, PieceType... types) {
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

	@Override
	public String toString() {
		return String.join("x", Collections.nCopies(3, String.valueOf(this.size)));
	}
}
