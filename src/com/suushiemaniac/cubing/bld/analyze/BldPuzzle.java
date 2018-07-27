package com.suushiemaniac.cubing.bld.analyze;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg;
import com.suushiemaniac.cubing.alglib.move.ImageLetterMove;
import com.suushiemaniac.cubing.alglib.move.Move;
import com.suushiemaniac.cubing.alglib.move.plane.ImageLetterPlane;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.LetterPairImage;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import com.suushiemaniac.cubing.bld.model.enumeration.puzzle.TwistyPuzzle;
import com.suushiemaniac.cubing.bld.model.source.AlgSource;
import com.suushiemaniac.cubing.bld.optim.BreakInOptim;
import com.suushiemaniac.cubing.bld.util.ArrayUtil;
import com.suushiemaniac.cubing.bld.util.ClosureUtil;
import com.suushiemaniac.cubing.bld.util.MapUtil;
import com.suushiemaniac.cubing.bld.util.SpeffzUtil;
import com.suushiemaniac.lang.json.JSON;

import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class BldPuzzle implements Cloneable {
	protected Algorithm scramble;
	protected Algorithm scrambleOrientationPremoves;

	protected Map<Move, Map<PieceType, Integer[]>> permutations;

	protected Map<PieceType, Integer[]> state;
	protected Map<PieceType, Integer[]> lastScrambledState;

	protected Map<PieceType, Integer[][]> cubies;

	protected String letterPairLanguage;
	protected Map<PieceType, String[]> letterSchemes;

	protected Map<PieceType, List<Integer>> cycles;
	protected Map<PieceType, Integer> cycleCount;

	protected Map<PieceType, Integer> mainBuffers;
	protected Map<PieceType, Queue<Integer>> backupBuffers;
	protected Map<PieceType, Map<Integer, Integer>> bufferFloats;

	protected Map<PieceType, Boolean[]> solvedPieces;
	protected Map<PieceType, Boolean[]> preSolvedPieces;
	protected Map<PieceType, Boolean[][]> misOrientedPieces;

	protected Map<PieceType, Boolean> parities;

	protected Map<PieceType, Boolean> avoidBreakIns;
	protected Map<PieceType, Boolean> optimizeBreakIns;

	protected TwistyPuzzle model;

	protected AlgSource algSource;
	protected BreakInOptim optim;
	protected MisOrientMethod misOrientMethod;

	public BldPuzzle(TwistyPuzzle model) {
		this.model = model;
		this.permutations = this.loadPermutations();

		this.cubies = this.initCubies();

		this.scrambleOrientationPremoves = new SimpleAlg();
		this.letterPairLanguage = System.getProperty("user.language");

		this.letterSchemes = MapUtil.INSTANCE.constantlyTo(this.getPieceTypes(), ClosureUtil.INSTANCE.constant(SpeffzUtil.INSTANCE.getFULL_SPEFFZ()));
		this.avoidBreakIns = MapUtil.INSTANCE.constantlyTo(this.getPieceTypes(), ClosureUtil.INSTANCE.constant(true));
		this.optimizeBreakIns = MapUtil.INSTANCE.constantlyTo(this.getPieceTypes(), ClosureUtil.INSTANCE.constant(true));

		this.mainBuffers = this.readCurrentBuffers();
		this.backupBuffers = MapUtil.INSTANCE.constantlyTo(this.getPieceTypes(), LinkedList::new);

		this.algSource = null;
		this.misOrientMethod = MisOrientMethod.SOLVE_DIRECT;

		this.resetPuzzle();
	}

	public BldPuzzle(TwistyPuzzle model, Algorithm scramble) {
		this(model);

		this.parseScramble(scramble);
	}

	private Map<Move, Map<PieceType, Integer[]>> loadPermutations() {
		String filename = "permutations/" + this.getModel().toString() + ".json";
		URL fileURL = this.getClass().getResource(filename);

		JSON json = JSON.fromURL(fileURL);
		Map<Move, Map<PieceType, Integer[]>> permutations = new HashMap<>();

		for (String key : json.nativeKeySet()) {
			Map<PieceType, Integer[]> typeMap = new HashMap<>();
			JSON moveJson = json.get(key);

			for (PieceType type : this.getPieceTypes(true)) {
				List<Integer> permutationList = moveJson.get(type.name()).nativeList(JSON::intValue);
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

				boolean exists = this.algSource.getAlgorithms(type, letterPair).stream()
						.anyMatch(filter);

				matches &= exists;
			}
		}

		return matches;
	}

	public boolean matchesExecution(Predicate<Algorithm> filter) {
		return this.getPieceTypes().stream()
				.allMatch(type -> this.matchesExecution(type, filter));
	}

	public boolean solves(PieceType type, Algorithm alg, String solutionCase, boolean pure) {
		Algorithm currentScramble = this.getScramble();

		this.parseScramble(alg.inverse());

		boolean solves = this.getSolutionRaw(type).equalsIgnoreCase(solutionCase.replaceAll("\\s", ""))
				&& this.getMisOrientedCount(type) == 0;

		if (pure) {
			List<PieceType> remainingTypes = new ArrayList<>(this.getPieceTypes());
			remainingTypes.remove(type);

			for (PieceType remainingType : remainingTypes) {
				solves &= this.getSolutionRaw(remainingType).equals("Solved") && this.getMisOrientedCount(remainingType) == 0;
			}
		}

		this.parseScramble(currentScramble == null ? new SimpleAlg() : currentScramble);

		return solves;
	}

	public boolean solves(PieceType type, Algorithm alg, String solutionCase) {
		return this.solves(type, alg, solutionCase, true);
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
		Algorithm current = this.getScramble();

		if (current != null) {
			this.parseScramble(current);
		}
	}

	protected Map<PieceType, Boolean[]> emptySolvedPieces() {
		Map<PieceType, Boolean[]> solvedPieces = new HashMap<>();

		for (PieceType type : this.getPieceTypes()) {
			Boolean[] nonSolved = IntStream.range(0, type.getNumPieces())
					.mapToObj(i -> false)
					.toArray(Boolean[]::new);

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

	protected Map<PieceType, Integer> readCurrentBuffers() {
		return this.getPieceTypes()
				.stream()
				.collect(Collectors.toMap(Function.identity(), this::getBuffer, (a, b) -> b));
	}

	protected void scramblePuzzle(Algorithm scramble) {
		scramble = this.getSolvingOrientationPremoves().merge(scramble);

		scramble.stream()
				.filter(move -> this.permutations.keySet().contains(move))
				.forEach(this::permute);
	}

	protected void permute(Move permutation) {
		for (PieceType type : this.getPieceTypes(true)) {
			Integer[] current = this.state.get(type);
			Integer[] perm = this.permutations.get(permutation).get(type);

			this.applyPermutations(current, perm);
		}
	}

	protected void applyPermutations(Integer[] current, Integer[] perm) {
		Integer[] exchanges = new Integer[perm.length];
		ArrayUtil.INSTANCE.fillWith(exchanges, -1);

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

	protected Map<PieceType, Integer[]> initState() {
		Map<PieceType, Integer[]> state = new HashMap<>();

		for (PieceType type : this.getPieceTypes(true)) {
			int stateLength = type.getNumPieces() * type.getTargetsPerPiece();

			state.put(type, ArrayUtil.INSTANCE.filledArray(stateLength));
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

	protected void saveState(PieceType type) {
		Integer[] current = this.state.get(type);
		Integer[] saved = new Integer[current.length];

		System.arraycopy(current, 0, saved, 0, current.length);

		this.lastScrambledState.put(type, saved);
	}

	protected void resetPuzzle() {
		this.state = this.initState();
		this.lastScrambledState = this.initState();

		this.cycles = MapUtil.INSTANCE.constantlyTo(this.getPieceTypes(), ArrayList::new);
		this.cycleCount = MapUtil.INSTANCE.constantlyTo(this.getPieceTypes(), ClosureUtil.INSTANCE.constant(0));

		this.solvedPieces = this.emptySolvedPieces();
		this.preSolvedPieces = this.emptySolvedPieces();
		this.misOrientedPieces = this.orientedPieces();

		this.parities = MapUtil.INSTANCE.constantlyTo(this.getPieceTypes(), ClosureUtil.INSTANCE.constant(false));

		this.mainBuffers.forEach(this::cycleCubiesForBuffer);
		this.bufferFloats = MapUtil.INSTANCE.constantlyTo(this.getPieceTypes(), HashMap::new);
	}

	protected void increaseCycleCount(PieceType type) {
		this.cycleCount.put(type, this.cycleCount.get(type) + 1);
	}

	protected int pieceToPosition(PieceType type, int piece) {
		return ArrayUtil.INSTANCE.deepOuterIndex(this.cubies.get(type), piece);
	}

	protected int getLastTarget(PieceType type) {
		List<Integer> cycles = this.cycles.get(type);

		return cycles.size() > 0 ? cycles.get(cycles.size() - 1) : -1;
	}

	protected List<Integer> getBreakInPermutationsAfter(int piece, PieceType type) {
		int targetCount = type.getNumPieces();
		return Arrays.asList(ArrayUtil.INSTANCE.filledArray(targetCount)).subList(1, targetCount);
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

	protected boolean cycleCubiesForBuffer(PieceType type, int newBuffer) {
		Integer[][] cubies = this.cubies.get(type);

		if (cubies != null) {
			int outer = ArrayUtil.INSTANCE.deepOuterIndex(cubies, newBuffer);

			if (outer > -1) {
				int inner = ArrayUtil.INSTANCE.deepInnerIndex(cubies, newBuffer);

				if (inner > -1) {
					for (int i = 0; i < outer; i++) ArrayUtil.INSTANCE.cycleLeft(cubies);
					for (int i = 0; i < inner; i++) ArrayUtil.INSTANCE.cycleLeft(cubies[0]);

					return true;
				}
			}
		}

		return false;
	}

	public boolean setBuffer(PieceType type, int newBuffer) {
		if (this.cycleCubiesForBuffer(type, newBuffer)) {
			this.mainBuffers.put(type, newBuffer);
			this.resolve();

			return true;
		}

		return false;
	}

	public boolean setBuffer(PieceType type, String newBuffer) {
		String[] letterScheme = this.letterSchemes.get(type);

		if (letterScheme != null) {
			int index = ArrayUtil.INSTANCE.index(letterScheme, newBuffer);

			if (index > -1) {
				return this.setBuffer(type, index);
			}
		}

		return false;
	}

	public boolean registerFloatingBuffer(PieceType type, int newBuffer) {
		Queue<Integer> floatingBuffers = this.backupBuffers.get(type);

		if (!floatingBuffers.contains(newBuffer) && this.mainBuffers.get(type) != newBuffer) {
			floatingBuffers.add(newBuffer);
			this.resolve();

			return true;
		}

		return false;
	}

	public boolean registerFloatingBuffer(PieceType type, String newBuffer) {
		String[] letterScheme = this.letterSchemes.get(type);

		if (letterScheme != null) {
			int index = ArrayUtil.INSTANCE.index(letterScheme, newBuffer);

			if (index > -1) {
				return this.registerFloatingBuffer(type, index);
			}
		}

		return false;
	}

	public void dropFloatingBuffers(PieceType type) {
		this.backupBuffers.get(type).clear();

		this.resolve();
	}

	public void dropFloatingBuffers() {
		this.getPieceTypes().forEach(this::dropFloatingBuffers);
	}

	public String getLetterPairLanguage() {
		return this.letterPairLanguage;
	}

	public void setLetterPairLanguage(String letterPairLanguage) {
		this.letterPairLanguage = letterPairLanguage;
	}

	public int getBuffer(PieceType type) {
		if (type instanceof LetterPairImage) {
			return 0;
		} else {
			return this.cubies.get(type)[0][0];
		}
	}

	public String getBufferTarget(PieceType type) {
		if (type instanceof LetterPairImage) {
			return this.letterPairLanguage;
		} else {
			return this.letterSchemes.get(type)[this.getBuffer(type)];
		}
	}

	public Integer[] getBufferPiece(PieceType type) {
		if (type instanceof LetterPairImage) {
			return new Integer[]{0};
		}

		Integer[] piece = this.cubies.get(type)[0];
		return Arrays.copyOf(piece, piece.length);
	}

	public String[] getBufferPieceTargets(PieceType type) {
		if (type instanceof LetterPairImage) {
			return new String[]{this.letterPairLanguage};
		}

		Integer[] piece = this.getBufferPiece(type);

		return Arrays.stream(piece)
				.map(integer -> this.letterSchemes.get(type)[integer])
				.toArray(String[]::new);
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
		if (type instanceof LetterPairImage) {
			Set<String> alphabet = new LinkedHashSet<>();

			for (PieceType permType : this.getPieceTypes()) {
				alphabet.addAll(Arrays.asList(this.getLetteringScheme(permType)));
			}

			return alphabet.toArray(new String[alphabet.size()]);
		} else {
			String[] original = this.letterSchemes.get(type);
			return Arrays.copyOf(original, original.length);
		}
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
		List<Integer> currentCycles = this.cycles.get(type);
		Map<Integer, Integer> bufferFloats = this.bufferFloats.get(type);

		String[] letters = this.letterSchemes.get(type);

		if (currentCycles.size() > 0) {
			StringBuilder solutionRaw = new StringBuilder();

			for (int i = 0; i < currentCycles.size(); i++) {
				if (bufferFloats.containsKey(i)) {
					String bufferLetter = letters[bufferFloats.get(i)];
					String position = SpeffzUtil.INSTANCE.speffzToSticker(SpeffzUtil.INSTANCE.normalize(bufferLetter, letters), type);

					solutionRaw.append("(").append(position).append(")");
				}

				solutionRaw.append(letters[currentCycles.get(i)]);
			}

			return solutionRaw.toString().trim();
		} else {
			return "Solved";
		}
	}

	public String getSolutionRaw(boolean withRotation) {
		List<String> solutionParts = new ArrayList<>();

		if (withRotation) {
			solutionParts.add("Rotations: " + (this.scrambleOrientationPremoves.algLength() > 0 ? this.scrambleOrientationPremoves.toFormatString() : "/"));
		}

		solutionParts.addAll(this.getExecutionOrderPieceTypes().stream()
				.map((type) -> type.humanName() + ": " + getSolutionRaw(type))
				.collect(Collectors.toList()));

		return String.join("\n", solutionParts);
	}

	public String getSolutionRaw() {
		return this.getSolutionRaw(false);
	}

	public String getSolutionPairs(PieceType type) {
		StringBuilder pairs = new StringBuilder();

		List<Integer> currentCycles = this.cycles.get(type);
		String[] letters = this.letterSchemes.get(type);

		Map<Integer, Integer> bufferFloats = this.bufferFloats.get(type);

		if (currentCycles.size() > 0 || this.getMisOrientedCount(type) > 0) {
			for (int i = 0; i < currentCycles.size(); i++) {
				if (bufferFloats.containsKey(i)) {
					String bufferLetter = letters[bufferFloats.get(i)];
					String position = SpeffzUtil.INSTANCE.speffzToSticker(SpeffzUtil.INSTANCE.normalize(bufferLetter, letters), type);

					pairs.append("(float ")
							.append(position)
							.append(") ");
				}

				pairs.append(letters[currentCycles.get(i)]);

				if (i % 2 == 1) {
					pairs.append(" ");
				}
			}

			pairs.append(pairs.toString().endsWith(" ") ? "" : " ");
			pairs.append(this.getRotationSolutions(type));
		} else {
			return "Solved";
		}

		return pairs.toString().trim();
	}

	protected String getRotationSolutions(PieceType type) {
		StringBuilder pairs = new StringBuilder();

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
							int outer = ArrayUtil.INSTANCE.deepOuterIndex(cubies, piece);
							int inner = ArrayUtil.INSTANCE.deepInnerIndex(cubies, piece);

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

		return pairs.toString().trim();
	}

	public String getSolutionPairs(boolean withRotation) {
		List<String> solutionParts = new ArrayList<>();

		if (withRotation) {
			solutionParts.add("Rotations: " + (this.scrambleOrientationPremoves.algLength() > 0 ? this.scrambleOrientationPremoves.toFormatString() : "/"));
		}

		solutionParts.addAll(this.getExecutionOrderPieceTypes().stream()
				.map(type -> type.humanName() + ": " + getSolutionPairs(type))
				.collect(Collectors.toList()));

		return String.join("\n", solutionParts);
	}

	public String getSolutionPairs() {
		return this.getSolutionPairs(false);
	}

	public String getSolutionAlgorithms(PieceType type) {
		if (this.algSource == null || !this.algSource.isReadable()) {
			return "";
		}

		StringBuilder pairs = new StringBuilder();

		List<Integer> currentCycles = this.cycles.get(type);

		if (currentCycles.size() > 0 || this.getMisOrientedCount(type) > 0) { // TODO alg sources buffer support
			for (int i = 0; i < currentCycles.size(); i += 2) {
				if (i + 1 >= currentCycles.size()) {
					continue;
				}

				String pair = this.letterSchemes.get(type)[currentCycles.get(i)] + this.letterSchemes.get(type)[currentCycles.get(i + 1)];

				Set<Algorithm> caseAlgs = this.algSource.getAlgorithms(type, pair);
				Algorithm thisAlg = new SimpleAlg(new ImageLetterMove(new ImageLetterPlane('L')), new ImageLetterMove(new ImageLetterPlane('O')), new ImageLetterMove(new ImageLetterPlane('L')));

				if (caseAlgs.size() > 0) {
					List<Algorithm> listAlgs = new ArrayList<>(caseAlgs);
					Collections.shuffle(listAlgs);
					thisAlg = listAlgs.get(0);
				}

				pairs.append(thisAlg.toFormatString());
				pairs.append("\n");
			}

			pairs.append(pairs.toString().endsWith(" ") ? "" : " ");
			pairs.append(this.getRotationSolutions(type));
		} else {
			return "Solved";
		}

		return pairs.toString().trim();
	}

	public String getSolutionAlgorithms(boolean withRotation) {
		List<String> solutionParts = new ArrayList<>();

		if (withRotation) {
			solutionParts.add("Rotations: " + (this.scrambleOrientationPremoves.algLength() > 0 ? this.scrambleOrientationPremoves.toFormatString() : "/"));
		}

		solutionParts.addAll(this.getExecutionOrderPieceTypes().stream()
				.map(type -> type.humanName() + ":\n" + getSolutionAlgorithms(type))
				.collect(Collectors.toList()));

		return String.join("\n", solutionParts);
	}

	public String getSolutionAlgorithms() {
		return this.getSolutionAlgorithms(false);
	}

	public String getRawSolutionAlgorithm(PieceType type) {
		if (this.algSource == null || !this.algSource.isReadable()) {
			return "";
		}

		Algorithm finalAlg = new SimpleAlg();

		List<Integer> currentCycles = this.cycles.get(type);

		if (currentCycles.size() > 0 || this.getMisOrientedCount(type) > 0) { // TODO alg sources buffer support
			for (int i = 0; i < currentCycles.size(); i += 2) {
				if (i + 1 >= currentCycles.size()) {
					continue;
				}

				String pair = this.letterSchemes.get(type)[currentCycles.get(i)] + this.letterSchemes.get(type)[currentCycles.get(i + 1)];

				Set<Algorithm> caseAlgs = this.algSource.getAlgorithms(type, pair);
				Algorithm thisAlg = new SimpleAlg(new ImageLetterMove(new ImageLetterPlane('L')), new ImageLetterMove(new ImageLetterPlane('O')), new ImageLetterMove(new ImageLetterPlane('L')));

				if (caseAlgs.size() > 0) {
					List<Algorithm> listAlgs = new ArrayList<>(caseAlgs);
					Collections.shuffle(listAlgs);
					thisAlg = listAlgs.get(0);
				}

				finalAlg = finalAlg.merge(thisAlg);
			}
		} else {
			return "Solved";
		}

		return finalAlg.toFormatString().trim();
	}

	public String getRawSolutionAlgorithm(boolean withRotation) {
		List<String> solutionParts = new ArrayList<>();

		if (withRotation) {
			solutionParts.add("Rotations: " + (this.scrambleOrientationPremoves.algLength() > 0 ? this.scrambleOrientationPremoves.toFormatString() : "/"));
		}

		solutionParts.addAll(this.getExecutionOrderPieceTypes().stream()
				.map(type -> type.humanName() + ": " + getRawSolutionAlgorithm(type))
				.collect(Collectors.toList()));

		return String.join("\n", solutionParts);
	}

	public String getRawSolutionAlgorithm() {
		return this.getRawSolutionAlgorithm(false);
	}

	public String getLetterPairCorrespondant(PieceType type, String letter) {
		String[] letterScheme = this.letterSchemes.get(type);

		if (letterScheme != null) {
			int index = ArrayUtil.INSTANCE.index(letterScheme, letter);

			if (index > -1) {
				return this.getLetterPairCorrespondant(type, index);
			}
		}

		return "";
	}

	public String getLetterPairCorrespondant(PieceType type, int piece) {
		Integer[][] cubies = this.cubies.get(type);
		String[] lettering = this.getLetteringScheme(type);

		if (cubies != null) {
			int outer = ArrayUtil.INSTANCE.deepOuterIndex(cubies, piece);

			if (outer > -1) {
				Integer[] pieceModel = cubies[outer];

				List<Integer> pieces = new ArrayList<>(Arrays.asList(pieceModel));
				pieces.remove(piece);

				return String.join("", pieces.stream().map(pInt -> lettering[pInt]).collect(Collectors.toList()));
			}
		}

		return "";
	}

	public Algorithm getRotations() {
		return new SimpleAlg(this.scrambleOrientationPremoves.allMoves());
	}

	public String getStatistics(PieceType type) {
		return type.humanName() + ": " + this.getStatLength(type) + "@" + this.getBreakInCount(type) + " w/ " + this.getPreSolvedCount(type) + "-" + this.getMisOrientedCount(type) + "\\" + this.getBufferFloatNum(type) + " > " + this.hasParity(type);
	}

	public String getStatistics() {
		List<PieceType> types = this.getExecutionOrderPieceTypes();
		Collections.reverse(types);

		List<String> statisticsParts = types.stream()
				.map(this::getStatistics)
				.collect(Collectors.toList());

		return String.join("\n", statisticsParts);
	}

	public int getStatLength(PieceType type) {
		return this.cycles.get(type).size();
	}

	public int getBreakInCount(PieceType type) {
		return this.cycleCount.get(type);
	}

	public boolean isSingleCycle() {
		return this.getPieceTypes().stream()
				.allMatch(this::isSingleCycle);
	}

	public boolean isSingleCycle(PieceType type) {
		return this.getBreakInCount(type) == 0;
	}

	public int getPreSolvedCount(PieceType type) {
		Boolean[] solvedFlags = this.preSolvedPieces.get(type);

		return (int) IntStream.range(1, solvedFlags.length)
				.filter(i -> solvedFlags[i])
				.count();
	}

	public int getMisOrientedCount() {
		return this.getPieceTypes().stream()
				.mapToInt(this::getMisOrientedCount)
				.sum();
	}

	public int getMisOrientedCount(int orientation) {
		return this.getPieceTypes().stream()
				.mapToInt(type -> this.getMisOrientedCount(type, orientation))
				.sum();
	}

	public int getMisOrientedCount(PieceType type) {
		return IntStream.range(0, type.getTargetsPerPiece())
				.map(i -> this.getMisOrientedCount(type, i))
				.sum();
	}

	public int getMisOrientedCount(PieceType type, int orientation) {
		orientation %= type.getTargetsPerPiece();

		Boolean[] orientations = this.misOrientedPieces.get(type)[orientation];

		return (int) IntStream.range(1, orientations.length)
				.filter(i -> orientations[i])
				.count();
	}

	protected List<Integer> getMisOrientedPieces(PieceType type, int orientation) {
		Boolean[] orientations = this.misOrientedPieces.get(type)[orientation];
		Integer[][] cubies = this.cubies.get(type);

		return IntStream.range(1, orientations.length)
				.filter(i -> orientations[i])
				.mapToObj(i -> cubies[i][orientation])
				.collect(Collectors.toList());
	}

	protected List<String> getMisOrientedPieceNames(PieceType type, int orientation) {
		String[] lettering = this.letterSchemes.get(type);
		List<Integer> pieces = this.getMisOrientedPieces(type, orientation);

		return pieces.stream()
				.map(piece -> lettering[piece])
				.collect(Collectors.toList());
	}

	public boolean hasParity(PieceType type) {
		return this.parities.get(type);
	}

	public boolean hasParity() {
		return this.getPieceTypes().stream()
				.anyMatch(this::hasParity);
	}

	public int getBufferFloatNum(PieceType type) {
		return this.bufferFloats.get(type).size();
	}

	public int getBufferFloatNum() {
		return this.getPieceTypes().stream()
				.mapToInt(this::getBufferFloatNum)
				.sum();
	}

	public boolean hasBufferFloat(PieceType type) {
		return this.getBufferFloatNum(type) > 0;
	}

	public boolean hasBufferFloat() {
		return this.getPieceTypes().stream()
				.anyMatch(this::hasBufferFloat);
	}

	public String getNoahtation(PieceType type) {
		String misOriented = String.join("", Collections.nCopies(this.getMisOrientedCount(type), "'"));

		return type.mnemonic() + ": " + this.getStatLength(type) + misOriented;
	}

	public String getNoahtation() {
		List<PieceType> types = this.getExecutionOrderPieceTypes();
		Collections.reverse(types);

		List<String> noahtationParts = types.stream()
				.map(this::getNoahtation)
				.collect(Collectors.toList());

		return String.join(" / ", noahtationParts);
	}

	protected Queue<Integer> getBackupBuffers(PieceType type) {
		return new LinkedList<>(this.backupBuffers.get(type));
	}

	public String getStatString(PieceType type, boolean indent) {
		StringBuilder statString = new StringBuilder(type.mnemonic() + ": ");

		statString.append(this.hasParity(type) ? "_" : (indent ? " " : ""));

		int targets = this.getStatLength(type);
		statString.append(targets);

		statString.append(this.isBufferSolved(type) ? "*" : (indent ? " " : ""));
		statString.append(this.isBufferSolvedAndMisOriented(type) ? "*" : (indent ? " " : ""));

		int numFloats = this.getBufferFloatNum(type);
		int floatsMax = this.getBackupBuffers(type).size();
		statString.append(String.join("", Collections.nCopies(numFloats, "\\")));

		if (indent) {
			statString.append(String.join("", Collections.nCopies(floatsMax - numFloats, " ")));
		}

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
		List<PieceType> types = this.getExecutionOrderPieceTypes();
		Collections.reverse(types);

		List<String> statStringParts = types.stream()
				.map(type -> getStatString(type, indent))
				.collect(Collectors.toList());

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

	public Integer[] getLastPieceConfiguration(PieceType type) {
		Integer[] conf = this.lastScrambledState.get(type);
		return Arrays.copyOf(conf, conf.length);
	}

	public int getOrientationSide(PieceType type, String letter) {
		String[] letterScheme = this.letterSchemes.get(type);

		if (letterScheme != null) {
			int index = ArrayUtil.INSTANCE.index(letterScheme, letter);

			if (index > -1) {
				return this.getOrientationSide(type, index);
			}
		}

		return -1;
	}

	public int getOrientationSide(PieceType type, int piece) {
		int targets = type.getTargetsPerPiece() * type.getNumPieces();
		int targetsPerSide = targets / this.getOrientationSideCount();

		return piece / targetsPerSide;
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
			return (BldPuzzle) super.clone(); // TODO deep clone
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public BldPuzzle clone(Algorithm scramble) {
		BldPuzzle clone = this.clone();
		clone.parseScramble(scramble);
		return clone;
	}

	public List<PieceType> getPieceTypes() {
		return this.getPieceTypes(false);
	}

	public TwistyPuzzle getModel() { // FIXME protect else circular shortcutting is possible
		return this.model;
	}

	protected abstract List<PieceType> getPermutationPieceTypes();

	protected abstract List<PieceType> getOrientationPieceTypes();

	protected abstract List<PieceType> getExecutionOrderPieceTypes();

	protected abstract int getOrientationSideCount();

	protected abstract Map<PieceType, Integer[][]> getDefaultCubies();

	protected abstract void solvePieces(PieceType type);

	protected abstract Algorithm getReorientationMoves();

	protected abstract Algorithm getSolvingOrientationPremoves();

	public enum MisOrientMethod {
		SOLVE_DIRECT, SINGLE_TARGET
	}
}