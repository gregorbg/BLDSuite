package com.suushiemaniac.cubing.bld.analyze.cube;

import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.suushiemaniac.cubing.bld.model.enumeration.TetrahedronPieceType.*;

public class PyraminxBldCube extends BldCube {
    protected int[] tips = new int[12];
    protected int[] centers = new int[12];
    protected int[] edges = new int[12];

    protected int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5, G = 6, H = 7, I = 8, J = 9, K = 10, L = 11, Z = -1;
    protected int LEFT = 0, FRONT = 1, RIGHT = 2, DOWN = 3;
    protected String[] tipLettering = {"U", "L", "B", "R"};
    protected String[] tipPositions = {"U", "L", "B", "R"};
    protected String[] centerLettering = {"U", "L", "B", "R"};
    protected String[] centerPositions = {"U", "L", "B", "R"};
    protected String[] edgeLettering = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"};
    protected String[] edgePositions = {"LR", "LF", "LD", "FL", "FR", "FD", "RF", "RL", "RD", "DF", "DR", "DL"};

    protected Integer[][] tipCubies = {{A, G, D}, {B, F, J}, {C, L, H}, {E, I, K}};
    protected boolean[] solvedTips = {true, true, true, true};
    protected boolean[] scrambledStateSolvedTips = {false, false, false, false};
    protected int[] scrambledStateTips = new int[12];
    protected ArrayList<Integer> cwTips = new ArrayList<>();
    protected ArrayList<Integer> ccwTips = new ArrayList<>();

    protected Integer[][] centerCubies = {{A, G, D}, {B, F, J}, {C, L, H}, {E, I, K}};
    protected boolean[] solvedCenters = {true, true, true, true};
    protected boolean[] scrambledStateSolvedCenters = {false, false, false, false};
    protected int[] scrambledStateCenters = new int[12];
    protected ArrayList<Integer> cwCenters = new ArrayList<>();
    protected ArrayList<Integer> ccwCenters = new ArrayList<>();

    protected Integer[][] edgeCubies = {{J, F}, {A, H}, {B, D}, {C, L}, {E, G}, {I, K}};
    protected boolean[] solvedEdges = {true, true, true, true, true, true};
    protected boolean[] scrambledStateSolvedEdges = {false, false, false, false, false, false};
    protected int[] scrambledStateEdges = new int[12];
    protected int edgeCycleNum = 0;
    protected ArrayList<Integer> edgeCycles = new ArrayList<>();
    protected ArrayList<Integer> flippedEdges = new ArrayList<>();

    protected int[] fixedCenterCubies = {LEFT, FRONT, RIGHT, DOWN};

    public PyraminxBldCube(String scramble) {
        initPermutations();
        parseScramble(scramble);
    }

