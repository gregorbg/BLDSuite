package com.suushiemaniac.cubing.bld.analyze;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public class SevenBldCube extends FiveBldCube {
	public SevenBldCube() {
		super();
	}

	public SevenBldCube(Algorithm scramble) {
		super(scramble);
	}

	@Override
	protected List<PieceType> getPermutationPieceTypes() {
		List<PieceType> superTypes = super.getPermutationPieceTypes();
		superTypes.addAll(Arrays.asList(INNERXCENTER, INNERWING, LEFTOBLIQUE, RIGHTOBLIQUE, INNERTCENTER));

		return superTypes;
	}

	@Override
	protected Map<PieceType, Integer[][]> getDefaultCubies() {
		Map<PieceType, Integer[][]> superCubies = super.getDefaultCubies();

		superCubies.put(INNERXCENTER, SPEFFZ_XCENTERS);
		superCubies.put(INNERTCENTER, SPEFFZ_TCENTERS);
		superCubies.put(INNERWING, SPEFFZ_WINGS);
		superCubies.put(LEFTOBLIQUE, SPEFFZ_OBLIQUES);
		superCubies.put(RIGHTOBLIQUE, SPEFFZ_OBLIQUES);

		return superCubies;
	}

	@Override
	protected int getPieceOrientations(PieceType type) {
		return type == XCENTER || type == TCENTER || type == INNERXCENTER || type == INNERTCENTER || type == LEFTOBLIQUE || type == RIGHTOBLIQUE ? 4 : super.getPieceOrientations(type);
	}

	@Override
	protected int getPiecePermutations(PieceType type) {
		return type == XCENTER || type == TCENTER || type == INNERXCENTER || type == INNERTCENTER || type == LEFTOBLIQUE || type == RIGHTOBLIQUE ? 6 : super.getPiecePermutations(type);
	}
}
