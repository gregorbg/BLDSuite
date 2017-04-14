package com.suushiemaniac.cubing.bld.analyze;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg;
import com.suushiemaniac.cubing.alglib.move.Move;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.util.ArrayUtil;

import java.util.*;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public class SixBldCube extends FourBldCube {
	@Override
	protected List<PieceType> getOrientationPieceTypes() {
		return new ArrayList<>();
	}

	@Override
	protected List<PieceType> getPermutationPieceTypes() {
		List<PieceType> superTypes = super.getPermutationPieceTypes();
		superTypes.addAll(Arrays.asList(INNERXCENTER, INNERWING, LEFTOBLIQUE, RIGHTOBLIQUE));

		return superTypes;
	}

	@Override
	protected Map<PieceType, Integer[][]> getDefaultCubies() {
		Map<PieceType, Integer[][]> superCubies = super.getDefaultCubies();

		superCubies.put(INNERWING, SPEFFZ_WINGS);
		superCubies.put(INNERXCENTER, SPEFFZ_XCENTERS);
		superCubies.put(LEFTOBLIQUE, SPEFFZ_OBLIQUES);
		superCubies.put(RIGHTOBLIQUE, SPEFFZ_OBLIQUES);

		return superCubies;
	}

	@Override
	protected Algorithm getReorientationMoves() {
		String[] possRotations = {
				"", "y'", "y", "y2",
				"z y", "z", "z y2", "z y'",
				"x y2", "x y'", "x y", "x",
				"z' y'", "z'", "z' y2", "z' y",
				"x'", "x' y'", "x' y", "x' y2",
				"x2 y'", "z2", "x2 y", "x2"
		};
		Map<PieceType, Integer[]> copyMap = new HashMap<>();
		double max = Double.MIN_VALUE;
		int maxIndex = 0;
		for (int i = 0; i < possRotations.length; i++) {
			double solvedCenters = 0, solvedBadCenters = 0;

			for (PieceType type : Arrays.asList(XCENTER, INNERXCENTER, RIGHTOBLIQUE, LEFTOBLIQUE)) {
				Integer[] state = this.state.get(type);
				copyMap.put(type, Arrays.copyOf(state, state.length));

				Integer[] copyState = copyMap.get(type);

				if (i > 0) for (String rotation : possRotations[i].split("\\s")) {
					Move permutation = type.getReader().parse(rotation).firstMove();

					Integer[] perm = this.permutations.get(permutation).get(type);
					Integer[] exchanges = new Integer[perm.length];
					ArrayUtil.fillWith(exchanges, -1);

					for (int j = 0; j < 24; j++) if (perm[j] != Z) exchanges[perm[j]] = copyState[j];
					for (int j = 0; j < 24; j++) if (exchanges[j] != Z) copyState[j] = exchanges[j];
				}

				for (int j = 0; j < copyState.length; j++) {
					if (copyState[j] / 4 == j / 4) {
						solvedCenters++;
						if (j > 15) solvedBadCenters++;
					}
				}
			}

			solvedCenters /= 96.;
			solvedBadCenters /= 32.;
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

	@Override
	protected int getPieceOrientations(PieceType type) {
		return type == XCENTER || type == INNERXCENTER || type == LEFTOBLIQUE || type == RIGHTOBLIQUE ? 4 : super.getPieceOrientations(type);
	}

	@Override
	protected int getPiecePermutations(PieceType type) {
		return type == XCENTER || type == INNERXCENTER || type == LEFTOBLIQUE || type == RIGHTOBLIQUE ? 6 : super.getPiecePermutations(type);
	}
}
