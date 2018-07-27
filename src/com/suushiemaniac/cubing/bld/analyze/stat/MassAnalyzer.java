package com.suushiemaniac.cubing.bld.analyze.stat;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import com.suushiemaniac.cubing.bld.util.ClosureUtil;
import com.suushiemaniac.cubing.bld.util.CountingMap;
import com.suushiemaniac.cubing.bld.util.MapUtil;
import net.gnehzr.tnoodle.scrambles.Puzzle;

import java.util.*;

public class MassAnalyzer {
	protected BldPuzzle analyze;

	public MassAnalyzer(BldPuzzle analyze) {
		this.analyze = analyze;
	}

	public void analyzeProperties(List<Algorithm> scrambles) {
		CountingMap<PieceType> parityCounts = new CountingMap<>();

		CountingMap<PieceType> solvedBufferCounts = new CountingMap<>();

		Map<PieceType, CountingMap<Integer>> targets = new HashMap<>();
		Map<PieceType, CountingMap<Integer>> breakIns = new HashMap<>();
		Map<PieceType, CountingMap<Integer>> preSolved = new HashMap<>();
		Map<PieceType, CountingMap<Integer>> misOriented = new HashMap<>();

		for (Algorithm scramble : scrambles) {
			this.analyze.parseScramble(scramble);

			for (PieceType type : this.analyze.getPieceTypes()) {
				if (this.analyze.hasParity(type)) {
					parityCounts.increment(type);
				}

				if (this.analyze.isBufferSolved(type)) {
					solvedBufferCounts.increment(type);
				}

				targets.computeIfAbsent(type, ClosureUtil.always(CountingMap::new)).increment(this.analyze.getStatLength(type));
				breakIns.computeIfAbsent(type, ClosureUtil.always(CountingMap::new)).increment(this.analyze.getBreakInCount(type));
				preSolved.computeIfAbsent(type, ClosureUtil.always(CountingMap::new)).increment(this.analyze.getPreSolvedCount(type));
				misOriented.computeIfAbsent(type, ClosureUtil.always(CountingMap::new)).increment(this.analyze.getMisOrientedCount(type));
			}
		}

		int numCubes = scrambles.size();

		System.out.println("Total scrambles: " + numCubes);

		for (PieceType type : this.analyze.getPieceTypes()) {
			System.out.println();
			System.out.println("Parity: " + parityCounts.get(type));
			System.out.println("Average: " + (parityCounts.get(type) / (float) numCubes));

			System.out.println();
			System.out.println("Buffer preSolved: " + solvedBufferCounts.get(type));
			System.out.println("Average: " + (solvedBufferCounts.get(type) / (float) numCubes));

			System.out.println();
			System.out.println(type.humanName() + " targets");
			numericMapPrint(targets.get(type));

			System.out.println();
			System.out.println(type.humanName() + " break-ins");
			numericMapPrint(breakIns.get(type));

			System.out.println();
			System.out.println(type.humanName() + " pre-solved");
			numericMapPrint(preSolved.get(type));

			System.out.println();
			System.out.println(type.humanName() + " mis-oriented");
			numericMapPrint(misOriented.get(type));
		}
	}

    public void analyzeProperties(int numCubes) {
		this.analyzeProperties(this.generateRandom(numCubes));
	}

	public void analyzeScrambleDist(List<Algorithm> scrambles) {
		Map<PieceType, CountingMap<String>> pieceTypeMap = new HashMap<>();

		CountingMap<String> overall = new CountingMap<>();

		for (Algorithm scramble : scrambles) {
			this.analyze.parseScramble(scramble);

			for (PieceType type : this.analyze.getPieceTypes()) {
				pieceTypeMap.computeIfAbsent(type, ClosureUtil.always(CountingMap::new)).increment(this.analyze.getStatString(type));
			}

			overall.increment(this.analyze.getStatString());
		}

		for (PieceType type : this.analyze.getPieceTypes()) {
			System.out.println();
			System.out.println(type.humanName());

			stringMapPrint(pieceTypeMap.get(type));
		}

		System.out.println();
		System.out.println("Overall");
		stringMapPrint(overall);
	}

    public void analyzeScrambleDist(int numCubes) {
		this.analyzeScrambleDist(this.generateRandom(numCubes));
	}

	public void analyzeLetterPairs(List<Algorithm> scrambles, boolean singleLetter) {
		Map<PieceType, CountingMap<String>> pieceTypeMap = new HashMap<>();

		for (Algorithm scramble : scrambles) {
			this.analyze.parseScramble(scramble);

			for (PieceType type : this.analyze.getPieceTypes()) {
				if (this.analyze.getStatLength(type) > 0) {
					String[] cornerPairs = this.analyze.getSolutionPairs(type).replaceAll(singleLetter ? "\\s+?" : "$.", "").split(singleLetter ? "" : "\\s+?");

					for (String pair : cornerPairs) {
						pieceTypeMap.computeIfAbsent(type, pt -> new CountingMap<>()).increment(pair);
					}
				}
			}
		}

		for (PieceType type : this.analyze.getPieceTypes()) {
			System.out.println();
			System.out.println(type.humanName());

			stringMapPrint(pieceTypeMap.get(type));
		}
	}

    public void analyzeLetterPairs(int numCubes, boolean singleLetter) {
		this.analyzeLetterPairs(this.generateRandom(numCubes), singleLetter);
	}

    public void analyzeLetterPairs(int numCubes) {
        this.analyzeLetterPairs(numCubes, false);
    }

	public List<Algorithm> generateRandom(int numCubes) {
		Puzzle tNoodle = this.analyze.getModel().getScramblingPuzzle();
		NotationReader reader = this.analyze.getModel().getReader();

		List<Algorithm> scrambles = new ArrayList<>();

		for (int i = 0; i < numCubes; i++) {
			if (i % (numCubes / Math.min(100, numCubes)) == 0) {
				System.out.println("Cube " + i);
			}

			String rawScramble = tNoodle.generateScramble();
			Algorithm scramble = reader.parse(rawScramble);

			scrambles.add(scramble);
		}

		return scrambles;
	}

    protected static void numericMapPrint(Map<Integer, Integer> toPrint) {
		MapUtil.INSTANCE.sortedMapPrint(toPrint);
		System.out.println("Average: " + MapUtil.INSTANCE.freqMapAverage(toPrint));
    }

    protected static void stringMapPrint(Map<String, Integer> toPrint) {
    	MapUtil.INSTANCE.sortedMapPrint(toPrint);
    }
}
