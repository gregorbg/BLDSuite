package com.suushiemaniac.cubing.bld.analyze.stat;

import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.analyze.cube.ThreeBldCube;
import com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType;
import puzzle.NoInspectionThreeByThreeCubePuzzle;

import java.util.HashMap;
import java.util.Map;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.CORNER;
import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.EDGE;

public class ThreeMassAnalyzer extends MassAnalyzer {
    @Override
    public void analyzeProperties(int numCubes) {
        long cornerParity = 0;

        long cornerBufferSolved = 0;
        long edgeBufferSolved = 0;

        Map<Integer, Integer> cornerTargets = new HashMap<>();
        Map<Integer, Integer> edgeTargets = new HashMap<>();
        Map<Integer, Integer> cornerBreakIn = new HashMap<>();
        Map<Integer, Integer> edgeBreakIn = new HashMap<>();
        Map<Integer, Integer> cornerSolved = new HashMap<>();
        Map<Integer, Integer> edgeSolved = new HashMap<>();
        Map<Integer, Integer> cornerMisOrient = new HashMap<>();
        Map<Integer, Integer> edgeMisOrient = new HashMap<>();

        NoInspectionThreeByThreeCubePuzzle threeNoodle = new NoInspectionThreeByThreeCubePuzzle();
        ThreeBldCube threeAnalyze = new ThreeBldCube();
		NotationReader reader = new CubicAlgorithmReader();

        int steps = numCubes / Math.min(100, numCubes);
        for (int i = 0; i < numCubes; i++) {
            if (i % steps == 0) System.out.println("Cube " + i);
            String scramble = threeNoodle.generateScramble();
			threeAnalyze.parseScramble(reader.parse(scramble));

            if (threeAnalyze.getStatLength(CORNER) == 0) System.out.println(scramble);
            if (threeAnalyze.getStatLength(EDGE) < 6) System.out.println(scramble);

            cornerParity += threeAnalyze.hasParity(CORNER) ? 1 : 0;

            cornerBufferSolved += threeAnalyze.isBufferSolved(CORNER) ? 1 : 0;
            edgeBufferSolved += threeAnalyze.isBufferSolved(EDGE) ? 1 : 0;

            cornerTargets.put(threeAnalyze.getStatLength(CORNER), cornerTargets.getOrDefault(threeAnalyze.getStatLength(CORNER), 0) + 1);
            edgeTargets.put(threeAnalyze.getStatLength(EDGE), edgeTargets.getOrDefault(threeAnalyze.getStatLength(EDGE), 0) + 1);
            cornerBreakIn.put(threeAnalyze.getBreakInCount(CORNER), cornerBreakIn.getOrDefault(threeAnalyze.getBreakInCount(CORNER), 0) + 1);
            edgeBreakIn.put(threeAnalyze.getBreakInCount(EDGE), edgeBreakIn.getOrDefault(threeAnalyze.getBreakInCount(EDGE), 0) + 1);
            cornerSolved.put(threeAnalyze.getPreSolvedCount(CORNER), cornerSolved.getOrDefault(threeAnalyze.getPreSolvedCount(CORNER), 0) + 1);
            edgeSolved.put(threeAnalyze.getPreSolvedCount(EDGE), edgeSolved.getOrDefault(threeAnalyze.getPreSolvedCount(EDGE), 0) + 1);
            cornerMisOrient.put(threeAnalyze.getMisOrientedCount(CORNER), cornerMisOrient.getOrDefault(threeAnalyze.getMisOrientedCount(CORNER), 0) + 1);
            edgeMisOrient.put(threeAnalyze.getMisOrientedCount(EDGE), edgeMisOrient.getOrDefault(threeAnalyze.getMisOrientedCount(EDGE), 0) + 1);
        }

        System.out.println();
        System.out.println("Parity: " + cornerParity);
        System.out.println("Average: " + (cornerParity / (float) numCubes));
        System.out.println();
        System.out.println("Corner buffer solved: " + cornerBufferSolved);
        System.out.println("Average: " + (cornerBufferSolved / (float) numCubes));
        System.out.println();
        System.out.println("Edge buffer solved: " + edgeBufferSolved);
        System.out.println("Average: " + (edgeBufferSolved / (float) numCubes));
        System.out.println();
        System.out.println("Corner targets");
        numericMapPrint(cornerTargets);
        System.out.println();
        System.out.println("Edge targets");
        numericMapPrint(edgeTargets);
        System.out.println();
        System.out.println("Corner break-ins");
        numericMapPrint(cornerBreakIn);
        System.out.println();
        System.out.println("Edge break-ins");
        numericMapPrint(edgeBreakIn);
        System.out.println();
        System.out.println("Corners solved");
        numericMapPrint(cornerSolved);
        System.out.println();
        System.out.println("Edges solved");
        numericMapPrint(edgeSolved);
        System.out.println();
        System.out.println("Corners mis-oriented");
        numericMapPrint(cornerMisOrient);
        System.out.println();
        System.out.println("Edges mis-oriented");
        numericMapPrint(edgeMisOrient);
    }

