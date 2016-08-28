package com.suushiemaniac.cubing.bld.analyze.cube;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg;
import com.suushiemaniac.cubing.alglib.move.Move;
import com.suushiemaniac.cubing.bld.model.AlgSource;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.util.ArrayUtil;
import com.suushiemaniac.lang.json.JSON;

import java.io.File;
import java.net.URL;
import java.util.*;

public abstract class BldPuzzle {
    protected Algorithm scramble;
	protected Algorithm scrambleOrientationPremoves;

    protected Map<Move, Map<PieceType, Integer[]>> permutations;

	protected Map<PieceType, Integer[]> state;
	protected Map<PieceType, Integer[]> lastScrambledState;

	protected Map<PieceType, Integer[][]> cubies;

	protected Map<PieceType, List<Integer>> cycles;
	protected Map<PieceType, Integer> cycleCount;
	protected Map<PieceType, Boolean[]> solvedPieces;
	protected Map<PieceType, Boolean[][]> misOrientedPieces;
	protected Map<PieceType, Boolean> parities;

	protected Map<PieceType, String[]> letterSchemes;

	protected AlgSource algSource;

	public BldPuzzle() {
		this.scrambleOrientationPremoves = new SimpleAlg();

		this.permutations = this.loadPermutations();
		this.cubies = this.initCubies();

		this.letterSchemes = this.initSchemes();

		this.algSource = null;

		this.resetPuzzle();
	}

