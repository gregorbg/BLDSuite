package com.suushiemaniac.cubing.bld.analyze;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg;
import com.suushiemaniac.cubing.alglib.move.Move;
import com.suushiemaniac.cubing.bld.model.AlgSource;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.optim.BreakInOptim;
import com.suushiemaniac.cubing.bld.util.ArrayUtil;
import com.suushiemaniac.lang.json.JSON;

import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class BldPuzzle implements Cloneable {
	public enum MisOrientMethod {
		SOLVE_DIRECT, SINGLE_TARGET
	}

    protected Algorithm scramble;
	protected Algorithm scrambleOrientationPremoves;

    protected Map<Move, Map<PieceType, Integer[]>> permutations;

	protected Map<PieceType, Integer[]> state;
	protected Map<PieceType, Integer[]> lastScrambledState;

	protected Map<PieceType, Integer[][]> cubies;

	protected Map<PieceType, List<Integer>> cycles;
	protected Map<PieceType, Integer> cycleCount;
	protected Map<PieceType, Boolean[]> solvedPieces;
	protected Map<PieceType, Boolean[]> preSolvedPieces;
	protected Map<PieceType, Boolean[][]> misOrientedPieces;
	protected Map<PieceType, Boolean> parities;

	protected Map<PieceType, String[]> letterSchemes;
	protected Map<PieceType, Boolean> avoidBreakIns;
	protected Map<PieceType, Boolean> optimizeBreakIns;

	protected AlgSource algSource;
	protected BreakInOptim optim;
	protected MisOrientMethod misOrientMethod;

	public BldPuzzle() {
		this.scrambleOrientationPremoves = new SimpleAlg();

		this.permutations = this.loadPermutations();
		this.cubies = this.initCubies();

		this.letterSchemes = this.initSchemes();
		this.avoidBreakIns = this.allActive();
		this.optimizeBreakIns = this.allActive();

		this.algSource = null;
		this.misOrientMethod = MisOrientMethod.SOLVE_DIRECT;

		this.resetPuzzle();
	}

	public BldPuzzle(Algorithm scramble) {
		this();
		this.parseScramble(scramble);
	}

	private Map<Move, Map<PieceType, Integer[]>> loadPermutations() {
		String filename = "permutations/" + getClass().getSimpleName() + ".json";
		URL fileURL = getClass().getResource(filename);

		JSON json = JSON.fromURL(fileURL);
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

	public boolean matchesExecution(PieceType type, Predicate<Algorithm> filter) {
		if (this.algSource == null) {
			return false;
		}

		String rawSolution = this.getSolutionRaw(type);
		boolean matches = true;

		if (rawSolution.equals("Solved")) {
			return true;
		} else {
			for (String letterPair : rawSolution.split("(?<=\\G[A-Z]{2})")) {
			    if (letterPair.length() < 2) {
			    	continue;
				}

				boolean exists = false;

			    for (Algorithm alg : this.algSource.getAlg(type, letterPair)) {
			        exists |= filter.test(alg);
			    }

				matches &= exists;
			}
		}

		return matches;
	}

	public boolean matchesExecution(Predicate<Algorithm> filter) {
		boolean matches = true;

		for (PieceType type : this.getPieceTypes()) {
		    matches &= this.matchesExecution(type, filter);
		}

		return matches;
	}

	public boolean solves(PieceType type, Algorithm alg, String solutionCase) {
		Algorithm currentScramble = this.getScramble();

		this.parseScramble(alg.inverse());
		boolean solves = this.getSolutionRaw(type).equalsIgnoreCase(solutionCase.replaceAll("\\s", ""));

		this.parseScramble(currentScramble);
		return solves;
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

		for (PieceType type : this.getPieceTypes()) {
			cycles.put(type, new ArrayList<>());
		}

		return cycles;
	}

	protected Map<PieceType, Integer> emptyCycleCount() {
		Map<PieceType, Integer> cycleCount = new HashMap<>();

		for (PieceType type : this.getPieceTypes()) {
			cycleCount.put(type, 0);
		}

		return cycleCount;
	}

	protected Map<PieceType, Boolean[]> emptySolvedPieces() {
		Map<PieceType, Boolean[]> solvedPieces = new HashMap<>();

		for (PieceType type : this.getPieceTypes()) {
			int numPieces = type.getNumPieces();
			Boolean[] nonSolved = new Boolean[numPieces];

			for (int i = 0; i < numPieces; i++) {
				nonSolved[i] = false;
			}

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

			for (int i = 0; i < targetsPerPiece; i++) {
				for (int j = 0; j < numPieces; j++) {
					oriented[i][j] = false;
				}
			}

			orientedPieces.put(type, oriented);
		}

		return orientedPieces;
	}

	protected Map<PieceType, Boolean> noParities() {
		Map<PieceType, Boolean> parities = new HashMap<>();

		for (PieceType type : this.getPieceTypes()) {
			parities.put(type, false);
		}

		return parities;
	}

	protected Map<PieceType, Boolean> allActive() {
		Map<PieceType, Boolean> optimize = new HashMap<>();

		for (PieceType type : this.getPieceTypes()) {
			optimize.put(type, true);
		}

		return optimize;
	}

	protected void scramblePuzzle(Algorithm scramble) {
		scramble = this.getSolvingOrientationPremoves().merge(scramble);

		scramble.stream().filter(move -> this.permutations.keySet().contains(move)).forEach(this::permute);
	}

	protected void permute(Move permutation) {
		for (PieceType type : this.getPieceTypes(true)) {
			Integer[] current = this.state.get(type);

			Integer[] perm = this.permutations.get(permutation).get(type);
			Integer[] exchanges = new Integer[perm.length];
			ArrayUtil.fillWith(exchanges, -1);

			for (int i = 0; i < exchanges.length; i++) {
				if (perm[i] != -1) {
					exchanges[perm[i]] = current[i];
				}
			}

			for (int i = 0; i < exchanges.length; i++) {
				if (exchanges[i] != -1) {
					current[i] = exchanges[i];
				}
			}
		}
	}

    protected void solvePuzzle() {
		this.getOrientationPieceTypes().forEach(this::saveState);

		this.reorientPuzzle();
		this.getPieceTypes().forEach(this::saveState);
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
		this.preSolvedPieces = this.emptySolvedPieces();
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

	protected List<Integer> getBreakInPermutationsAfter(int piece, PieceType type) {
		int targetCount = type.getNumPieces();
		return Arrays.asList(ArrayUtil.autobox(ArrayUtil.fill(targetCount))).subList(1, targetCount);
	}

	protected int getBreakInOrientationsAfter(int piece, PieceType type) {
		return 0;
	}

	public void setAvoidBreakIns(PieceType type, boolean optimize) {
		this.avoidBreakIns.put(type, optimize);
	}

	public void setOptimizeBreakIns(PieceType type, boolean optimize) {
		this.optimizeBreakIns.put(type, optimize);
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

	public String[] getLetteringScheme(PieceType type) {
		String[] original = this.letterSchemes.get(type);
		return Arrays.copyOf(original, original.length);
	}

	public MisOrientMethod getMisOrientMethod() {
		return this.misOrientMethod;
	}

	public void setMisOrientMethod(MisOrientMethod method) {
		this.misOrientMethod = method;
	}

	public float getScrambleScore(PieceType type) { // TODO refine
		int num = type.getNumPieces();
		float scoreBase = num * num;

		scoreBase -= this.getStatLength(type);
		scoreBase += this.getPreSolvedCount(type);
		scoreBase -= this.getMisOrientedCount(type) * type.getTargetsPerPiece();
		scoreBase -= this.getBreakInCount(type) * type.getNumPiecesNoBuffer();

		if (this.hasParity(type)) {
			scoreBase -= 0.25 * scoreBase;
		}

		if (this.isBufferSolved(type)) {
			scoreBase -= 0.25 * scoreBase;
		}

		return Math.max(0, scoreBase);
	}

	public float getScrambleScore() {
		float score = 0;
		int weight = 0;
		List<PieceType> pieceTypes = this.getPieceTypes();

		for (PieceType type : pieceTypes) {
			weight += type.getNumPieces();
			score += type.getNumPieces() * this.getScrambleScore(type);
		}

		return score / weight;
	}

	public String getSolutionRaw(PieceType type) {
		StringBuilder pairs = new StringBuilder();

		List<Integer> currentCycles = this.cycles.get(type);

		if (currentCycles.size() > 0) {
			for (Integer currentCycle : currentCycles) {
				pairs.append(this.letterSchemes.get(type)[currentCycle]);
			}
		} else {
			return "Solved";
		}

		return pairs.toString().trim();
	}

	public String getSolutionRaw(boolean withRotation) {
		List<String> solutionParts = new ArrayList<>();

		if (withRotation) {
			solutionParts.add("Rotations: " + (this.scrambleOrientationPremoves.algLength() > 0 ? this.scrambleOrientationPremoves.toFormatString() : "/"));
		}

		solutionParts.addAll(this.getPieceTypes().stream().map((type) -> type.humanName() + ": " + getSolutionRaw(type)).collect(Collectors.toList()));

		return String.join("\n", solutionParts);
	}

	public String getSolutionRaw() {
		return this.getSolutionRaw(false);
	}

	public String getSolutionPairs(PieceType type) {
		StringBuilder pairs = new StringBuilder();

		List<Integer> currentCycles = this.cycles.get(type);

		if (currentCycles.size() > 0 || this.getMisOrientedCount(type) > 0) {
			for (int i = 0; i < currentCycles.size(); i++) {
				pairs.append(this.letterSchemes.get(type)[currentCycles.get(i)]);

				if (i % 2 == 1) {
					pairs.append(" ");
				}
			}

			int orientations = type.getTargetsPerPiece();

			for (int i = 1; i < orientations; i++) {
				if (this.getMisOrientedCount(type, i) > 0) {
					pairs.append(pairs.toString().endsWith(" ") ? "" : " ");

					switch (this.getMisOrientMethod()) {
						case SOLVE_DIRECT:
							pairs.append("Orient ").append(i).append(": ");
							pairs.append(String.join(" ", this.getMisOrientedPieceNames(type, i)));
							break;

						case SINGLE_TARGET:
							List<Integer> misOrients = this.getMisOrientedPieces(type, i);
							String[] lettering = this.getLetteringScheme(type);
							Integer[][] cubies = this.cubies.get(type);

							for (Integer piece : misOrients) {
								int outer = ArrayUtil.deepOuterIndex(cubies, piece);
								int inner = ArrayUtil.deepInnerIndex(cubies, piece);

								pairs.append(lettering[piece]);
								pairs.append(lettering[cubies[outer][(inner + i) % orientations]]);
								pairs.append(" ");
							}

							break;

						default:
							pairs.append("This should not happen. Please contact the developer ;)");
							break;
					}
				}
			}
		} else {
			return "Solved";
		}

		return pairs.toString().trim();
	}

	public String getSolutionPairs(boolean withRotation) {
		List<String> solutionParts = new ArrayList<>();

		if (withRotation) {
			solutionParts.add("Rotations: " + (this.scrambleOrientationPremoves.algLength() > 0 ? this.scrambleOrientationPremoves.toFormatString() : "/"));
		}

		solutionParts.addAll(this.getPieceTypes().stream().map(type -> type.humanName() + ": " + getSolutionPairs(type)).collect(Collectors.toList()));

		return String.join("\n", solutionParts);
	}

	public String getSolutionPairs() {
		return this.getSolutionPairs(false);
	}

	public Algorithm getRotations() {
		return new SimpleAlg(this.scrambleOrientationPremoves.allMoves());
	}

	public String getStatistics(PieceType type) {
		return type.humanName() + ": " + this.getStatLength(type) + "@" + this.getBreakInCount(type) + " w/ " + this.getPreSolvedCount(type) + "-" + this.getMisOrientedCount(type) + " > " + this.hasParity(type);
	}

	public String getStatistics() {
		List<String> statisticsParts = this.getPieceTypes().stream().map(this::getStatistics).collect(Collectors.toList());

		return String.join("\n", statisticsParts);
	}

	public int getStatLength(PieceType type) {
		return this.cycles.get(type).size();
	}

	public int getBreakInCount(PieceType type) {
		return this.cycleCount.get(type);
	}

	public boolean isSingleCycle() {
		boolean singleCycle = true;

		for (PieceType type : this.getPieceTypes()) {
		    singleCycle &= this.isSingleCycle(type);
		}

		return singleCycle;
	}

	public boolean isSingleCycle(PieceType type) {
		return this.getBreakInCount(type) == 0;
	}

	public int getPreSolvedCount(PieceType type) {
		int count = 0;
		Boolean[] solvedFlags = this.preSolvedPieces.get(type);

		for (int i = 1; i < solvedFlags.length; i++) {
			if (solvedFlags[i]) {
				count++;
			}
		}

		return count;
	}

	public int getMisOrientedCount() {
		int count = 0;

		for (PieceType type : this.getPieceTypes()) {
		    count += this.getMisOrientedCount(type);
		}

		return count;
	}

	public int getMisOrientedCount(int orientation) {
		int count = 0;

		for (PieceType type : this.getPieceTypes()) {
			count += this.getMisOrientedCount(type, orientation);
		}

		return count;
	}

	public int getMisOrientedCount(PieceType type) {
		int count = 0;

		for (int i = 0; i < type.getTargetsPerPiece(); i++) {
			count += this.getMisOrientedCount(type, i);
		}

		return count;
	}

	public int getMisOrientedCount(PieceType type, int orientation) {
		orientation %= type.getTargetsPerPiece();

		Boolean[] orientations = this.misOrientedPieces.get(type)[orientation];
		int count = 0;

		for (int i = 1; i < orientations.length; i++) {
			if (orientations[i]) {
				count++;
			}
		}

		return count;
	}

	protected List<Integer> getMisOrientedPieces(PieceType type, int orientation) {
		Boolean[] orientations = this.misOrientedPieces.get(type)[orientation];
		Integer[][] cubies = this.cubies.get(type);

		List<Integer> misOrientedPieces = new ArrayList<>();

		for (int i = 1; i < orientations.length; i++) {
			if (orientations[i]) {
				misOrientedPieces.add(cubies[i][orientation]);
			}
		}

		return misOrientedPieces;
	}

	protected List<String> getMisOrientedPieceNames(PieceType type, int orientation) {
		String[] lettering = this.letterSchemes.get(type);
		List<Integer> pieces = this.getMisOrientedPieces(type, orientation);

		List<String> pieceNames = new ArrayList<>();

		for (Integer piece : pieces) {
		    pieceNames.add(lettering[piece]);
		}

		return pieceNames;
	}

	public boolean hasParity(PieceType type) {
		return this.parities.get(type);
	}

	public boolean hasParity() {
		boolean hasParity = false;

		for (PieceType type : this.getPieceTypes()) {
		    hasParity |= this.hasParity(type);
		}

		return hasParity;
	}

	public String getNoahtation(PieceType type) {
		StringBuilder misOriented = new StringBuilder();

		for (int i = 0; i < this.getMisOrientedCount(type); i++) {
			misOriented.append("'");
		}

		return type.mnemonic() + ": " + this.getStatLength(type) + misOriented;
	}

	public String getNoahtation() {
		List<String> noahtationParts = this.getPieceTypes().stream().map(this::getNoahtation).collect(Collectors.toList());

		return String.join(" / ", noahtationParts);
	}

	public String getStatString(PieceType type, boolean indent) {
		StringBuilder statString = new StringBuilder(type.mnemonic() + ": ");

		statString.append(this.hasParity(type) ? "_" : (indent ? " " : ""));

		int targets = this.getStatLength(type);
		statString.append(targets);

		statString.append(this.isBufferSolved(type) ? "*" : (indent ? " " : ""));
		statString.append(this.isBufferSolvedAndMisOriented(type) ? "*" : (indent ? " " : ""));

		int maxTargets = ((type.getNumPiecesNoBuffer() / 2) * 3) + (type.getNumPiecesNoBuffer() % 2);
		int lenDiff = Integer.toString(maxTargets).length() - Integer.toString(targets).length();
		statString.append(String.join("", Collections.nCopies(lenDiff + 1, " ")));

		int breakInMax = type.getNumPiecesNoBuffer() / 2;
		int breakIns = this.getBreakInCount(type);

		statString.append(String.join("", Collections.nCopies(breakIns, "#")));

		if (indent) {
			statString.append(String.join("", Collections.nCopies(breakInMax - breakIns, " ")));
		}

		int misOrientPreSolvedMax = type.getNumPiecesNoBuffer();

		if (type.getTargetsPerPiece() > 1) {
			int misOriented = this.getMisOrientedCount(type);

			if (indent || (misOriented > 0 && !statString.toString().endsWith(" "))) {
				statString.append(" ");
			}

			statString.append(String.join("", Collections.nCopies(misOriented, "~")));

			if (indent) {
				statString.append(String.join("", Collections.nCopies(misOrientPreSolvedMax - misOriented, " ")));
			}
		}

		int preSolved = this.getPreSolvedCount(type);

		if (indent || (preSolved > 0 && !statString.toString().endsWith(" "))) {
			statString.append(" ");
		}

		statString.append(String.join("", Collections.nCopies(preSolved, "+")));

		if (indent) {
			statString.append(String.join("", Collections.nCopies(misOrientPreSolvedMax - preSolved, " ")));
		}

		return indent ? statString.toString() : statString.toString().trim();
	}

	public String getStatString(PieceType type) {
		return this.getStatString(type, false);
	}

	public String getStatString(boolean indent) {
		List<String> statStringParts = this.getPieceTypes().stream().map(type -> getStatString(type, indent)).collect(Collectors.toList());

		return String.join(" | ", statStringParts);
	}

	public String getStatString() {
		return this.getStatString(false);
	}

	public boolean isBufferSolved(PieceType type, boolean acceptMisOrient) {
		boolean bufferSolved = this.preSolvedPieces.get(type)[0];

		return bufferSolved || (acceptMisOrient && this.isBufferSolvedAndMisOriented(type));
	}

	public boolean isBufferSolved(PieceType type) {
		return this.isBufferSolved(type, true);
	}

	public boolean isBufferSolvedAndMisOriented(PieceType type) {
		boolean bufferTwisted = false;

		int orientations = type.getTargetsPerPiece();

		Integer[][] reference = this.cubies.get(type);
		Integer[] state = this.lastScrambledState.get(type);

		for (int i = 1; i < orientations; i++) {
			boolean bufferCurrentOrigin = true;

			for (int j = 0; j < orientations; j++) {
				bufferCurrentOrigin &= state[reference[0][j]].equals(reference[0][(j + i) % orientations]);
			}

			bufferTwisted |= bufferCurrentOrigin;
		}

		return bufferTwisted;
	}

	public List<PieceType> getPieceTypes(boolean withOrientationModel) {
		List<PieceType> pieceTypes = this.getPermutationPieceTypes();

		if (withOrientationModel) {
			pieceTypes.addAll(this.getOrientationPieceTypes());
		}

		return pieceTypes;
	}

	@Override
	public BldPuzzle clone() {
		try {
			return (BldPuzzle) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<PieceType> getPieceTypes() {
		return this.getPieceTypes(false);
	}

	protected int getPiecePermutations(PieceType type) {
		return type.getNumPieces();
	}

	protected int getPieceOrientations(PieceType type) {
		return type.getTargetsPerPiece();
	}

	protected abstract List<PieceType> getPermutationPieceTypes();
	protected abstract List<PieceType> getOrientationPieceTypes();
	protected abstract Map<PieceType, Integer[][]> getDefaultCubies();

	protected abstract void solvePieces(PieceType type);

	protected abstract Algorithm getReorientationMoves();
	protected abstract Algorithm getSolvingOrientationPremoves();

	protected abstract Map<PieceType, String[]> initSchemes();
}