package com.suushiemaniac.cubing.bld.analyze;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg;
import com.suushiemaniac.cubing.alglib.move.Move;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.CubicPuzzle;
import com.suushiemaniac.cubing.bld.optim.BreakInOptim;
import com.suushiemaniac.cubing.bld.util.ArrayUtil;

import java.util.*;
import java.util.stream.Collectors;

import static com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType.*;

public class BldCube extends BldPuzzle {
	protected static final int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5, G = 6, H = 7, I = 8, J = 9, K = 10, L = 11, M = 12, N = 13, O = 14, P = 15, Q = 16, R = 17, S = 18, T = 19, U = 20, V = 21, W = 22, X = 23;

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

	protected CornerParityMethod cornerParityMethod;
	protected ReorientMethod reorientMethod;

	protected List<PieceType> cornerParityDependents;
	protected List<PieceType> executionOrder;

	protected int top, front;

	public BldCube(CubicPuzzle model) {
		super(model);

		this.top = 0;
		this.front = 2;

		this.solvingOrientationPremoves = new SimpleAlg();

		this.cornerParityMethod = CornerParityMethod.SWAP_UB_UL;
		this.cornerParityDependents = this.getCornerParityDependents();

		this.reorientMethod = ReorientMethod.DYNAMIC;

		this.executionOrder = new ArrayList<>(Arrays.asList(TCENTER, XCENTER, WING, EDGE, CORNER));
	}

	public BldCube(CubicPuzzle model, Algorithm scramble) {
		this(model);

		this.parseScramble(scramble);
	}

	protected List<PieceType> getCornerParityDependents() {
		List<PieceType> allDeps = new ArrayList<>(Arrays.asList(EDGE, WING, INNERWING));
		allDeps.retainAll(Arrays.asList(this.model.getPieceTypes()));

		return allDeps;
	}

	protected List<PieceType> getDynamicReorientPieceTypes() {
		List<PieceType> dynamicTypes = new ArrayList<>(Arrays.asList(XCENTER, TCENTER, INNERXCENTER, INNERTCENTER, LEFTOBLIQUE, RIGHTOBLIQUE));
		dynamicTypes.retainAll(Arrays.asList(this.model.getPieceTypes()));

		return dynamicTypes;
	}

	@Override
	protected List<PieceType> getOrientationPieceTypes() {
		if (Arrays.asList(this.model.getPieceTypes()).contains(CENTER)) {
			//noinspection ArraysAsListWithZeroOrOneArgument
			return Arrays.asList(CENTER);
		}

		return new ArrayList<>();
	}

	@Override
	protected List<PieceType> getPermutationPieceTypes() {
		List<PieceType> pieceTypes = new ArrayList<>(Arrays.asList(this.model.getPieceTypes()));
		pieceTypes.removeAll(this.getOrientationPieceTypes());

		return pieceTypes;
	}

	@Override
	protected List<PieceType> getExecutionOrderPieceTypes() {
		List<PieceType> definitiveOrder = new ArrayList<>(this.executionOrder);
		List<PieceType> permutations = this.getPermutationPieceTypes();

		definitiveOrder.retainAll(permutations);
		permutations.removeAll(definitiveOrder);
		definitiveOrder.addAll(permutations);

		return definitiveOrder;
	}

	public void setExecutionOrderPieceTypes(List<PieceType> types) {
		this.executionOrder = types;
	}

	@Override
	protected Map<PieceType, Integer[][]> getDefaultCubies() {
		Map<PieceType, Integer[][]> cubies = new HashMap<>();
		cubies.put(CENTER, SPEFFZ_CENTERS);
		cubies.put(CORNER, SPEFFZ_CORNERS);
		cubies.put(EDGE, SPEFFZ_EDGES);
		cubies.put(WING, SPEFFZ_WINGS);
		cubies.put(XCENTER, SPEFFZ_XCENTERS);
		cubies.put(TCENTER, SPEFFZ_TCENTERS);
		cubies.put(INNERWING, SPEFFZ_WINGS);
		cubies.put(RIGHTOBLIQUE, SPEFFZ_OBLIQUES);
		cubies.put(LEFTOBLIQUE, SPEFFZ_OBLIQUES);
		cubies.put(INNERXCENTER, SPEFFZ_XCENTERS);
		cubies.put(INNERTCENTER, SPEFFZ_TCENTERS);

		Map<PieceType, Integer[][]> cubeCubies = new HashMap<>();

		for (PieceType type : this.getPieceTypes(true)) {
			cubeCubies.put(type, cubies.get(type));
		}

		return cubeCubies;
	}