	public BldPuzzle(Algorithm scramble) {
		this();
		this.parseScramble(scramble);
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

			for (PieceType type : this.getPieceTypes(true)) {
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

	public void setAlgSource(AlgSource source) {
		this.algSource = source;
	}

	public void parseScramble(Algorithm scramble) {
		this.resetPuzzle();

		this.scramble = scramble;

		this.scramblePuzzle(scramble);
		this.solvePuzzle();
	}

	public Algorithm getScramble() {
		return this.scramble;
	}

	public void resolve() {
		this.parseScramble(this.getScramble());
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

	protected Map<PieceType, Boolean[]> emptySolvedPieces() {
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

	protected Map<PieceType, Boolean[][]> orientedPieces() {
		Map<PieceType, Boolean[][]> orientedPieces = new HashMap<>();

		for (PieceType type : this.getPieceTypes()) {
			int numPieces = type.getNumPieces();
			int targetsPerPiece = type.getTargetsPerPiece();

			Boolean[][] oriented = new Boolean[targetsPerPiece][numPieces];

			for (int i = 0; i < targetsPerPiece; i++)
				for (int j = 0; j < numPieces; j++)
					oriented[i][j] = false;

			orientedPieces.put(type, oriented);
		}

		return orientedPieces;
	}

	protected Map<PieceType, Boolean> noParities() {
		Map<PieceType, Boolean> parities = new HashMap<>();

		for (PieceType type : this.getPieceTypes())
			parities.put(type, false);

		return parities;
	}

	protected void scramblePuzzle(Algorithm scramble) {
		scramble = this.getSolvingOrientationPremoves().merge(scramble);

		scramble.forEach(this::permute);
	}

	protected void permute(Move permutation) {
		for (PieceType type : this.getPieceTypes(true)) {
			Integer[] current = this.state.get(type);

			Integer[] perm = this.permutations.get(permutation).get(type);
			Integer[] exchanges = new Integer[perm.length];
			ArrayUtil.fillWith(exchanges, -1);

			for (int i = 0; i < exchanges.length; i++) if (perm[i] != -1) exchanges[perm[i]] = current[i];
			for (int i = 0; i < exchanges.length; i++) if (exchanges[i] != -1) current[i] = exchanges[i];
		}
	}

    protected void solvePuzzle() {
		this.getPieceTypes(true).forEach(this::saveState);

		this.reorientPuzzle();

		this.getPieceTypes().forEach(this::solvePieces);
	}

	protected void reorientPuzzle() {
		this.scrambleOrientationPremoves = this.getReorientationMoves();

		this.scrambleOrientationPremoves.forEach(this::permute);
	}

	protected void saveState(PieceType type) {
		Integer[] current = this.state.get(type);
		Integer[] saved = new Integer[current.length];

		System.arraycopy(current, 0, saved, 0, current.length);

		this.lastScrambledState.put(type, saved);
	}

	protected void resetPuzzle() {
		this.state = this.initState();
		this.lastScrambledState = this.initState();

		this.cycles = this.emptyCycles();
		this.cycleCount = this.emptyCycleCount();
		this.solvedPieces = this.emptySolvedPieces();
		this.misOrientedPieces = this.orientedPieces();
		this.parities = this.noParities();
	}

	protected Map<PieceType, Integer[]> initState() {
		Map<PieceType, Integer[]> state = new HashMap<>();

		for (PieceType type : this.getPieceTypes(true)) {
			int stateLength = type.getNumPieces() * type.getTargetsPerPiece();

			state.put(type, ArrayUtil.autobox(ArrayUtil.fill(stateLength)));
		}

		return state;
	}

	protected Map<PieceType, Integer[][]> initCubies() {
		Map<PieceType, Integer[][]> cubies = this.getDefaultCubies();

		Set<PieceType> illegalCubies = new HashSet<>(cubies.keySet());
		illegalCubies.removeAll(this.getPieceTypes());

		illegalCubies.forEach(cubies::remove);

		return cubies;
	}

	protected void increaseCycleCount(PieceType type) {
		this.cycleCount.put(type, this.cycleCount.get(type) + 1);
	}

	protected int getLastTarget(PieceType type) {
		List<Integer> cycles = this.cycles.get(type);

		return cycles.size() > 0 ? cycles.get(cycles.size() - 1) : -1;
	}

	protected List<Integer> getBreakInsAfter(int piece, PieceType type) {
		int targetCount = type.getNumPieces();
		return Arrays.asList(ArrayUtil.autobox(ArrayUtil.fill(targetCount))).subList(1, targetCount);
	}

	public boolean setBuffer(PieceType type, int newBuffer) {
		Integer[][] cubies = this.cubies.get(type);

		if (cubies != null) {
			int outer = ArrayUtil.deepOuterIndex(cubies, newBuffer);

			if (outer > -1) {
				int inner = ArrayUtil.deepInnerIndex(cubies, newBuffer);

				if (inner > -1) {
					for (int i = 0; i < outer; i++) ArrayUtil.cycleLeft(cubies);
					for (int i = 0; i < inner; i++) ArrayUtil.cycleLeft(cubies[0]);

					this.resolve();
					return true;
				}
			}
		}

		return false;
	}

	public boolean setBuffer(PieceType type, String newBuffer) {
		String[] letterScheme = this.letterSchemes.get(type);

		if (letterScheme != null) {
			int index = ArrayUtil.index(letterScheme, newBuffer);

			if (index > -1) {
				return this.setBuffer(type, index);
			}
		}

		return false;
	}

	public boolean setLetteringScheme(PieceType type, String[] newScheme) {
		String[] oldScheme = this.letterSchemes.get(type);

		if (oldScheme != null && oldScheme.length == newScheme.length) {
			this.letterSchemes.put(type, newScheme);
			this.resolve();
			return true;
		}

		return false;
	}

	public String[] getScheme(PieceType type) {
		String[] original = this.letterSchemes.get(type);
		return Arrays.copyOf(original, original.length);
	}

	public String getSolutionPairs(PieceType type) {
		String pairs = type.humanName() + ": ";

		List<Integer> currentCycles = this.cycles.get(type);

		if (currentCycles.size() > 0 || this.getMisOrientedCount(type) > 0) {
			for (int i = 0; i < currentCycles.size(); i++) {
				pairs += this.letterSchemes.get(type)[currentCycles.get(i)];
				if (i % 2 == 1) pairs += " ";
			}

			for (int i = 1; i < type.getTargetsPerPiece(); i++) {
				if (this.getMisOrientedCount(type, i) > 0) {
					pairs += pairs.endsWith(" ") ? "" : " ";
					pairs += "Orient " + i + ": ";
					pairs += String.join(" ", this.getMisOrientedPieces(type, i));
				}
			}
		} else {
			return "Solved";
		}

		return pairs.trim();
	}

	public String getSolutionPairs(boolean withRotation) {
		List<String> solutionParts = new ArrayList<>();

		if (withRotation)
			solutionParts.add("Rotations: " + this.scrambleOrientationPremoves.toFormatString());

		for (PieceType type : this.getPieceTypes())
			solutionParts.add(this.getSolutionPairs(type));

		return String.join("\n", solutionParts);
	}

	public String getSolutionPairs() {
		return this.getSolutionPairs(false);
	}

	public String getStatistics(PieceType type) {
		return type.humanName() + ": " + this.getStatLength(type) + "@" + this.getBreakInCount(type) + " w/ " + this.getPreSolvedCount(type) + "-" + this.getMisOrientedCount(type) + " > " + this.hasParity(type);
	}

	public String getStatistics() {
		List<String> statisticsParts = new ArrayList<>();

		for (PieceType type : this.getPieceTypes())
			statisticsParts.add(this.getStatistics(type));

		return String.join("\n", statisticsParts);
	}

	public int getStatLength(PieceType type) {
		return this.cycles.get(type).size();
	}

	public int getBreakInCount(PieceType type) {
		return this.cycleCount.get(type);
	}

	public boolean isSingleCycle(PieceType type) {
		return this.getBreakInCount(type) == 0;
	}

	public int getPreSolvedCount(PieceType type) {
		int count = 0;
		Boolean[] solvedFlags = this.solvedPieces.get(type);

		for (boolean b : solvedFlags)
			if (b) count++;

		return count;
	}

	public int getMisOrientedCount(PieceType type) {
		int count = 0;

		for (int i = 0; i < type.getTargetsPerPiece(); i++)
			count += this.getMisOrientedCount(type, i);

		return count;
	}

	public int getMisOrientedCount(PieceType type, int orientation) {
		orientation %= type.getTargetsPerPiece();

		Boolean[] orientations = this.misOrientedPieces.get(type)[orientation];
		int count = 0;

		for (int i = 1; i < orientations.length; i++) {
			if (orientations[i])
				count++;
		}

		return count;
	}

	protected List<String> getMisOrientedPieces(PieceType type, int orientation) {
		Boolean[] orientations = this.misOrientedPieces.get(type)[orientation];
		Integer[][] cubies = this.cubies.get(type);
		String[] lettering = this.letterSchemes.get(type);

		List<String> misOrientedPieces = new ArrayList<>();

		for (int i = 1; i < orientations.length; i++)
			if (orientations[i])
				misOrientedPieces.add(lettering[cubies[i][orientation]]);

		return misOrientedPieces;
	}

	public boolean hasParity(PieceType type) {
		return this.parities.get(type);
	}

	public String getNoahtation(PieceType type) {
		String misOriented = "";
		for (int i = 0; i < this.getMisOrientedCount(type); i++) misOriented += "'";

		return type.mnemonic() + ": " + this.getStatLength(type) + misOriented;
	}

	public String getNoahtation() {
		List<String> noahtationParts = new ArrayList<>();

		for (PieceType type : this.getPieceTypes())
			noahtationParts.add(this.getNoahtation(type));

		return String.join(" / ", noahtationParts);
	}

	public String getStatString(PieceType type) {
		String cornerStat = type.mnemonic() + ":";
		cornerStat += this.hasParity(type) ? "_" : " ";
		cornerStat += this.getStatLength(type);
		cornerStat += this.isBufferSolved(type) ? "*" : " ";
		cornerStat += " ";

		for (int i = 0; i < this.getBreakInCount(type); i++) cornerStat += "#";
		if (cornerStat.endsWith("#")) cornerStat += " ";

		for (int i = 0; i < this.getMisOrientedCount(type); i++) cornerStat += "~";
		if (cornerStat.endsWith("~")) cornerStat += " ";

		for (int i = 0; i < this.getPreSolvedCount(type); i++) cornerStat += "+";
		return cornerStat;
	}

	public String getStatString() {
		List<String> statStringParts = new ArrayList<>();

		for (PieceType type : this.getPieceTypes())
			statStringParts.add(this.getStatString(type));

		return String.join(" | ", statStringParts);
	}

	public boolean isBufferSolved(PieceType type) {
		return this.solvedPieces.get(type)[0];
	}

	protected List<PieceType> getPieceTypes(boolean withOrientationModel) {
		List<PieceType> pieceTypes = this.getPermutationPieceTypes();

		if (withOrientationModel)
			pieceTypes.addAll(this.getOrientationPieceTypes());

		return pieceTypes;
	}

	protected List<PieceType> getPieceTypes() {
		return this.getPieceTypes(false);
	}

	protected abstract List<PieceType> getPermutationPieceTypes();
	protected abstract List<PieceType> getOrientationPieceTypes();
	protected abstract Map<PieceType, Integer[][]> getDefaultCubies();

	protected abstract void solvePieces(PieceType type);

	protected abstract Algorithm getReorientationMoves();
	protected abstract Algorithm getSolvingOrientationPremoves();

	protected abstract Map<PieceType, String[]> initSchemes();
}