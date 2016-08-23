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

	public BldPuzzle() {
		this.permutations = this.loadPermutations();

		this.state = this.initState();
		this.lastScrambledState = this.initState();

		this.cubies = this.initCubies();
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

	protected void scramblePuzzle(Algorithm scramble) {
		scramble = this.solvingOrientationPremoves.merge(scramble);

		for (Move move : scramble) {
			this.permute(move);
		}
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
    	//TODO this.reorientCube();

    	for (PieceType type : this.state.keySet()) {
    		this.saveState(type);
    		this.solvePieces(type);
		}
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

	protected abstract List<PieceType> getPieceTypes();
	protected abstract Map<PieceType, Integer[][]> getDefaultCubies();
	protected abstract void solvePieces(PieceType type);
}