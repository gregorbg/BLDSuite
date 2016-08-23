package com.suushiemaniac.cubing.bld.analyze.cube;

import com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.util.ArrayUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public class FourBldCube extends ThreeBldCube {
    public static boolean isCenterSensitive(String alg) {
        FourBldCube referenceCube = new FourBldCube(alg);
        int[] solvedX = new int[24];
        for (int i = 0; i < 24; i++) solvedX[i] = i;
        return !Arrays.equals(referenceCube.xCenters, solvedX);
    }

    public static boolean solves(PieceType cubicPieceType, String alg, String lpCase) {
        FourBldCube referenceCube = new FourBldCube(cubicPieceType.getReader().parse(alg).inverse().plain().toFormatString());
        String solutionPairs;
        if (!(cubicPieceType instanceof CubicPieceType)) return false;
        switch ((CubicPieceType) cubicPieceType) {
            case CORNER:
                solutionPairs = referenceCube.getCornerPairs();
                break;
            case WING:
                solutionPairs = referenceCube.getWingPairs();
                break;
            case XCENTER:
                solutionPairs = referenceCube.getXCenterPairs();
                break;
            default:
                solutionPairs = "";
                break;
        }
        return solutionPairs.length() > 0 && solutionPairs.equals(lpCase);
    }

    public static CubicPieceType[] getPieceTypeArray() {
        return new CubicPieceType[]{CORNER, WING, XCENTER};
    }

    // Vars
    protected int[] wings = new int[24]; // Position of all 24 wing stickers in the cube
    protected int[] xCenters = new int[24]; // Position of all 24 xCenter stickers in the cube

    // Speffz
    protected String[] wingLettering = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X"};
    protected String[] wingPositions = {"DFr", "UBr", "URf", "UFl", "ULb", "LUf", "LFd", "LDb", "LBu", "FUr", "FRd", "FDl", "FLu", "RUb", "RBd", "RDf", "RFu", "BUl", "BLd", "BDr", "BRu", "DRb", "DBl", "DLf"};
    protected String[] xCenterLettering = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X"};
    protected String[] xCenterPositions = {"Ubl", "Urb", "Ufr", "Ulf", "Lub", "Lfu", "Ldf", "Lbd", "Ful", "Fru", "Fdr", "Fld", "Ruf", "Rbu", "Rdb", "Rfd", "Bur", "Blu", "Bdl", "Brd", "Dfl", "Drf", "Dbr", "Dlb"};

    // Edge and corner cubies
    // Sticker in position [0] or [0][0] respectively of cubie arrays represents the buffer
    protected Integer[] wingCubies = {U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, V, W, X};
    protected boolean[] solvedWings = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
    protected boolean[] scrambledStateSolvedWings = {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    protected int[] scrambledStateWings = new int[24];
    protected int wingCycleNum = 0;
    protected ArrayList<Integer> wingCycles = new ArrayList<>();

    protected Integer[][] xCenterCubies = {{A, B, C, D}, {E, F, G, H}, {I, J, K, L}, {M, N, O, P}, {Q, R, S, T}, {U, V, W, X}};
    protected boolean[] solvedXCenters = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
    protected boolean[] scrambledStateSolvedXCenters = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
    protected int[] scrambledStateXCenters = new int[24];
    protected int xCenterCycleNum = 0;
    protected ArrayList<Integer> xCenterCycles = new ArrayList<>();

    private boolean optimizeCenters = true;
    private boolean avoidXBreakIns = true;

    /**
     * The constructor for creating a new FourBldCube object
     *
     * @param scramble The scramble to be parsed. Supports full WCA notation including rotations and inner slice moves (lowercase)
     */
    public FourBldCube(String scramble) {
        initPermutations();
        this.parseScramble(scramble);
    }

    protected FourBldCube() {
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

                "u", "u'", "u2",
                "f", "f'", "f2",
                "r", "r'", "r2",
                "l", "l'", "l2",
                "b", "b'", "b2",
                "d", "d'", "d2"
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

                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
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
                {Z, Z, Z, Z, Z, Z}
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
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
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
            tempMap.put(EDGE, edgeFacePerms[i]);
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
        Integer[][] wingFacePerms = {
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
                {Z, Z, Z, Z, Z, N, O, Z, Z, R, S, Z, Z, F, G, Z, Z, J, K, Z, W, X, U, V},

                {Z, Z, Z, Z, Z, Z, Z, T, Z, Z, Z, H, Z, Z, Z, L, Z, Z, Z, P, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, L, Z, Z, Z, P, Z, Z, Z, T, Z, Z, Z, H, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, P, Z, Z, Z, T, Z, Z, Z, H, Z, Z, Z, L, Z, Z, Z, Z},
                {Z, O, Z, Z, B, Z, Z, Z, Z, Z, Z, Z, Z, Z, X, Z, Z, Z, Z, Z, Z, Z, Z, E},
                {Z, E, Z, Z, X, Z, Z, Z, Z, Z, Z, Z, Z, Z, B, Z, Z, Z, Z, Z, Z, Z, Z, O},
                {Z, X, Z, Z, O, Z, Z, Z, Z, Z, Z, Z, Z, Z, E, Z, Z, Z, Z, Z, Z, Z, Z, B},
                {S, Z, Z, Z, Z, Z, Z, Z, A, Z, Z, Z, Z, Z, Z, Z, Z, Z, U, Z, I, Z, Z, Z},
                {I, Z, Z, Z, Z, Z, Z, Z, U, Z, Z, Z, Z, Z, Z, Z, Z, Z, A, Z, S, Z, Z, Z},
                {U, Z, Z, Z, Z, Z, Z, Z, S, Z, Z, Z, Z, Z, Z, Z, Z, Z, I, Z, A, Z, Z, Z},
                {Z, Z, K, Z, Z, Z, Z, Z, Z, Z, W, Z, Z, Z, Z, Z, C, Z, Z, Z, Z, Z, Q, Z},
                {Z, Z, Q, Z, Z, Z, Z, Z, Z, Z, C, Z, Z, Z, Z, Z, W, Z, Z, Z, Z, Z, K, Z},
                {Z, Z, W, Z, Z, Z, Z, Z, Z, Z, Q, Z, Z, Z, Z, Z, K, Z, Z, Z, Z, Z, C, Z},
                {Z, Z, Z, G, Z, Z, V, Z, Z, Z, Z, Z, D, Z, Z, Z, Z, Z, Z, Z, Z, M, Z, Z},
                {Z, Z, Z, M, Z, Z, D, Z, Z, Z, Z, Z, V, Z, Z, Z, Z, Z, Z, Z, Z, G, Z, Z},
                {Z, Z, Z, V, Z, Z, M, Z, Z, Z, Z, Z, G, Z, Z, Z, Z, Z, Z, Z, Z, D, Z, Z},
                {Z, Z, Z, Z, Z, J, Z, Z, Z, N, Z, Z, Z, R, Z, Z, Z, F, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, R, Z, Z, Z, F, Z, Z, Z, J, Z, Z, Z, N, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, N, Z, Z, Z, R, Z, Z, Z, F, Z, Z, Z, J, Z, Z, Z, Z, Z, Z},

                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},

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
        Integer[][] xCenterFacePerms = {
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

                {Z, Z, Z, Z, Q, R, Z, Z, E, F, Z, Z, I, J, Z, Z, M, N, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, I, J, Z, Z, M, N, Z, Z, Q, R, Z, Z, E, F, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, M, N, Z, Z, Q, R, Z, Z, E, F, Z, Z, I, J, Z, Z, Z, Z, Z, Z},
                {Z, Z, P, M, Z, C, D, Z, Z, Z, Z, Z, V, Z, Z, U, Z, Z, Z, Z, F, G, Z, Z},
                {Z, Z, F, G, Z, U, V, Z, Z, Z, Z, Z, D, Z, Z, C, Z, Z, Z, Z, P, M, Z, Z},
                {Z, Z, U, V, Z, P, M, Z, Z, Z, Z, Z, G, Z, Z, F, Z, Z, Z, Z, C, D, Z, Z},
                {Z, T, Q, Z, Z, Z, Z, Z, Z, B, C, Z, Z, Z, Z, Z, W, Z, Z, V, Z, J, K, Z},
                {Z, J, K, Z, Z, Z, Z, Z, Z, V, W, Z, Z, Z, Z, Z, C, Z, Z, B, Z, T, Q, Z},
                {Z, V, W, Z, Z, Z, Z, Z, Z, T, Q, Z, Z, Z, Z, Z, K, Z, Z, J, Z, B, C, Z},
                {I, Z, Z, L, Z, Z, Z, Z, U, Z, Z, X, Z, Z, Z, Z, Z, D, A, Z, S, Z, Z, R},
                {S, Z, Z, R, Z, Z, Z, Z, A, Z, Z, D, Z, Z, Z, Z, Z, X, U, Z, I, Z, Z, L},
                {U, Z, Z, X, Z, Z, Z, Z, S, Z, Z, R, Z, Z, Z, Z, Z, L, I, Z, A, Z, Z, D},
                {H, E, Z, Z, X, Z, Z, W, Z, Z, Z, Z, Z, A, B, Z, Z, Z, Z, Z, Z, Z, N, O},
                {N, O, Z, Z, B, Z, Z, A, Z, Z, Z, Z, Z, W, X, Z, Z, Z, Z, Z, Z, Z, H, E},
                {W, X, Z, Z, O, Z, Z, N, Z, Z, Z, Z, Z, H, E, Z, Z, Z, Z, Z, Z, Z, A, B},
                {Z, Z, Z, Z, Z, Z, K, L, Z, Z, O, P, Z, Z, S, T, Z, Z, G, H, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, S, T, Z, Z, G, H, Z, Z, K, L, Z, Z, O, P, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, O, P, Z, Z, S, T, Z, Z, G, H, Z, Z, K, L, Z, Z, Z, Z},

                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},

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
            tempMap.put(WING, wingFacePerms[i]);
            tempMap.put(XCENTER, xCenterFacePerms[i]);
            permutations.put(faceNames[i], tempMap);
        }
    }

    // Sets cube to solved position
    @Override
    protected void resetCube(boolean orientationOnly) {
        super.resetCube(orientationOnly);
        // Corners and edges are initialized in solved position
        for (int i = 0; i < 24; i++) {
            if (!orientationOnly) {
                this.wings[i] = i;
                this.xCenters[i] = i;
            }
            solvedWings[i] = false;
            solvedXCenters[i] = false;
        }
    }

    // Perform a permutation on the cube
    @Override
    protected void permute(String permutation) {
        // Corners are permuted (edges are also permuted but later ignored)
        super.permute(permutation);
        // Wings are permuted
        int[] exchanges = new int[]{Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
        Integer[] perm = permutations.get(permutation).get(WING);
        for (int i = 0; i < 24; i++) if (perm[i] != Z) exchanges[perm[i]] = wings[i];
        for (int i = 0; i < 24; i++) if (exchanges[i] != Z) wings[i] = exchanges[i];
        // XCenters are permuted
        exchanges = new int[]{Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
        perm = permutations.get(permutation).get(XCENTER);
        for (int i = 0; i < 24; i++) if (perm[i] != Z) exchanges[perm[i]] = xCenters[i];
        for (int i = 0; i < 24; i++) if (exchanges[i] != Z) xCenters[i] = exchanges[i];
    }

    // Finds a BLD solution for the cube in its current state
    @Override
    protected void solveCube() {
        System.arraycopy(this.corners, 0, this.scrambledStateCorners, 0, 24);
        System.arraycopy(this.wings, 0, this.scrambledStateWings, 0, 24);
        System.arraycopy(this.xCenters, 0, this.scrambledStateXCenters, 0, 24);
        if (this.optimizeCenters) reorientCube();
        solveCorners();
        solveWings();
        solveXCenters();
    }

    @Override
    protected void reorientCube() {
        this.centerRotations = "";
        String[] possRotations = {"", "y'", "y", "y2", "z y", "z", "z y2", "z y'", "x y2", "x y'", "x y", "x", "z' y'", "z'", "z' y2", "z' y", "x'", "x' y'", "x' y", "x' y2", "x2 y'", "z2", "x2 y", "x2"};
        int[] copyXCenters = new int[24];
        double max = Double.MIN_VALUE;
        int maxIndex = 0;
        for (int i = 0; i < possRotations.length; i++) {
            System.arraycopy(this.xCenters, 0, copyXCenters, 0, 24);
            if (i > 0) for (String rotation : possRotations[i].split("\\s")) {
                int[] exchanges = new int[]{Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
                Integer[] perm = this.permutations.get(rotation).get(XCENTER);
                for (int j = 0; j < 24; j++) if (perm[j] != Z) exchanges[perm[j]] = copyXCenters[j];
                for (int j = 0; j < 24; j++) if (exchanges[j] != Z) copyXCenters[j] = exchanges[j];
            }

            double solvedCenters = 0, solvedBadCenters = 0;
            for (int j = 0; j < copyXCenters.length; j++)
                if (copyXCenters[j] / 4 == j / 4) {
                    solvedCenters++;
                    if (j > 15) solvedBadCenters++;
                }
            solvedCenters /= 24.;
            solvedBadCenters /= 8.;
            double solvedCoeff = (2 * solvedCenters + solvedBadCenters) / 3.;
            if (solvedCoeff > max) {
                max = solvedCoeff;
                maxIndex = i;
            }
        }

        if (maxIndex > 0) {
            String rotation = possRotations[maxIndex];
            this.centerRotations = rotation;
            for (String singleRotation : rotation.split("\\s")) this.permute(singleRotation);
        }
    }

    public void optimizeCenters(boolean optimize) {
        this.optimizeCenters = optimize;
        this.parseScramble(this.getScramble());
    }

    public void avoidXBreakIns(boolean avoid) {
        this.avoidXBreakIns = avoid;
        this.parseScramble(this.getScramble());
    }

    // Solves all 24 wings in the cube
    protected void solveWings() {
        if (!wingsSolved()) System.arraycopy(solvedWings, 0, scrambledStateSolvedWings, 0, solvedWings.length);
        else
            this.scrambledStateSolvedWings = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
        this.resetCube(true);
        wingCycles.clear();

        this.wingCycleNum = 0;
        while (!wingsSolved()) cycleWingBuffer();
    }

    // Replaces the wing buffer with another wing
    private void cycleWingBuffer() {
        boolean wingCycled = false;

        // If the buffer is solved, replace it with an unsolved wing
        if (solvedWings[0]) {
            this.wingCycleNum++;
            int[] wingPref = {1, 10, 12, 2, 4, 18, 20, 21, 23, 9, 5, 6, 7, 13, 14, 15, 19, 11, 3, 8, 16, 17, 22};
            // First unsolved wing is selected
            for (int i = 0; i < 23 && !wingCycled; i++) {
                int j = this.wingCubies[0] == U ? wingPref[i] : i;
                if (!solvedWings[j]) {
                    // Buffer is placed in a... um... buffer
                    int tempWing = wings[wingCubies[0]];

                    // Buffer wing is replaced with wing
                    wings[wingCubies[0]] = wings[wingCubies[j]];

                    // wing is replaced with buffer
                    wings[wingCubies[j]] = tempWing;

                    // wing cycle is inserted into solution array
                    wingCycles.add(wingCubies[j]);
                    wingCycled = true;
                }
            }
        }
        // If the buffer is not solved, swap it to the position where the wing belongs
        else {
            for (int i = 0; i < 24 && !wingCycled; i++) {
                if (wings[wingCubies[0]] == wingCubies[i]) {
                    // Buffer wing is replaced with wing
                    wings[wingCubies[0]] = wings[wingCubies[i]];

                    // wing is solved
                    wings[wingCubies[i]] = wingCubies[i];

                    // wing cycle is inserted into solution array
                    wingCycles.add(wingCubies[i]);
                    wingCycled = true;
                }
            }
        }
    }

    // Checks if all 12 wings are already solved
    // Ignores orientation
    private boolean wingsSolved() {
        boolean wingsSolved = true;

        // Check if corners marked as unsolved haven't been solved yet
        for (int i = 0; i < 24; i++) {
            if (i == 0 || !solvedWings[i]) {
                // wing is solved in correct orientation
                if (wings[wingCubies[i]] == wingCubies[i]) solvedWings[i] = true;
                else {
                    // Found at least one unsolved wing
                    solvedWings[i] = false;
                    wingsSolved = false;
                }
            }
        }
        return wingsSolved;
    }

    protected void solveXCenters() {
        if (!xCentersSolved())
            System.arraycopy(solvedXCenters, 0, scrambledStateSolvedXCenters, 0, solvedXCenters.length);
        else
            scrambledStateSolvedXCenters = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
        this.resetCube(true);
        xCenterCycles.clear();
        this.xCenterCycleNum = 0;
        while (!xCentersSolved()) cycleCenterBuffer();
    }

    private void cycleCenterBuffer() {
        boolean centerCycled = false;

        // If the buffer is solved, replace it with an unsolved corner
        if (solvedXCenters[0]) {
            this.xCenterCycleNum++;
            // First unsolved corner is selected
            for (int i = 0; i < 23 && !centerCycled; i++) {
                if (!solvedXCenters[i]) {
                    int centerIndex = i;
                    if (avoidXBreakIns && xCenters[xCenterCubies[i / 4][i % 4]] / 4 == xCenterCubies[0][0] / 4)
                        for (int j = i; j < (i + 4) - (i % 4); j++)
                            if (!solvedXCenters[j] && xCenters[xCenterCubies[j / 4][j % 4]] / 4 != xCenterCubies[0][0] / 4) {
                                centerIndex = j;
                                break;
                            }
                    // Buffer is placed in a... um... buffer
                    int tempXCenter = xCenters[xCenterCubies[0][0]];

                    // Buffer corner is replaced with corner
                    xCenters[xCenterCubies[0][0]] = xCenters[xCenterCubies[centerIndex / 4][centerIndex % 4]];

                    // Corner is replaced with buffer
                    xCenters[xCenterCubies[centerIndex / 4][centerIndex % 4]] = tempXCenter;

                    // Corner cycle is inserted into solution array
                    xCenterCycles.add(xCenterCubies[centerIndex / 4][centerIndex % 4]);
                    centerCycled = true;
                }
            }
        }
        // If the buffer is not solved, swap it to the position where the corner belongs
        else for (int i = 0; i < 6 && !centerCycled; i++)
            for (int j = 0; j < 4 && !centerCycled; j++)
                if (xCenters[xCenterCubies[0][0]] == xCenterCubies[i][j]) {
                    int centerIndex = j;
                    for (int k = (i * 4 + j) - (j % 4); k < ((i + 1) * 4 + j) - (j % 4); k++)
                        if (!solvedXCenters[k]) {
                            centerIndex = k % 4;
                            break;
                        }
                    if (avoidXBreakIns && xCenters[xCenterCubies[i][centerIndex]] / 4 == xCenterCubies[0][0] / 4)
                        for (int l = (i * 4) + centerIndex; l < (i + 1) * 4; l++)
                            if (!solvedXCenters[l] && xCenters[xCenterCubies[l / 4][l % 4]] / 4 != xCenterCubies[0][0] / 4) {
                                centerIndex = l % 4;
                                break;
                            }
                    // Buffer corner is replaced with corner
                    xCenters[xCenterCubies[0][0]] = xCenters[xCenterCubies[i][centerIndex]];

                    // Corner is solved
                    xCenters[xCenterCubies[i][centerIndex]] = xCenterCubies[i][centerIndex];

                    // Corner cycle is inserted into solution array
                    xCenterCycles.add(xCenterCubies[i][centerIndex]);
                    centerCycled = true;
                }
    }

    private boolean xCentersSolved() {
        boolean xCentersSolved = true;

        // Check if xCenters marked as unsolved haven't been solved yet
        for (int i = 0; i < 24; i++) {
            if (i == 0 || !solvedXCenters[i]) {
                // XCenter is solved in correct orientation
                int j = i / 4;
                if (xCenters[xCenterCubies[j][i % 4]] == xCenterCubies[j][i % 4]
                        || xCenters[xCenterCubies[j][i % 4]] == xCenterCubies[j][(i + 1) % 4]
                        || xCenters[xCenterCubies[j][i % 4]] == xCenterCubies[j][(i + 2) % 4]
                        || xCenters[xCenterCubies[j][i % 4]] == xCenterCubies[j][(i + 3) % 4]) {
                    solvedXCenters[i] = true;
                } else {
                    // Found at least one unsolved wing
                    solvedXCenters[i] = false;
                    xCentersSolved = false;
                }
            }
        }
        return xCentersSolved;
    }

    public String getWingPairs() {
        String wingPairs = "";
        if (wingCycles.size() != 0) {
            for (int i = 0; i < wingCycles.size(); i++) {
                wingPairs += wingLettering[wingCycles.get(i)];
                if (i % 2 == 1) wingPairs += " ";
            }
        } else return "Solved";
        return wingPairs.trim();
    }

    public String getXCenterPairs() {
        String xCenterPairs = "";
        if (xCenterCycles.size() != 0) {
            for (int i = 0; i < xCenterCycles.size(); i++) {
                xCenterPairs += xCenterLettering[xCenterCycles.get(i)];
                if (i % 2 == 1) xCenterPairs += " ";
            }
        } else return "Solved";
        return xCenterPairs.trim();
    }

    @Override
    public String getSolutionPairs(boolean withRotation) {
        return (withRotation ? this.getRotations() + "\n" : "")
                + "XCenters: " + this.getXCenterPairs()
                + "\nWings: " + this.getWingPairs()
                + "\nCorners: " + this.getCornerPairs();
    }

    @Override
    public String getStatstics() {
        return "Corners: " + this.getCornerLength() + "@" + this.getCornerBreakInNum() + " w/ " + this.getNumPreSolvedCorners() + "-" + this.getNumPreTwistedCorners() + " > " + this.hasCornerParity()
                + "\nWings: " + this.getWingLength() + "@" + this.getWingBreakInNum() + " w/ " + this.getNumPreSolvedWings() + " > " + this.hasWingParity()
                + "\nXCenters: " + this.getXCenterLength() + "@" + this.getXCenterBreakInNum() + " w/ " + this.getNumPreSolvedXCenters() + " > " + this.hasXCenterParity();
    }

    @Override
    public String getNoahtation() {
        return "C:" + this.getCornerNoahtation() + " / W:" + this.getWingNoahtation() + " / X:" + this.getXCenterNoahtation();
    }

    @Override
    public String getStatString(boolean spaced, boolean newLine) {
        return "C: " + this.getCornerStatString(spaced) + (newLine ? "\n" : " | ") + "W: " + this.getWingStatString(spaced) + (newLine ? "\n" : " | ") + "X: " + this.getXCenterStatString(spaced);
    }

    @Override
    public String getPuzzleString() {
        return "444";
    }

    public String getWingStatString(boolean spaced) {
        String wingStat = this.hasWingParity() ? "_" : " ";
        wingStat += this.getWingLength();
        wingStat += this.isWingBufferSolved() ? "*" : " ";
        wingStat += spaced ? "\t" : " ";
        for (int i = 0; i < 11; i++) wingStat += i < this.getWingBreakInNum() ? "#" : spaced ? " " : "";
        if (spaced || wingStat.endsWith("#")) wingStat += " ";
        for (int i = 0; i < 23; i++) wingStat += i < this.getNumPreSolvedWings() ? "+" : spaced ? " " : "";
        return wingStat;
    }

    public String getWingStatString() {
        return this.getWingStatString(false);
    }

    public String getXCenterStatString(boolean spaced) {
        String xCenterStat = this.hasXCenterParity() ? "_" : " ";
        xCenterStat += this.getXCenterLength();
        xCenterStat += this.isXCenterBufferSolved() ? "*" : " ";
        xCenterStat += spaced ? "\t" : " ";
        for (int i = 0; i < 11; i++) xCenterStat += i < this.getXCenterBreakInNum() ? "#" : spaced ? " " : "";
        if (spaced || xCenterStat.endsWith("#")) xCenterStat += " ";
        for (int i = 0; i < 23; i++) xCenterStat += i < this.getNumPreSolvedXCenters() ? "+" : spaced ? " " : "";
        return xCenterStat;
    }

    public String getXCenterStatString() {
        return this.getXCenterStatString(false);
    }

    public boolean hasWingParity() {
        return this.wingCycles.size() % 2 == 1;
    }

    public boolean isWingBufferSolved() {
        return this.scrambledStateSolvedWings[0];
    }

    public int getWingLength() {
        return this.wingCycles.size();
    }

    public int getWingBreakInNum() {
        return this.wingCycleNum;
    }

    public boolean isWingSingleCycle() {
        return this.wingCycleNum == 0;
    }

    public int getNumPreSolvedWings() {
        int preSolved = 0;
        for (int i = 1; i < scrambledStateSolvedWings.length; i++) if (scrambledStateSolvedWings[i]) preSolved++;
        return preSolved;
    }

    public String getPreSolvedWings() {
        String solvedEdges = "";
        for (int i = 1; i < scrambledStateSolvedWings.length; i++)
            if (scrambledStateSolvedWings[i])
                solvedEdges += (solvedEdges.length() > 0 ? " " : "") + wingPositions[i];
        return solvedEdges;
    }

    public String getWingNoahtation() {
        return this.getWingLength() + "";
    }

    public boolean hasXCenterParity() {
        return this.xCenterCycles.size() % 2 == 1;
    }

    public boolean isXCenterBufferSolved() {
        return this.scrambledStateSolvedXCenters[0];
    }

    public int getXCenterLength() {
        return this.xCenterCycles.size();
    }

    public int getXCenterBreakInNum() {
        return this.xCenterCycleNum;
    }

    public boolean isXCenterSingleCycle() {
        return this.xCenterCycleNum == 0;
    }

    public int getNumPreSolvedXCenters() {
        int preSolved = 0;
        for (int i = 1; i < scrambledStateSolvedXCenters.length; i++) if (scrambledStateSolvedXCenters[i]) preSolved++;
        return preSolved;
    }

    public String getPreSolvedXCenters() {
        String solvedXCenters = "";
        for (int i = 1; i < scrambledStateSolvedXCenters.length; i++)
            if (scrambledStateSolvedXCenters[i])
                solvedXCenters += (solvedXCenters.length() > 0 ? " " : "") + xCenterPositions[i];
        return solvedXCenters;
    }

    public String getXCenterNoahtation() {
        return this.getXCenterLength() + "";
    }

    public void setWingScheme(String scheme) {
        this.setWingScheme(scheme.split(""));
    }

    public void setWingScheme(String[] scheme) {
        if (scheme.length == 24) this.wingLettering = scheme;
    }

    public void setXCenterScheme(String scheme) {
        this.setXCenterScheme(scheme.split(""));
    }

    public void setXCenterScheme(String[] scheme) {
        if (scheme.length == 24) this.xCenterLettering = scheme;
    }

    public void setWingBuffer(String bufferAsLetter) {
        if (ArrayUtil.contains(this.wingLettering, bufferAsLetter)) {
            int speffz = ArrayUtil.index(this.wingLettering, bufferAsLetter);
            int index = ArrayUtil.index(this.wingCubies, speffz);
            for (int i = 0; i < index; i++) ArrayUtil.cycleLeft(this.wingCubies);
            this.parseScramble(this.getScramble());
        }
    }

    public void setXCenterBuffer(String bufferAsLetter) {
        if (ArrayUtil.contains(this.xCenterLettering, bufferAsLetter)) {
            int speffz = ArrayUtil.index(this.xCenterLettering, bufferAsLetter);
            int outer = ArrayUtil.deepOuterIndex(this.xCenterCubies, speffz), inner = ArrayUtil.deepInnerIndex(this.xCenterCubies, speffz);
            for (int i = 0; i < outer; i++) ArrayUtil.cycleLeft(this.xCenterCubies);
            for (int i = 0; i < inner; i++) ArrayUtil.cycleLeft(this.xCenterCubies[0]);
            this.parseScramble(this.getScramble());
        }
    }

    public void setScheme(String type, String[] scheme) {
        switch (type.toLowerCase()) {
            case "xcenter":
                this.setXCenterScheme(scheme);
                break;
            case "wing":
                this.setWingScheme(scheme);
                break;
            default:
                super.setScheme(type, scheme);
        }
    }

    public String[] getScheme(String type) {
        switch (type.toLowerCase()) {
            case "xcenter":
                return this.xCenterLettering;
            case "wing":
                return this.wingLettering;
            default:
                return super.getScheme(type);
        }
    }
}