    @Override
    public void analyzeScrambleDist(int numCubes) {
        Map<String, Integer> corner = new HashMap<>();
        Map<String, Integer> edge = new HashMap<>();

        Map<String, Integer> overall = new HashMap<>();

        NoInspectionThreeByThreeCubePuzzle threeNoodle = new NoInspectionThreeByThreeCubePuzzle();
        ThreeBldCube threeAnalyze = new ThreeBldCube();
		NotationReader reader = new CubicAlgorithmReader();
        int steps = numCubes / Math.min(100, numCubes);
        for (int i = 0; i < numCubes; i++) {
            if (i % steps == 0) System.out.println("Cube " + i);
			String scrString = threeNoodle.generateScramble();
            threeAnalyze.parseScramble(reader.parse(scrString));

            corner.put(threeAnalyze.getStatString(CORNER), corner.getOrDefault(threeAnalyze.getStatString(CORNER), 0) + 1);
            edge.put(threeAnalyze.getStatString(EDGE), edge.getOrDefault(threeAnalyze.getStatString(EDGE), 0) + 1);
            overall.put(threeAnalyze.getStatString(), overall.getOrDefault(threeAnalyze.getStatString(), 0) + 1);
        }

        System.out.println();
        System.out.println("Corner");
        stringMapPrint(corner);
        System.out.println();
        System.out.println("Edge");
        stringMapPrint(edge);
        System.out.println();
        System.out.println("Overall");
        stringMapPrint(overall);
    }

    @Override
    public void analyzeLetterPairs(int numCubes, boolean singleLetter) {
        Map<String, Integer> corner = new HashMap<>();
        Map<String, Integer> edge = new HashMap<>();

        NoInspectionThreeByThreeCubePuzzle threeNoodle = new NoInspectionThreeByThreeCubePuzzle();
        ThreeBldCube threeAnalyze = new ThreeBldCube();
		NotationReader reader = new CubicAlgorithmReader();

        int steps = numCubes / Math.min(100, numCubes);
        for (int i = 0; i < numCubes; i++) {
            if (i % steps == 0) System.out.println("Cube " + i);
			String scrString = threeNoodle.generateScramble();
            threeAnalyze.parseScramble(reader.parse(scrString));

            if (threeAnalyze.getStatLength(CORNER) > 0) {
                String[] cornerPairs = threeAnalyze.getSolutionPairs(CORNER).replaceAll(singleLetter ? "\\s+?" : "$.", "").split(singleLetter ? "" : "\\s+?");

                for (String pair : cornerPairs) {
                    corner.put(pair, corner.getOrDefault(pair, 0) + 1);
                }
            }

            if (threeAnalyze.getStatLength(EDGE) > 0) {
                String[] edgePairs = threeAnalyze.getSolutionPairs(EDGE).replaceAll(singleLetter ? "\\s+?" : "$.", "").split(singleLetter ? "" : "\\s+?");

                for (String pair : edgePairs) {
                    edge.put(pair, edge.getOrDefault(pair, 0) + 1);
                }
            }
        }

        System.out.println();
        System.out.println("Corner");
        stringMapPrint(corner);
        System.out.println();
        System.out.println("Edge");
        stringMapPrint(edge);
    }
}
