package com.suushiemaniac.cubing.bld.analyze.newcube;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.move.Move;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.util.ArrayUtil;
import com.suushiemaniac.lang.json.JSON;

import java.io.File;
import java.net.URL;
import java.util.*;

public abstract class BldPuzzle {
    protected Algorithm scramble;
    protected Algorithm solvingOrientationPremoves;

    protected Map<Move, Map<PieceType, Integer[]>> permutations;
	protected Map<PieceType, Integer[]> state;
	protected Map<PieceType, Integer[]> lastScrambledState;
	protected Map<PieceType, Integer[][]> cubies;

	protected Map<PieceType, List<Integer>> cycles;
	protected Map<PieceType, Integer> cycleCount;
	protected Map<PieceType, Boolean[]> solvedPieces;

	protected Map<PieceType, String[]> letterSchemes;

	public BldPuzzle() {
		this.permutations = this.loadPermutations();

		this.state = this.initState();
		this.lastScrambledState = this.initState();

		this.cubies = this.initCubies();

		this.cycles = this.emptyCycles();
		this.cycleCount = this.emptyCycleCount();
		this.solvedPieces = this.emptySolvedPieces();

		this.letterSchemes = this.initSchemes();
	}

	private Map<Move, Map<PieceType, Integer[]>> loadPermutations() {
		String filename = "permutations/" + getClass().getSimpleName() + ".json";
		URL fileURL = getClass().getResource(filename);
		File jsonFile = new File(fileURL.getFile());

		JSON json = JSON.fromFile(jsonFile);
		Map<Move, Map<PieceType, Integer[]>> permutations = new HashMap<>();

		for (String key : json.nativeKeySet()) {
			Map<PieceType, Integer[]> typeMap = new HashMap<>();
			JSON moveJson = json.get(key);

			for (PieceType type : this.getPieceTypes()) {
				List<Object> permutationList = moveJson.get(type.name()).nativeList();
				//noinspection SuspiciousToArrayCall
				Integer[] permutationArray = permutationList.toArray(new Integer[permutationList.size()]);

				Move move = type.getReader().parse(key).firstMove();
				typeMap.put(type, permutationArray);

				permutations.put(move, typeMap);
			}
		}

		return permutations;
	}

	public void parseScramble(Algorithm scramble) {
		this.resetPuzzle();

		this.scramblePuzzle(scramble);
		this.solvePuzzle();
	}

	protected Map<PieceType, List<Integer>> emptyCycles() {
		Map<PieceType, List<Integer>> cycles = new HashMap<>();

		for (PieceType type : this.getPieceTypes())
			cycles.put(type, new ArrayList<>());

		return cycles;
	}

	protected Map<PieceType, Integer> emptyCycleCount() {
		Map<PieceType, Integer> cycleCount = new HashMap<>();

		for (PieceType type : this.getPieceTypes())
			cycleCount.put(type, 0);

		return cycleCount;
	}

	private Map<PieceType, Boolean[]> emptySolvedPieces() {
		Map<PieceType, Boolean[]> solvedPieces = new HashMap<>();

		for (PieceType type : this.getPieceTypes()) {
			int numPieces = type.getNumPieces();
			Boolean[] nonSolved = new Boolean[numPieces];

			for (int i = 0; i < numPieces; i++)
				nonSolved[i] = false;

			solvedPieces.put(type, nonSolved);
		}

		return solvedPieces;
	}

	protected void scramblePuzzle(Algorithm scramble) {
		scramble = this.solvingOrientationPremoves.merge(scramble);

		for (Move move : scramble)
			this.permute(move);
	}

	protected void permute(Move permutation) {
		for (PieceType type : this.state.keySet()) {
			Integer[] current = this.state.get(type);

			Integer[] perm = this.permutations.get(permutation).get(type);
			Integer[] exchanges = new Integer[perm.length];

			for (int i = 0; i < exchanges.length; i++) if (perm[i] != -1) exchanges[perm[i]] = current[i];
			for (int i = 0; i < exchanges.length; i++) if (exchanges[i] != -1) current[i] = exchanges[i];
		}
	}

    protected void solvePuzzle() {
    	this.reorientPuzzle();

    	for (PieceType type : this.state.keySet()) {
    		this.saveState(type);
    		this.solvePieces(type);
		}
	}

	protected void reorientPuzzle() {
		Integer top = this.getOrientationModelTop();
		Integer front = this.getOrientationModelFront();

		Algorithm neededRotations = this.getRotationsFromOrientation(top, front);

		for (Move move : neededRotations)
			this.permute(move);
	}

	protected void saveState(PieceType type) {
		Integer[] current = this.state.get(type);
		Integer[] saved = new Integer[current.length];

		System.arraycopy(current, 0, saved, 0, current.length);

		this.lastScrambledState.put(type, saved);
	}

