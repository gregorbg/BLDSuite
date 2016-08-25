package com.suushiemaniac.cubing.bld.analyze.newcube;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public class ThreeBldCube extends TwoBldCube {
	public ThreeBldCube() {
		super();
	}

	public ThreeBldCube(Algorithm scramble) {
		super(scramble);
	}

	@Override
	protected List<PieceType> getPermutationPieceTypes() {
		List<PieceType> superTypes = super.getPermutationPieceTypes();
		//noinspection ArraysAsListWithZeroOrOneArgument
		superTypes.addAll(Arrays.asList(EDGE));

		return superTypes;
	}

	@Override
	protected Map<PieceType, Integer[][]> getDefaultCubies() {
		Map<PieceType, Integer[][]> superCubies = super.getDefaultCubies();
		superCubies.put(EDGE, SPEFFZ_EDGES);

		return superCubies;
	}
}