	public Algorithm getRotationsFromOrientation(int orientationModelTop, int orientationModelFront) {
		String neededRotations = REORIENTATIONS[orientationModelTop][orientationModelFront];
		return CENTER.getReader().parse(neededRotations);
	}

	@Override
	protected Algorithm getReorientationMoves() {
		if (this.getOrientationPieceTypes().contains(CENTER)) {
			Integer top = this.lastScrambledState.get(CENTER)[0];
			Integer front = this.lastScrambledState.get(CENTER)[2];

			return this.getRotationsFromOrientation(top, front);
		} else if (this.getReorientMethod() == ReorientMethod.FIXED_DLB_CORNER) {
			String[] reorientation = {
					"x2 y", "z2", "x2 y'", "x2",
					"z'", "z' y", "z' y2", "z' y'",
					"x' y", "x' y2", "x' y'", "x'",
					"z y2", "z y'", "z", "z y",
					"x y'", "x", "x y", "x y2",
					"y", "y2", "y'", ""
			};

			int xPosition = ArrayUtil.INSTANCE.index(this.state.get(CORNER), X);

			if (xPosition > -1) {
				String neededRotation = reorientation[xPosition];
				return CORNER.getReader().parse(neededRotation);
			}

			return new SimpleAlg();
		} else if (this.getReorientMethod() == ReorientMethod.DYNAMIC) {
			String[] possRotations = {
					"", "y'", "y", "y2",
					"z y", "z", "z y2", "z y'",
					"x y2", "x y'", "x y", "x",
					"z' y'", "z'", "z' y2", "z' y",
					"x'", "x' y'", "x' y", "x' y2",
					"x2 y'", "z2", "x2 y", "x2"
			};

			double max = Double.MIN_VALUE;
			int maxIndex = 0;

			List<PieceType> orientationTypes = this.getDynamicReorientPieceTypes();

			for (int i = 0; i < possRotations.length; i++) {
				double solvedCenters = 0, solvedBadCenters = 0, totalCheckedCenters = 0;

				for (PieceType type : orientationTypes) {
					Integer[] state = this.state.get(type);
					Integer[] copyState = Arrays.copyOf(state, state.length);

					totalCheckedCenters += state.length;

					if (i > 0) {
						Algorithm rotation = type.getReader().parse(possRotations[i]);

						for (Move permutation : rotation) {
							Integer[] perm = this.permutations.get(permutation).get(type);
							this.applyPermutations(copyState, perm);
						}
					}

					for (int j = 0; j < copyState.length; j++) {
						int norm = this.getPieceOrientations(type);

						if (copyState[j] / norm == j / norm) {
							solvedCenters++;
							if (j >= 2 * (copyState.length / 3)) solvedBadCenters++;
						}
					}
				}

				solvedCenters /= totalCheckedCenters;
				solvedBadCenters /= (totalCheckedCenters / 3.);

				double solvedCoeff = (2 * solvedCenters + solvedBadCenters) / 3.;

				if (solvedCoeff > max) {
					max = solvedCoeff;
					maxIndex = i;
				}
			}

			if (maxIndex > 0) {
				String rotation = possRotations[maxIndex];
				return this.model.getReader().parse(rotation);
			}

			return new SimpleAlg();
		}

		return new SimpleAlg();
	}

	@Override
	protected Algorithm getSolvingOrientationPremoves() {
		return this.getRotationsFromOrientation(this.top, this.front);
	}

	public boolean setSolvingOrientation(int top, int front) {
		if (this.getAdjacentCenters(top).contains(front)) {
			this.top = top;
			this.front = front;

			return true;
		}

		return false;
	}

	public CornerParityMethod getCornerParityMethod() {
		return this.cornerParityMethod;
	}

	public void setCornerParityMethod(CornerParityMethod cornerParityMethod) {
		this.cornerParityMethod = cornerParityMethod;
		this.resolve();
	}

	public ReorientMethod getReorientMethod() {
		return this.reorientMethod;
	}

	public void setReorientMethod(ReorientMethod reorientMethod) {
		this.reorientMethod = reorientMethod;
		this.resolve();
	}

