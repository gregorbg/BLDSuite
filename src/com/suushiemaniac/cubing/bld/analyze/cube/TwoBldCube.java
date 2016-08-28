package com.suushiemaniac.cubing.bld.analyze.cube;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.optim.BreakInOptim;
import com.suushiemaniac.cubing.bld.util.ArrayUtil;

import java.util.*;
import java.util.stream.Collectors;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.CORNER;

public class TwoBldCube extends BldCube {
	public TwoBldCube() {
		super();
	}

	public TwoBldCube(Algorithm scramble) {
		super(scramble);
	}

	@Override
	protected List<PieceType> getPermutationPieceTypes() {
		//noinspection ArraysAsListWithZeroOrOneArgument
		return new ArrayList<>(Arrays.asList(CORNER));
	}

	@Override
	protected List<PieceType> getOrientationPieceTypes() {
		return new ArrayList<>();
	}

	@Override
	protected Algorithm getReorientationMoves() {
		String[] reorientation = {
				"x2 y", "z2", "x2 y'", "x2",
				"z'", "z' y", "z' y2", "z' y'",
				"x' y", "x' y2", "x' y'", "x'",
				"z y2", "z y'", "z", "z y",
				"x y'", "x", "x y", "x y2",
				"y", "y2", "y'", ""
		};

		int xPosition = -1;
		Integer[] cornerState = this.state.get(CORNER);
		for (int i = 0; i < 24; i++) if (cornerState[i] == X && xPosition == -1) xPosition = i;

		if (xPosition > -1) {
			String neededRotation = reorientation[xPosition];
			return CORNER.getReader().parse(neededRotation);
		}

		return new SimpleAlg();
	}

	@Override
	protected Map<PieceType, Integer[][]> getDefaultCubies() {
		Map<PieceType, Integer[][]> cubies = new HashMap<>();
		cubies.put(CORNER, SPEFFZ_CORNERS);

		return cubies;
	}

	@Override
	protected List<Integer> getBreakInsAfter(int piece, PieceType type) {
		if (this.algSource == null)
			return super.getBreakInsAfter(piece, type);

		char lastTarget = this.letterSchemes.get(type)[piece].charAt(0);
		List<String> bestTargets = new BreakInOptim(this.algSource).optimizeBreakInTargetsAfter(lastTarget, type);
		return bestTargets.stream().map(t -> ArrayUtil.index(this.letterSchemes.get(type), t)).collect(Collectors.toList());
	}
}
