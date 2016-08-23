package com.suushiemaniac.cubing.bld.analyze.newcube;

import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public class ThreeBldCube extends TwoBldCube {
	@Override
	protected List<PieceType> getPieceTypes() {
		List<PieceType> superTypes = super.getPieceTypes();
		superTypes.addAll(Arrays.asList(EDGE, CENTER));

		return superTypes;
	}

	@Override
	protected Map<PieceType, Integer[][]> getDefaultCubies() {
		Map<PieceType, Integer[][]> superCubies = super.getDefaultCubies();

		superCubies.put(CENTER, SPEFFZ_CENTERS);
		superCubies.put(EDGE, SPEFFZ_EDGES);

		return superCubies;
	}

	@Override
	protected void solvePieces(PieceType type) {
		super.solvePieces(type);
	}
}
