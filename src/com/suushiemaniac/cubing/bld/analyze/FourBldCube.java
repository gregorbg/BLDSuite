package com.suushiemaniac.cubing.bld.analyze;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg;
import com.suushiemaniac.cubing.alglib.move.Move;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import com.suushiemaniac.cubing.bld.util.ArrayUtil;

import java.util.*;

import static com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType.*;

public class FourBldCube extends BldCube {
	public FourBldCube() {
		super();
	}

	public FourBldCube(Algorithm scramble) {
		super(scramble);
	}

	@Override
	protected List<PieceType> getOrientationPieceTypes() {
		return new ArrayList<>();
	}

	@Override
	protected List<PieceType> getPermutationPieceTypes() {
		return new ArrayList<>(Arrays.asList(CORNER, XCENTER, WING));
	}

	@Override
	protected Map<PieceType, Integer[][]> getDefaultCubies() {
		Map<PieceType, Integer[][]> cubies = new HashMap<>();

		cubies.put(CORNER, SPEFFZ_CORNERS);
		cubies.put(WING, SPEFFZ_WINGS);
		cubies.put(XCENTER, SPEFFZ_XCENTERS);

		return cubies;
	}

	@Override
	protected Algorithm getReorientationMoves() {
		String[] possRotations = {
				"", "y'", "y", "y2", "z y", "z",
				"z y2", "z y'", "x y2", "x y'", "x y", "x",
				"z' y'", "z'", "z' y2", "z' y", "x'", "x' y'",
				"x' y", "x' y2", "x2 y'", "z2", "x2 y", "x2"
		};
		Integer[] copyXCenters = new Integer[24];
		double max = Double.MIN_VALUE;
		int maxIndex = 0;
		for (int i = 0; i < possRotations.length; i++) {
			System.arraycopy(this.state.get(XCENTER), 0, copyXCenters, 0, 24);

			if (i > 0) for (String rotation : possRotations[i].split("\\s")) {
				Move permutation = XCENTER.getReader().parse(rotation).firstMove();

				Integer[] perm = this.permutations.get(permutation).get(XCENTER);
				Integer[] exchanges = new Integer[perm.length];
				ArrayUtil.fillWith(exchanges, -1);

				for (int j = 0; j < exchanges.length; j++) if (perm[j] != -1) exchanges[perm[j]] = copyXCenters[j];
				for (int j = 0; j < exchanges.length; j++) if (exchanges[j] != -1) copyXCenters[j] = exchanges[j];
			}

			double solvedCenters = 0, solvedBadCenters = 0;
			for (int j = 0; j < copyXCenters.length; j++)
				if (copyXCenters[j] / 4 == j / 4) {
					if (j > 15) solvedBadCenters++;
					solvedCenters++;
				}

			solvedCenters /= 24.;
			solvedBadCenters /= 8.;
			double solvedCoeff = (2 * solvedCenters + solvedBadCenters) / 3.;
			if (solvedCoeff > max) {
				max = solvedCoeff;
				maxIndex = i;
			}
		}

		if (maxIndex > 0) {
			String rotation = possRotations[maxIndex];
			return XCENTER.getReader().parse(rotation);
		}

		return new SimpleAlg();
	}

	//TODO maybe refactor the following two methods to be handled internally (if PieceType != referenceArray.length)

	@Override
	protected int getPieceOrientations(PieceType type) {
		return type == XCENTER ? 4 : super.getPieceOrientations(type);
	}

	@Override
	protected int getPiecePermutations(PieceType type) {
		return type == XCENTER ? 6 : super.getPiecePermutations(type);
	}
}
