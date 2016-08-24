package com.suushiemaniac.cubing.bld.analyze.newcube;

import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.*;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.CORNER;

public class TwoBldCube extends BldCube {
	@Override
	protected List<PieceType> getPieceTypes() {
		List<PieceType> superTypes = super.getPieceTypes();
		//noinspection ArraysAsListWithZeroOrOneArgument
		superTypes.addAll(Arrays.asList(CORNER));

		return superTypes;
	}

	@Override
	protected Map<PieceType, Integer[][]> getDefaultCubies() {
		Map<PieceType, Integer[][]> superCubies = super.getDefaultCubies();
		superCubies.put(CORNER, SPEFFZ_CORNERS);

		return superCubies;
	}

	@Override
	protected void solvePieces(PieceType type) {

	}
}
