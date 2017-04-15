package com.suushiemaniac.cubing.bld.analyze;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.optim.BreakInOptim;
import com.suushiemaniac.cubing.bld.util.ArrayUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public class ThreeBldCube extends BldCube {
	public ThreeBldCube() {
		super();
	}

	public ThreeBldCube(Algorithm scramble) {
		super(scramble);
	}

	@Override
	protected List<PieceType> getPermutationPieceTypes() {
		return new ArrayList<>(Arrays.asList(EDGE, CORNER));
	}

	@Override
	protected Map<PieceType, Integer[][]> getDefaultCubies() {
		Map<PieceType, Integer[][]> superCubies = super.getDefaultCubies();
		superCubies.put(CORNER, SPEFFZ_CORNERS);
		superCubies.put(EDGE, SPEFFZ_EDGES);

		return superCubies;
	}

	@Override
	protected void solvePieces(PieceType type) {
		if (type == EDGE) {
			if (!this.isSolved(CORNER))
				this.solvePieces(CORNER);

			if (this.parities.get(CORNER) && this.getCornerParityMethod() == CornerParityMethod.SWAP_UB_UL)
				this.swapParityEdges();
		}

		super.solvePieces(type);
	}

	private void swapParityEdges() {
		int UB = -1;
		int UL = -1;

		Integer[] edges = this.state.get(EDGE);
		Integer[][] edgeCubies = this.cubies.get(EDGE);

		// Positions of UB and UL edges are found
		for (int i = 0; i < EDGE.getNumPieces() && (UB == -1 || UL == -1); i++) {
			if ((edges[edgeCubies[i][0]] == A && edges[edgeCubies[i][1]] == Q) || (edges[edgeCubies[i][1]] == A && edges[edgeCubies[i][0]] == Q))
				UB = i;
			if ((edges[edgeCubies[i][0]] == D && edges[edgeCubies[i][1]] == E) || (edges[edgeCubies[i][1]] == D && edges[edgeCubies[i][0]] == E))
				UL = i;
		}

		// UB is stored in buffer
		int[] tempEdge = {edges[edgeCubies[UB][0]], edges[edgeCubies[UB][1]]};

		// Make sure that UB goes to UL and BU goes to LU
		int index = 0;
		if ((edges[edgeCubies[UB][0]] == A && edges[edgeCubies[UL][0]] == E) || (edges[edgeCubies[UB][0]] == Q && edges[edgeCubies[UL][0]] == D))
			index = 1;

		// UL is placed in UB
		edges[edgeCubies[UB][0]] = edges[edgeCubies[UL][index]];
		edges[edgeCubies[UB][1]] = edges[edgeCubies[UL][(index + 1) % 2]];

		// buffer is placed in UL
		edges[edgeCubies[UL][0]] = tempEdge[index];
		edges[edgeCubies[UL][1]] = tempEdge[(index + 1) % 2];
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
				.map(t -> ArrayUtil.index(this.letterSchemes.get(type), t))
				.filter(i -> i > -1)
				.map(i -> ArrayUtil.deepOuterIndex(this.cubies.get(type), i))
				.filter(i -> i > -1)
				.distinct()
				.collect(Collectors.toList());

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
				.map(t -> ArrayUtil.index(this.letterSchemes.get(type), t))
				.filter(i -> i > -1)
				.map(i -> ArrayUtil.deepInnerIndex(this.cubies.get(type), i))
				.filter(i -> i > -1)
				.collect(Collectors.toList());

		return breakInOrients.get(0); //TODO why discard everything else here and simply take [0] ??
	}
}
