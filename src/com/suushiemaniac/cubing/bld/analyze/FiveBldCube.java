package com.suushiemaniac.cubing.bld.analyze;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType.*;

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

	@Override
	protected int getPieceOrientations(PieceType type) {
		return type == XCENTER || type == TCENTER ? 4 : super.getPieceOrientations(type);
	}

	@Override
	protected int getPiecePermutations(PieceType type) {
		return type == XCENTER || type == TCENTER ? 6 : super.getPiecePermutations(type);
	}
}
