package com.suushiemaniac.cubing.bld.analyze.oldcube;

import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.ArrayList;
import java.util.HashMap;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.CENTER;
import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.CORNER;

public class SkewbBldCube extends BldCube {
    protected static final int CORNERS = 0;
    protected static final int CENTERS = 1;

    protected int[] corners = new int[24];
    protected int[] centers = new int[6];

    protected int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5, G = 6, H = 7, I = 8, J = 9, K = 10, L = 11, M = 12, N = 13, O = 14, P = 15, Q = 16, R = 17, S = 18, T = 19, U = 20, V = 21, W = 22, X = 23, Z = -1;
    protected int UP = 0, LEFT = 1, FRONT = 2, RIGHT = 3, BACK = 4, DOWN = 5;

    protected String[] cornerLettering = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X"};
    protected String[] cornerPositions = {"UBL", "URB", "UFR", "ULF", "DFL", "DRF", "DBR", "DLB"};
    protected String[] centerLettering = {"A", "B", "C", "D", "E", "F"};
    protected String[] centerPositions = {"U", "L", "F", "R", "B", "D"};

    protected Integer[][] cornerCubies = {{A, E, R}, {B, Q, N}, {C, M, J}, {D, I, F}, {L, U, G}, {P, V, K}, {T, W, O}, {H, X, S}};
    protected boolean[] solvedCorners = {true, true, true, true, true, true, true, true};
    protected boolean[] scrambledStateSolvedCorners = {false, false, false, false, false, false, false, false};
    protected int[] scrambledStateCorners = new int[24];
    protected int cornerCycleNum = 0;
    protected ArrayList<Integer> cornerCycles = new ArrayList<>();
    protected ArrayList<Integer> cwCorners = new ArrayList<>();
    protected ArrayList<Integer> ccwCorners = new ArrayList<>();

    protected Integer[][] centerCubies = {{A}, {B}, {C}, {D}, {E}, {F}};
    protected boolean[] solvedCenters = {true, true, true, true, true, true};
    protected boolean[] scrambledStateSolvedCenters = {false, false, false, false, false, false};
    protected int[] scrambledStateCenters = new int[6];
    protected int centerCycleNum = 0;
    protected ArrayList<Integer> centerCycles = new ArrayList<>();

    public SkewbBldCube(String scramble) {
        initPermutations();
        parseScramble(scramble);
    }

