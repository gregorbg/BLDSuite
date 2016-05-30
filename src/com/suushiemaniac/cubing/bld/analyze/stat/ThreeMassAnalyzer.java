package com.suushiemaniac.cubing.bld.analyze.stat;

import com.suushiemaniac.cubing.bld.analyze.cube.ThreeBldCube;
import puzzle.NoInspectionThreeByThreeCubePuzzle;

import java.util.HashMap;
import java.util.Map;

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
        ThreeBldCube threeAnalyze = new ThreeBldCube("");
        int steps = numCubes / Math.min(100, numCubes);
        for (int i = 0; i < numCubes; i++) {
            if (i % steps == 0) System.out.println("Cube " + i);
            String scramble = threeNoodle.generateScramble();
            threeAnalyze.parseScramble(scramble);

            if (threeAnalyze.getCornerLength() == 0) System.out.println(scramble);
            if (threeAnalyze.getEdgeLength() < 6) System.out.println(scramble);

            cornerParity += threeAnalyze.hasCornerParity() ? 1 : 0;

            cornerBufferSolved += threeAnalyze.isCornerBufferSolved() ? 1 : 0;
            edgeBufferSolved += threeAnalyze.isEdgeBufferSolved() ? 1 : 0;

            cornerTargets.put(threeAnalyze.getCornerLength(), cornerTargets.getOrDefault(threeAnalyze.getCornerLength(), 0) + 1);
            edgeTargets.put(threeAnalyze.getEdgeLength(), edgeTargets.getOrDefault(threeAnalyze.getEdgeLength(), 0) + 1);
            cornerBreakIn.put(threeAnalyze.getCornerBreakInNum(), cornerBreakIn.getOrDefault(threeAnalyze.getCornerBreakInNum(), 0) + 1);
            edgeBreakIn.put(threeAnalyze.getEdgeBreakInNum(), edgeBreakIn.getOrDefault(threeAnalyze.getEdgeBreakInNum(), 0) + 1);
            cornerSolved.put(threeAnalyze.getNumPreSolvedCorners(), cornerSolved.getOrDefault(threeAnalyze.getNumPreSolvedCorners(), 0) + 1);
            edgeSolved.put(threeAnalyze.getNumPreSolvedEdges(), edgeSolved.getOrDefault(threeAnalyze.getNumPreSolvedEdges(), 0) + 1);
            cornerMisOrient.put(threeAnalyze.getNumPreTwistedCorners(), cornerMisOrient.getOrDefault(threeAnalyze.getNumPreTwistedCorners(), 0) + 1);
            edgeMisOrient.put(threeAnalyze.getNumPreFlippedEdges(), edgeMisOrient.getOrDefault(threeAnalyze.getNumPreFlippedEdges(), 0) + 1);
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
        ThreeBldCube threeAnalyze = new ThreeBldCube("");
        int steps = numCubes / Math.min(100, numCubes);
        for (int i = 0; i < numCubes; i++) {
            if (i % steps == 0) System.out.println("Cube " + i);
            threeAnalyze.parseScramble(threeNoodle.generateScramble());

            corner.put(threeAnalyze.getCornerStatString(), corner.getOrDefault(threeAnalyze.getCornerStatString(), 0) + 1);
            edge.put(threeAnalyze.getEdgeStatString(), edge.getOrDefault(threeAnalyze.getEdgeStatString(), 0) + 1);
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
        ThreeBldCube threeAnalyze = new ThreeBldCube("");
        int steps = numCubes / Math.min(100, numCubes);
        for (int i = 0; i < numCubes; i++) {
            if (i % steps == 0) System.out.println("Cube " + i);
            threeAnalyze.parseScramble(threeNoodle.generateScramble());

            if (threeAnalyze.getCornerLength() > 0) {
                String[] cornerPairs = threeAnalyze.getCornerPairs(false).replaceAll(singleLetter ? "\\s+?" : "$.", "").split(singleLetter ? "" : "\\s+?");

                for (String pair : cornerPairs) {
                    corner.put(pair, corner.getOrDefault(pair, 0) + 1);
                }
            }

            if (threeAnalyze.getEdgeLength() > 0) {
                String[] edgePairs = threeAnalyze.getEdgePairs(false).replaceAll(singleLetter ? "\\s+?" : "$.", "").split(singleLetter ? "" : "\\s+?");

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
