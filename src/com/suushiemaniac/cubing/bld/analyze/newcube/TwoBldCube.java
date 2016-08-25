package com.suushiemaniac.cubing.bld.analyze.newcube;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.*;

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
		return new SimpleAlg(); //TODO
	}

	@Override
	protected Map<PieceType, Integer[][]> getDefaultCubies() {
		Map<PieceType, Integer[][]> superCubies = super.getDefaultCubies();
		superCubies.put(CORNER, SPEFFZ_CORNERS);

		return superCubies;
	}
}
