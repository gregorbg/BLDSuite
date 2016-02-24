package com.suushiemaniac.cubing.bld.analyze.stat;

import com.suushiemaniac.cubing.bld.analyze.cube.FourBldCube;
import puzzle.NoInspectionFourByFourCubePuzzle;

import java.util.HashMap;
import java.util.Map;

public class FourMassAnalyzer extends MassAnalyzer {
    @Override
    public void analyzeProperties(int numCubes) {
        long cornerParity = 0;
        long wingParity = 0;
        long xCenterParity = 0;

        Map<Integer, Integer> cornerTargets = new HashMap<>();
        Map<Integer, Integer> wingTargets = new HashMap<>();
        Map<Integer, Integer> xCenterTargets = new HashMap<>();
        Map<Integer, Integer> cornerBreakIn = new HashMap<>();
        Map<Integer, Integer> wingBreakIn = new HashMap<>();
        Map<Integer, Integer> xCenterBreakIn = new HashMap<>();
        Map<Integer, Integer> cornerSolved = new HashMap<>();
        Map<Integer, Integer> wingSolved = new HashMap<>();
        Map<Integer, Integer> xCenterSolved = new HashMap<>();
        Map<Integer, Integer> cornerMisOrient = new HashMap<>();

        NoInspectionFourByFourCubePuzzle fourNoodle = new NoInspectionFourByFourCubePuzzle();
        FourBldCube fourAnalyze = new FourBldCube("");
        int steps = numCubes / Math.min(100, numCubes);
        for (int i = 0; i < numCubes; i++) {
            if (i % steps == 0) System.out.println("Cube " + i);
            String scramble = fourNoodle.generateScramble();
            fourAnalyze.parseScramble(scramble);

            cornerParity += fourAnalyze.hasCornerParity() ? 1 : 0;
            wingParity += fourAnalyze.hasWingParity() ? 1 : 0;
            xCenterParity += fourAnalyze.hasXCenterParity() ? 1 : 0;

            cornerTargets.put(fourAnalyze.getCornerLength(), cornerTargets.getOrDefault(fourAnalyze.getCornerLength(), 0) + 1);
            wingTargets.put(fourAnalyze.getWingLength(), wingTargets.getOrDefault(fourAnalyze.getWingLength(), 0) + 1);
            xCenterTargets.put(fourAnalyze.getXCenterLength(), xCenterTargets.getOrDefault(fourAnalyze.getXCenterLength(), 0) + 1);
            cornerBreakIn.put(fourAnalyze.getCornerBreakInNum(), cornerBreakIn.getOrDefault(fourAnalyze.getCornerBreakInNum(), 0) + 1);
            wingBreakIn.put(fourAnalyze.getWingBreakInNum(), wingBreakIn.getOrDefault(fourAnalyze.getWingBreakInNum(), 0) + 1);
            xCenterBreakIn.put(fourAnalyze.getXCenterBreakInNum(), xCenterBreakIn.getOrDefault(fourAnalyze.getXCenterBreakInNum(), 0) + 1);
            cornerSolved.put(fourAnalyze.getNumPreSolvedCorners(), cornerSolved.getOrDefault(fourAnalyze.getNumPreSolvedCorners(), 0) + 1);
            wingSolved.put(fourAnalyze.getNumPreSolvedWings(), wingSolved.getOrDefault(fourAnalyze.getNumPreSolvedWings(), 0) + 1);
            xCenterSolved.put(fourAnalyze.getNumPreSolvedXCenters(), xCenterSolved.getOrDefault(fourAnalyze.getNumPreSolvedXCenters(), 0) + 1);
            cornerMisOrient.put(fourAnalyze.getNumPreTwistedCorners(), cornerMisOrient.getOrDefault(fourAnalyze.getNumPreTwistedCorners(), 0) + 1);
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
        System.out.println("Corner targets");
        numericMapPrint(cornerTargets);
        System.out.println();
        System.out.println("Wing targets");
        numericMapPrint(wingTargets);
        System.out.println();
        System.out.println("XCenter targets");
        numericMapPrint(xCenterTargets);
        System.out.println();
        System.out.println("Corner break-ins");
        numericMapPrint(cornerBreakIn);
        System.out.println();
        System.out.println("Wing break-ins");
        numericMapPrint(wingBreakIn);
        System.out.println();
        System.out.println("XCenter break-ins");
        numericMapPrint(xCenterBreakIn);
        System.out.println();
        System.out.println("Corners solved");
        numericMapPrint(cornerSolved);
        System.out.println();
        System.out.println("Wings solved");
        numericMapPrint(wingSolved);
        System.out.println();
        System.out.println("XCenter solved");
        numericMapPrint(xCenterSolved);
        System.out.println();
        System.out.println("Corners mis-oriented");
        numericMapPrint(cornerMisOrient);
    }

    @Override
    public void analyzeScrambleDist(int numCubes) {
        Map<String, Integer> corner = new HashMap<>();
        Map<String, Integer> wing = new HashMap<>();
        Map<String, Integer> xCenter = new HashMap<>();

        Map<String, Integer> cornerWing = new HashMap<>();
        Map<String, Integer> cornerXCenter = new HashMap<>();
        Map<String, Integer> wingXCenter = new HashMap<>();

        Map<String, Integer> overall = new HashMap<>();

        NoInspectionFourByFourCubePuzzle fourNoodle = new NoInspectionFourByFourCubePuzzle();
        FourBldCube fourAnalyze = new FourBldCube("");
        int steps = numCubes / Math.min(100, numCubes);
        for (int i = 0; i < numCubes; i++) {
            if (i % steps == 0) System.out.println("Cube " + i);
            fourAnalyze.parseScramble(fourNoodle.generateScramble());

            String cornerWingStat = "C: " + fourAnalyze.getCornerStatString() + " | W: " + fourAnalyze.getWingStatString();
            String cornerXCenterStat = "C: " + fourAnalyze.getCornerStatString() + " | X: " + fourAnalyze.getXCenterStatString();
            String wingXCenterStat = "W: " + fourAnalyze.getWingStatString() + " | X: " + fourAnalyze.getXCenterStatString();

            corner.put(fourAnalyze.getCornerStatString(), corner.getOrDefault(fourAnalyze.getCornerStatString(), 0) + 1);
            wing.put(fourAnalyze.getWingStatString(), wing.getOrDefault(fourAnalyze.getWingStatString(), 0) + 1);
            xCenter.put(fourAnalyze.getXCenterStatString(), xCenter.getOrDefault(fourAnalyze.getXCenterStatString(), 0) + 1);
            cornerWing.put(cornerWingStat, cornerWing.getOrDefault(cornerWingStat, 0) + 1);
            cornerXCenter.put(cornerXCenterStat, cornerXCenter.getOrDefault(cornerXCenterStat, 0) + 1);
            wingXCenter.put(wingXCenterStat, wingXCenter.getOrDefault(wingXCenterStat, 0) + 1);
            overall.put(fourAnalyze.getStatString(), overall.getOrDefault(fourAnalyze.getStatString(), 0) + 1);
        }

        System.out.println();
        System.out.println("Corner");
        stringMapPrint(corner);
        System.out.println();
        System.out.println("Wing");
        stringMapPrint(wing);
        System.out.println();
        System.out.println("XCenter");
        stringMapPrint(xCenter);
        System.out.println();
        System.out.println("Corner:Wing");
        stringMapPrint(cornerWing);
        System.out.println();
        System.out.println("Corner:XCenter");
        stringMapPrint(cornerXCenter);
        System.out.println();
        System.out.println("Wing:XCenter");
        stringMapPrint(wingXCenter);
        System.out.println();
        System.out.println("Overall");
        stringMapPrint(overall);
    }
}