    protected void initPermutations() {
        String[] faceNames = {
                "U", "U'",
                "R", "R'",
                "L", "L'",
                "B", "B'",

                "u", "u'",
                "r", "r'",
                "l", "l'",
                "b", "b'",

                "rx", "rx'",
                "lx", "lx'",
                "y", "y'",
                "z", "z'"
        };
        Integer[][] tipFacePerms = {
                {G, Z, Z, A, Z, Z, D, Z, Z, Z, Z, Z},
                {D, Z, Z, G, Z, Z, A, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, I, Z, Z, Z, K, Z, E, Z},
                {Z, Z, Z, Z, K, Z, Z, Z, E, Z, I, Z},
                {Z, F, Z, Z, Z, J, Z, Z, Z, B, Z, Z},
                {Z, J, Z, Z, Z, B, Z, Z, Z, F, Z, Z},
                {Z, Z, L, Z, Z, Z, Z, C, Z, Z, Z, H},
                {Z, Z, H, Z, Z, Z, Z, L, Z, Z, Z, C},

                {G, Z, Z, A, Z, Z, D, Z, Z, Z, Z, Z},
                {D, Z, Z, G, Z, Z, A, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, I, Z, Z, Z, K, Z, E, Z},
                {Z, Z, Z, Z, K, Z, Z, Z, E, Z, I, Z},
                {Z, F, Z, Z, Z, J, Z, Z, Z, B, Z, Z},
                {Z, J, Z, Z, Z, B, Z, Z, Z, F, Z, Z},
                {Z, Z, L, Z, Z, Z, Z, C, Z, Z, Z, H},
                {Z, Z, H, Z, Z, Z, Z, L, Z, Z, Z, C},

                {L, J, K, C, A, B, H, I, G, F, D, E},
                {E, F, D, K, L, J, I, G, H, B, C, A},
                {B, C, A, J, K, L, F, D, E, H, I, G},
                {C, A, B, H, I, G, L, J, K, D, E, F},
                {G, H, I, A, B, C, D, E, F, L, J, K},
                {D, E, F, G, H, I, A, B, C, K, L, J},
                {I, G, H, E, F, D, K, L, J, A, B, C},
                {J, K, L, F, D, E, B, C, A, I, G, H}
        };
        Integer[][] centerFacePerms = {
                {G, Z, Z, A, Z, Z, D, Z, Z, Z, Z, Z},
                {D, Z, Z, G, Z, Z, A, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, I, Z, Z, Z, K, Z, E, Z},
                {Z, Z, Z, Z, K, Z, Z, Z, E, Z, I, Z},
                {Z, F, Z, Z, Z, J, Z, Z, Z, B, Z, Z},
                {Z, J, Z, Z, Z, B, Z, Z, Z, F, Z, Z},
                {Z, Z, L, Z, Z, Z, Z, C, Z, Z, Z, H},
                {Z, Z, H, Z, Z, Z, Z, L, Z, Z, Z, C},

                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},

                {L, J, K, C, A, B, H, I, G, F, D, E},
                {E, F, D, K, L, J, I, G, H, B, C, A},
                {B, C, A, J, K, L, F, D, E, H, I, G},
                {C, A, B, H, I, G, L, J, K, D, E, F},
                {G, H, I, A, B, C, D, E, F, L, J, K},
                {D, E, F, G, H, I, A, B, C, K, L, J},
                {I, G, H, E, F, D, K, L, J, A, B, C},
                {J, K, L, F, D, E, B, C, A, I, G, H}
        };
        Integer[][] edgeFacePerms = {
                {G, H, Z, A, B, Z, D, E, Z, Z, Z, Z},
                {D, E, Z, G, H, Z, A, B, Z, Z, Z, Z},
                {Z, Z, Z, Z, I, G, K, Z, J, E, F, Z},
                {Z, Z, Z, Z, J, K, F, Z, E, I, G, Z},
                {Z, F, D, J, Z, L, Z, Z, Z, C, Z, B},
                {Z, L, J, C, Z, B, Z, Z, Z, D, Z, F},
                {L, Z, K, Z, Z, Z, Z, C, A, Z, H, I},
                {I, Z, H, Z, Z, Z, Z, K, L, Z, C, A},

                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},

