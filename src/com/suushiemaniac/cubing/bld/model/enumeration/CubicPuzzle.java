package com.suushiemaniac.cubing.bld.model.enumeration;

import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.analyze.*;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public enum CubicPuzzle implements TwistyPuzzle {
	TWO(2, new TwoByTwoCubePuzzle(), new TwoBldCube(), CORNER),
	THREE(3, new ThreeByThreeCubePuzzle(), new ThreeBldCube(), CORNER, CENTER, EDGE),
	THREE_BLD(3, new NoInspectionThreeByThreeCubePuzzle(), new ThreeBldCube(), CORNER, CENTER, EDGE),
	THREE_FMC(3, new ThreeByThreeCubeFewestMovesPuzzle(), new ThreeBldCube(), CORNER, CENTER, EDGE),
	FOUR(4, new FourByFourCubePuzzle(), new FourBldCube(), CORNER, XCENTER, WING),
	FOUR_RAND(4, new FourByFourRandomTurnsCubePuzzle(), new FourBldCube(), CORNER, XCENTER, WING),
	FOUR_BLD(4, new NoInspectionFourByFourCubePuzzle(), new FourBldCube(), CORNER, XCENTER, WING),
	FIVE(5, new puzzle.CubePuzzle(5), new FiveBldCube(), CORNER, CENTER, EDGE, XCENTER, TCENTER, WING),
	FIVE_BLD(5, new NoInspectionFiveByFiveCubePuzzle(), new FiveBldCube(), CORNER, CENTER, EDGE, TCENTER, WING),
	SIX(6, new puzzle.CubePuzzle(6), new SixBldCube(), CORNER, XCENTER, WING, INNERXCENTER, INNERWING, LEFTOBLIQUE, RIGHTOBLIQUE),
	SEVEN(7, new puzzle.CubePuzzle(7), new SevenBldCube(), CORNER, CENTER, EDGE, XCENTER, TCENTER, WING, INNERXCENTER, INNERWING, LEFTOBLIQUE, RIGHTOBLIQUE, INNERTCENTER);

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
	private Puzzle scramblingPuzzle;
	private BldPuzzle analyzingPuzzle;
	private PieceType[] types;

	CubicPuzzle(int size, Puzzle scramblingPuzzle, BldPuzzle analyzingPuzzle, PieceType... types) {
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
