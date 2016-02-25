package com.suushiemaniac.cubing.bld.analyze.cube;

import com.suushiemaniac.cubing.bld.enumeration.CubicPieceType;
import com.suushiemaniac.cubing.bld.enumeration.PieceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.suushiemaniac.cubing.bld.enumeration.CubicPieceType.*;

public class FiveBldCube extends FourBldCube {
    public static boolean isCenterSensitive(String alg) {
        FiveBldCube referenceCube = new FiveBldCube(alg);
        int[] solvedX = new int[24], solvedT = new int[24];
        for (int i = 0; i < 24; i++) {
            solvedX[i] = i;
            solvedT[i] = i;
        }
        return !Arrays.equals(referenceCube.xCenters, solvedX) || !Arrays.equals(referenceCube.tCenters, solvedT);
    }

    public static boolean solves(CubicPieceType cubicPieceType, String alg, String lpCase) {
        FiveBldCube referenceCube = new FiveBldCube(invAlg(alg));
        String solutionPairs;
        switch (cubicPieceType) {
            case CORNER:
                solutionPairs = referenceCube.getCornerPairs();
                break;
            case EDGE:
                solutionPairs = referenceCube.getEdgePairs();
                break;
            case WING:
                solutionPairs = referenceCube.getWingPairs();
                break;
            case XCENTER:
                solutionPairs = referenceCube.getXCenterPairs();
                break;
            case TCENTER:
                solutionPairs = referenceCube.getTCenterPairs();
                break;
            default:
                solutionPairs = "";
                break;
        }
        return solutionPairs.length() > 0 && solutionPairs.equals(lpCase);
    }

    public static CubicPieceType[] getPieceTypeArray() {
        return new CubicPieceType[]{CORNER, EDGE, WING, XCENTER, TCENTER};
    }

    // Vars
    protected int[] tCenters = new int[24];

    // Speffz
    protected String[] tCenterLettering = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X"};
    protected String[] tCenterPositions = {"Df", "Dr", "Db", "Dl", "Ub", "Ur", "Uf", "Ul", "Lu", "Lf", "Ld", "Lb", "Fu", "Fr", "Fd", "Fl", "Ru", "Rb", "Rd", "Rf", "Bu", "Bl", "Bd", "Br"};

    // Edge and corner cubies
    // Sticker in position 0,0 of cubie arrays represents the buffer
    protected Integer[][] tCenterCubies = {{U, V, W, X}, {A, B, C, D}, {E, F, G, H}, {I, J, K, L}, {M, N, O, P}, {Q, R, S, T}};
    protected boolean[] solvedTCenters = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
    protected boolean[] scrambledStateSolvedTCenters = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
    protected int[] scrambledStateTCenters = new int[24];
    protected int tCenterCycleNum = 0;
    protected ArrayList<Integer> tCenterCycles = new ArrayList<>();

    private boolean avoidTBreakIns = true;

    /**
     * The constructor for creating a new FourBldCube object
     *
     * @param scramble The scramble to be parsed. Supports full WCA notation including rotations and inner slice moves (lowercase)
     */
    public FiveBldCube(String scramble) {
        initPermutations();
        this.parseScramble(scramble);
    }

    protected FiveBldCube() {
    }

