package com.suushiemaniac.cubing.bld.analyze.stat;

import com.suushiemaniac.cubing.bld.analyze.cube.FiveBldCube;
import puzzle.NoInspectionFiveByFiveCubePuzzle;

import java.util.HashMap;
import java.util.Map;

public class FiveMassAnalyzer extends MassAnalyzer {
    @Override
    public void analyzeProperties(int numCubes) {
        long cornerParity = 0;
        long wingParity = 0;
        long xCenterParity = 0;
        long tCenterParity = 0;

        long cornerBufferSolved = 0;
        long edgeBufferSolved = 0;
        long wingBufferSolved = 0;
        long xCenterBufferSolved = 0;
        long tCenterBufferSolved = 0;

        Map<Integer, Integer> cornerTargets = new HashMap<>();
        Map<Integer, Integer> edgeTargets = new HashMap<>();
        Map<Integer, Integer> wingTargets = new HashMap<>();
        Map<Integer, Integer> xCenterTargets = new HashMap<>();
        Map<Integer, Integer> tCenterTargets = new HashMap<>();
        Map<Integer, Integer> cornerBreakIn = new HashMap<>();
        Map<Integer, Integer> edgeBreakIn = new HashMap<>();
        Map<Integer, Integer> wingBreakIn = new HashMap<>();
        Map<Integer, Integer> xCenterBreakIn = new HashMap<>();
        Map<Integer, Integer> tCenterBreakIn = new HashMap<>();
        Map<Integer, Integer> cornerSolved = new HashMap<>();
        Map<Integer, Integer> edgeSolved = new HashMap<>();
        Map<Integer, Integer> wingSolved = new HashMap<>();
        Map<Integer, Integer> xCenterSolved = new HashMap<>();
        Map<Integer, Integer> tCenterSolved = new HashMap<>();
        Map<Integer, Integer> cornerMisOrient = new HashMap<>();
        Map<Integer, Integer> edgeMisOrient = new HashMap<>();

        NoInspectionFiveByFiveCubePuzzle fiveNoodle = new NoInspectionFiveByFiveCubePuzzle();
        FiveBldCube fiveAnalyze = new FiveBldCube("");
        int steps = numCubes / Math.min(100, numCubes);
        for (int i = 0; i < numCubes; i++) {
            if (i % steps == 0) System.out.println("Cube " + i);
            String scramble = fiveNoodle.generateScramble();
            fiveAnalyze.parseScramble(scramble);

            cornerParity += fiveAnalyze.hasCornerParity() ? 1 : 0;
            wingParity += fiveAnalyze.hasWingParity() ? 1 : 0;
            xCenterParity += fiveAnalyze.hasXCenterParity() ? 1 : 0;
            tCenterParity += fiveAnalyze.hasTCenterParity() ? 1 : 0;

            cornerBufferSolved += fiveAnalyze.isCornerBufferSolved() ? 1 : 0;
            edgeBufferSolved += fiveAnalyze.isEdgeBufferSolved() ? 1 : 0;
            wingBufferSolved += fiveAnalyze.isWingBufferSolved() ? 1 : 0;
            xCenterBufferSolved += fiveAnalyze.isXCenterBufferSolved() ? 1 : 0;
            tCenterBufferSolved += fiveAnalyze.isTCenterBufferSolved() ? 1 : 0;

            cornerTargets.put(fiveAnalyze.getCornerLength(), cornerTargets.getOrDefault(fiveAnalyze.getCornerLength(), 0) + 1);
            edgeTargets.put(fiveAnalyze.getEdgeLength(), edgeTargets.getOrDefault(fiveAnalyze.getEdgeLength(), 0) + 1);
            wingTargets.put(fiveAnalyze.getWingLength(), wingTargets.getOrDefault(fiveAnalyze.getWingLength(), 0) + 1);
            xCenterTargets.put(fiveAnalyze.getXCenterLength(), xCenterTargets.getOrDefault(fiveAnalyze.getXCenterLength(), 0) + 1);
            tCenterTargets.put(fiveAnalyze.getTCenterLength(), tCenterTargets.getOrDefault(fiveAnalyze.getTCenterLength(), 0) + 1);
            cornerBreakIn.put(fiveAnalyze.getCornerBreakInNum(), cornerBreakIn.getOrDefault(fiveAnalyze.getCornerBreakInNum(), 0) + 1);
            edgeBreakIn.put(fiveAnalyze.getEdgeBreakInNum(), edgeBreakIn.getOrDefault(fiveAnalyze.getEdgeBreakInNum(), 0) + 1);
            wingBreakIn.put(fiveAnalyze.getWingBreakInNum(), wingBreakIn.getOrDefault(fiveAnalyze.getWingBreakInNum(), 0) + 1);
            xCenterBreakIn.put(fiveAnalyze.getXCenterBreakInNum(), xCenterBreakIn.getOrDefault(fiveAnalyze.getXCenterBreakInNum(), 0) + 1);
            tCenterBreakIn.put(fiveAnalyze.getTCenterBreakInNum(), tCenterBreakIn.getOrDefault(fiveAnalyze.getTCenterBreakInNum(), 0) + 1);
            cornerSolved.put(fiveAnalyze.getNumPreSolvedCorners(), cornerSolved.getOrDefault(fiveAnalyze.getNumPreSolvedCorners(), 0) + 1);
            edgeSolved.put(fiveAnalyze.getNumPreSolvedEdges(), edgeSolved.getOrDefault(fiveAnalyze.getNumPreSolvedEdges(), 0) + 1);
            wingSolved.put(fiveAnalyze.getNumPreSolvedWings(), wingSolved.getOrDefault(fiveAnalyze.getNumPreSolvedWings(), 0) + 1);
            xCenterSolved.put(fiveAnalyze.getNumPreSolvedXCenters(), xCenterSolved.getOrDefault(fiveAnalyze.getNumPreSolvedXCenters(), 0) + 1);
            tCenterSolved.put(fiveAnalyze.getNumPreSolvedTCenters(), tCenterSolved.getOrDefault(fiveAnalyze.getNumPreSolvedTCenters(), 0) + 1);
            cornerMisOrient.put(fiveAnalyze.getNumPreTwistedCorners(), cornerMisOrient.getOrDefault(fiveAnalyze.getNumPreTwistedCorners(), 0) + 1);
            edgeMisOrient.put(fiveAnalyze.getNumPreFlippedEdges(), edgeMisOrient.getOrDefault(fiveAnalyze.getNumPreFlippedEdges(), 0) + 1);
        }

        System.out.println();
        System.out.println("CornerParity: " + cornerParity);
        System.out.println("Average: " + (cornerParity / (float) numCubes));
        System.out.println();
        System.out.println("WingParity: " + wingParity);
        System.out.println("Average: " + (wingParity / (float) numCubes));
        System.out.println();
        System.out.println("XCenterParity: " + xCenterParity);
        System.out.println("Average: " + (xCenterParity / (float) numCubes));
        System.out.println();
        System.out.println("TCenterParity: " + tCenterParity);
        System.out.println("Average: " + (tCenterParity / (float) numCubes));
        System.out.println();
        System.out.println("Corner buffer solved: " + cornerBufferSolved);
        System.out.println("Average: " + (cornerBufferSolved / (float) numCubes));
        System.out.println();
        System.out.println("Edge buffer solved: " + edgeBufferSolved);
        System.out.println("Average: " + (edgeBufferSolved / (float) numCubes));
        System.out.println();
        System.out.println("Wing buffer solved: " + wingBufferSolved);
        System.out.println("Average: " + (wingBufferSolved / (float) numCubes));
        System.out.println();
        System.out.println("XCenter buffer solved: " + xCenterBufferSolved);
        System.out.println("Average: " + (xCenterBufferSolved / (float) numCubes));
        System.out.println();
        System.out.println("TCenter buffer solved: " + tCenterBufferSolved);
        System.out.println("Average: " + (tCenterBufferSolved / (float) numCubes));
        System.out.println();
        System.out.println("Corner targets");
        numericMapPrint(cornerTargets);
        System.out.println();
        System.out.println("Edge targets");
        numericMapPrint(edgeTargets);
        System.out.println();
        System.out.println("Wing targets");
        numericMapPrint(wingTargets);
        System.out.println();
        System.out.println("XCenter targets");
        numericMapPrint(xCenterTargets);
        System.out.println();
        System.out.println("TCenter targets");
        numericMapPrint(tCenterTargets);
        System.out.println();
        System.out.println("Corner break-ins");
        numericMapPrint(cornerBreakIn);
        System.out.println();
        System.out.println("Edge break-ins");
        numericMapPrint(edgeBreakIn);
        System.out.println();
        System.out.println("Wing break-ins");
        numericMapPrint(wingBreakIn);
        System.out.println();
        System.out.println("XCenter break-ins");
        numericMapPrint(xCenterBreakIn);
        System.out.println();
        System.out.println("TCenter break-ins");
        numericMapPrint(tCenterBreakIn);
        System.out.println();
        System.out.println("Corners solved");
        numericMapPrint(cornerSolved);
        System.out.println();
        System.out.println("Edge solved");
        numericMapPrint(edgeSolved);
        System.out.println();
        System.out.println("Wings solved");
        numericMapPrint(wingSolved);
        System.out.println();
        System.out.println("XCenter solved");
        numericMapPrint(xCenterSolved);
        System.out.println();
        System.out.println("TCenter solved");
        numericMapPrint(tCenterSolved);
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
        Map<String, Integer> wing = new HashMap<>();
        Map<String, Integer> xCenter = new HashMap<>();
        Map<String, Integer> tCenter = new HashMap<>();

        Map<String, Integer> cornerEdge = new HashMap<>();
        Map<String, Integer> cornerWing = new HashMap<>();
        Map<String, Integer> cornerXCenter = new HashMap<>();
        Map<String, Integer> cornerTCenter = new HashMap<>();
        Map<String, Integer> edgeWing = new HashMap<>();
        Map<String, Integer> edgeXCenter = new HashMap<>();
        Map<String, Integer> edgeTCenter = new HashMap<>();
        Map<String, Integer> wingXCenter = new HashMap<>();
        Map<String, Integer> wingTCenter = new HashMap<>();
        Map<String, Integer> xCenterTCenter = new HashMap<>();

        Map<String, Integer> cornerEdgeWing = new HashMap<>();
        Map<String, Integer> cornerEdgeXCenter = new HashMap<>();
        Map<String, Integer> cornerEdgeTCenter = new HashMap<>();
        Map<String, Integer> cornerWingXCenter = new HashMap<>();
        Map<String, Integer> cornerWingTCenter = new HashMap<>();
        Map<String, Integer> cornerXCenterTCenter = new HashMap<>();
        Map<String, Integer> edgeWingXCenter = new HashMap<>();
        Map<String, Integer> edgeWingTCenter = new HashMap<>();
        Map<String, Integer> edgeXCenterTCenter = new HashMap<>();
        Map<String, Integer> wingXCenterTCenter = new HashMap<>();

        Map<String, Integer> cornerEdgeWingXCenter = new HashMap<>();
        Map<String, Integer> cornerEdgeWingTCenter = new HashMap<>();
        Map<String, Integer> cornerEdgeXCenterTCenter = new HashMap<>();
        Map<String, Integer> cornerWingXCenterTCenter = new HashMap<>();
        Map<String, Integer> edgeWingXCenterTCenter = new HashMap<>();

        Map<String, Integer> overall = new HashMap<>();

        NoInspectionFiveByFiveCubePuzzle fiveNoodle = new NoInspectionFiveByFiveCubePuzzle();
        FiveBldCube fiveAnalyze = new FiveBldCube("");
        int steps = numCubes / Math.min(100, numCubes);
        for (int i = 0; i < numCubes; i++) {
            if (i % steps == 0) System.out.println("Cube " + i);
            fiveAnalyze.parseScramble(fiveNoodle.generateScramble());

            String cornerEdgeStat = "C: " + fiveAnalyze.getCornerStatString() + " | E: " + fiveAnalyze.getEdgeStatString();
            String cornerWingStat = "C: " + fiveAnalyze.getCornerStatString() + " | W: " + fiveAnalyze.getWingStatString();
            String cornerXCenterStat = "C: " + fiveAnalyze.getCornerStatString() + " | X: " + fiveAnalyze.getXCenterStatString();
            String cornerTCenterStat = "C: " + fiveAnalyze.getCornerStatString() + " | T: " + fiveAnalyze.getTCenterStatString();
            String edgeWingStat = "E: " + fiveAnalyze.getEdgeStatString() + " | W: " + fiveAnalyze.getWingStatString();
            String edgeXCenterStat = "E: " + fiveAnalyze.getEdgeStatString() + " | X: " + fiveAnalyze.getXCenterStatString();
            String edgeTCenterStat = "E: " + fiveAnalyze.getEdgeStatString() + " | T: " + fiveAnalyze.getTCenterStatString();
            String wingXCenterStat = "W: " + fiveAnalyze.getWingStatString() + " | X: " + fiveAnalyze.getXCenterStatString();
            String wingTCenterStat = "W: " + fiveAnalyze.getWingStatString() + " | T: " + fiveAnalyze.getTCenterStatString();
            String xCenterTCenterStat = "X: " + fiveAnalyze.getXCenterStatString() + " | T: " + fiveAnalyze.getTCenterStatString();

            String cornerEdgeWingStat = "C: " + fiveAnalyze.getCornerStatString() + " | E: " + fiveAnalyze.getEdgeStatString() + " | W: " + fiveAnalyze.getWingStatString();
            String cornerEdgeXCenterStat = "C: " + fiveAnalyze.getCornerStatString() + " | E: " + fiveAnalyze.getEdgeStatString() + " | X: " + fiveAnalyze.getXCenterStatString();
            String cornerEdgeTCenterStat = "C: " + fiveAnalyze.getCornerStatString() + " | E: " + fiveAnalyze.getEdgeStatString() + " | T: " + fiveAnalyze.getTCenterStatString();
            String cornerWingXCenterStat = "C: " + fiveAnalyze.getCornerStatString() + " | W: " + fiveAnalyze.getWingStatString() + " | X: " + fiveAnalyze.getXCenterStatString();
            String cornerWingTCenterStat = "C: " + fiveAnalyze.getCornerStatString() + " | W: " + fiveAnalyze.getWingStatString() + " | T: " + fiveAnalyze.getTCenterStatString();
            String cornerXCenterTCenterStat = "C: " + fiveAnalyze.getCornerStatString() + " | X: " + fiveAnalyze.getXCenterStatString() + " | T: " + fiveAnalyze.getTCenterStatString();
            String edgeWingXCenterStat = "E: " + fiveAnalyze.getEdgeStatString() + " | W: " + fiveAnalyze.getWingStatString() + " | X: " + fiveAnalyze.getXCenterStatString();
            String edgeWingTCenterStat = "E: " + fiveAnalyze.getEdgeStatString() + " | W: " + fiveAnalyze.getWingStatString() + " | T: " + fiveAnalyze.getTCenterStatString();
            String edgeXCenterTCenterStat = "E: " + fiveAnalyze.getEdgeStatString() + " | X: " + fiveAnalyze.getXCenterStatString() + " | T: " + fiveAnalyze.getTCenterStatString();
            String wingXCenterTCenterStat = "W: " + fiveAnalyze.getWingStatString() + " | X: " + fiveAnalyze.getXCenterStatString() + " | T: " + fiveAnalyze.getTCenterStatString();

            String cornerEdgeWingXCenterStat = "C: " + fiveAnalyze.getCornerStatString() + " | E: " + fiveAnalyze.getEdgeStatString() + " | W: " + fiveAnalyze.getWingStatString() + " | X: " + fiveAnalyze.getXCenterStatString();
            String cornerEdgeWingTCenterStat = "C: " + fiveAnalyze.getCornerStatString() + " | E: " + fiveAnalyze.getEdgeStatString() + " | W: " + fiveAnalyze.getWingStatString() + " | T: " + fiveAnalyze.getTCenterStatString();
            String cornerEdgeXCenterTCenterStat = "C: " + fiveAnalyze.getCornerStatString() + " | E: " + fiveAnalyze.getEdgeStatString() + " | X: " + fiveAnalyze.getXCenterStatString() + " | T: " + fiveAnalyze.getTCenterStatString();
            String cornerWingXCenterTCenterStat = "C: " + fiveAnalyze.getCornerStatString() + " | W: " + fiveAnalyze.getWingStatString() + " | X: " + fiveAnalyze.getXCenterStatString() + " | T: " + fiveAnalyze.getTCenterStatString();
            String edgeWingXCenterTCenterStat = "E: " + fiveAnalyze.getEdgeStatString() + " | W: " + fiveAnalyze.getWingStatString() + " | X: " + fiveAnalyze.getXCenterStatString() + " | T: " + fiveAnalyze.getTCenterStatString();

            corner.put(fiveAnalyze.getCornerStatString(), corner.getOrDefault(fiveAnalyze.getCornerStatString(), 0) + 1);
            edge.put(fiveAnalyze.getEdgeStatString(), edge.getOrDefault(fiveAnalyze.getEdgeStatString(), 0) + 1);
            wing.put(fiveAnalyze.getWingStatString(), wing.getOrDefault(fiveAnalyze.getWingStatString(), 0) + 1);
            xCenter.put(fiveAnalyze.getXCenterStatString(), xCenter.getOrDefault(fiveAnalyze.getXCenterStatString(), 0) + 1);
            tCenter.put(fiveAnalyze.getTCenterStatString(), tCenter.getOrDefault(fiveAnalyze.getTCenterStatString(), 0) + 1);

            cornerWing.put(cornerWingStat, cornerWing.getOrDefault(cornerWingStat, 0) + 1);
            cornerEdge.put(cornerEdgeStat, cornerEdge.getOrDefault(cornerEdgeStat, 0) + 1);
            cornerXCenter.put(cornerXCenterStat, cornerXCenter.getOrDefault(cornerXCenterStat, 0) + 1);
            cornerTCenter.put(cornerTCenterStat, cornerTCenter.getOrDefault(cornerTCenterStat, 0) + 1);
            edgeWing.put(edgeWingStat, edgeWing.getOrDefault(edgeWingStat, 0) + 1);
            edgeXCenter.put(edgeXCenterStat, edgeXCenter.getOrDefault(edgeXCenterStat, 0) + 1);
            edgeTCenter.put(edgeTCenterStat, edgeTCenter.getOrDefault(edgeTCenterStat, 0) + 1);
            wingXCenter.put(wingXCenterStat, wingXCenter.getOrDefault(wingXCenterStat, 0) + 1);
            wingTCenter.put(wingTCenterStat, wingTCenter.getOrDefault(wingTCenterStat, 0) + 1);
            xCenterTCenter.put(xCenterTCenterStat, xCenterTCenter.getOrDefault(xCenterTCenterStat, 0) + 1);

            cornerEdgeWing.put(cornerEdgeWingStat, cornerEdgeWing.getOrDefault(cornerEdgeWingStat, 0) + 1);
            cornerEdgeXCenter.put(cornerEdgeXCenterStat, cornerEdgeXCenter.getOrDefault(cornerEdgeXCenterStat, 0) + 1);
            cornerEdgeTCenter.put(cornerEdgeTCenterStat, cornerEdgeTCenter.getOrDefault(cornerEdgeTCenterStat, 0) + 1);
            cornerWingXCenter.put(cornerWingXCenterStat, cornerWingXCenter.getOrDefault(cornerWingXCenterStat, 0) + 1);
            cornerWingTCenter.put(cornerWingTCenterStat, cornerWingTCenter.getOrDefault(cornerWingTCenterStat, 0) + 1);
            cornerXCenterTCenter.put(cornerXCenterTCenterStat, cornerXCenterTCenter.getOrDefault(cornerXCenterTCenterStat, 0) + 1);
            edgeWingXCenter.put(edgeWingXCenterStat, edgeWingXCenter.getOrDefault(edgeWingXCenterStat, 0) + 1);
            edgeWingTCenter.put(edgeWingTCenterStat, edgeWingTCenter.getOrDefault(edgeWingTCenterStat, 0) + 1);
            edgeXCenterTCenter.put(edgeXCenterTCenterStat, edgeXCenterTCenter.getOrDefault(edgeXCenterTCenterStat, 0) + 1);
            wingXCenterTCenter.put(wingXCenterTCenterStat, wingXCenterTCenter.getOrDefault(wingXCenterTCenterStat, 0) + 1);

            cornerEdgeWingXCenter.put(cornerEdgeWingXCenterStat, cornerEdgeWingXCenter.getOrDefault(cornerEdgeWingXCenterStat, 0) + 1);
            cornerEdgeWingTCenter.put(cornerEdgeWingTCenterStat, cornerEdgeWingTCenter.getOrDefault(cornerEdgeWingTCenterStat, 0) + 1);
            cornerEdgeXCenterTCenter.put(cornerEdgeXCenterTCenterStat, cornerEdgeXCenterTCenter.getOrDefault(cornerEdgeXCenterTCenterStat, 0) + 1);
            cornerWingXCenterTCenter.put(cornerWingXCenterTCenterStat, cornerWingXCenterTCenter.getOrDefault(cornerWingXCenterTCenterStat, 0) + 1);
            edgeWingXCenterTCenter.put(edgeWingXCenterTCenterStat, edgeWingXCenterTCenter.getOrDefault(edgeWingXCenterTCenterStat, 0) + 1);

            overall.put(fiveAnalyze.getStatString(), overall.getOrDefault(fiveAnalyze.getStatString(), 0) + 1);
        }

        System.out.println();
        System.out.println("Corner");
        stringMapPrint(corner);
        System.out.println();
        System.out.println("Edge");
        stringMapPrint(edge);
        System.out.println();
        System.out.println("Wing");
        stringMapPrint(wing);
        System.out.println();
        System.out.println("XCenter");
        stringMapPrint(xCenter);
        System.out.println();
        System.out.println("TCenter");
        stringMapPrint(tCenter);
        System.out.println();
        System.out.println("Corner:Edge");
        stringMapPrint(cornerEdge);
        System.out.println();
        System.out.println("Corner:Wing");
        stringMapPrint(cornerWing);
        System.out.println();
        System.out.println("Corner:XCenter");
        stringMapPrint(cornerXCenter);
        System.out.println();
        System.out.println("Corner:TCenter");
        stringMapPrint(cornerTCenter);
        System.out.println();
        System.out.println("Edge:Wing");
        stringMapPrint(edgeWing);
        System.out.println();
        System.out.println("Edge:XCenter");
        stringMapPrint(edgeXCenter);
        System.out.println();
        System.out.println("Edge:TCenter");
        stringMapPrint(edgeTCenter);
        System.out.println();
        System.out.println("Wing:XCenter");
        stringMapPrint(wingXCenter);
        System.out.println();
        System.out.println("Wing:TCenter");
        stringMapPrint(wingTCenter);
        System.out.println();
        System.out.println("XCenter:TCenter");
        stringMapPrint(xCenterTCenter);
        System.out.println();
        System.out.println("Corner:Edge:Wing");
        stringMapPrint(cornerEdgeWing);
        System.out.println();
        System.out.println("Corner:Edge:XCenter");
        stringMapPrint(cornerEdgeXCenter);
        System.out.println();
        System.out.println("Corner:Edge:TCenter");
        stringMapPrint(cornerEdgeTCenter);
        System.out.println();
        System.out.println("Corner:Wing:XCenter");
        stringMapPrint(cornerWingXCenter);
        System.out.println();
        System.out.println("Corner:Wing:TCenter");
        stringMapPrint(cornerWingTCenter);
        System.out.println();
        System.out.println("Corner:XCenter:TCenter");
        stringMapPrint(cornerXCenterTCenter);
        System.out.println();
        System.out.println("Edge:Wing:XCenter");
        stringMapPrint(edgeWingXCenter);
        System.out.println();
        System.out.println("Edge:Wing:TCenter");
        stringMapPrint(edgeWingTCenter);
        System.out.println();
        System.out.println("Edge:XCenter:TCenter");
        stringMapPrint(edgeXCenterTCenter);
        System.out.println();
        System.out.println("Wing:XCenter:TCenter");
        stringMapPrint(wingXCenterTCenter);
        System.out.println();
        System.out.println("Corner:Edge:Wing:XCenter");
        stringMapPrint(cornerEdgeWingXCenter);
        System.out.println();
        System.out.println("Corner:Edge:Wing:TCenter");
        stringMapPrint(cornerEdgeWingTCenter);
        System.out.println();
        System.out.println("Corner:Edge:XCenter:TCenter");
        stringMapPrint(cornerEdgeXCenterTCenter);
        System.out.println();
        System.out.println("Corner:Wing:XCenter:TCenter");
        stringMapPrint(cornerWingXCenterTCenter);
        System.out.println();
        System.out.println("Edge:Wing:XCenter:TCenter");
        stringMapPrint(edgeWingXCenterTCenter);
        System.out.println();
        System.out.println("Overall");
        stringMapPrint(overall);
    }

    @Override
    public void analyzeLetterPairs(int numCubes, boolean singleLetter) {
        //TODO
    }
}
