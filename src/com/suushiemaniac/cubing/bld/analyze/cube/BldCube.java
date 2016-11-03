package com.suushiemaniac.cubing.bld.analyze.cube;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.util.ArrayUtil;
import com.suushiemaniac.cubing.bld.util.SpeffzUtil;

import java.util.*;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public abstract class BldCube extends BldPuzzle {
	protected static final int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5, G = 6, H = 7, I = 8, J = 9, K = 10, L = 11, M = 12, N = 13, O = 14, P = 15, Q = 16, R = 17, S = 18, T = 19, U = 20, V = 21, W = 22, X = 23, Z = -1;
	protected static final int UP = 0, LEFT = 1, FRONT = 2, RIGHT = 3, BACK = 4, DOWN = 5;

	protected static final Integer[][] SPEFFZ_CORNERS = {{A, E, R}, {B, Q, N}, {C, M, J}, {D, I, F}, {L, U, G}, {P, V, K}, {T, W, O}, {H, X, S}};
	protected static final Integer[][] SPEFFZ_CENTERS = {{UP}, {LEFT}, {FRONT}, {RIGHT}, {BACK}, {DOWN}};
	protected static final Integer[][] SPEFFZ_EDGES = {{U, K}, {A, Q}, {B, M}, {C, I}, {D, E}, {L, F}, {X, G}, {R, H}, {J, P}, {T, N}, {V, O}, {W, S}};
	protected static final Integer[][] SPEFFZ_WINGS = {{U}, {A}, {B}, {C}, {D}, {E}, {F}, {G}, {H}, {I}, {J}, {K}, {L}, {M}, {N}, {O}, {P}, {Q}, {R}, {S}, {T}, {V}, {W}, {X}};
	protected static final Integer[][] SPEFFZ_XCENTERS = {{A, B, C, D}, {E, F, G, H}, {I, J, K, L}, {M, N, O, P}, {Q, R, S, T}, {U, V, W, X}};
	protected static final Integer[][] SPEFFZ_TCENTERS = {{U, V, W, X}, {A, B, C, D}, {E, F, G, H}, {I, J, K, L}, {M, N, O, P}, {Q, R, S, T}};
	protected static final Integer[][] SPEFFZ_OBLIQUES = {{U, V, W, X}, {A, B, C, D}, {E, F, G, H}, {I, J, K, L}, {M, N, O, P}, {Q, R, S, T}};

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
		return this.getRotationsFromOrientation(this.top, this.front);
	}

	public void setSolvingOrientation(int top, int front) {
		if (this.getAdjacentCenters(top).contains(front)) {
			this.top = top;
			this.front = front;
		}
	}

	private Set<Integer> getAdjacentCenters(int center) {
		Integer[][] adjacenceMatrix = {
				{1,2,3,4},
				{0,2,4,5},
				{0,1,3,5}
		};

		int index = Math.min(center, this.getOppositeCenter(center));
		return new HashSet<>(Arrays.asList(adjacenceMatrix[index]));
	}

	private int getOppositeCenter(int center) {
		Integer[] opposites = {5, 3, 4, 1, 2, 0};
		return opposites[center];
	}

	@Override
	protected void solvePieces(PieceType type) {
		while (!this.isSolved(type))
			this.cycleByBuffer(type);

		if (this.cycles.get(type).size() % 2 == 1)
			this.parities.put(type, true);
	}

	protected boolean isSolved(PieceType type) {
		boolean isSolved = true;

		Integer[] state = this.state.get(type);
		Integer[] lastScrambledState = this.lastScrambledState.get(type);
		Integer[][] reference = this.cubies.get(type);

		Boolean[] solvedPieces = this.solvedPieces.get(type);
		Boolean[] preSolvedPieces = this.preSolvedPieces.get(type);
		Boolean[][] misOrientations = this.misOrientedPieces.get(type);

		int divBase = type.getNumPieces() / this.getPiecePermutations(type);
		int modBase = this.getPieceOrientations(type);

		// Check if pieces marked as unsolved haven't been solved yet
		for (int i = 0; i < type.getNumPieces(); i++) {
			if (i == 0 || !solvedPieces[i]) {
				int baseIndex = i / divBase;

				boolean assumeSolved = false;
				for (int j = 0; j < divBase; j++) {
					boolean currentSolved = true;

					for (int k = 0; k < type.getTargetsPerPiece(); k++)
						currentSolved &= state[reference[baseIndex][(i + k) % modBase]].equals(reference[baseIndex][(i + k + j) % modBase]);

					assumeSolved |= currentSolved;
				}

				// Piece is solved and oriented
				if (assumeSolved) {
					solvedPieces[i] = true;

					boolean assumePreSolved = true;

					for (int k = 0; k < type.getTargetsPerPiece(); k++)
						assumePreSolved &= lastScrambledState[reference[baseIndex][(i + k) % modBase]].equals(reference[baseIndex][(i + k) % modBase]);

					preSolvedPieces[i] = assumePreSolved;
				} else {
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

		Boolean breakInOpt = this.avoidBreakIns.get(type);

		int divBase = type.getNumPieces() / this.getPiecePermutations(type);
		int modBase = this.getPieceOrientations(type);

		// If the buffer is solved, replace it with an unsolved corner
		if (solvedPieces[0]) {
			this.increaseCycleCount(type);

			int targetCount = type.getNumPieces();

			int lastTarget = this.getLastTarget(type);
			int currentCycleCount = this.cycles.get(type).size();
			boolean optimizeBreakIns = this.optimizeBreakIns.get(type);

			List<Integer> breakInPerms = optimizeBreakIns && lastTarget > 0 && currentCycleCount % 2 == 1
					? this.getBreakInPermutationsAfter(lastTarget, type)
					: Arrays.asList(ArrayUtil.autobox(ArrayUtil.fill(targetCount))).subList(1, targetCount);

			for (int i = 0; i < type.getNumPiecesNoBuffer() && !pieceCycled; i++) {
				int b = breakInPerms.get(i);

				// First unsolved piece is selected
				if (!solvedPieces[b]) {
					int baseIndex = b / divBase;
					int parts = type.getTargetsPerPiece();

					int bestOrient = optimizeBreakIns && lastTarget > 0 && currentCycleCount % 2 == 1
							? this.getBreakInOrientationsAfter(lastTarget, type)
							: 0;
					bestOrient += modBase - b;
					bestOrient %= parts;

					int[] tempPiece = new int[parts];
					for (int j = 0; j < parts; j++) {
						int extIndex = (b + bestOrient + j) % modBase;

						// Buffer is placed in a temp piece
						tempPiece[j] = state[reference[0][j % modBase]];

						// Buffer is replaced with piece
						state[reference[0][j % modBase]] = state[reference[baseIndex][extIndex]];

						// Piece is replaced with temp piece
						state[reference[baseIndex][extIndex]] = tempPiece[j];
					}

					// Piece cycle is inserted into solution array
					cycles.add(reference[baseIndex][(b + bestOrient) % modBase]);
					pieceCycled = true;
				}
			}
		}

		// If the buffer is not solved, swap it to the position where the piece belongs
		else {
			for (int i = 0; i < this.getPiecePermutations(type) && !pieceCycled; i++) {

				for (int j = 0; j < this.getPieceOrientations(type) && !pieceCycled; j++) {

					int pieceTargets = type.getTargetsPerPiece();
					boolean assumeMatch = true;
					for (int k = 0; k < pieceTargets; k++)
						assumeMatch &= state[reference[0][k]] / divBase == reference[i][(j + k) % modBase] / divBase;

					if (assumeMatch && !solvedPieces[(i * divBase) + (j / pieceTargets)]) {
						int pieceIndex = j;
						if (breakInOpt && state[reference[i][pieceIndex % modBase]] / divBase == reference[0][0] / divBase) {
							for (int k = j + 1; k < divBase; k++) {
								if (!solvedPieces[(i * divBase) + k / pieceTargets] && state[reference[i][k % modBase]] / divBase != reference[0][0] / divBase) {
									pieceIndex = k;
									break;
								}
							}
						}

						for (int k = 0; k < pieceTargets; k++) {
							int currentRot = (pieceIndex + k) % modBase;

							// Buffer piece is replaced with piece
							state[reference[0][k]] = state[reference[i][currentRot]];

							// Piece is solved
							state[reference[i][currentRot]] = reference[i][currentRot];
						}

						// Piece cycle is inserted into solution array
						cycles.add(reference[i][pieceIndex]);
						pieceCycled = true;
					}
				}
			}
		}
	}
}