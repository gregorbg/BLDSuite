package com.suushiemaniac.cubing.bld.analyze.cube;

import com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.util.ArrayUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public class ThreeBldCube extends TwoBldCube {
    public enum CornerParityMethod {
        SWAP_UB_UL, USE_ALG
    }

    //[U' Rw U' Rw' U ; M2]
    public static String CORNER_PARITY_ALG = "U' Rw U' Rw' U M2 U' Rw U Rw' U";

    public static void setCornerParityAlg(String alg) {
        CORNER_PARITY_ALG = alg;
    }

    public static boolean solves(PieceType cubicPieceType, String alg, String lpCase) {
        ThreeBldCube referenceCube = new ThreeBldCube(cubicPieceType.getReader().parse(alg).inverse().plain().toFormatString());
        String solutionPairs;
        if (!(cubicPieceType instanceof CubicPieceType)) return false;
        switch ((CubicPieceType) cubicPieceType) {
            case CORNER:
                solutionPairs = referenceCube.getCornerPairs();
                break;
            case EDGE:
                solutionPairs = referenceCube.getEdgePairs();
                break;
            default:
                solutionPairs = "";
                break;
        }
        return solutionPairs.length() > 0 && solutionPairs.equals(lpCase);
    }

    public static boolean isCenterSensitive(String alg) {
        HashMap<String, Integer> rotMap = new HashMap<>();
        for (String move : alg.split("\\s")) {
            String pureMove = move;
            while (pureMove.endsWith("2") || pureMove.endsWith("'")) pureMove = pureMove.substring(0, pureMove.length() - 1);
            Integer faceInt = rotMap.getOrDefault(pureMove, 0);
            faceInt += move.endsWith("2") ? 2 : move.endsWith("'") ? -1 : 1;
            rotMap.put(pureMove, faceInt);
        }
        for (String pureMove : rotMap.keySet()) if (Math.abs(rotMap.get(pureMove)) % 4 != 0) return true;
        return false;
    }

    public static CubicPieceType[] getPieceTypeArray() {
        return new CubicPieceType[]{CORNER, EDGE};
    }

    protected int[] edges = new int[24]; // Position of all 24 edge stickers in the cube
    protected int[] centers = new int[6]; // Position of all 6 center stickers in the cube

    // Speffz
    protected String[] edgeLettering = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X"};
    protected String[] edgeStickers = {"UB", "UR", "UF", "UL", "LU", "LF", "LD", "LB", "FU", "FR", "FD", "FL", "RU", "RB", "RD", "RF", "BU", "BL", "BD", "BR", "DF", "DR", "DB", "DL"};
    protected String[] edgePositions = {"DF", "UB", "UR", "UF", "UL", "BR", "BL", "FR", "FL", "DR", "DB", "DL"};

    // Edge and corner cubies
    // Sticker in position [0][0] of cubie arrays represents the buffer
    protected Integer[][] edgeCubies = {{U, K}, {A, Q}, {B, M}, {C, I}, {D, E}, {R, H}, {T, N}, {L, F}, {J, P}, {V, O}, {W, S}, {X, G}};
    protected boolean[] solvedEdges = {true, true, true, true, true, true, true, true, true, true, true, true};
    protected boolean[] scrambledStateSolvedEdges = {false, false, false, false, false, false, false, false, false, false, false, false};
    protected int[] scrambledStateEdges = new int[24];
    protected int edgeCycleNum = 0;
    protected ArrayList<Integer> edgeCycles = new ArrayList<>();
    protected ArrayList<Integer> flippedEdges = new ArrayList<>();

    protected CornerParityMethod cornerParityMethod = CornerParityMethod.SWAP_UB_UL;

    /**
     * The constructor for creating a new ThreeBldCube object
     *
     * @param scramble The scramble to be parsed. Supports full WCA notation including rotations
     */
    public ThreeBldCube(String scramble) {
        this.initPermutations();
        this.parseScramble(scramble);
    }

    protected ThreeBldCube() {
    }

    @Override
    protected void initPermutations() {
        super.initPermutations();
        String[] faceNames = {
                "Uw", "Uw'", "Uw2",
                "Fw", "Fw'", "Fw2",
                "Rw", "Rw'", "Rw2",
                "Lw", "Lw'", "Lw2",
                "Bw", "Bw'", "Bw2",
                "Dw", "Dw'", "Dw2",

                "M", "M'", "M2",
                "S", "S'", "S2",
                "E", "E'", "E2",
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

                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z}
        };
        for (int i = 0; i < faceNames.length; i++) {
            HashMap<PieceType, Integer[]> tempMap = permutations.get(faceNames[i]);
            if (tempMap == null) tempMap = new HashMap<>();
            tempMap.put(CORNER, cornerFacePerms[i]);
            permutations.put(faceNames[i], tempMap);
        }
        initPermutationsChain();
    }

    private void initPermutationsChain() {
        String[] faceNames = {
                "U", "U'", "U2",
                "F", "F'", "F2",
                "R", "R'", "R2",
                "L", "L'", "L2",
                "B", "B'", "B2",
                "D", "D'", "D2",

                "Uw", "Uw'", "Uw2",
                "Fw", "Fw'", "Fw2",
                "Rw", "Rw'", "Rw2",
                "Lw", "Lw'", "Lw2",
                "Bw", "Bw'", "Bw2",
                "Dw", "Dw'", "Dw2",

                "M", "M'", "M2",
                "S", "S'", "S2",
                "E", "E'", "E2",

                "x", "x'", "x2",
                "y", "y'", "y2",
                "z", "z'", "z2"
        };
        Integer[][] edgeFacePerms = {
                {B, C, D, A, Q, Z, Z, Z, E, Z, Z, Z, I, Z, Z, Z, M, Z, Z, Z, Z, Z, Z, Z},
                {D, A, B, C, I, Z, Z, Z, M, Z, Z, Z, Q, Z, Z, Z, E, Z, Z, Z, Z, Z, Z, Z},
                {C, D, A, B, M, Z, Z, Z, Q, Z, Z, Z, E, Z, Z, Z, I, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, P, Z, Z, C, Z, Z, J, K, L, I, Z, Z, Z, U, Z, Z, Z, Z, F, Z, Z, Z},
                {Z, Z, F, Z, Z, U, Z, Z, L, I, J, K, Z, Z, Z, C, Z, Z, Z, Z, P, Z, Z, Z},
                {Z, Z, U, Z, Z, P, Z, Z, K, L, I, J, Z, Z, Z, F, Z, Z, Z, Z, C, Z, Z, Z},
                {Z, T, Z, Z, Z, Z, Z, Z, Z, B, Z, Z, N, O, P, M, Z, Z, Z, V, Z, J, Z, Z},
                {Z, J, Z, Z, Z, Z, Z, Z, Z, V, Z, Z, P, M, N, O, Z, Z, Z, B, Z, T, Z, Z},
                {Z, V, Z, Z, Z, Z, Z, Z, Z, T, Z, Z, O, P, M, N, Z, Z, Z, J, Z, B, Z, Z},
                {Z, Z, Z, L, F, G, H, E, Z, Z, Z, X, Z, Z, Z, Z, Z, D, Z, Z, Z, Z, Z, R},
                {Z, Z, Z, R, H, E, F, G, Z, Z, Z, D, Z, Z, Z, Z, Z, X, Z, Z, Z, Z, Z, L},
                {Z, Z, Z, X, G, H, E, F, Z, Z, Z, R, Z, Z, Z, Z, Z, L, Z, Z, Z, Z, Z, D},
                {H, Z, Z, Z, Z, Z, Z, W, Z, Z, Z, Z, Z, A, Z, Z, R, S, T, Q, Z, Z, N, Z},
                {N, Z, Z, Z, Z, Z, Z, A, Z, Z, Z, Z, Z, W, Z, Z, T, Q, R, S, Z, Z, H, Z},
                {W, Z, Z, Z, Z, Z, Z, N, Z, Z, Z, Z, Z, H, Z, Z, S, T, Q, R, Z, Z, A, Z},
                {Z, Z, Z, Z, Z, Z, K, Z, Z, Z, O, Z, Z, Z, S, Z, Z, Z, G, Z, V, W, X, U},
                {Z, Z, Z, Z, Z, Z, S, Z, Z, Z, G, Z, Z, Z, K, Z, Z, Z, O, Z, X, U, V, W},
                {Z, Z, Z, Z, Z, Z, O, Z, Z, Z, S, Z, Z, Z, G, Z, Z, Z, K, Z, W, X, U, V},

                {B, C, D, A, Q, R, Z, T, E, F, Z, H, I, J, Z, L, M, N, Z, P, Z, Z, Z, Z},
                {D, A, B, C, I, J, Z, L, M, N, Z, P, Q, R, Z, T, E, F, Z, H, Z, Z, Z, Z},
                {C, D, A, B, M, N, Z, P, Q, R, Z, T, E, F, Z, H, I, J, Z, L, Z, Z, Z, Z},
                {Z, O, P, M, B, C, D, Z, J, K, L, I, V, Z, X, U, Z, Z, Z, Z, F, G, Z, E},
                {Z, E, F, G, X, U, V, Z, L, I, J, K, D, Z, B, C, Z, Z, Z, Z, P, M, Z, O},
                {Z, X, U, V, O, P, M, Z, K, L, I, J, G, Z, E, F, Z, Z, Z, Z, C, D, Z, B},
                {S, T, Q, Z, Z, Z, Z, Z, A, B, C, Z, N, O, P, M, W, Z, U, V, I, J, K, Z},
                {I, J, K, Z, Z, Z, Z, Z, U, V, W, Z, P, M, N, O, C, Z, A, B, S, T, Q, Z},
                {U, V, W, Z, Z, Z, Z, Z, S, T, Q, Z, O, P, M, N, K, Z, I, J, A, B, C, Z},
                {I, Z, K, L, F, G, H, E, U, Z, W, X, Z, Z, Z, Z, C, D, A, Z, S, Z, Q, R},
                {S, Z, Q, R, H, E, F, G, A, Z, C, D, Z, Z, Z, Z, W, X, U, Z, I, Z, K, L},
                {U, Z, W, X, G, H, E, F, S, Z, Q, R, Z, Z, Z, Z, K, L, I, Z, A, Z, C, D},
                {H, E, Z, G, X, Z, V, W, Z, Z, Z, Z, D, A, B, Z, R, S, T, Q, Z, M, N, O},
                {N, O, Z, M, B, Z, D, A, Z, Z, Z, Z, V, W, X, Z, T, Q, R, S, Z, G, H, E},
                {W, X, Z, V, O, Z, M, N, Z, Z, Z, Z, G, H, E, Z, S, T, Q, R, Z, D, A, B},
                {Z, Z, Z, Z, Z, J, K, L, Z, N, O, P, Z, R, S, T, Z, F, G, H, V, W, X, U},
                {Z, Z, Z, Z, Z, R, S, T, Z, F, G, H, Z, J, K, L, Z, N, O, P, X, U, V, W},
                {Z, Z, Z, Z, Z, N, O, P, Z, R, S, T, Z, F, G, H, Z, J, K, L, W, X, U, V},

                {I, Z, K, Z, Z, Z, Z, Z, U, Z, W, Z, Z, Z, Z, Z, C, Z, A, Z, S, Z, Q, Z},
                {S, Z, Q, Z, Z, Z, Z, Z, A, Z, C, Z, Z, Z, Z, Z, W, Z, U, Z, I, Z, K, Z},
                {U, Z, W, Z, Z, Z, Z, Z, S, Z, Q, Z, Z, Z, Z, Z, K, Z, I, Z, A, Z, C, Z},
                {Z, O, Z, M, B, Z, D, Z, Z, Z, Z, Z, V, Z, X, Z, Z, Z, Z, Z, Z, G, Z, E},
                {Z, E, Z, G, X, Z, V, Z, Z, Z, Z, Z, D, Z, B, Z, Z, Z, Z, Z, Z, M, Z, O},
                {Z, X, Z, V, O, Z, M, Z, Z, Z, Z, Z, G, Z, E, Z, Z, Z, Z, Z, Z, D, Z, B},
                {Z, Z, Z, Z, Z, J, Z, L, Z, N, Z, P, Z, R, Z, T, Z, F, Z, H, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, R, Z, T, Z, F, Z, H, Z, J, Z, L, Z, N, Z, P, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, N, Z, P, Z, R, Z, T, Z, F, Z, H, Z, J, Z, L, Z, Z, Z, Z},

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
        Integer[][] centerFacePerms = {
                {Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z},

                {Z, BACK, LEFT, FRONT, RIGHT, Z},
                {Z, FRONT, RIGHT, BACK, LEFT, Z},
                {Z, RIGHT, BACK, LEFT, FRONT, Z},
                {RIGHT, UP, Z, DOWN, Z, LEFT},
                {LEFT, DOWN, Z, UP, Z, RIGHT},
                {DOWN, RIGHT, Z, LEFT, Z, UP},
                {BACK, Z, UP, Z, DOWN, FRONT},
                {FRONT, Z, DOWN, Z, UP, BACK},
                {DOWN, Z, BACK, Z, FRONT, UP},
                {FRONT, Z, DOWN, Z, UP, BACK},
                {BACK, Z, UP, Z, DOWN, FRONT},
                {DOWN, Z, BACK, Z, FRONT, UP},
                {LEFT, DOWN, Z, UP, Z, RIGHT},
                {RIGHT, UP, Z, DOWN, Z, LEFT},
                {DOWN, RIGHT, Z, LEFT, Z, UP},
                {Z, FRONT, RIGHT, BACK, LEFT, Z},
                {Z, BACK, LEFT, FRONT, RIGHT, Z},
                {Z, RIGHT, BACK, LEFT, FRONT, Z},

                {FRONT, Z, DOWN, Z, UP, BACK},
                {BACK, Z, UP, Z, DOWN, FRONT},
                {DOWN, Z, BACK, Z, FRONT, UP},
                {RIGHT, UP, Z, DOWN, Z, LEFT},
                {LEFT, DOWN, Z, UP, Z, RIGHT},
                {DOWN, RIGHT, Z, LEFT, Z, UP},
                {Z, FRONT, RIGHT, BACK, LEFT, Z},
                {Z, BACK, LEFT, FRONT, RIGHT, Z},
                {Z, RIGHT, BACK, LEFT, FRONT, Z},

                {BACK, Z, UP, Z, DOWN, FRONT},
                {FRONT, Z, DOWN, Z, UP, BACK},
                {DOWN, Z, BACK, Z, FRONT, UP},
                {Z, BACK, LEFT, FRONT, RIGHT, Z},
                {Z, FRONT, RIGHT, BACK, LEFT, Z},
                {Z, RIGHT, BACK, LEFT, FRONT, Z},
                {RIGHT, UP, Z, DOWN, Z, LEFT},
                {LEFT, DOWN, Z, UP, Z, RIGHT},
                {DOWN, RIGHT, Z, LEFT, Z, UP}
        };
        for (int i = 0; i < faceNames.length; i++) {
            HashMap<PieceType, Integer[]> tempMap = permutations.get(faceNames[i]);
            if (tempMap == null) tempMap = new HashMap<>();
            tempMap.put(EDGE, edgeFacePerms[i]);
            tempMap.put(CENTER, centerFacePerms[i]);
            permutations.put(faceNames[i], tempMap);
        }
    }

    // Sets cube to solved position
    @Override
    protected void resetCube(boolean orientationOnly) {
        super.resetCube(orientationOnly);
        // Corners and edges and centers are initialized in solved position
        for (int i = 0; i < 24; i++) {
            if (!orientationOnly) {
                edges[i] = i;
                if (i < 6) centers[i] = i;
            }
            if (i < 12) solvedEdges[i] = false;
        }
    }

    // Perform a permutation on the cube
    @Override
    protected void permute(String permutation) {
        super.permute(permutation);
        // Edges are permuted
        int[] exchanges = new int[]{Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
        Integer[] perm = permutations.get(permutation).get(EDGE);
        for (int i = 0; i < 24; i++) if (perm[i] != Z) exchanges[perm[i]] = edges[i];
        for (int i = 0; i < 24; i++) if (exchanges[i] != Z) edges[i] = exchanges[i];
        // Centers are permuted
        exchanges = new int[]{Z, Z, Z, Z, Z, Z};
        perm = permutations.get(permutation).get(CENTER);
        for (int i = 0; i < 6; i++) if (perm[i] != Z) exchanges[perm[i]] = centers[i];
        for (int i = 0; i < 6; i++) if (exchanges[i] != Z) centers[i] = exchanges[i];
    }

    // Finds a BLD solution for the cube in its current state
    @Override
    protected void solveCube() {
        System.arraycopy(this.corners, 0, this.scrambledStateCorners, 0, 24);
        System.arraycopy(this.edges, 0, this.scrambledStateEdges, 0, 24);
        System.arraycopy(this.centers, 0, this.scrambledStateCenters, 0, 6);
        reorientCube();
        solveCorners();
        solveEdges();
    }

    public void setCornerParityMethod(CornerParityMethod cornerParityMethod) {
        this.cornerParityMethod = cornerParityMethod;
        this.parseScramble(this.getScramble());
    }

    @Override
    protected void reorientCube() {
        this.centerRotations = "";
        String neededRotation = this.getRotationsFromOrientation(this.centerCubies[0], this.centerCubies[2], this.centers);

        if (neededRotation.length() > 0) {
            this.centerRotations = neededRotation;
            for (String rotation : neededRotation.split("\\s")) this.permute(rotation);
        }
    }

    // Solves all 12 edges in the cube
    protected void solveEdges() {
        // Parity is solved by swapping UL and UB
        if (cornerCycles.size() % 2 == 1 && this.cornerParityMethod == CornerParityMethod.SWAP_UB_UL) {
            int UB = -1;
            int UL = -1;

            // Positions of UB and UL edges are found
            for (int i = 0; i < 12 && (UB == -1 || UL == -1); i++) {
                if ((edges[edgeCubies[i][0]] == A && edges[edgeCubies[i][1]] == Q) || (edges[edgeCubies[i][1]] == A && edges[edgeCubies[i][0]] == Q))
                    UB = i;
                if ((edges[edgeCubies[i][0]] == D && edges[edgeCubies[i][1]] == E) || (edges[edgeCubies[i][1]] == D && edges[edgeCubies[i][0]] == E))
                    UL = i;
            }

            // UB is stored in buffer
            int[] tempEdge = {edges[edgeCubies[UB][0]], edges[edgeCubies[UB][1]]};

            // Make sure that UB goes to UL and BU goes to LU
            int index = 0;
            if ((edges[edgeCubies[UB][0]] == A && edges[edgeCubies[UL][0]] == E) || (edges[edgeCubies[UB][0]] == Q && edges[edgeCubies[UL][0]] == D))
                index = 1;

            // UL is placed in UB
            edges[edgeCubies[UB][0]] = edges[edgeCubies[UL][index]];
            edges[edgeCubies[UB][1]] = edges[edgeCubies[UL][(index + 1) % 2]];

            // buffer is placed in UL
            edges[edgeCubies[UL][0]] = tempEdge[index];
            edges[edgeCubies[UL][1]] = tempEdge[(index + 1) % 2];
        }
        if (!edgesSolved()) System.arraycopy(solvedEdges, 0, scrambledStateSolvedEdges, 0, solvedEdges.length);
        else
            this.scrambledStateSolvedEdges = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true};
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
            int[] edgePref = {1, 8, 7, 2, 4, 3, 5, 6, 9, 10, 11};
            for (int i = 0; i < 11 && !edgeCycled; i++) {
                int j = this.edgeCubies[0][0] == U ? edgePref[i] : i;
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
            for (int i = 0; i < 12 && !edgeCycled; i++) {
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
        for (int i = 0; i < 12; i++) {
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

    /**
     * Gets the edge solution pairs in Speffz
     *
     * @return A string of the form AB CD EF containing all Speffz targets needed to solve the edges
     */
    public String getEdgePairs(boolean withFlipped) {
        return this.getEdgeSolutionFromNameArray(this.edgeLettering, withFlipped, false);
    }

    /**
     * Simple alias
     * @return String like @see{getEdgePairs}
     */
    public String getEdgePairs() {
        return this.getEdgePairs(true);
    }

    /**
     * Same as getEdgePairs, but with stickers instead of Speffz lettering
     * @return A string of the form UB UR UF containing all sticker targets needed to solve the corners
     */
    public String getEdgeStickerCycles(boolean withFlipped) {
        return this.getEdgeSolutionFromNameArray(this.edgeStickers, withFlipped, true);
    }

    /**
     * Simple alias
     * @return String like @see{getEdgeStickerCycles}
     */
    public String getEdgeStickerCycles() {
        return this.getEdgeStickerCycles(true);
    }

    protected String getEdgeSolutionFromNameArray(String[] names, boolean withFlipped, boolean forceGap) {
        String edgePairs = "";
        if (edgeCycles.size() != 0 || flippedEdges.size() != 0) {
            for (int i = 0; i < edgeCycles.size(); i++) {
                edgePairs += names[edgeCycles.get(i)];
                if (forceGap || i % 2 == 1) edgePairs += " ";
            }
            if (edgeCycles.size() % 2 == 1 && this.cornerParityMethod == CornerParityMethod.USE_ALG)
                edgePairs += "\tParity: " + CORNER_PARITY_ALG;
            if (withFlipped && flippedEdges.size() != 0) {
                edgePairs += "\tFlip: ";
                for (Integer flippedEdge : flippedEdges) edgePairs += names[flippedEdge] + " ";
            }
        } else return "Solved";
        return edgePairs.trim();
    }

    /**
     * Get's a solution for all flipped edges in single target OP fashion
     *
     * @return String with sticker targets
     */
    public String getFlippedEdgeSingleTargetStickerCycles() {
        if (this.getNumPreFlippedEdges() < 1) return "";
        String flippedEdges = this.getPreFlippedEdges();
        String opSolution = "";

        for (String flippedEdge : flippedEdges.split("\\s+?")) {
            opSolution += flippedEdge + " ";
            Character[] flippedEdgeChars = ArrayUtil.autobox(flippedEdge.toCharArray());
            ArrayUtil.cycleLeft(flippedEdgeChars);
            opSolution += new String(ArrayUtil.autobox(flippedEdgeChars)) + " ";
        }

        return opSolution.trim();
    }

    /**
     * Get's a solution for all flipped edges in single target OP fashion
     *
     * @return String with Speffz targets
     */
    public String getFlippedEdgeSingleTargetPairs() {
        if (this.getNumPreFlippedEdges() < 1) return "";
        String flippedEdgeOpSpeffz = this.getFlippedEdgeSingleTargetStickerCycles();
        String opStickerSolution = "";

        String[] split = flippedEdgeOpSpeffz.split("\\s+?");
        for (int i = 0; i < split.length; i++) {
            String target = split[i];
            opStickerSolution += this.edgeLettering[ArrayUtil.index(this.edgeStickers, target)];
            if (i % 2 == 1) opStickerSolution += " ";
        }

        return opStickerSolution.trim();
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
                + "Edges: " + this.getEdgePairs()
                + "\nCorners: " + this.getCornerPairs();
    }

    /**
     * Gets all available statistics in one String
     *
     * @return mutiple lines, with each one equal to one piece type in the form "targets@break-ins w/ solved-misoriented > parity"
     */
    @Override
    public String getStatstics() {
        return "Corners: " + this.getCornerLength() + "@" + this.getCornerBreakInNum() + " w/ " + this.getNumPreSolvedCorners() + "-" + this.getNumPreTwistedCorners() + " > " + this.hasCornerParity()
                + "\nEdges: " + this.getEdgeLength() + "@" + this.getEdgeBreakInNum() + " w/ " + this.getNumPreSolvedEdges() + "-" + this.getNumPreFlippedEdges() + " > " + this.hasCornerParity();
    }

    /**
     * Gets the complete Noahtation for the cube in it's current state
     *
     * @return String with Noahtation for all individual piece types
     */
    @Override
    public String getNoahtation() {
        return "C: " + this.getCornerNoahtation() + " / E: " + this.getEdgeNoahtation();
    }

    public String getStatString(boolean spaced, boolean newLine) {
        return "C: " + this.getCornerStatString(spaced) + (newLine ? "\n" : " | ") + "E: " + this.getEdgeStatString(spaced);
    }

    @Override
    public String getPuzzleString() {
        return "333";
    }

    public String getEdgeStatString(boolean spaced) {
        String edgeStat = "" + this.getEdgeLength();
        edgeStat += this.isEdgeBufferSolved() ? "*" : " ";
        edgeStat += spaced ? "\t" : " ";
        for (int i = 0; i < 5; i++) edgeStat += i < this.getEdgeBreakInNum() ? "#" : spaced ? " " : "";
        if (spaced || edgeStat.endsWith("#")) edgeStat += " ";
        for (int i = 0; i < 11; i++) edgeStat += i < this.getNumPreFlippedEdges() ? "~" : spaced ? " " : "";
        if (spaced || edgeStat.endsWith("~")) edgeStat += " ";
        for (int i = 0; i < 11; i++) edgeStat += i < this.getNumPreSolvedEdges() ? "+" : spaced ? " " : "";
        return edgeStat;
    }

    public String getEdgeStatString() {
        return this.getEdgeStatString(false);
    }

    @Override
    public String getRotations() {
        return this.centerRotations.length() > 0 ? this.centerRotations : "/";
    }

    @Override
    public boolean isSingleCycle() {
        return this.isEdgeSingleCycle() && this.isCornerSingleCycle();
    }

    public boolean isEdgeBufferSolved() {
        return this.scrambledStateSolvedEdges[0];
    }

    @Override
    public String getScramble() {
        return this.scramble;
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
        this.setEdgeScheme(scheme.split(scheme.length() == 24 ? "" : "\\s+?"));
    }

    public void setEdgeScheme(String[] scheme) {
        if (scheme.length == 24) this.edgeLettering = scheme;
    }

    public void setEdgeBuffer(String bufferAsLetter) {
        if (ArrayUtil.contains(this.edgeLettering, bufferAsLetter)) {
            int speffz = ArrayUtil.index(this.edgeLettering, bufferAsLetter);
            int outer = ArrayUtil.deepOuterIndex(this.edgeCubies, speffz), inner = ArrayUtil.deepInnerIndex(this.edgeCubies, speffz);
            for (int i = 0; i < outer; i++) ArrayUtil.cycleLeft(this.edgeCubies);
            for (int i = 0; i < inner; i++) ArrayUtil.cycleLeft(this.edgeCubies[0]);
            this.parseScramble(this.getScramble());
        }
    }

    public void setScheme(String type, String[] scheme) {
        switch (type.toLowerCase()) {
            case "edge":
                this.setEdgeScheme(scheme);
                break;
            default:
                super.setScheme(type, scheme);
        }
    }

    public String[] getScheme(String type) {
        switch (type.toLowerCase()) {
            case "edge":
                return this.edgeLettering;
            default:
                return super.getScheme(type);
        }
    }
}