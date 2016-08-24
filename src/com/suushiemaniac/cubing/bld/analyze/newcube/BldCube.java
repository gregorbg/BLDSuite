package com.suushiemaniac.cubing.bld.analyze.newcube;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.*;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public abstract class BldCube extends BldPuzzle {
	protected static final int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5, G = 6, H = 7, I = 8, J = 9, K = 10, L = 11, M = 12, N = 13, O = 14, P = 15, Q = 16, R = 17, S = 18, T = 19, U = 20, V = 21, W = 22, X = 23, Z = -1;
	protected static final int UP = 0, LEFT = 1, FRONT = 2, RIGHT = 3, BACK = 4, DOWN = 5;

	protected static final Integer[][] SPEFFZ_CORNERS = {{A, E, R}, {B, Q, N}, {C, M, J}, {D, I, F}, {L, U, G}, {P, V, K}, {T, W, O}, {H, X, S}};
	protected static final Integer[][] SPEFFZ_CENTERS = {{UP}, {LEFT}, {FRONT}, {RIGHT}, {BACK}, {DOWN}};
	protected static final Integer[][] SPEFFZ_EDGES = {{U, K}, {A, Q}, {B, M}, {C, I}, {D, E}, {R, H}, {T, N}, {L, F}, {J, P}, {V, O}, {W, S}, {X, G}};

	protected static final String[][] REORIENTATIONS = {
			{"", "y'", "", "y", "y2", ""},
			{"z y", "", "z", "", "z y2", "z y'"},
			{"x y2", "x y'", "", "x y", "", "x"},
			{"z' y'", "", "z'", "", "z' y2", "z' y"},
			{"x'", "x' y'", "", "x' y", "", "x' y2"},
			{"", "x2 y'", "z2", "x2 y", "x2", ""}
	};

	@Override
	protected List<PieceType> getPieceTypes() {
		//noinspection ArraysAsListWithZeroOrOneArgument
		return new ArrayList<>(Arrays.asList(CENTER));
	}

	@Override
	protected Map<PieceType, Integer[][]> getDefaultCubies() {
		Map<PieceType, Integer[][]> cubies = new HashMap<>();
		cubies.put(CORNER, SPEFFZ_CENTERS);

		return cubies;
	}

	@Override
	protected Integer getOrientationModelTop() {
		return this.state.get(CENTER)[0];
	}

	@Override
	protected Integer getOrientationModelFront() {
		return this.state.get(CENTER)[2];
	}

	@Override
	protected Algorithm getRotationsFromOrientation(int orientationModelTop, int orientationModelFront) {
		String neededRotations = REORIENTATIONS[orientationModelTop][orientationModelFront];
		return CENTER.getReader().parse(neededRotations);
	}

	@Override
	protected String getRotations() {
		Integer top = this.lastScrambledState.get(CENTER)[0];
		Integer front = this.lastScrambledState.get(CENTER)[2];
		return this.getRotationsFromOrientation(top, front).toFormatString();
	}
}
