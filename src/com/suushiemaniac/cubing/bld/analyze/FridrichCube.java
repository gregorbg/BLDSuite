package com.suushiemaniac.cubing.bld.analyze;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg;
import com.suushiemaniac.cubing.alglib.move.Move;
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.CubicPuzzle;
import com.suushiemaniac.cubing.bld.util.BruteForceUtil;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType.*;

public class FridrichCube extends BldCube {
	public FridrichCube(CubicPuzzle model) {
		super(model);
	}

	public FridrichCube(CubicPuzzle model, Algorithm scramble) {
		super(model, scramble);
	}

	public void parseScramble(Algorithm scramble) {
		this.parseScramble(scramble, true);
	}

	protected void parseScramble(Algorithm scramble, boolean reset) {
		if (reset) {
			this.resetPuzzle();
		}

		this.scramble = scramble;
		this.scramblePuzzle(scramble);
	}

	protected void scramblePuzzle(Algorithm scramble) {
		scramble.stream().filter(move -> this.permutations.keySet().contains(move)).forEach(this::permute);
	}

	protected String bruteForceSolve(Predicate<FridrichCube> checkSolved) {
		if (checkSolved.test(this)) {
			return "Solved already!";
		}

		Set<Move> moveSet = this.permutations.keySet().stream()
				.filter(m -> "UDLRFB".contains(m.getPlane().toFormatString()))
				.filter(m -> m.getDepth() == 1)
				.collect(Collectors.toSet());

		int iter = 1;

		Algorithm orientation = this.getSolvingOrientationPremoves().inverse();

		while (iter < 6) {
			System.out.println("Trying length " + iter + "…");
			List<List<Move>> currentPerm = BruteForceUtil.permute(moveSet, iter, false, true);

			for (List<Move> algorithm : currentPerm) {
				Algorithm tempScramble = new SimpleAlg(algorithm);
				Algorithm insert = new SimpleAlg(orientation).merge(tempScramble);

				this.parseScramble(insert, false);

				if (checkSolved.test(this)) {
					return tempScramble.toFormatString();
				} else {
					this.parseScramble(insert.inverse(), false);
				}
			}

			iter++;
		}

		return "Not found!";
	}

	public String solveCross() {
		return this.bruteForceSolve(FridrichCube::isCrossSolved);
	}

	public boolean isCrossSolved() {
		boolean sidesMatch = IntStream.range(1, 5)
				.allMatch(i -> (this.state.get(EDGE)[4 * i + 2] / 4) == this.state.get(CENTER)[i]);

		boolean bottomMatches = IntStream.range(20, 24)
				.allMatch(i -> (this.state.get(EDGE)[i] / 4) == this.state.get(CENTER)[5]);

		return sidesMatch && bottomMatches;
	}

	public boolean isF2LPairSolved(int slot) {
		int mappedSlot = -1 * slot + 3;
		int bottomSlot = mappedSlot + 20;

		int firstSide = mappedSlot + 1;
		int secondSide = mappedSlot + 2;

		boolean cornerBottomMatches = (this.state.get(CORNER)[bottomSlot] / 4) == this.state.get(CENTER)[5];
		boolean cornerFirstMatches = (this.state.get(CORNER)[firstSide * 4 + 2] / 4) == this.state.get(CENTER)[firstSide];
		boolean cornerSecondMatches = (this.state.get(CORNER)[secondSide * 4 + 3] / 4) == this.state.get(CENTER)[secondSide];

		boolean cornerMatches = cornerBottomMatches && cornerFirstMatches && cornerSecondMatches;

		boolean edgeFirstMatches = (this.state.get(EDGE)[firstSide * 4 + 1] / 4) == this.state.get(CENTER)[firstSide];
		boolean edgeSecondMatches = (this.state.get(EDGE)[secondSide * 4 + 3] / 4) == this.state.get(CENTER)[secondSide];

		boolean edgeMatches = edgeFirstMatches && edgeSecondMatches;

		return cornerMatches && edgeMatches;
	}

	public boolean isOLLSolved() {
		return IntStream.range(0, 4).allMatch(i -> {
			boolean cornerOkay = (this.state.get(CORNER)[i] / 4) == this.state.get(CENTER)[0];
			boolean edgeOkay = (this.state.get(EDGE)[i] / 4) == this.state.get(CENTER)[0];

			return cornerOkay && edgeOkay;
		});
	}

	public boolean isPLLSolved() {
		return IntStream.range(0, 4).allMatch(i -> {
			boolean cornerTopOkay = (this.state.get(CORNER)[i] / 4) == this.state.get(CENTER)[0];

			int cornerFirst = this.state.get(CORNER)[i * 4] / 4;
			int cornerSecond = this.state.get(CORNER)[i * 4 + 1] / 4;

			boolean cornerHeadlightsOkay = cornerFirst == cornerSecond;

			boolean cornerOkay = cornerTopOkay && cornerHeadlightsOkay;

			boolean edgeTopOkay = (this.state.get(EDGE)[i] / 4) == this.state.get(CENTER)[0];

			int edgeSide = this.state.get(EDGE)[i * 4] / 4;

			boolean edgeSideOkay = edgeSide == cornerFirst && edgeSide == cornerSecond;

			boolean edgeOkay = edgeTopOkay && edgeSideOkay;

			return cornerOkay && edgeOkay;
		});
	}

	public boolean isFullySolved() {
		return IntStream.range(0, 6).allMatch(i -> {
			boolean cornerOkay = IntStream.range(0, 4).allMatch(j -> (this.state.get(CORNER)[i * 4 + j] / 4) == this.state.get(CENTER)[i]);
			boolean edgeOkay = IntStream.range(0, 4).allMatch(j -> (this.state.get(EDGE)[i * 4 + j] / 4) == this.state.get(CENTER)[i]);

			return cornerOkay && edgeOkay;
		});
	}
}