	protected void resetPuzzle(boolean orientationOnly) {
		for (PieceType type : this.state.keySet()) {
			Integer[] pieces = this.state.get(type);
			Integer[] savedPieces = this.lastScrambledState.get(type);

			int numTargets = type.getNumPieces() * type.getTargetsPerPiece();
			for (int i = 0; i < numTargets; i++) {
				pieces[i] = i;

				if (!orientationOnly)
					savedPieces[i] = i;
			}
		}
	}

	protected void resetPuzzle() {
		this.resetPuzzle(false);
	}

	protected Map<PieceType, Integer[]> initState() {
		Map<PieceType, Integer[]> state = new HashMap<>();

		for (PieceType type : this.getPieceTypes()) {
			int stateLength = type.getNumPieces() * type.getTargetsPerPiece();

			state.put(type, ArrayUtil.autobox(ArrayUtil.fill(stateLength)));
		}

		return state;
	}

	protected Map<PieceType, Integer[][]> initCubies() {
		Map<PieceType, Integer[][]> cubies = this.getDefaultCubies();

		Set<PieceType> illegalCubies = new HashSet<>(cubies.keySet());
		illegalCubies.removeAll(this.getPieceTypes());

		for (PieceType type : illegalCubies)
			cubies.remove(type);

		return cubies;
	}

	protected String getSolutionPairs(PieceType type) {
		String pairs = "";

		List<Integer> currentCycles = this.cycles.get(type);

		if (currentCycles.size() > 0) {
			for (int i = 0; i < currentCycles.size(); i++) {
				pairs += this.letterSchemes.get(type)[currentCycles.get(i)];
				if (i % 2 == 1) pairs += " ";
			}
		} else {
			return "Solved";
		}

		return pairs.trim();
	}

	protected String getSolutionPairs(boolean withRotation) {
		List<String> solutionParts = new ArrayList<>();

		if (withRotation)
			solutionParts.add("Rotations: " + this.getRotations());

		for (PieceType type : this.getPieceTypes())
			solutionParts.add(this.getSolutionPairs(type));

		return String.join("\n", solutionParts);
	}

	protected String getSolutionPairs() {
		return this.getSolutionPairs(false);
	}

	protected String getStatistics(PieceType type) {
		return type.humanName() + ": " + this.getStatLength(type) + "@" + this.getBreakInNum(type) + " w/ " + this.getPreSolvedCount(type);
	}

	protected String getStatistics() {
		List<String> statisticsParts = new ArrayList<>();

		for (PieceType type : this.getPieceTypes())
			statisticsParts.add(this.getStatistics(type));

		return String.join("\n", statisticsParts);
	}

	protected int getStatLength(PieceType type) {
		return this.cycles.get(type).size();
	}

	protected int getBreakInNum(PieceType type) {
		return this.cycleCount.get(type);
	}

	protected int getPreSolvedCount(PieceType type) {
		int count = 0;
		Boolean[] solvedFlags = this.solvedPieces.get(type);

		for (boolean b : solvedFlags)
			if (b) count++;

		return count;
	}

	protected String getNoahtation() {
		List<String> noahtationParts = new ArrayList<>();

		for (PieceType type : this.getPieceTypes())
			noahtationParts.add(this.getNoahtation(type));

		return String.join(" / ", noahtationParts);
	}

	protected String getNoahtation(PieceType type) {
		return type.mnemonic() + ": " + this.getStatLength(type);
	}

	protected boolean isBufferSolved(PieceType type) {
		return this.solvedPieces.get(type)[0];
	}

	protected String getStatString(PieceType type) {
		String cornerStat = type.mnemonic() + ": ";
		cornerStat += this.getStatLength(type);
		cornerStat += this.isBufferSolved(type) ? "*" : " ";
		cornerStat += " ";

		for (int i = 0; i < this.getBreakInNum(type); i++) cornerStat += "#";
		if (cornerStat.endsWith("#")) cornerStat += " ";

		for (int i = 0; i < this.getPreSolvedCount(type); i++) cornerStat += "+";
		return cornerStat;
	}

	protected String getStatString() {
		List<String> statStringParts = new ArrayList<>();

		for (PieceType type : this.getPieceTypes())
			statStringParts.add(this.getStatString(type));

		return String.join(" | ", statStringParts);
	}

	protected abstract List<PieceType> getPieceTypes();
	protected abstract Map<PieceType, Integer[][]> getDefaultCubies();

	protected abstract void solvePieces(PieceType type);

	protected abstract Integer getOrientationModelTop();
	protected abstract Integer getOrientationModelFront();
	protected abstract Algorithm getRotationsFromOrientation(int orientationModelTop, int orientationModelFront);
	protected abstract String getRotations();

	protected abstract Map<PieceType, String[]> initSchemes();
}