	protected void swapParityTargets(PieceType type) { // FIXME is this working really?
		Integer[] state = this.state.get(type);

		Map<Integer, Integer> swaps = new HashMap<>();
		swaps.put(A, D);
		swaps.put(Q, E);

		for (Map.Entry<Integer, Integer> swap : swaps.entrySet()) {
			ArrayUtil.INSTANCE.swap(state, ArrayUtil.INSTANCE.index(state, swap.getKey()), ArrayUtil.INSTANCE.index(state, swap.getValue()));
		}
	}

	@Override
	protected int getOrientationSideCount() {
		return 6;
	}

	protected Set<Integer> getAdjacentCenters(int center) {
		Integer[][] adjacenceMatrix = {
				{1, 2, 3, 4},
				{0, 2, 4, 5},
				{0, 1, 3, 5}
		};

		int index = Math.min(center, this.getOppositeCenter(center));
		return new HashSet<>(Arrays.asList(adjacenceMatrix[index]));
	}

	protected int getOppositeCenter(int center) {
		Integer[] opposites = {5, 3, 4, 1, 2, 0};
		return opposites[center];
	}

	@Override
	protected List<Integer> getBreakInPermutationsAfter(int piece, PieceType type) {
		if (this.algSource == null)
			return super.getBreakInPermutationsAfter(piece, type);

		if (this.optim == null)
			this.optim = new BreakInOptim(this.algSource, this, false);

		String lastTarget = this.letterSchemes.get(type)[piece];
		List<String> bestTargets = this.optim.optimizeBreakInTargetsAfter(lastTarget, type);
		List<Integer> breakInPerms = bestTargets.stream()
				.map(t -> ArrayUtil.INSTANCE.index(this.letterSchemes.get(type), t))
				.filter(i -> i > -1)
				.map(i -> ArrayUtil.INSTANCE.deepOuterIndex(this.cubies.get(type), i))
				.filter(i -> i > -1)
				.distinct()
				.collect(Collectors.toList());

		List<Integer> expected = new ArrayList<>(super.getBreakInPermutationsAfter(piece, type));
		expected.removeAll(breakInPerms);

		breakInPerms.addAll(expected);

		return breakInPerms;
	}

	@Override
	protected int getBreakInOrientationsAfter(int piece, PieceType type) {
		if (this.algSource == null)
			return super.getBreakInOrientationsAfter(piece, type);

		if (this.optim == null)
			this.optim = new BreakInOptim(this.algSource, this, false);

		String lastTarget = this.letterSchemes.get(type)[piece];
		List<String> bestTargets = this.optim.optimizeBreakInTargetsAfter(lastTarget, type);
		List<Integer> breakInOrients = bestTargets.stream()
				.map(t -> ArrayUtil.INSTANCE.index(this.letterSchemes.get(type), t))
				.filter(i -> i > -1)
				.map(i -> ArrayUtil.INSTANCE.deepInnerIndex(this.cubies.get(type), i))
				.filter(i -> i > -1)
				.collect(Collectors.toList());

		List<Integer> expected = new ArrayList<>(Arrays.asList(ArrayUtil.INSTANCE.filledArray(type.getTargetsPerPiece())));
		expected.removeAll(breakInOrients);

		breakInOrients.addAll(expected);

		return breakInOrients.get(0);
	}

	@Override
	protected void solvePieces(PieceType type) {
		if (this.cornerParityDependents.contains(type)) {
			if (!this.isSolved(CORNER)) {
				this.solvePieces(CORNER);
			}

			if (this.hasParity(CORNER) && this.getCornerParityMethod() == CornerParityMethod.SWAP_UB_UL) {
				this.swapParityTargets(type);
			}
		}

		while (!this.isSolved(type)) {
			this.cycleByBuffer(type);
		}

		if (this.cycles.get(type).size() % 2 == 1) {
			this.parities.put(type, true);
		}
	}

