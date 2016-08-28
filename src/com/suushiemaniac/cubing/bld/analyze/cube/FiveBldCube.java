package com.suushiemaniac.cubing.bld.analyze.cube;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public class FiveBldCube extends ThreeBldCube {
	public FiveBldCube() {
		super();
	}

	public FiveBldCube(Algorithm scramble) {
		super(scramble);
	}

	@Override
	protected List<PieceType> getPermutationPieceTypes() {
		List<PieceType> superTypes = super.getPermutationPieceTypes();
		superTypes.addAll(Arrays.asList(XCENTER, TCENTER, WING));

		return superTypes;
	}

	@Override
	protected Map<PieceType, Integer[][]> getDefaultCubies() {
		Map<PieceType, Integer[][]> superTypes = super.getDefaultCubies();

		superTypes.put(XCENTER, SPEFFZ_XCENTERS);
		superTypes.put(TCENTER, SPEFFZ_TCENTERS);
		superTypes.put(WING, SPEFFZ_WINGS);

		return superTypes;
	}

	public static boolean solves(PieceType type, String algString, String letterPair) { //TODO
		return algString.length() % 2 == 1;
	}
}