                {K, L, J, C, A, B, H, I, G, D, E, F},
                {E, F, D, J, K, L, I, G, H, C, A, B},
                {B, C, A, L, J, K, F, D, E, I, G, H},
                {C, A, B, H, I, G, K, L, J, E, F, D},
                {G, H, I, A, B, C, D, E, F, L, J, K},
                {D, E, F, G, H, I, A, B, C, K, L, J},
                {I, G, H, E, F, D, J, K, L, B, C, A},
                {L, J, K, F, D, E, B, C, A, G, H, I}
        };
        for (int i = 0; i < faceNames.length; i++) {
            HashMap<PieceType, Integer[]> tempMap = permutations.get(faceNames[i]);
            if (tempMap == null) tempMap = new HashMap<>();
            tempMap.put(TIP, tipFacePerms[i]);
            tempMap.put(CENTER, centerFacePerms[i]);
            tempMap.put(EDGE, edgeFacePerms[i]);
            permutations.put(faceNames[i], tempMap);
        }
    }

    // Sets cube to solved position
    protected void resetCube(boolean orientationOnly) {
        // Corners and edges and centers are initialized in solved position
        for (int i = 0; i < 12; i++) {
            if (!orientationOnly) {
                tips[i] = i;
                centers[i] = i;
                edges[i] = i;
            }
            if (i < 4) {
                solvedTips[i] = false;
                solvedCenters[i] = false;
            }
            if (i < 6) solvedEdges[i] = false;
        }
    }

    // Perform a permutation on the cube
    protected void permute(String permutation) {
        // Corners are permuted
        int[] exchanges = {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
        Integer[] perm = permutations.get(permutation).get(TIP);
        for (int i = 0; i < 12; i++) if (perm[i] != Z) exchanges[perm[i]] = tips[i];
        for (int i = 0; i < 12; i++) if (exchanges[i] != Z) tips[i] = exchanges[i];
        // Corners are permuted
        exchanges = new int[]{Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
        perm = permutations.get(permutation).get(CENTER);
        for (int i = 0; i < 12; i++) if (perm[i] != Z) exchanges[perm[i]] = centers[i];
        for (int i = 0; i < 12; i++) if (exchanges[i] != Z) centers[i] = exchanges[i];
        // Corners are permuted
        exchanges = new int[]{Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
        perm = permutations.get(permutation).get(EDGE);
        for (int i = 0; i < 12; i++) if (perm[i] != Z) exchanges[perm[i]] = edges[i];
        for (int i = 0; i < 12; i++) if (exchanges[i] != Z) edges[i] = exchanges[i];
    }

    // Finds a BLD solution for the cube in its current state
    protected void solveCube() {
        System.arraycopy(this.tips, 0, this.scrambledStateTips, 0, 12);
        System.arraycopy(this.centers, 0, this.scrambledStateCenters, 0, 12);
        System.arraycopy(this.edges, 0, this.scrambledStateEdges, 0, 12);
        reorientCube();
        solveCenters();
        solveTips();
        solveEdges();
    }

    protected String getRotationsFromOrientation(int bottom, int front, int[] checkArray) {
        String[][] reorientation = {
                {"", "z'", "z' y'", "z' y"},
                {"rx'", "", "lx", "lx' z"},
                {"z y", "z", "", "z y'"},
                {"y'", "", "y", ""},
        };
        int bottomPosition = -1, frontPosition = -1;
        for (int i = 0; i < 4; i++) {
            if (checkArray[i] == bottom && bottomPosition == -1) bottomPosition = i;
            if (checkArray[i] == front && frontPosition == -1) frontPosition = i;
        }
        if (bottomPosition == -1 || frontPosition == -1 || bottomPosition == frontPosition) return "";
        else return reorientation[bottomPosition][frontPosition];
    }

    protected void reorientCube() {
        this.centerRotations = "";
        String[][] reorientation = {
                {"", "lx'", "z y'", "rx"},
                {"y", "", "lx", "lx z'"},
                {"", "z", "", "z'"},
                {"y'", "rx'", "rx'", ""},
        };
        int lPosition = -1, aPosition = -1;
        for (int i = 0; i < 12; i++) {
            if (arrayContains(this.centerCubies[2], this.centers[i]) && lPosition == -1)
                lPosition = deepArrayOuterIndex(this.centerCubies, i);
            if (arrayContains(this.centerCubies[0], this.centers[i]) && aPosition == -1)
                aPosition = deepArrayOuterIndex(this.centerCubies, i);
        }
        if (lPosition > -1 && aPosition > -1 && lPosition != aPosition) {
            String neededRotation = reorientation[lPosition][aPosition];
            this.centerRotations = neededRotation;
            if (neededRotation.length() > 0) for (String rotation : neededRotation.split("\\s")) this.permute(rotation);
        }
    }

    public void setSolvingOrientation(int bottom, int front) {
        String neededRotation = this.getRotationsFromOrientation(bottom, front, this.fixedCenterCubies);
        if (neededRotation.length() > 0) {
            this.solvingOrPremoves = this.invertMoves(neededRotation);
            this.parseScramble(this.getScramble());
        }
    }

    // Solves all 12 edges in the cube
    protected void solveEdges() {
        if (!edgesSolved()) System.arraycopy(solvedEdges, 0, scrambledStateSolvedEdges, 0, solvedEdges.length);
        else
            this.scrambledStateSolvedEdges = new boolean[]{true, true, true, true, true, true};
        this.resetCube(true);
        edgeCycles.clear();
        flippedEdges.clear();
        this.edgeCycleNum = 0;
        while (!edgesSolved()) cycleEdgeBuffer();
    }

    // Replaces the edge buffer with another edge
    private void cycleEdgeBuffer() {
        boolean edgeCycled = false;

        // If the buffer is solved, replace it with an unsolved edge
        if (solvedEdges[0]) {
            this.edgeCycleNum++;
            // First unsolved edge is selected
            int[] edgePref = {2, 4, 1, 3, 5};
            for (int i = 0; i < 5 && !edgeCycled; i++) {
                int j = this.edgeCubies[0][0] == J ? edgePref[i] : i;
                if (!solvedEdges[j]) {
                    // Buffer is placed in a... um... buffer
                    int[] tempEdge = {edges[edgeCubies[0][0]], edges[edgeCubies[0][1]]};

                    // Buffer edge is replaced with edge
                    edges[edgeCubies[0][0]] = edges[edgeCubies[j][0]];
                    edges[edgeCubies[0][1]] = edges[edgeCubies[j][1]];

                    // Edge is replaced with buffer
                    edges[edgeCubies[j][0]] = tempEdge[0];
                    edges[edgeCubies[j][1]] = tempEdge[1];

                    // Edge cycle is inserted into solution array
                    edgeCycles.add(edgeCubies[j][0]);
                    edgeCycled = true;
                }
            }
        }
        // If the buffer is not solved, swap it to the position where the edge belongs
        else {
            for (int i = 0; i < 6 && !edgeCycled; i++) {
                for (int j = 0; j < 2 && !edgeCycled; j++) {
                    if (edges[edgeCubies[0][0]] == edgeCubies[i][j % 2] && edges[edgeCubies[0][1]] == edgeCubies[i][(j + 1) % 2]) {
                        // Buffer edge is replaced with edge
                        edges[edgeCubies[0][0]] = edges[edgeCubies[i][j % 2]];
                        edges[edgeCubies[0][1]] = edges[edgeCubies[i][(j + 1) % 2]];

                        // Edge is solved
                        edges[edgeCubies[i][0]] = edgeCubies[i][0];
                        edges[edgeCubies[i][1]] = edgeCubies[i][1];

                        // Edge cycle is inserted into solution array
                        edgeCycles.add(edgeCubies[i][j % 2]);
                        edgeCycled = true;
                    }
                }
            }
        }
    }

    // Checks if all 12 edges are already solved
    // Ignores orientation
    private boolean edgesSolved() {
        boolean edgesSolved = true;

        // Check if corners marked as unsolved haven't been solved yet
        for (int i = 0; i < 6; i++) {
            if (i == 0 || !solvedEdges[i]) {
                // Edge is solved in correct orientation
                if (edges[edgeCubies[i][0]] == edgeCubies[i][0] && edges[edgeCubies[i][1]] == edgeCubies[i][1]) {
                    solvedEdges[i] = true;
                }
                // Edge is solved but mis-oriented
                else if (edges[edgeCubies[i][0]] == edgeCubies[i][1] && edges[edgeCubies[i][1]] == edgeCubies[i][0]) {
                    solvedEdges[i] = true;
                    if (i != 0) flippedEdges.add(edgeCubies[i][0]);
                } else {
                    // Found at least one unsolved edge
                    solvedEdges[i] = false;
                    edgesSolved = false;
                }
            }
        }
        return edgesSolved;
    }

    // Solves all 8 centers in the cube
    // Ignores mis-oriented centers
    protected void solveCenters() {
        this.resetCube(true);
        cwCenters.clear();
        ccwCenters.clear();
        if (!centersSolved()) System.arraycopy(solvedCenters, 0, scrambledStateSolvedCenters, 0, solvedCenters.length);
        else scrambledStateSolvedCenters = new boolean[]{true, true, true, true};
    }

    // Checks if all 8 centers are already solved
    private boolean centersSolved() {
        boolean centersSolved = true;

        // Check if centers marked as unsolved haven't been solved yet
        for (int i = 0; i < 4; i++) {
            if (!solvedCenters[i]) {
                if (centers[centerCubies[i][0]] == centerCubies[i][1] && centers[centerCubies[i][1]] == centerCubies[i][2] && centers[centerCubies[i][2]] == centerCubies[i][0]) {
                    solvedCenters[i] = true;
                    cwCenters.add(i);
                }
                // Center is in correct position but needs to be rotated counter-clockwise
                else if (centers[centerCubies[i][0]] == centerCubies[i][2] && centers[centerCubies[i][1]] == centerCubies[i][0] && centers[centerCubies[i][2]] == centerCubies[i][1]) {
                    solvedCenters[i] = true;
                    ccwCenters.add(i);
                } else {
                    // Found at least one unsolved center
                    solvedCenters[i] = false;
                    centersSolved = false;
                }
            }
        }
        return centersSolved;
    }

    // Solves all 8 centers in the cube
    // Ignores mis-oriented centers
    protected void solveTips() {
        this.resetCube(true);
        cwTips.clear();
        ccwTips.clear();
        if (!tipsSolved()) System.arraycopy(solvedTips, 0, scrambledStateSolvedTips, 0, solvedTips.length);
        else scrambledStateSolvedTips = new boolean[]{true, true, true, true};
    }

    // Checks if all 8 tips are already solved
    private boolean tipsSolved() {
        boolean tipsSolved = true;

        // Check if tips marked as unsolved haven't been solved yet
        for (int i = 0; i < 4; i++) {
            if (!solvedTips[i]) {
                // Tip solved
                if (tips[tipCubies[i][0]] == tipCubies[i][0] && tips[tipCubies[i][1]] == tipCubies[i][1] && tips[tipCubies[i][2]] == tipCubies[i][2]) {
                    solvedTips[i] = true;
                    if (ccwCenters.contains(i)) cwTips.add(i);
                    else if (cwCenters.contains(i)) ccwTips.add(i);
                }
                // Tip needs to be rotated clockwise
                if (tips[tipCubies[i][0]] == tipCubies[i][1] && tips[tipCubies[i][1]] == tipCubies[i][2] && tips[tipCubies[i][2]] == tipCubies[i][0]) {
                    solvedTips[i] = true;
                    if (ccwCenters.contains(i)) ccwTips.add(i);
                    else if (!cwCenters.contains(i)) cwTips.add(i);
                }
                // Tip needs to be rotated counter-clockwise
                else if (tips[tipCubies[i][0]] == tipCubies[i][2] && tips[tipCubies[i][1]] == tipCubies[i][0] && tips[tipCubies[i][2]] == tipCubies[i][1]) {
                    solvedTips[i] = true;
                    if (cwCenters.contains(i)) cwTips.add(i);
                    else if (!ccwCenters.contains(i)) ccwTips.add(i);
                } else {
                    // Found at least one unsolved tip
                    solvedTips[i] = false;
                    tipsSolved = false;
                }
            }
        }
        return tipsSolved;
    }

    @Override
    public String getRotations() {
        return this.centerRotations.length() > 0 ? this.centerRotations : "/";
    }

    @Override
    public String getSolutionPairs(boolean withRotation) {
        return (withRotation ? this.getRotations() + "\n" : "")
                + "Edges: " + this.getEdgePairs()
                + "\nCenters: " + this.getCenterPairs()
                + "\nTips: " + this.getTipPairs();
    }

    public String getEdgePairs() {
        String edgePairs = "";
        if (edgeCycles.size() != 0 || flippedEdges.size() != 0) {
            for (int i = 0; i < edgeCycles.size(); i++) {
                edgePairs += edgeLettering[edgeCycles.get(i)];
                if (i % 2 == 1) edgePairs += " ";
            }
            if (flippedEdges.size() != 0) {
                edgePairs += "\tFlip: ";
                for (Integer flippedEdge : flippedEdges) edgePairs += edgeLettering[flippedEdge] + " ";
            }
        }
        return edgePairs;
    }

    public String getCenterPairs() {
        String centerPairs = "";
        if (cwCenters.size() != 0) for (int cwCenter : cwCenters) centerPairs += centerLettering[cwCenter] + "++ ";
        if (ccwCenters.size() != 0) for (int ccwCenter : ccwCenters) centerPairs += centerLettering[ccwCenter] + "-- ";
        return centerPairs.trim();
    }

    public String getTipPairs() {
        String tipPairs = "";
        if (cwTips.size() != 0) for (int cwTip : cwTips) tipPairs += tipLettering[cwTip] + "++ ";
        if (ccwTips.size() != 0) for (int ccwTip : ccwTips) tipPairs += tipLettering[ccwTip] + "-- ";
        return tipPairs.trim();
    }

    @Override
    public String getStatstics() {
        return "Edges: " + this.getEdgeLength() + "@" + this.getEdgeBreakInNum() + " w/ " + this.getNumPreSolvedEdges() + "-" + this.getNumPreFlippedEdges()
                + "\nCenters: " + this.getCenterLength() + " w/ " + this.getNumPreSolvedCenters()
                + "\nTips: " + this.getTipLength() + " w/ " + this.getNumPreSolvedTips();
    }

    @Override
    public String getNoahtation() {
        return "E:" + this.getEdgeNoahtation() + " / Ce:" + this.getCenterNoahtation() + " / Tp:" + this.getTipNoahtation();
    }

    public String getStatString(boolean spaced, boolean newLine) {
        return "E: " + this.getEdgeStatString(spaced) + (newLine ? "\n" : " | ") + "Ce: " + this.getCenterStatString(spaced) + (newLine ? "\n" : " | ") + "Tp: " + this.getTipStatString(spaced);
    }

    @Override
    public String getPuzzleString() {
        return "pyram";
    }

    public String getEdgeStatString(boolean spaced) {
        String edgeStat = "" + this.getEdgeLength();
        edgeStat += this.isEdgeBufferSolved() ? "*" : " ";
        edgeStat += spaced ? "\t" : " ";
        for (int i = 0; i < 2; i++) edgeStat += i < this.getEdgeBreakInNum() ? "#" : spaced ? " " : "";
        if (spaced || edgeStat.endsWith("#")) edgeStat += " ";
        for (int i = 0; i < 5; i++) edgeStat += i < this.getNumPreFlippedEdges() ? "~" : spaced ? " " : "";
        if (spaced || edgeStat.endsWith("~")) edgeStat += " ";
        for (int i = 0; i < 5; i++) edgeStat += i < this.getNumPreSolvedEdges() ? "+" : spaced ? " " : "";
        return edgeStat;
    }

    public String getEdgeStatString() {
        return this.getEdgeStatString(false);
    }

    public String getCenterStatString(boolean spaced) {
        String centerStat = "" + this.getCenterLength();
        centerStat += spaced ? "\t" : " ";
        for (int i = 0; i < 4; i++) centerStat += i < this.getNumPreFlippedCenters() ? "~" : spaced ? " " : "";
        if (spaced || centerStat.endsWith("~")) centerStat += " ";
        for (int i = 0; i < 4; i++) centerStat += i < this.getNumPreSolvedCenters() ? "+" : spaced ? " " : "";
        return centerStat;
    }

    public String getCenterStatString() {
        return this.getCenterStatString(false);
    }

    public String getTipStatString(boolean spaced) {
        String tipStat = "" + this.getTipLength();
        tipStat += spaced ? "\t" : " ";
        for (int i = 0; i < 4; i++) tipStat += i < this.getNumPreFlippedTips() ? "~" : spaced ? " " : "";
        if (spaced || tipStat.endsWith("~")) tipStat += " ";
        for (int i = 0; i < 4; i++) tipStat += i < this.getNumPreSolvedTips() ? "+" : spaced ? " " : "";
        return tipStat;
    }

    public String getTipStatString() {
        return this.getTipStatString(false);
    }

    @Override
    public String getScramble() {
        return this.scramble;
    }

    public boolean isEdgeBufferSolved() {
        return this.scrambledStateSolvedEdges[0];
    }

    public int getEdgeLength() {
        return this.edgeCycles.size();
    }

    public int getEdgeBreakInNum() {
        return this.edgeCycleNum;
    }

    public boolean isEdgeSingleCycle() {
        return this.edgeCycleNum == 0;
    }

    public int getNumPreSolvedEdges() {
        return this.getNumPreEdges(false, this.flippedEdges);
    }

    public String getPreSolvedEdges() {
        return this.getPreEdges(false, this.flippedEdges);
    }

    public int getNumPreFlippedEdges() {
        return this.getNumPreEdges(true, this.flippedEdges);
    }

    public String getPreFlippedEdges() {
        return this.getPreEdges(true, this.flippedEdges);
    }

    public int getNumPrePermutedEdges() {
        return this.getNumPreSolvedEdges() + this.getNumPreFlippedEdges();
    }

    public String getPrePermutedEdges() {
        return this.getPreSolvedEdges() + this.getPreFlippedEdges();
    }

    protected int getNumPreEdges(boolean flipped, List<Integer> searchList) {
        int preSolved = 0;
        for (int i = 1; i < scrambledStateSolvedEdges.length; i++)
            if (scrambledStateSolvedEdges[i] && flipped == searchList.contains(edgeCubies[i][0])) preSolved++;
        return preSolved;
    }

    protected String getPreEdges(boolean flipped, List<Integer> searchList) {
        String solvedEdges = "";
        for (int i = 1; i < scrambledStateSolvedEdges.length; i++)
            if (scrambledStateSolvedEdges[i] && flipped == searchList.contains(edgeCubies[i][0]))
                solvedEdges += (solvedEdges.length() > 0 ? " " : "") + edgePositions[i];
        return solvedEdges;
    }

    public String getEdgeNoahtation() {
        String edgeLength = this.getEdgeLength() + "";
        for (int ignored : this.flippedEdges) edgeLength += "'";
        return edgeLength;
    }

    public void setEdgeScheme(String scheme) {
        this.setEdgeScheme(scheme.split(""));
    }

    public void setEdgeScheme(String[] scheme) {
        if (scheme.length == 12) this.edgeLettering = scheme;
    }

    public void setEdgeBuffer(String bufferAsLetter) {
        if (arrayContains(this.edgeLettering, bufferAsLetter)) {
            int speffz = arrayIndex(this.edgeLettering, bufferAsLetter);
            int outer = deepArrayOuterIndex(this.edgeCubies, speffz), inner = deepArrayInnerIndex(this.edgeCubies, speffz);
            for (int i = 0; i < outer; i++) cycleArrayLeft(this.edgeCubies);
            for (int i = 0; i < inner; i++) cycleArrayLeft(this.edgeCubies[0]);
            this.parseScramble(this.getScramble());
        }
    }

    public int getCenterLength() {
        return this.getNumPreFlippedCenters();
    }

    public int getNumPreSolvedCenters() {
        return this.getNumPreCenters(false);
    }

    public String getPreSolvedCenters() {
        return this.getPreCenters(false);
    }

    public int getNumPreFlippedCenters() {
        return this.getNumPreCenters(true);
    }

    public String getPreFlippedCenters() {
        return this.getPreCenters(true);
    }

    public int getNumPreCenters(boolean flipped) {
        int preSolved = 0;
        for (int i = 0; i < scrambledStateSolvedCenters.length; i++)
            if (scrambledStateSolvedCenters[i]) {
                if (flipped) {
                    if (cwCenters.contains(centerCubies[i][0]) || ccwCenters.contains(centerCubies[i][0])) preSolved++;
                } else if (!cwCenters.contains(centerCubies[i][0]) && !ccwCenters.contains(centerCubies[i][0]))
                    preSolved++;
            }
        return preSolved;
    }

    public String getPreCenters(boolean flipped) {
        String solvedCenters = "";
        for (int i = 0; i < scrambledStateSolvedCenters.length; i++)
            if (scrambledStateSolvedCenters[i]) {
                if (flipped) {
                    if (this.cwCenters.contains(centerCubies[i][0]) || this.ccwCenters.contains(centerCubies[i][0]))
                        solvedCenters += (solvedCenters.length() > 0 ? " " : "") + centerPositions[i];
                } else if (!this.cwCenters.contains(centerCubies[i][0]) && !this.ccwCenters.contains(centerCubies[i][0]))
                    solvedCenters += (solvedCenters.length() > 0 ? " " : "") + centerPositions[i];
            }
        return solvedCenters;
    }

    public String getCenterNoahtation() {
        return this.getCenterLength() + "";
    }

    public void setCenterScheme(String scheme) {
        this.setCenterScheme(scheme.split(""));
    }

    public void setCenterScheme(String[] scheme) {
        if (scheme.length == 4) this.centerLettering = scheme;
    }

    public int getTipLength() {
        return this.getNumPreFlippedTips();
    }

    public int getNumPreSolvedTips() {
        return this.getNumPreTips(false);
    }

    public String getPreSolvedTips() {
        return this.getPreTips(false);
    }

    public int getNumPreFlippedTips() {
        return this.getNumPreTips(true);
    }

    public String getPreFlippedTips() {
        return this.getPreTips(true);
    }

    public int getNumPreTips(boolean flipped) {
        int preSolved = 0;
        for (int i = 0; i < scrambledStateSolvedTips.length; i++)
            if (scrambledStateSolvedTips[i]) {
                if (flipped) {
                    if (cwTips.contains(tipCubies[i][0]) || ccwTips.contains(tipCubies[i][0])) preSolved++;
                } else if (!cwTips.contains(tipCubies[i][0]) && !ccwTips.contains(tipCubies[i][0])) preSolved++;
            }
        return preSolved;
    }

    public String getPreTips(boolean flipped) {
        String solvedTips = "";
        for (int i = 0; i < scrambledStateSolvedTips.length; i++)
            if (scrambledStateSolvedTips[i]) {
                if (flipped) {
                    if (this.cwTips.contains(tipCubies[i][0]) || this.ccwTips.contains(tipCubies[i][0]))
                        solvedTips += (solvedTips.length() > 0 ? " " : "") + tipPositions[i];
                } else if (!this.cwTips.contains(tipCubies[i][0]) && !this.ccwTips.contains(tipCubies[i][0]))
                    solvedTips += (solvedTips.length() > 0 ? " " : "") + tipPositions[i];
            }
        return solvedTips;
    }

    public String getTipNoahtation() {
        return this.getTipLength() + "";
    }

    public void setTipScheme(String scheme) {
        this.setTipScheme(scheme.split(""));
    }

    public void setTipScheme(String[] scheme) {
        if (scheme.length == 4) this.tipLettering = scheme;
    }
}