    @Override
    void initPermutations() {
        String[] faceNames = {
                "U", "U'",
                "L", "L'",
                "R", "R'",
                "B", "B'"
        };
        Integer[][] cornerFacePerms = {
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z}
        };
        Integer[][] centerFacePerms = {
                {B, E, Z, Z, A, Z},
                {E, A, Z, Z, B, Z},
                {Z, C, F, Z, Z, B},
                {Z, F, B, Z, Z, C},
                {Z, Z, Z, E, F, D},
                {Z, Z, Z, F, D, E},
                {Z, F, Z, Z, B, E},
                {Z, E, Z, Z, F, B}
        };
        for (int i = 0; i < faceNames.length; i++) {
            HashMap<PieceType, Integer[]> tempMap = permutations.get(faceNames[i]);
            if (tempMap == null) tempMap = new HashMap<>();
            tempMap.put(CORNER, cornerFacePerms[i]);
            tempMap.put(CENTER, centerFacePerms[i]);
            permutations.put(faceNames[i], tempMap);
        }
    }

    @Override
    void resetCube(boolean orientationOnly) {
        for (int i = 0; i < 24; i++) {
            if (!orientationOnly) {
                corners[i] = i;
                if (i < 6) centers[i] = i;
            }
            if (i < 8) solvedCorners[i] = false;
            if (i < 6) solvedCenters[i] = false;
        }
    }

    @Override
    void permute(String permutation) {
        // Corners are permuted
        int[] exchanges = {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
        Integer[] perm = permutations.get(permutation).get(CORNER);
        for (int i = 0; i < 24; i++) if (perm[i] != Z) exchanges[perm[i]] = corners[i];
        for (int i = 0; i < 24; i++) if (exchanges[i] != Z) corners[i] = exchanges[i];
        // Corners are permuted
        exchanges = new int[]{Z, Z, Z, Z, Z, Z};
        perm = permutations.get(permutation).get(CENTER);
        for (int i = 0; i < 6; i++) if (perm[i] != Z) exchanges[perm[i]] = centers[i];
        for (int i = 0; i < 6; i++) if (exchanges[i] != Z) centers[i] = exchanges[i];
    }

    @Override
    void solveCube() {
        System.arraycopy(this.corners, 0, this.scrambledStateCorners, 0, 24);
        System.arraycopy(this.centers, 0, this.scrambledStateCenters, 0, 6);
        solveCorners();
        solveCenters();
    }

    // Solves all 8 corners in the cube
    // Ignores mis-oriented corners
    protected void solveCorners() {
        if (!cornersSolved()) System.arraycopy(solvedCorners, 0, scrambledStateSolvedCorners, 0, solvedCorners.length);
        else
            scrambledStateSolvedCorners = new boolean[]{true, true, true, true, true, true, true, true};
        this.resetCube(true);
        cornerCycles.clear();
        cwCorners.clear();
        ccwCorners.clear();
        this.cornerCycleNum = 0;
        while (!cornersSolved()) cycleCornerBuffer();
    }

    // Replaces the corner buffer with another corner
    private void cycleCornerBuffer() {
        boolean cornerCycled = false;

        // If the buffer is solved, replace it with an unsolved corner
        if (solvedCorners[0]) {
            this.cornerCycleNum++;
            // First unsolved corner is selected
            for (int i = 0; i < 7 && !cornerCycled; i++) {
                if (!solvedCorners[i]) {
                    // Buffer is placed in a... um... buffer
                    int[] tempCorner = {corners[cornerCubies[0][0]], corners[cornerCubies[0][1]], corners[cornerCubies[0][2]]};

                    // Buffer corner is replaced with corner
                    corners[cornerCubies[0][0]] = corners[cornerCubies[i][0]];
                    corners[cornerCubies[0][1]] = corners[cornerCubies[i][1]];
                    corners[cornerCubies[0][2]] = corners[cornerCubies[i][2]];

                    // Corner is replaced with buffer
                    corners[cornerCubies[i][0]] = tempCorner[0];
                    corners[cornerCubies[i][1]] = tempCorner[1];
                    corners[cornerCubies[i][2]] = tempCorner[2];

                    // Corner cycle is inserted into solution array
                    cornerCycles.add(cornerCubies[i][0]);
                    cornerCycled = true;
                }
            }
        }
        // If the buffer is not solved, swap it to the position where the corner belongs
        else {
            for (int i = 0; i < 8 && !cornerCycled; i++) {
                for (int j = 0; j < 3 && !cornerCycled; j++) {
                    if (corners[cornerCubies[0][0]] == cornerCubies[i][j % 3] && corners[cornerCubies[0][1]] == cornerCubies[i][(j + 1) % 3] && corners[cornerCubies[0][2]] == cornerCubies[i][(j + 2) % 3]) {
                        // Buffer corner is replaced with corner
                        corners[cornerCubies[0][0]] = corners[cornerCubies[i][j % 3]];
                        corners[cornerCubies[0][1]] = corners[cornerCubies[i][(j + 1) % 3]];
                        corners[cornerCubies[0][2]] = corners[cornerCubies[i][(j + 2) % 3]];

                        // Corner is solved
                        corners[cornerCubies[i][0]] = cornerCubies[i][0];
                        corners[cornerCubies[i][1]] = cornerCubies[i][1];
                        corners[cornerCubies[i][2]] = cornerCubies[i][2];

                        // Corner cycle is inserted into solution array
                        cornerCycles.add(cornerCubies[i][j % 3]);
                        cornerCycled = true;
                    }
                }
            }
        }
    }

    // Checks if all 8 corners are already solved
    private boolean cornersSolved() {
        boolean cornersSolved = true;

        // Check if corners marked as unsolved haven't been solved yet
        for (int i = 0; i < 8; i++) {
            if (i == 0 || !solvedCorners[i]) {
                // Corner is solved and oriented
                if (corners[cornerCubies[i][0]] == cornerCubies[i][0] && corners[cornerCubies[i][1]] == cornerCubies[i][1] && corners[cornerCubies[i][2]] == cornerCubies[i][2])
                    solvedCorners[i] = true;
                    // Corner is in correct position but needs to be rotated clockwise
                else if (corners[cornerCubies[i][0]] == cornerCubies[i][1] && corners[cornerCubies[i][1]] == cornerCubies[i][2] && corners[cornerCubies[i][2]] == cornerCubies[i][0]) {
                    solvedCorners[i] = true;
                    if (i != 0) cwCorners.add(cornerCubies[i][0]);
                }
                // Corner is in correct position but needs to be rotated counter-clockwise
                else if (corners[cornerCubies[i][0]] == cornerCubies[i][2] && corners[cornerCubies[i][1]] == cornerCubies[i][0] && corners[cornerCubies[i][2]] == cornerCubies[i][1]) {
                    solvedCorners[i] = true;
                    if (i != 0) ccwCorners.add(cornerCubies[i][0]);
                } else {
                    // Found at least one unsolved corner
                    solvedCorners[i] = false;
                    cornersSolved = false;
                }
            }
        }
        return cornersSolved;
    }

    // Solves all 8 corners in the cube
    // Ignores mis-oriented corners
    protected void solveCenters() {
        if (!centersSolved()) System.arraycopy(solvedCenters, 0, scrambledStateSolvedCenters, 0, solvedCenters.length);
        else
            scrambledStateSolvedCenters = new boolean[]{true, true, true, true, true, true};
        this.resetCube(true);
        centerCycles.clear();
        this.centerCycleNum = 0;
        while (!centersSolved()) cycleCenterBuffer();
    }

    // Replaces the corner buffer with another corner
    private void cycleCenterBuffer() {
        boolean centerCycled = false;

        // If the buffer is solved, replace it with an unsolved corner
        if (solvedCenters[0]) {
            this.centerCycleNum++;
            // First unsolved corner is selected
            for (int i = 0; i < 5 && !centerCycled; i++) {
                if (!solvedCenters[i]) {
                    // Buffer is placed in a... um... buffer
                    int[] tempcenter = {centers[centerCubies[0][0]]};

                    // Buffer center is replaced with center
                    centers[centerCubies[0][0]] = centers[centerCubies[i][0]];

                    // center is replaced with buffer
                    centers[centerCubies[i][0]] = tempcenter[0];

                    // center cycle is inserted into solution array
                    centerCycles.add(centerCubies[i][0]);
                    centerCycled = true;
                }
            }
        }
        // If the buffer is not solved, swap it to the position where the center belongs
        else {
            for (int i = 0; i < 6 && !centerCycled; i++) {
                if (centers[centerCubies[0][0]] == centerCubies[i][0]) {
                    // Buffer center is replaced with center
                    centers[centerCubies[0][0]] = centers[centerCubies[i][0]];

                    // center is solved
                    centers[centerCubies[i][0]] = centerCubies[i][0];

                    // center cycle is inserted into solution array
                    centerCycles.add(centerCubies[i][0]);
                    centerCycled = true;
                }
            }
        }
    }

    // Checks if all 8 centers are already solved
    private boolean centersSolved() {
        boolean centersSolved = true;

        // Check if centers marked as unsolved haven't been solved yet
        for (int i = 0; i < 6; i++) {
            if (i == 0 || !solvedCenters[i]) {
                // center is solved and oriented
                if (centers[centerCubies[i][0]] == centerCubies[i][0])
                    solvedCenters[i] = true;
                    // center is in correct position but needs to be rotated clockwise
                else {
                    // Found at least one unsolved center
                    solvedCenters[i] = false;
                    centersSolved = false;
                }
            }
        }
        return centersSolved;
    }

    @Override
    String getRotationsFromOrientation(int top, int front, int[] checkArray) {
        return "";
    }

    @Override
    public String getRotations() {
        return "/";
    }

    @Override
    public String getSolutionPairs(boolean withRotation) {
        return (withRotation ? this.getRotations() + "\n" : "")
                + "Corners: " + this.getCornerPairs()
                + "\nCenters: " + this.getCenterPairs();
    }
    
    public String getCornerPairs() {
        String cornerPairs = "";
        if (cornerCycles.size() != 0 || cwCorners.size() != 0 || ccwCorners.size() != 0) {
            for (int i = 0; i < cornerCycles.size(); i++) {
                cornerPairs += cornerLettering[cornerCycles.get(i)];
                if (i % 2 == 1) cornerPairs += " ";
            }
            if (cwCorners.size() != 0) {
                cornerPairs += "\tTwist Clockwise: ";
                for (int cwCorner : cwCorners) cornerPairs += cornerLettering[cwCorner] + " ";
            }
            if (ccwCorners.size() != 0) {
                cornerPairs += "\tTwist Counterclockwise: ";
                for (int ccwCorner : ccwCorners) cornerPairs += cornerLettering[ccwCorner] + " ";
            }
        } else return "Solved";
        return cornerPairs;
    }
    
    public String getCenterPairs() {
        String centerPairs = "";
        if (centerCycles.size() != 0) {
            for (int i = 0; i < centerCycles.size(); i++) {
                centerPairs += centerLettering[centerCycles.get(i)];
                if (i % 2 == 1) centerPairs += " ";
            }
        } else return "Solved";
        return centerPairs;
    }

    @Override
    public String getStatstics() {
        return null;
    }

    @Override
    public String getScramble() {
        return this.scramble;
    }

    @Override
    public String getNoahtation() {
        return null;
    }

    @Override
    public String getStatString(boolean spaced, boolean newLine) {
        return null;
    }

    @Override
    public String getPuzzleString() {
        return "skewb";
    }
}