    @Override
    protected void initPermutations() {
        super.initPermutations();
        String[] faceNames = {
                "3Uw", "3Uw'", "3Uw2",
                "3Fw", "3Fw'", "3Fw2",
                "3Rw", "3Rw'", "3Rw2",
                "3Lw", "3Lw'", "3Lw2",
                "3Bw", "3Bw'", "3Bw2",
                "3Dw", "3Dw'", "3Dw2"
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
                {Z, Z, Z, Z, Z, Z, O, P, Z, Z, S, T, Z, Z, G, H, Z, Z, K, L, W, X, U, V}
        };
        Integer[][] edgeFacePerms = {
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
                {Z, Z, Z, Z, Z, N, O, P, Z, R, S, T, Z, F, G, H, Z, J, K, L, W, X, U, V}
        };
        Integer[][] wingFacePerms = {
                {B, C, D, A, Q, Z, Z, T, E, Z, Z, H, I, Z, Z, L, M, Z, Z, P, Z, Z, Z, Z},
                {D, A, B, C, I, Z, Z, L, M, Z, Z, P, Q, Z, Z, T, E, Z, Z, H, Z, Z, Z, Z},
                {C, D, A, B, M, Z, Z, P, Q, Z, Z, T, E, Z, Z, H, I, Z, Z, L, Z, Z, Z, Z},
                {Z, O, P, Z, B, C, Z, Z, J, K, L, I, Z, Z, X, U, Z, Z, Z, Z, F, Z, Z, E},
                {Z, E, F, Z, X, U, Z, Z, L, I, J, K, Z, Z, B, C, Z, Z, Z, Z, P, Z, Z, O},
                {Z, X, U, Z, O, P, Z, Z, K, L, I, J, Z, Z, E, F, Z, Z, Z, Z, C, Z, Z, B},
                {S, T, Z, Z, Z, Z, Z, Z, A, B, Z, Z, N, O, P, M, Z, Z, U, V, I, J, Z, Z},
                {I, J, Z, Z, Z, Z, Z, Z, U, V, Z, Z, P, M, N, O, Z, Z, A, B, S, T, Z, Z},
                {U, V, Z, Z, Z, Z, Z, Z, S, T, Z, Z, O, P, M, N, Z, Z, I, J, A, B, Z, Z},
                {Z, Z, K, L, F, G, H, E, Z, Z, W, X, Z, Z, Z, Z, C, D, Z, Z, Z, Z, Q, R},
                {Z, Z, Q, R, H, E, F, G, Z, Z, C, D, Z, Z, Z, Z, W, X, Z, Z, Z, Z, K, L},
                {Z, Z, W, X, G, H, E, F, Z, Z, Q, R, Z, Z, Z, Z, K, L, Z, Z, Z, Z, C, D},
                {H, Z, Z, G, Z, Z, V, W, Z, Z, Z, Z, D, A, Z, Z, R, S, T, Q, Z, M, N, Z},
                {N, Z, Z, M, Z, Z, D, A, Z, Z, Z, Z, V, W, Z, Z, T, Q, R, S, Z, G, H, Z},
                {W, Z, Z, V, Z, Z, M, N, Z, Z, Z, Z, G, H, Z, Z, S, T, Q, R, Z, D, A, Z},
                {Z, Z, Z, Z, Z, J, K, Z, Z, N, O, Z, Z, R, S, Z, Z, F, G, Z, V, W, X, U},
                {Z, Z, Z, Z, Z, R, S, Z, Z, F, G, Z, Z, J, K, Z, Z, N, O, Z, X, U, V, W},
                {Z, Z, Z, Z, Z, N, O, Z, Z, R, S, Z, Z, F, G, Z, Z, J, K, Z, W, X, U, V}
        };
        Integer[][] xCenterFacePerms = {
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
                {Z, Z, Z, Z, Z, Z, O, P, Z, Z, S, T, Z, Z, G, H, Z, Z, K, L, W, X, U, V}
        };
        Integer[][] centerFacePerms = {
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
                {Z, RIGHT, BACK, LEFT, FRONT, Z}
        };
        for (int i = 0; i < faceNames.length; i++) {
            HashMap<PieceType, Integer[]> tempMap = permutations.get(faceNames[i]);
            if (tempMap == null) tempMap = new HashMap<>();
            tempMap.put(CORNER, cornerFacePerms[i]);
            tempMap.put(EDGE, edgeFacePerms[i]);
            tempMap.put(WING, wingFacePerms[i]);
            tempMap.put(XCENTER, xCenterFacePerms[i]);
            tempMap.put(CENTER, centerFacePerms[i]);
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

                "3Uw", "3Uw'", "3Uw2",
                "3Fw", "3Fw'", "3Fw2",
                "3Rw", "3Rw'", "3Rw2",
                "3Lw", "3Lw'", "3Lw2",
                "3Bw", "3Bw'", "3Bw2",
                "3Dw", "3Dw'", "3Dw2",

                "u", "u'", "u2",
                "f", "f'", "f2",
                "r", "r'", "r2",
                "l", "l'", "l2",
                "b", "b'", "b2",
                "d", "d'", "d2",

                "M", "M'", "M2",
                "S", "S'", "S2",
                "E", "E'", "E2",

                "x", "x'", "x2",
                "y", "y'", "y2",
                "z", "z'", "z2"
        };
        Integer[][] tCenterFacePerms = {
                {B, C, D, A, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {D, A, B, C, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {C, D, A, B, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, J, K, L, I, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, L, I, J, K, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, K, L, I, J, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, N, O, P, M, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, P, M, N, O, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, O, P, M, N, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, F, G, H, E, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, H, E, F, G, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, G, H, E, F, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, R, S, T, Q, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, T, Q, R, S, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, S, T, Q, R, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, V, W, X, U},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, X, U, V, W},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, W, X, U, V},

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

                {Z, Z, Z, Z, Q, Z, Z, Z, E, Z, Z, Z, I, Z, Z, Z, M, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, I, Z, Z, Z, M, Z, Z, Z, Q, Z, Z, Z, E, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, M, Z, Z, Z, Q, Z, Z, Z, E, Z, Z, Z, I, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, P, Z, Z, C, Z, Z, Z, Z, Z, Z, Z, Z, Z, U, Z, Z, Z, Z, F, Z, Z, Z},
                {Z, Z, F, Z, Z, U, Z, Z, Z, Z, Z, Z, Z, Z, Z, C, Z, Z, Z, Z, P, Z, Z, Z},
                {Z, Z, U, Z, Z, P, Z, Z, Z, Z, Z, Z, Z, Z, Z, F, Z, Z, Z, Z, C, Z, Z, Z},
                {Z, T, Z, Z, Z, Z, Z, Z, Z, B, Z, Z, Z, Z, Z, Z, Z, Z, Z, V, Z, J, Z, Z},
                {Z, J, Z, Z, Z, Z, Z, Z, Z, V, Z, Z, Z, Z, Z, Z, Z, Z, Z, B, Z, T, Z, Z},
                {Z, V, Z, Z, Z, Z, Z, Z, Z, T, Z, Z, Z, Z, Z, Z, Z, Z, Z, J, Z, B, Z, Z},
                {Z, Z, Z, L, Z, Z, Z, Z, Z, Z, Z, X, Z, Z, Z, Z, Z, D, Z, Z, Z, Z, Z, R},
                {Z, Z, Z, R, Z, Z, Z, Z, Z, Z, Z, D, Z, Z, Z, Z, Z, X, Z, Z, Z, Z, Z, L},
                {Z, Z, Z, X, Z, Z, Z, Z, Z, Z, Z, R, Z, Z, Z, Z, Z, L, Z, Z, Z, Z, Z, D},
                {H, Z, Z, Z, Z, Z, Z, W, Z, Z, Z, Z, Z, A, Z, Z, Z, Z, Z, Z, Z, Z, N, Z},
                {N, Z, Z, Z, Z, Z, Z, A, Z, Z, Z, Z, Z, W, Z, Z, Z, Z, Z, Z, Z, Z, H, Z},
                {W, Z, Z, Z, Z, Z, Z, N, Z, Z, Z, Z, Z, H, Z, Z, Z, Z, Z, Z, Z, Z, A, Z},
                {Z, Z, Z, Z, Z, Z, K, Z, Z, Z, O, Z, Z, Z, S, Z, Z, Z, G, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, S, Z, Z, Z, G, Z, Z, Z, K, Z, Z, Z, O, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, O, Z, Z, Z, S, Z, Z, Z, G, Z, Z, Z, K, Z, Z, Z, Z, Z},

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
        for (int i = 0; i < faceNames.length; i++) {
            HashMap<PieceType, Integer[]> tempMap = permutations.get(faceNames[i]);
            if (tempMap == null) tempMap = new HashMap<>();
            tempMap.put(TCENTER, tCenterFacePerms[i]);
            permutations.put(faceNames[i], tempMap);
        }
    }

    // Sets cube to solved position
    @Override
    protected void resetCube(boolean orientationOnly) {
        // Corners and edges are initialized in solved position
        super.resetCube(orientationOnly);
        for (int i = 0; i < 24; i++) {
            if (!orientationOnly) tCenters[i] = i;
            solvedTCenters[i] = false;
        }
    }

    // Perform a permutation on the cube
    @Override
    protected void permute(String permutation) {
        super.permute(permutation);
        // TCenters are permuted
        int[] exchanges = new int[]{Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
        Integer[] perm = permutations.get(permutation).get(TCENTER);
        for (int i = 0; i < 24; i++) if (perm[i] != Z) exchanges[perm[i]] = tCenters[i];
        for (int i = 0; i < 24; i++) if (exchanges[i] != Z) tCenters[i] = exchanges[i];
    }

    // Finds a BLD solution for the cube in its current state
    @Override
    protected void solveCube() {
        System.arraycopy(this.corners, 0, this.scrambledStateCorners, 0, 24);
        System.arraycopy(this.edges, 0, this.scrambledStateEdges, 0, 24);
        System.arraycopy(this.wings, 0, this.scrambledStateWings, 0, 24);
        System.arraycopy(this.xCenters, 0, this.scrambledStateXCenters, 0, 24);
        System.arraycopy(this.tCenters, 0, this.scrambledStateTCenters, 0, 24);
        System.arraycopy(this.centers, 0, this.scrambledStateCenters, 0, 6);
        reorientCube();
        solveCorners();
        solveEdges();
        solveWings();
        solveXCenters();
        solveTCenters();
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

    public void avoidTBreakIns(boolean avoid) {
        this.avoidTBreakIns = avoid;
        this.parseScramble(this.getScramble());
    }


    protected void solveTCenters() {
        if (!tCentersSolved())
            System.arraycopy(solvedTCenters, 0, scrambledStateSolvedTCenters, 0, solvedTCenters.length);
        else
            scrambledStateSolvedTCenters = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
        this.resetCube(true);
        tCenterCycles.clear();
        this.tCenterCycleNum = 0;
        while (!tCentersSolved()) cycleTCenterBuffer();
    }

    private void cycleTCenterBuffer() {
        boolean tCenterCycled = false;

        // If the buffer is solved, replace it with an unsolved corner
        if (solvedTCenters[0]) {
            this.tCenterCycleNum++;
            // First unsolved corner is selected
            for (int i = 0; i < 23 && !tCenterCycled; i++) {
                if (!solvedTCenters[i]) {
                    int centerIndex = i;
                    if (avoidTBreakIns && tCenters[tCenterCubies[i / 4][i % 4]] / 4 == tCenterCubies[0][0] / 4)
                        for (int j = i; j < (i + 4) - (i % 4); j++)
                            if (!solvedTCenters[j] && tCenters[tCenterCubies[j / 4][j % 4]] / 4 != tCenterCubies[0][0] / 4) {
                                centerIndex = j;
                                break;
                            }
                    // Buffer is placed in a... um... buffer
                    int tempXCenter = tCenters[tCenterCubies[0][0]];

                    // Buffer corner is replaced with corner
                    tCenters[tCenterCubies[0][0]] = tCenters[tCenterCubies[centerIndex / 4][centerIndex % 4]];

                    // Corner is replaced with buffer
                    tCenters[tCenterCubies[centerIndex / 4][centerIndex % 4]] = tempXCenter;

                    // Corner cycle is inserted into solution array
                    tCenterCycles.add(tCenterCubies[centerIndex / 4][centerIndex % 4]);
                    tCenterCycled = true;
                }
            }
        }
        // If the buffer is not solved, swap it to the position where the corner belongs
        else {
            for (int i = 0; i < 6 && !tCenterCycled; i++) {
                for (int j = 0; j < 4 && !tCenterCycled; j++) {
                    if (tCenters[tCenterCubies[0][0]] == tCenterCubies[i][j]) {
                        int centerIndex = j;
                        for (int k = ((i * 4) + j) - (j % 4); k < (((i + 1) * 4) + j) - (j % 4); k++)
                            if (!solvedTCenters[k]) {
                                centerIndex = k % 4;
                                break;
                            }
                        if (avoidTBreakIns && tCenters[tCenterCubies[i][centerIndex]] / 4 == tCenterCubies[0][0] / 4)
                            for (int l = (i * 4) + centerIndex; l < (i + 1) * 4; l++)
                                if (!solvedTCenters[l] && tCenters[tCenterCubies[l / 4][l % 4]] / 4 != tCenterCubies[0][0] / 4) {
                                    centerIndex = l % 4;
                                    break;
                                }
                        // Buffer corner is replaced with corner
                        tCenters[tCenterCubies[0][0]] = tCenters[tCenterCubies[i][centerIndex]];

                        // Corner is solved
                        tCenters[tCenterCubies[i][centerIndex]] = tCenterCubies[i][centerIndex];

                        // Corner cycle is inserted into solution array
                        tCenterCycles.add(tCenterCubies[i][centerIndex]);
                        tCenterCycled = true;
                    }
                }
            }
        }
    }

    private boolean tCentersSolved() {
        boolean tCentersSolved = true;

        // Check if tCenters marked as unsolved haven't been solved yet
        for (int i = 0; i < 24; i++) {
            if (i == 0 || !solvedTCenters[i]) {
                // XCenter is solved in correct orientation
                int j = i / 4;
                if (tCenters[tCenterCubies[j][i % 4]] == tCenterCubies[j][i % 4]
                        || tCenters[tCenterCubies[j][i % 4]] == tCenterCubies[j][(i + 1) % 4]
                        || tCenters[tCenterCubies[j][i % 4]] == tCenterCubies[j][(i + 2) % 4]
                        || tCenters[tCenterCubies[j][i % 4]] == tCenterCubies[j][(i + 3) % 4]) {
                    solvedTCenters[i] = true;
                } else {
                    // Found at least one unsolved edge
                    solvedTCenters[i] = false;
                    tCentersSolved = false;
                }
            }
        }
        return tCentersSolved;
    }

    public String getTCenterPairs() {
        String tCenterPairs = "";
        if (tCenterCycles.size() != 0) {
            for (int i = 0; i < tCenterCycles.size(); i++) {
                tCenterPairs += tCenterLettering[tCenterCycles.get(i)];
                if (i % 2 == 1) tCenterPairs += " ";
            }
        } else return "Solved";
        return tCenterPairs.trim();
    }

    @Override
    public String getSolutionPairs(boolean withRotation) {
        return (withRotation ? this.getRotations() + "\n" : "")
                + "TCenters: " + this.getTCenterPairs()
                + "\nXCenters: " + this.getXCenterPairs()
                + "\nWings: " + this.getWingPairs()
                + "\nEdges: " + this.getEdgePairs()
                + "\nCorners: " + this.getCornerPairs();
    }

    @Override
    public String getStatstics() {
        return "Corners: " + this.getCornerLength() + "@" + this.getCornerBreakInNum() + " w/ " + this.getNumPreSolvedCorners() + "-" + this.getNumPreTwistedCorners() + " > " + this.hasCornerParity()
                + "\nEdges: " + this.getEdgeLength() + "@" + this.getEdgeBreakInNum() + " w/ " + this.getNumPreSolvedEdges() + "-" + this.getNumPreFlippedEdges() + " > " + this.hasCornerParity()
                + "\nWings: " + this.getWingLength() + "@" + this.getWingBreakInNum() + " w/ " + this.getNumPreSolvedWings() + " > " + this.hasWingParity()
                + "\nXCenters: " + this.getXCenterLength() + "@" + this.getXCenterBreakInNum() + " w/ " + this.getNumPreSolvedXCenters() + " > " + this.hasXCenterParity()
                + "\nTCenters: " + this.getTCenterLength() + "@" + this.getTCenterBreakInNum() + " w/ " + this.getNumPreSolvedTCenters() + " > " + this.hasTCenterParity();
    }

    @Override
    public String getNoahtation() {
        return "C:" + this.getCornerNoahtation() + " / E:" + this.getEdgeNoahtation() + " / W:" + this.getWingNoahtation() + " / X:" + this.getXCenterNoahtation() + " / T:" + this.getTCenterNoahtation();
    }

    public String getStatString(boolean spaced, boolean newLine) {
        return "C: " + this.getCornerStatString(spaced) + (newLine ? "\n" : " | ") + "E: " + this.getEdgeStatString(spaced) + (newLine ? "\n" : " | ") + "W: " + this.getWingStatString(spaced) + (newLine ? "\n" : " | ") + "X: " + this.getXCenterStatString(spaced) + (newLine ? "\n" : " | ") + "T: " + this.getTCenterStatString(spaced);
    }

    public String getTCenterStatString(boolean spaced) {
        String tCenterStat = this.hasTCenterParity() ? "_" : " ";
        tCenterStat += this.getTCenterLength();
        tCenterStat += this.isTCenterBufferSolved() ? "*" : " ";
        tCenterStat += spaced ? "\t" : " ";
        for (int i = 0; i < 11; i++) tCenterStat += i < this.getTCenterBreakInNum() ? "#" : spaced ? " " : "";
        if (spaced || tCenterStat.endsWith("#")) tCenterStat += " ";
        for (int i = 0; i < 23; i++) tCenterStat += i < this.getNumPreSolvedTCenters() ? "+" : spaced ? " " : "";
        return tCenterStat;
    }

    public String getTCenterStatString() {
        return this.getTCenterStatString(false);
    }

    public boolean hasTCenterParity() {
        return this.tCenterCycles.size() % 2 == 1;
    }

    public boolean isTCenterBufferSolved() {
        return this.scrambledStateSolvedTCenters[0];
    }

    public int getTCenterLength() {
        return this.tCenterCycles.size();
    }

    public int getTCenterBreakInNum() {
        return this.tCenterCycleNum;
    }

    public boolean isTCenterSingleCycle() {
        return this.tCenterCycleNum == 0;
    }

    public int getNumPreSolvedTCenters() {
        int preSolved = 0;
        for (int i = 1; i < scrambledStateSolvedTCenters.length; i++) if (scrambledStateSolvedTCenters[i]) preSolved++;
        return preSolved;
    }

    public String getPreSolvedTCenters() {
        String solvedTCenters = "";
        for (int i = 1; i < scrambledStateSolvedTCenters.length; i++)
            if (scrambledStateSolvedTCenters[i])
                solvedTCenters += (solvedTCenters.length() > 0 ? " " : "") + tCenterPositions[i];
        return solvedTCenters;
    }

    public String getTCenterNoahtation() {
        return this.getTCenterLength() + "";
    }

    public void setTCenterScheme(String scheme) {
        this.setTCenterScheme(scheme.split(""));
    }

    public void setTCenterScheme(String[] scheme) {
        if (scheme.length == 24) this.tCenterLettering = scheme;
    }

    public void setTCenterBuffer(String bufferAsLetter) {
        if (arrayContains(this.tCenterLettering, bufferAsLetter)) {
            int speffz = arrayIndex(this.tCenterLettering, bufferAsLetter);
            int outer = deepArrayOuterIndex(this.tCenterCubies, speffz), inner = deepArrayInnerIndex(this.tCenterCubies, speffz);
            for (int i = 0; i < outer; i++) cycleArrayLeft(this.tCenterCubies);
            for (int i = 0; i < inner; i++) cycleArrayLeft(this.tCenterCubies[0]);
            this.parseScramble(this.getScramble());
        }
    }

    public void setScheme(String type, String[] scheme) {
        switch (type.toLowerCase()) {
            case "tcenter":
                this.setTCenterScheme(scheme);
                break;
            default:
                super.setScheme(type, scheme);
        }
    }

    public String[] getScheme(String type) {
        switch (type.toLowerCase()) {
            case "tcenter":
                return this.tCenterLettering;
            default:
                return super.getScheme(type);
        }
    }
}
