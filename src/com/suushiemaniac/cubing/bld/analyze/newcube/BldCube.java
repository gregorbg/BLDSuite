package com.suushiemaniac.cubing.bld.analyze.newcube;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.util.SpeffzUtil;

import java.util.*;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public abstract class BldCube extends BldPuzzle {
	protected static final int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5, G = 6, H = 7, I = 8, J = 9, K = 10, L = 11, M = 12, N = 13, O = 14, P = 15, Q = 16, R = 17, S = 18, T = 19, U = 20, V = 21, W = 22, X = 23, Z = -1;
	protected static final int UP = 0, LEFT = 1, FRONT = 2, RIGHT = 3, BACK = 4, DOWN = 5;

	protected static final Integer[][] SPEFFZ_CORNERS = {{A, E, R}, {B, Q, N}, {C, M, J}, {D, I, F}, {L, U, G}, {P, V, K}, {T, W, O}, {H, X, S}};
	protected static final Integer[][] SPEFFZ_CENTERS = {{UP}, {LEFT}, {FRONT}, {RIGHT}, {BACK}, {DOWN}};
	protected static final Integer[][] SPEFFZ_EDGES = {{U, K}, {A, Q}, {B, M}, {C, I}, {D, E}, {R, H}, {T, N}, {L, F}, {J, P}, {V, O}, {W, S}, {X, G}};

	protected static final String[][] REORIENTATIONS = {
			{"", "y", "", "y'", "y2", ""},
			{"y' z'", "", "z'", "", "y2 z'", "y z'"},
			{"x y2", "y x'", "", "y' x'", "", "x'"},
			{"y z", "", "z", "", "y2 z", "y' z"},
			{"x", "y x", "", "y' x", "", "y2 x"},
			{"", "y x2", "z2", "y' x2", "x2", ""}
	};

	protected Algorithm solvingOrientationPremoves;
	protected int top, front;

	public BldCube() {
		super();
		this.top = 0;
		this.front = 2;
		this.solvingOrientationPremoves = new SimpleAlg();
	}

	public BldCube(Algorithm scramble) {
		super(scramble);
		this.top = 0;
		this.front = 2;
		this.solvingOrientationPremoves = new SimpleAlg();
	}

	@Override
	protected List<PieceType> getOrientationPieceTypes() {
		//noinspection ArraysAsListWithZeroOrOneArgument
		return new ArrayList<>(Arrays.asList(CENTER));
	}

	@Override
	protected Map<PieceType, Integer[][]> getDefaultCubies() {
		Map<PieceType, Integer[][]> cubies = new HashMap<>();
		cubies.put(CENTER, SPEFFZ_CENTERS);

		return cubies;
	}

	@Override
	protected Map<PieceType, String[]> initSchemes() {
		Map<PieceType, String[]> schemes = new HashMap<>();

		for (PieceType type : this.getPieceTypes())
			schemes.put(type, SpeffzUtil.FULL_SPEFFZ);

		return schemes;
	}

	protected Algorithm getRotationsFromOrientation(int orientationModelTop, int orientationModelFront) {
		String neededRotations = REORIENTATIONS[orientationModelTop][orientationModelFront];
		return CENTER.getReader().parse(neededRotations);
	}

	@Override
	protected Algorithm getReorientationMoves() {
		Integer top = this.lastScrambledState.get(CENTER)[0];
		Integer front = this.lastScrambledState.get(CENTER)[2];
		return this.getRotationsFromOrientation(top, front);
	}

	@Override
	protected Algorithm getSolvingOrientationPremoves() {
		return this.getRotationsFromOrientation(this.top, this.front).inverse();
	}

	public void setSolvingOrientation(int top, int front) {
		if (!this.getAdjacentCenters(top).contains(front))
			return;

		this.top = top;
		this.front = front;
	}

	private Set<Integer> getAdjacentCenters(int center) {
		//0: 1,2,3,4
		//5: 1,2,3,4
		//1: 0,2,4,5
		//3: 0,2,4,5
		//2: 0,1,3,5
		//4: 0,1,3,5
		//TODO
		return Collections.emptySet();
	}

	@Override
	protected void solvePieces(PieceType type) {
		while (!this.isSolved(type))
			this.cycleByBuffer(type);
	}

	protected boolean isSolved(PieceType type) {
		boolean isSolved = true;

		Integer[] state = this.state.get(type);
		Integer[][] reference = this.cubies.get(type);

		Boolean[] solvedPieces = this.solvedPieces.get(type);
		Boolean[][] misOrientations = this.misOrientedPieces.get(type);

		// Check if pieces marked as unsolved haven't been solved yet
		for (int i = 0; i < type.getNumPieces(); i++) {
			if (i == 0 || !solvedPieces[i]) {

				boolean assumeSolved = true;
				for (int j = 0; j < type.getTargetsPerPiece(); j++)
					assumeSolved &= state[reference[i][j]].equals(reference[i][j]);

				// Piece is solved and oriented
				if (assumeSolved)
					solvedPieces[i] = true;
				else {
					// Piece is in correct position but needs to be rotated
					boolean isRotated = false;

					//Rotations
					int rotations = type.getTargetsPerPiece();
					for (int j = 1; j < rotations && !isRotated; j++) {
						boolean assumeRotated = true;

						for (int k = 0; k < rotations; k++)
							assumeRotated &= state[reference[i][k]].equals(reference[i][(k + j) % rotations]);

						if (assumeRotated) {
							isRotated = true;
							solvedPieces[i] = true;
							misOrientations[j][i] = true;
						}
					}

					if (!isRotated) {
						solvedPieces[i] = false;
						isSolved = false;
					}
				}
			}
		}

		return isSolved;
	}

	protected void cycleByBuffer(PieceType type) {
		boolean pieceCycled = false;

		Integer[] state  = this.state.get(type);
		Integer[][] reference = this.cubies.get(type);

		Boolean[] solvedPieces = this.solvedPieces.get(type);

		List<Integer> cycles = this.cycles.get(type);

		// If the buffer is solved, replace it with an unsolved corner
		if (solvedPieces[0]) {
			this.increaseCycleCount(type);
			// First unsolved piece is selected

			for (int i = 1; i < type.getNumPieces() && !pieceCycled; i++) {
				if (!solvedPieces[i]) {
					// Buffer is placed in a... um... buffer
					int parts = type.getTargetsPerPiece();
					int[] tempPiece = new int[parts];

					for (int j = 0; j < parts; j++) {
						tempPiece[j] = state[reference[0][j]];

						// Buffer piece is replaced with corner
						state[reference[0][j]] = state[reference[i][j]];

						// Piece is replaced with buffer
						state[reference[i][j]] = tempPiece[j];
					}

					// Piece cycle is inserted into solution array
					cycles.add(reference[i][0]);
					pieceCycled = true;
				}
			}
		}

		// If the buffer is not solved, swap it to the position where the piece belongs
		else { //TODO Edge parities
			for (int i = 0; i < type.getNumPieces() && !pieceCycled; i++) {

				int parts = type.getTargetsPerPiece();
				for (int j = 0; j < parts && !pieceCycled; j++) {

					boolean assumeMatch = true;
					for (int k = 0; k < parts; k++)
						assumeMatch &= state[reference[0][k]].equals(reference[i][(j + k) % parts]);

					if (assumeMatch) {

						for (int k = 0; k < parts; k++) {
							int currentRot = (j + k) % parts;

							// Buffer piece is replaced with piece
							state[reference[0][k]] = state[reference[i][currentRot]];

							// Piece is solved
							state[reference[i][currentRot]] = reference[i][currentRot];
						}

						// Piece cycle is inserted into solution array
						cycles.add(reference[i][j % parts]);
						pieceCycled = true;
					}
				}
			}
		}
	}
}