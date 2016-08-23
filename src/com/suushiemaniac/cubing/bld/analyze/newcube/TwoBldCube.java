package com.suushiemaniac.cubing.bld.analyze.newcube;

import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.*;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.CORNER;

public class TwoBldCube extends BldCube {
	@Override
	protected List<PieceType> getPieceTypes() {
		//noinspection ArraysAsListWithZeroOrOneArgument
		return new ArrayList<>(Arrays.asList(CORNER));
	}

	@Override
	protected Map<PieceType, Integer[][]> getDefaultCubies() {
		Map<PieceType, Integer[][]> cubies = new HashMap<>();

		cubies.put(CORNER, SPEFFZ_CORNERS);

		return cubies;
	}

	@Override
	protected void solvePieces(PieceType type) {

	}
}