	@Override
	public boolean hasParity(PieceType type) {
		if (type == CubicPieceType.EDGE) {
			return this.hasParity(CORNER);
		}

		return super.hasParity(type);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	protected boolean isSolved(PieceType type) {
		boolean isSolved = true;

		Integer[] state = this.state.get(type);
		Integer[] lastScrambledState = this.lastScrambledState.get(type);
		Integer[][] ref = this.cubies.get(type);

		Boolean[] solvedPieces = this.solvedPieces.get(type);
		Boolean[] preSolvedPieces = this.preSolvedPieces.get(type);
		Boolean[][] misOrientations = this.misOrientedPieces.get(type);

		Integer currentCycleLength = this.cycles.get(type).size();

		int divBase = type.getNumPieces() / this.getPiecePermutations(type);
		int modBase = this.getPieceOrientations(type);

		// Check if pieces marked as unsolved haven't been preSolved yet
		for (int i = 0; i < type.getNumPieces(); i++) {
			if (i == 0 || !solvedPieces[i]) {
				int baseIndex = i / divBase;

				boolean assumeSolved = false;

				for (int j = 0; j < divBase; j++) {
					boolean currentSolved = true;

					for (int k = 0; k < type.getTargetsPerPiece(); k++) {
						currentSolved &= state[ref[baseIndex][(i + k) % modBase]].equals(ref[baseIndex][(i + k + j) % modBase]);
					}

					assumeSolved |= currentSolved;
				}

				// Piece is preSolved and oriented
				if (assumeSolved) {
					solvedPieces[i] = true;

					boolean assumePreSolved = true;

					for (int k = 0; k < type.getTargetsPerPiece(); k++) {
						assumePreSolved &= lastScrambledState[ref[baseIndex][(i + k) % modBase]].equals(ref[baseIndex][(i + k) % modBase]);
					}

					preSolvedPieces[i] = assumePreSolved;
				} else {
					// Piece is in correct position but needs to be rotated
					boolean isRotated = false;

					//Rotations
					int rotations = type.getTargetsPerPiece();

					for (int j = 1; j < rotations && !isRotated; j++) {
						boolean assumeRotated = true;

						for (int k = 0; k < rotations; k++) {
							assumeRotated &= state[ref[i][k]].equals(ref[i][(k + j) % rotations]);
						}

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

		if (!isSolved && solvedPieces[0] && currentCycleLength % 2 == 0) {
			int unsolvedCount = ArrayUtil.INSTANCE.countOf(solvedPieces, false);

			if (unsolvedCount > 2) {
				Map<Integer, Integer> bufferFloats = this.bufferFloats.get(type);
				Queue<Integer> floatingBuffers = new LinkedList<>(this.backupBuffers.get(type));

				Integer nextBufferFloat;
				int floatPosition;

				do {
					nextBufferFloat = floatingBuffers.poll();
					floatPosition = ArrayUtil.INSTANCE.deepOuterIndex(ref, nextBufferFloat);
				} while (nextBufferFloat != null
						&& (solvedPieces[floatPosition] || bufferFloats.containsValue(nextBufferFloat)));

				if (nextBufferFloat != null && nextBufferFloat >= 0) {
					this.cycleCubiesForBuffer(type, nextBufferFloat);

					for (int i = 0; i < floatPosition; i++) {
						ArrayUtil.INSTANCE.cycleLeft(solvedPieces);

						for (int j = 0; j < type.getTargetsPerPiece(); j++) {
							ArrayUtil.INSTANCE.cycleLeft(misOrientations[j]);
						}
					}

					bufferFloats.put(currentCycleLength, nextBufferFloat);
				}
			}
		}

		return isSolved;
	}

	protected void cycleByBuffer(PieceType type) {
		boolean pieceCycled = false;

		Integer[] state = this.state.get(type);
		Integer[][] ref = this.cubies.get(type);

		Boolean[] solvedPieces = this.solvedPieces.get(type);

		List<Integer> cycles = this.cycles.get(type);

		Boolean avoidBreakIns = this.avoidBreakIns.get(type);
		Boolean optimizeBreakIns = this.optimizeBreakIns.get(type);

		int currentCycleLength = this.cycles.get(type).size();

		int divBase = type.getNumPieces() / this.getPiecePermutations(type);
		int modBase = this.getPieceOrientations(type);

		// If the buffer is preSolved, replace it with an unsolved corner
		if (solvedPieces[0]) {
			this.increaseCycleCount(type);

			int lastTarget = this.getLastTarget(type);

			List<Integer> breakInPerms = optimizeBreakIns && lastTarget > 0 && currentCycleLength % 2 == 1
					? this.getBreakInPermutationsAfter(lastTarget, type)
					: super.getBreakInPermutationsAfter(lastTarget, type);

			for (int pieceIndex = 0; pieceIndex < type.getNumPiecesNoBuffer() && !pieceCycled; pieceIndex++) {
				int piece = breakInPerms.get(pieceIndex);

				// First unsolved piece is selected
				if (!solvedPieces[piece]) {
					int baseIndex = piece / divBase;
					int parts = type.getTargetsPerPiece();

					int bestOrient = optimizeBreakIns && lastTarget > 0 && currentCycleLength % 2 == 1
							? this.getBreakInOrientationsAfter(lastTarget, type)
							: super.getBreakInOrientationsAfter(lastTarget, type);

					bestOrient += modBase - piece;
					bestOrient %= parts;

					int[] tempPiece = new int[parts];

					for (int targetFaces = 0; targetFaces < parts; targetFaces++) {
						int extIndex = (piece + bestOrient + targetFaces) % modBase;

						// Buffer is placed in a temp piece
						tempPiece[targetFaces] = state[ref[0][targetFaces % modBase]];

						// Buffer is replaced with piece
						state[ref[0][targetFaces % modBase]] = state[ref[baseIndex][extIndex]];

						// Piece is replaced with temp piece
						state[ref[baseIndex][extIndex]] = tempPiece[targetFaces];
					}

					// Piece cycle is inserted into solution array
					cycles.add(ref[baseIndex][(piece + bestOrient) % modBase]);

					// set flag to break out of loop
					pieceCycled = true;
				}
			}
		}

		// If the buffer is not preSolved, swap it to the position where the piece belongs
		else {
			for (int permutation = 0; permutation < this.getPiecePermutations(type) && !pieceCycled; permutation++) {
				for (int orientation = 0; orientation < this.getPieceOrientations(type) && !pieceCycled; orientation++) {
					int pieceTargets = type.getTargetsPerPiece();

					boolean assumeMatch = true;

					for (int targetFaces = 0; targetFaces < pieceTargets; targetFaces++) {
						int currentlyInBuffer = state[ref[0][targetFaces]] / divBase;
						int currentLoopTarget = ref[permutation][(targetFaces + orientation) % modBase] / divBase;

						assumeMatch &= currentlyInBuffer == currentLoopTarget;
					}

					if (assumeMatch && !solvedPieces[(permutation * divBase) + (orientation / pieceTargets)]) {
						int pieceIndex = orientation;

						if (avoidBreakIns) {
							int aimedTarget = state[ref[permutation][pieceIndex % modBase]] / divBase;
							int normalizedBuffer = ref[0][0] / divBase;

							if (aimedTarget == normalizedBuffer) {
								for (int alternative = orientation + 1; alternative < divBase; alternative++) {
									boolean alternativeSolved = solvedPieces[(permutation * divBase) + (alternative / pieceTargets)];

									int alternativePiece = state[ref[permutation][alternative % modBase]] / divBase;
									boolean alternativeSuitable = alternativePiece != normalizedBuffer;

									if (!alternativeSolved && alternativeSuitable) {
										pieceIndex = alternative;
										break;
									}
								}
							}
						}

						for (int targetFaces = 0; targetFaces < pieceTargets; targetFaces++) {
							int currentTarget = (pieceIndex + targetFaces) % modBase;

							// Buffer piece is replaced with piece
							state[ref[0][targetFaces]] = state[ref[permutation][currentTarget]];

							// Piece is solved
							state[ref[permutation][currentTarget]] = ref[permutation][currentTarget];
						}

						// Piece cycle is inserted into solution array
						cycles.add(ref[permutation][pieceIndex]);

						// set flag to break out of loop
						pieceCycled = true;
					}
				}
			}
		}
	}

	protected int getPieceOrientations(PieceType type) {
		if (type.getNumPieces() != this.cubies.get(type).length) {
			return this.cubies.get(type)[0].length;
		}

		return type.getTargetsPerPiece();
	}

	protected int getPiecePermutations(PieceType type) {
		if (type.getNumPieces() != this.cubies.get(type).length) {
			return this.getOrientationSideCount();
		}

		return type.getNumPieces();
	}

	public enum CornerParityMethod {
		SWAP_UB_UL, APPLY_ALGORITHM
	}

	public enum ReorientMethod {
		DYNAMIC, FIXED_DLB_CORNER
	}
}