package com.suushiemaniac.cubing.bld.analyze.cube;

import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public class TwoBldCube extends BldCube {
    protected int[] corners = new int[24]; // Position of all 24 corner stickers in the cube

    protected String[] colorScheme = {"0xffffff", "0x00ff00", "0xff8000", "0x0000ff", "0xff0000", "0xffff00"};

    // Speffz
    protected int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5, G = 6, H = 7, I = 8, J = 9, K = 10, L = 11, M = 12, N = 13, O = 14, P = 15, Q = 16, R = 17, S = 18, T = 19, U = 20, V = 21, W = 22, X = 23, Z = -1;
    protected int UP = 0, LEFT = 1, FRONT = 2, RIGHT = 3, BACK = 4, DOWN = 5;
    protected String[] cornerLettering = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X"};
    protected String[] cornerStickers = {"UBL", "URB", "UFR", "ULF", "LUB", "LFU", "LDF", "LBD", "FUL", "FRU", "FDR", "FLD", "RUF", "RBU", "RDB", "RFD", "BUR", "BLU", "BDL", "BRD", "DFL", "DRF", "DBR", "DLB"};
    protected String[] cornerPositions = {"UBL", "URB", "UFR", "ULF", "DFL", "DRF", "DBR", "DLB"};

    // Edge and corner cubies
    // Sticker in position [0][0] of cubie arrays represents the buffer
    protected Integer[][] cornerCubies = {{A, E, R}, {B, Q, N}, {C, M, J}, {D, I, F}, {L, U, G}, {P, V, K}, {T, W, O}, {H, X, S}};
    protected boolean[] solvedCorners = {true, true, true, true, true, true, true, true};
    protected boolean[] scrambledStateSolvedCorners = {false, false, false, false, false, false, false, false};
    protected int[] scrambledStateCorners = new int[24];
    protected int cornerCycleNum = 0;
    protected ArrayList<Integer> cornerCycles = new ArrayList<>();
    protected ArrayList<Integer> cwCorners = new ArrayList<>();
    protected ArrayList<Integer> ccwCorners = new ArrayList<>();

    protected int[] centerCubies = {UP, LEFT, FRONT, RIGHT, BACK, DOWN};
    protected int[] scrambledStateCenters = new int[6];

    private boolean optimizeByCorner = true;

    /**
     * The constructor for creating a new ThreeBldCube object
     *
     * @param scramble The scramble to be parsed. Supports full WCA notation including rotations
     */
    public TwoBldCube(String scramble) {
        this.initPermutations();
        this.parseScramble(scramble);
    }

    protected TwoBldCube() {
    }

    protected void initPermutations() {
        String[] faceNames = {
                "U", "U'", "U2",
                "F", "F'", "F2",
                "R", "R'", "R2",
                "L", "L'", "L2",
                "B", "B'", "B2",
                "D", "D'", "D2",

                "x", "x'", "x2",
                "y", "y'", "y2",
                "z", "z'", "z2"
        };
        Integer[][] cornerFacePerms = {
                {B, C, D, A, Q, R, Z, Z, E, F, Z, Z, I, J, Z, Z, M, N, Z, Z, Z, Z, Z, Z},
                {D, A, B, C, I, J, Z, Z, M, N, Z, Z, Q, R, Z, Z, E, F, Z, Z, Z, Z, Z, Z},
                {C, D, A, B, M, N, Z, Z, Q, R, Z, Z, E, F, Z, Z, I, J, Z, Z, Z, Z, Z, Z},
                {Z, Z, P, M, Z, C, D, Z, J, K, L, I, V, Z, Z, U, Z, Z, Z, Z, F, G, Z, Z},
                {Z, Z, F, G, Z, U, V, Z, L, I, J, K, D, Z, Z, C, Z, Z, Z, Z, P, M, Z, Z},
                {Z, Z, U, V, Z, P, M, Z, K, L, I, J, G, Z, Z, F, Z, Z, Z, Z, C, D, Z, Z},
                {Z, T, Q, Z, Z, Z, Z, Z, Z, B, C, Z, N, O, P, M, W, Z, Z, V, Z, J, K, Z},
                {Z, J, K, Z, Z, Z, Z, Z, Z, V, W, Z, P, M, N, O, C, Z, Z, B, Z, T, Q, Z},
                {Z, V, W, Z, Z, Z, Z, Z, Z, T, Q, Z, O, P, M, N, K, Z, Z, J, Z, B, C, Z},
                {I, Z, Z, L, F, G, H, E, U, Z, Z, X, Z, Z, Z, Z, Z, D, A, Z, S, Z, Z, R},
                {S, Z, Z, R, H, E, F, G, A, Z, Z, D, Z, Z, Z, Z, Z, X, U, Z, I, Z, Z, L},
                {U, Z, Z, X, G, H, E, F, S, Z, Z, R, Z, Z, Z, Z, Z, L, I, Z, A, Z, Z, D},
                {H, E, Z, Z, X, Z, Z, W, Z, Z, Z, Z, Z, A, B, Z, R, S, T, Q, Z, Z, N, O},
                {N, O, Z, Z, B, Z, Z, A, Z, Z, Z, Z, Z, W, X, Z, T, Q, R, S, Z, Z, H, E},
                {W, X, Z, Z, O, Z, Z, N, Z, Z, Z, Z, Z, H, E, Z, S, T, Q, R, Z, Z, A, B},
                {Z, Z, Z, Z, Z, Z, K, L, Z, Z, O, P, Z, Z, S, T, Z, Z, G, H, V, W, X, U},
                {Z, Z, Z, Z, Z, Z, S, T, Z, Z, G, H, Z, Z, K, L, Z, Z, O, P, X, U, V, W},
                {Z, Z, Z, Z, Z, Z, O, P, Z, Z, S, T, Z, Z, G, H, Z, Z, K, L, W, X, U, V},

                {S, T, Q, R, H, E, F, G, A, B, C, D, N, O, P, M, W, X, U, V, I, J, K, L},
                {I, J, K, L, F, G, H, E, U, V, W, X, P, M, N, O, C, D, A, B, S, T, Q, R},
                {U, V, W, X, G, H, E, F, S, T, Q, R, O, P, M, N, K, L, I, J, A, B, C, D},
                {B, C, D, A, Q, R, S, T, E, F, G, H, I, J, K, L, M, N, O, P, X, U, V, W},
                {D, A, B, C, I, J, K, L, M, N, O, P, Q, R, S, T, E, F, G, H, V, W, X, U},
                {C, D, A, B, M, N, O, P, Q, R, S, T, E, F, G, H, I, J, K, L, W, X, U, V},
                {N, O, P, M, B, C, D, A, J, K, L, I, V, W, X, U, T, Q, R, S, F, G, H, E},
                {H, E, F, G, X, U, V, W, L, I, J, K, D, A, B, C, R, S, T, Q, P, M, N, O},
                {W, X, U, V, O, P, M, N, K, L, I, J, G, H, E, F, S, T, Q, R, C, D, A, B}
        };
        for (int i = 0; i < faceNames.length; i++) {
            HashMap<PieceType, Integer[]> tempMap = permutations.get(faceNames[i]);
            if (tempMap == null) tempMap = new HashMap<>();
            tempMap.put(CORNER, cornerFacePerms[i]);
            permutations.put(faceNames[i], tempMap);
        }
    }

    // Sets cube to solved position
    protected void resetCube(boolean orientationOnly) {
        // Corners and edges and centers are initialized in solved position
        for (int i = 0; i < 24; i++) {
            if (!orientationOnly) corners[i] = i;
            if (i < 8) solvedCorners[i] = false;
        }
    }

    // Perform a permutation on the cube
    protected void permute(String permutation) {
        int[] exchanges = {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
        // Corners are permuted
        Integer[] perm = permutations.get(permutation).get(CORNER);
        for (int i = 0; i < 24; i++) if (perm[i] != Z) exchanges[perm[i]] = corners[i];
        for (int i = 0; i < 24; i++) if (exchanges[i] != Z) corners[i] = exchanges[i];
    }

    // Finds a BLD solution for the cube in its current state
    protected void solveCube() {
        System.arraycopy(this.corners, 0, this.scrambledStateCorners, 0, 24);
        if (optimizeByCorner) reorientCube();
        solveCorners();
    }

    protected String getRotationsFromOrientation(int top, int front, int[] checkArray) {
        String[][] reorientation = {
                {"", "y'", "", "y", "y2", ""},
                {"z y", "", "z", "", "z y2", "z y'"},
                {"x y2", "x y'", "", "x y", "", "x"},
                {"z' y'", "", "z'", "", "z' y2", "z' y"},
                {"x'", "x' y'", "", "x' y", "", "x' y2"},
                {"", "x2 y'", "z2", "x2 y", "x2", ""}
        };
        int topPosition = -1, frontPosition = -1;
        for (int i = 0; i < 6; i++) {
            if (checkArray[i] == top && topPosition == -1) topPosition = i;
            if (checkArray[i] == front && frontPosition == -1) frontPosition = i;
        }
        if (topPosition == -1 || frontPosition == -1 || topPosition == frontPosition) return "";
        else return reorientation[topPosition][frontPosition];
    }

    protected void reorientCube() {
        this.centerRotations = "";
        String[] reorientation = {
                "x2 y", "z2", "x2 y'", "x2",
                "z'", "z' y", "z' y2", "z' y'",
                "x' y", "x' y2", "x' y'", "x'",
                "z y2", "z y'", "z", "z y",
                "x y'", "x", "x y", "x y2",
                "y", "y2", "y'", ""
        };
        int xPosition = -1;
        for (int i = 0; i < 24; i++) if (this.corners[i] == X && xPosition == -1) xPosition = i;
        if (xPosition > -1) {
            String neededRotation = reorientation[xPosition];
            this.centerRotations = neededRotation;
            if (neededRotation.length() > 0) for (String rotation : neededRotation.split("\\s")) this.permute(rotation);
        }
    }

    public void setSolvingOrientation(int top, int front) {
        String neededRotation = this.getRotationsFromOrientation(top, front, this.centerCubies);
        if (neededRotation.length() > 0) {
            this.solvingOrPremoves = this.invertMoves(neededRotation);
            this.parseScramble(this.getScramble());
        }
    }

    public void setOptimizeByCorner(boolean optimize) {
        this.optimizeByCorner = optimize;
        this.parseScramble(this.getScramble());
    }

    // Solves all 8 corners in the cube
    // Ignores mis-oriented corners
    protected void solveCorners() {
        if (!cornersSolved()) System.arraycopy(solvedCorners, 0, scrambledStateSolvedCorners, 0, solvedCorners.length);
        else scrambledStateSolvedCorners = new boolean[]{true, true, true, true, true, true, true, true};
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
            int[] cornerPref = {5, 2, 4, 6, 3, 1, 7};
            for (int i = 0; i < 7 && !cornerCycled; i++) {
                int j = this.cornerCubies[0][0] == A ? cornerPref[i] : i;
                if (!solvedCorners[j]) {
                    // Buffer is placed in a... um... buffer
                    int[] tempCorner = {corners[cornerCubies[0][0]], corners[cornerCubies[0][1]], corners[cornerCubies[0][2]]};

                    // Buffer corner is replaced with corner
                    corners[cornerCubies[0][0]] = corners[cornerCubies[j][0]];
                    corners[cornerCubies[0][1]] = corners[cornerCubies[j][1]];
                    corners[cornerCubies[0][2]] = corners[cornerCubies[j][2]];

                    // Corner is replaced with buffer
                    corners[cornerCubies[j][0]] = tempCorner[0];
                    corners[cornerCubies[j][1]] = tempCorner[1];
                    corners[cornerCubies[j][2]] = tempCorner[2];

                    // Corner cycle is inserted into solution array
                    cornerCycles.add(cornerCubies[j][0]);
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

    /**
     * Gets the corner solution pairs in Speffz
     *
     * @param withTwisted Whether twisted pieces should be appended to the String
     * @return A string of the form AB CD EF containing all Speffz targets needed to solve the corners
     */
    public String getCornerPairs(boolean withTwisted) {
        return this.getCornerSolutionFromNameArray(this.cornerLettering, withTwisted, false);
    }

    /**
     * Simple alias
     * @return String like @see{getCornerPairs}
     */
    public String getCornerPairs() {
        return this.getCornerPairs(true);
    }

    /**
     * Same as getCornerPairs, but with stickers instead of Speffz lettering
     *
     * @param withTwisted Whether twisted pieces should be appended to the string
     * @return A string of the form UBL URB UFL containing all sticker targets needed to solve the corners
     */
    public String getCornerStickerCycles(boolean withTwisted) {
        return this.getCornerSolutionFromNameArray(this.cornerStickers, withTwisted, true);
    }

    /**
     * Simple alias
     * @return String like @see{getCornerStickerCycles}
     */
    public String getCornerStickerCycles() {
        return this.getCornerStickerCycles(true);
    }

    protected String getCornerSolutionFromNameArray(String[] names, boolean withTwisted, boolean forceGap) {
        String cornerPairs = "";
        if (cornerCycles.size() != 0 || cwCorners.size() != 0 || ccwCorners.size() != 0) {
            for (int i = 0; i < cornerCycles.size(); i++) {
                cornerPairs += names[cornerCycles.get(i)];
                if (forceGap || i % 2 == 1) cornerPairs += " ";
            }
            if (withTwisted) {
                if (cwCorners.size() != 0) {
                    cornerPairs += "\tTwist Clockwise: ";
                    for (int cwCorner : cwCorners) cornerPairs += names[cwCorner] + " ";
                }
                if (ccwCorners.size() != 0) {
                    cornerPairs += "\tTwist Counterclockwise: ";
                    for (int ccwCorner : ccwCorners) cornerPairs += names[ccwCorner] + " ";
                }
            }
        } else return "Solved";
        return cornerPairs.trim();
    }

    protected String getTwistedCornerSingleTargetStickerCycles(boolean isCw) {
        if ((isCw ? this.getNumPreCWCorners() : this.getNumPreCCWCorners()) < 1) return "";
        String twistedCorners = isCw ? this.getPreCWCorners() : this.getPreCCWCorners();
        String opSolution = "";

        for (String twistedCorner : twistedCorners.split("\\s+?")) {
            opSolution += twistedCorner + " ";
            Character[] flippedEdgeChars = autoboxArray(twistedCorner.toCharArray());
            if (isCw) this.cycleArrayRight(flippedEdgeChars);
            else this.cycleArrayLeft(flippedEdgeChars);
            opSolution += new String(autoboxArray(flippedEdgeChars)) + " ";
        }

        return opSolution.trim();
    }

    protected String getTwistedCornerSingleTargetPairs(boolean isCw) {
        if ((isCw ? this.getNumPreCWCorners() : this.getNumPreCCWCorners()) < 1) return "";
        String flippedEdgeOpSpeffz = this.getTwistedCornerSingleTargetStickerCycles(isCw);
        String opStickerSolution = "";

        String[] split = flippedEdgeOpSpeffz.split("\\s+?");
        for (int i = 0; i < split.length; i++) {
            String target = split[i];
            opStickerSolution += this.cornerLettering[this.arrayIndex(this.cornerStickers, target)];
            if (i % 2 == 1) opStickerSolution += " ";
        }

        return opStickerSolution.trim();
    }

    /**
     * Get's a solution for CW corners in single target OP fashion
     *
     * @return String with Speffz targets
     */
    public String getCwCornerSingleTargetPairs() {
        return this.getTwistedCornerSingleTargetPairs(true);
    }

    /**
     * Get's a solution for CCW corners in single target OP fashion
     *
     * @return String with Speffz targets
     */
    public String getCcwCornerSingleTargetPairs() {
        return this.getTwistedCornerSingleTargetPairs(false);
    }

    /**
     * Get's a solution for CW corners in single target OP fashion
     *
     * @return String with sticker targets
     */
    public String getCwCornerSingleTargetStickerCycles() {
        return this.getTwistedCornerSingleTargetStickerCycles(true);
    }

    /**
     * Get's a solution for CCW corners in single target OP fashion
     *
     * @return String with sticker targets
     */
    public String getCcwCornerSingleTargetStickerCycles() {
        return this.getTwistedCornerSingleTargetStickerCycles(false);
    }

    /**
     * Get's a solution for all twisted corners in single target OP fashion
     *
     * @return String with Speffz targets
     */
    public String getTwistedCornerSingleTargetPairs() {
        String cw = this.getCwCornerSingleTargetPairs();
        String ccw = this.getCcwCornerSingleTargetPairs();
        return cw + (cw.length() > 0 ? " " : "") + ccw;
    }

    /**
     * Get's a solution for all twisted corners in single target OP fashion
     *
     * @return String with Speffz targets
     */
    public String getTwistedCornerSingleTargetStickerCycles() {
        String cw = this.getCwCornerSingleTargetStickerCycles();
        String ccw = this.getCcwCornerSingleTargetStickerCycles();
        return cw + (cw.length() > 0 ? " " : "") + ccw;
    }

    /**
     * Gets the cube solution pairs in Speffz
     *
     * @param withRotation Whether to include the pre-rotations needed to reorient the cube in the final scramble
     * @return A string with multiple lines, with each line equal to one of the solution pair getters
     */
    @Override
    public String getSolutionPairs(boolean withRotation) {
        return (withRotation ? this.getRotations() + "\n" : "")
                + "Corners: " + this.getCornerPairs();
    }

    /**
     * Gets all available statistics in one String
     *
     * @return mutiple lines, with each one equal to one piece type in the form "targets@break-ins w/ solved-misoriented > parity"
     */
    @Override
    public String getStatstics() {
        return "Corners: " + this.getCornerLength() + "@" + this.getCornerBreakInNum() + " w/ " + this.getNumPreSolvedCorners() + "-" + this.getNumPreTwistedCorners() + " > " + this.hasCornerParity();
    }

    /**
     * Gets the complete Noahtation for the cube in it's current state
     *
     * @return String with Noahtation for all individual piece types
     */
    @Override
    public String getNoahtation() {
        return "C: " + this.getCornerNoahtation();
    }

    public String getStatString(boolean spaced, boolean newLine) {
        return "C: " + this.getCornerStatString(spaced);
    }

    public String getCornerStatString(boolean spaced) {
        String cornerStat = this.hasCornerParity() ? "_" : " ";
        cornerStat += this.getCornerLength();
        cornerStat += this.isCornerBufferSolved() ? "*" : " ";
        cornerStat += spaced ? "\t" : " ";
        for (int i = 0; i < 3; i++) cornerStat += i < this.getCornerBreakInNum() ? "#" : spaced ? " " : "";
        if (spaced || cornerStat.endsWith("#")) cornerStat += " ";
        for (int i = 0; i < 7; i++) cornerStat += i < this.getNumPreTwistedCorners() ? "~" : spaced ? " " : "";
        if (spaced || cornerStat.endsWith("~")) cornerStat += " ";
        for (int i = 0; i < 7; i++) cornerStat += i < this.getNumPreSolvedCorners() ? "+" : spaced ? " " : "";
        return cornerStat;
    }

    public String getCornerStatString() {
        return this.getCornerStatString(false);
    }

    public String getRotations() {
        return this.centerRotations.length() > 0 ? this.centerRotations : "/";
    }

    public boolean hasCornerParity() {
        return this.cornerCycles.size() % 2 == 1;
    }

    public boolean isCornerBufferSolved() {
        return this.scrambledStateSolvedCorners[0];
    }

    public boolean isSingleCycle() {
        return this.isCornerSingleCycle();
    }

    public String getScramble() {
        return this.scramble;
    }

    public int getCornerLength() {
        return this.cornerCycles.size();
    }

    public int getCornerBreakInNum() {
        return this.cornerCycleNum;
    }

    public boolean isCornerSingleCycle() {
        return this.cornerCycleNum == 0;
    }

    public int getNumPreSolvedCorners() {
        return this.getNumPreCorners(false, this.getFullTwistedCorners());
    }

    public String getPreSolvedCorners() {
        return this.getPreCorners(false, this.getFullTwistedCorners());
    }

    public int getNumPreTwistedCorners() {
        return this.getNumPreCorners(true, this.getFullTwistedCorners());
    }

    public String getPreTwistedCorners() {
        return this.getPreCorners(true, this.getFullTwistedCorners());
    }

    protected List<Integer> getFullTwistedCorners() {
        List<Integer> full = new ArrayList<>();
        full.addAll(this.cwCorners);
        full.addAll(this.ccwCorners);
        return full;
    }

    public int getNumPreCWCorners() {
        return this.getNumPreCorners(true, this.cwCorners);
    }

    public String getPreCWCorners() {
        return this.getPreCorners(true, this.cwCorners);
    }

    public int getNumPreCCWCorners() {
        return this.getNumPreCorners(true, this.ccwCorners);
    }

    public String getPreCCWCorners() {
        return this.getPreCorners(true, this.ccwCorners);
    }

    public int getNumPrePermutedCorners() {
        return this.getNumPreSolvedCorners() + this.getNumPreTwistedCorners();
    }

    public String getPrePermutedCorners() {
        return this.getPreSolvedCorners() + this.getPreTwistedCorners();
    }

    protected int getNumPreCorners(boolean twisted, List<Integer> searchList) {
        int preSolved = 0;
        for (int i = 1; i < scrambledStateSolvedCorners.length; i++)
            if (scrambledStateSolvedCorners[i] && twisted == searchList.contains(cornerCubies[i][0])) preSolved++;
        return preSolved;
    }

    protected String getPreCorners(boolean twisted, List<Integer> searchList) {
        String solvedCorners = "";
        for (int i = 1; i < scrambledStateSolvedCorners.length; i++)
            if (scrambledStateSolvedCorners[i] && twisted == searchList.contains(cornerCubies[i][0]))
                solvedCorners += (solvedCorners.length() > 0 ? " " : "") + cornerPositions[i];
        return solvedCorners;
    }

    public String getCornerNoahtation() {
        String cornerLength = this.getCornerLength() + "";
        for (int ignored : this.cwCorners) cornerLength += "'";
        for (int ignored : this.ccwCorners) cornerLength += "'";
        return cornerLength;
    }

    public void setCornerScheme(String scheme) {
        this.setCornerScheme(scheme.split(""));
    }

    public void setCornerScheme(String[] scheme) {
        if (scheme.length == 24) this.cornerLettering = scheme;
    }

    public void setCornerBuffer(String bufferAsLetter) {
        if (arrayContains(this.cornerLettering, bufferAsLetter)) {
            int speffz = arrayIndex(this.cornerLettering, bufferAsLetter);
            int outer = deepArrayOuterIndex(this.cornerCubies, speffz), inner = deepArrayInnerIndex(this.cornerCubies, speffz);
            for (int i = 0; i < outer; i++) cycleArrayLeft(this.cornerCubies);
            for (int i = 0; i < inner; i++) cycleArrayLeft(this.cornerCubies[0]);
            this.parseScramble(this.getScramble());
        }
    }

    public void setColorScheme(String[] scheme) {
        if (scheme.length == 6) this.colorScheme = scheme;
    }

    public void setScheme(String type, String[] scheme) {
        switch (type.toLowerCase()) {
            case "corner":
                this.setCornerScheme(scheme);
                break;
            case "color":
                this.setColorScheme(scheme);
        }
    }

    public String[] getScheme(String type) {
        switch (type.toLowerCase()) {
            case "corner":
                return this.cornerLettering;
            case "color":
                return this.colorScheme;
            default:
                return null;
        }
    }
}
