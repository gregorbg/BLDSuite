package com.suushiemaniac.cubing.bld.analyze.cube;

import com.suushiemaniac.cubing.bld.enumeration.CubicPieceType;
import com.suushiemaniac.cubing.bld.enumeration.PieceType;

import java.util.ArrayList;
import java.util.HashMap;

import static com.suushiemaniac.cubing.bld.enumeration.CubicPieceType.*;

public class SixBldCube extends FiveBldCube {
    protected int[] innerXCenters = new int[24];
    protected int[] leftObliques = new int[24];
    protected int[] rightObliques = new int[24];
    protected int[] innerWings = new int[24];

    protected String[] innerXCenterLettering = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X"};
    protected String[] innerXCenterPositions = {"Ubl", "Urb", "Ufr", "Ulf", "Lub", "Lfu", "Ldf", "Lbd", "Ful", "Fru", "Fdr", "Fld", "Ruf", "Rbu", "Rdb", "Rfd", "Bur", "Blu", "Bdl", "Brd", "Dfl", "Drf", "Dbr", "Dlb"};
    protected String[] leftObliqueLettering = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X"};
    protected String[] leftObliquePositions = {"Ubl", "Urb", "Ufr", "Ulf", "Lub", "Lfu", "Ldf", "Lbd", "Ful", "Fru", "Fdr", "Fld", "Ruf", "Rbu", "Rdb", "Rfd", "Bur", "Blu", "Bdl", "Brd", "Dfl", "Drf", "Dbr", "Dlb"};
    protected String[] rightObliqueLettering = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X"};
    protected String[] rightObliquePositions = {"Ubr", "Urf", "Ufl", "Ulb", "Luf", "Lfd", "Ldb", "Lbu", "Fur", "Frd", "Fdl", "Flu", "Rub", "Rbd", "Rdf", "Rfu", "Bul", "Bld", "Bdr", "Bru", "Dfr", "Drb", "Dbl", "Dlf"};
    protected String[] innerWingLettering = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X"};
    protected String[] innerWingPositions = {"DFr", "UBr", "URf", "UFl", "ULb", "LUf", "LFd", "LDb", "LBu", "FUr", "FRd", "FDl", "FLu", "RUb", "RBd", "RDf", "RFu", "BUl", "BLd", "BDr", "BRu", "DRb", "DBl", "DLf"};

    protected Integer[] innerWingCubies = {U, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, V, W, X};
    protected boolean[] solvedInnerWings = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
    protected boolean[] scrambledStateSolvedInnerWings = {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    protected int[] scrambledStateInnerWings = new int[24];
    protected int innerWingCycleNum = 0;
    protected ArrayList<Integer> innerWingCycles = new ArrayList<>();

    protected Integer[][] innerXCenterCubies = {{A, B, C, D}, {E, F, G, H}, {I, J, K, L}, {M, N, O, P}, {Q, R, S, T}, {U, V, W, X}};
    protected boolean[] solvedInnerXCenters = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
    protected boolean[] scrambledStateSolvedInnerXCenters = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
    protected int[] scrambledStateInnerXCenters = new int[24];
    protected int innerXCenterCycleNum = 0;
    protected ArrayList<Integer> innerXCenterCycles = new ArrayList<>();

    protected Integer[][] rightObliqueCubies = {{U, V, W, X}, {A, B, C, D}, {E, F, G, H}, {I, J, K, L}, {M, N, O, P}, {Q, R, S, T}};
    protected boolean[] solvedRightObliques = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
    protected boolean[] scrambledStateSolvedRightObliques = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
    protected int[] scrambledStateRightObliques = new int[24];
    protected int rightObliqueCycleNum = 0;
    protected ArrayList<Integer> rightObliqueCycles = new ArrayList<>();

    protected Integer[][] leftObliqueCubies = {{U, V, W, X}, {A, B, C, D}, {E, F, G, H}, {I, J, K, L}, {M, N, O, P}, {Q, R, S, T}};
    protected boolean[] solvedLeftObliques = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
    protected boolean[] scrambledStateSolvedLeftObliques = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
    protected int[] scrambledStateLeftObliques = new int[24];
    protected int leftObliqueCycleNum = 0;
    protected ArrayList<Integer> leftObliqueCycles = new ArrayList<>();

    private boolean optimizeCenters = true;
    private boolean avoidInnerXBreakIns = true;
    private boolean avoidRightObliqueBreakIns = true;
    private boolean avoidLeftObliqueBreakIns = true;

    public SixBldCube(String scramble) {
        initPermutations();
        this.parseScramble(scramble);
    }

    protected SixBldCube() {
    }

    protected void initPermutations() {
        super.initPermutations();
        String[] faceNames = {
                "3Uw", "3Uw'", "3Uw2",
                "3Fw", "3Fw'", "3Fw2",
                "3Rw", "3Rw'", "3Rw2",
                "3Lw", "3Lw'", "3Lw2",
                "3Bw", "3Bw'", "3Bw2",
                "3Dw", "3Dw'", "3Dw2",

                "2u", "2u'", "2u2",
                "2f", "2f'", "2f2",
                "2r", "2r'", "2r2",
                "2l", "2l'", "2l2",
                "2b", "2b'", "2b2",
                "2d", "2d'", "2d2"
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
                {Z, Z, Z, Z, Z, N, O, Z, Z, R, S, Z, Z, F, G, Z, Z, J, K, Z, W, X, U, V},

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
        Integer[][] tCenterFacePerms = {
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
        for (int i = 0; i < faceNames.length; i++) {
            HashMap<PieceType, Integer[]> tempMap = permutations.get(faceNames[i]);
            if (tempMap == null) tempMap = new HashMap<>();
            tempMap.put(CORNER, cornerFacePerms[i]);
            tempMap.put(EDGE, edgeFacePerms[i]);
            tempMap.put(WING, wingFacePerms[i]);
            tempMap.put(XCENTER, xCenterFacePerms[i]);
            tempMap.put(TCENTER, tCenterFacePerms[i]);
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

                "2u", "2u'", "2u2",
                "2f", "2f'", "2f2",
                "2r", "2r'", "2r2",
                "2l", "2l'", "2l2",
                "2b", "2b'", "2b2",
                "2d", "2d'", "2d2",

                "M", "M'", "M2",
                "S", "S'", "S2",
                "E", "E'", "E2",

                "x", "x'", "x2",
                "y", "y'", "y2",
                "z", "z'", "z2"
        };
        Integer[][] innerWingFacePerms = {
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
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},

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
        Integer[][] innerXCenterFacePerms = {
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
                {Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z},

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
            tempMap.put(INNERWING, innerWingFacePerms[i]);
            tempMap.put(INNERXCENTER, innerXCenterFacePerms[i]);
            permutations.put(faceNames[i], tempMap);
        }
        initPermutationsSecondChain();
    }

    private void initPermutationsSecondChain() {
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

                "2u", "2u'", "2u2",
                "2f", "2f'", "2f2",
                "2r", "2r'", "2r2",
                "2l", "2l'", "2l2",
                "2b", "2b'", "2b2",
                "2d", "2d'", "2d2",

                "M", "M'", "M2",
                "S", "S'", "S2",
                "E", "E'", "E2",

                "x", "x'", "x2",
                "y", "y'", "y2",
                "z", "z'", "z2"
        };
        Integer[][] rightObliqueFacePerms = {
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
        Integer[][] leftObliqueFacePerms = {
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

                {Z, Z, Z, Z, Z, R, Z, Z, Z, F, Z, Z, Z, J, Z, Z, Z, N, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, J, Z, Z, Z, N, Z, Z, Z, R, Z, Z, Z, F, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, N, Z, Z, Z, R, Z, Z, Z, F, Z, Z, Z, J, Z, Z, Z, Z, Z, Z},
                {Z, Z, Z, M, Z, Z, D, Z, Z, Z, Z, Z, V, Z, Z, Z, Z, Z, Z, Z, Z, G, Z, Z},
                {Z, Z, Z, G, Z, Z, V, Z, Z, Z, Z, Z, D, Z, Z, Z, Z, Z, Z, Z, Z, M, Z, Z},
                {Z, Z, Z, V, Z, Z, M, Z, Z, Z, Z, Z, G, Z, Z, Z, Z, Z, Z, Z, Z, D, Z, Z},
                {Z, Z, Q, Z, Z, Z, Z, Z, Z, Z, C, Z, Z, Z, Z, Z, W, Z, Z, Z, Z, Z, K, Z},
                {Z, Z, K, Z, Z, Z, Z, Z, Z, Z, W, Z, Z, Z, Z, Z, C, Z, Z, Z, Z, Z, Q, Z},
                {Z, Z, W, Z, Z, Z, Z, Z, Z, Z, Q, Z, Z, Z, Z, Z, K, Z, Z, Z, Z, Z, C, Z},
                {I, Z, Z, Z, Z, Z, Z, Z, U, Z, Z, Z, Z, Z, Z, Z, Z, Z, A, Z, S, Z, Z, Z},
                {S, Z, Z, Z, Z, Z, Z, Z, A, Z, Z, Z, Z, Z, Z, Z, Z, Z, U, Z, I, Z, Z, Z},
                {U, Z, Z, Z, Z, Z, Z, Z, S, Z, Z, Z, Z, Z, Z, Z, Z, Z, I, Z, A, Z, Z, Z},
                {Z, E, Z, Z, X, Z, Z, Z, Z, Z, Z, Z, Z, Z, B, Z, Z, Z, Z, Z, Z, Z, Z, O},
                {Z, O, Z, Z, B, Z, Z, Z, Z, Z, Z, Z, Z, Z, X, Z, Z, Z, Z, Z, Z, Z, Z, E},
                {Z, X, Z, Z, O, Z, Z, Z, Z, Z, Z, Z, Z, Z, E, Z, Z, Z, Z, Z, Z, Z, Z, B},
                {Z, Z, Z, Z, Z, Z, Z, L, Z, Z, Z, P, Z, Z, Z, T, Z, Z, Z, H, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, T, Z, Z, Z, H, Z, Z, Z, L, Z, Z, Z, P, Z, Z, Z, Z},
                {Z, Z, Z, Z, Z, Z, Z, P, Z, Z, Z, T, Z, Z, Z, H, Z, Z, Z, L, Z, Z, Z, Z},

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
            tempMap.put(RIGHTOBLIQUE, rightObliqueFacePerms[i]);
            tempMap.put(LEFTOBLIQUE, leftObliqueFacePerms[i]);
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
                this.innerWings[i] = i;
                this.innerXCenters[i] = i;
                this.leftObliques[i] = i;
                this.rightObliques[i] = i;
            }
            solvedInnerWings[i] = false;
            solvedInnerXCenters[i] = false;
            solvedLeftObliques[i] = false;
            solvedRightObliques[i] = false;
        }
    }

    // Perform a permutation on the cube
    @Override
    protected void permute(String permutation) {
        super.permute(permutation);
        // Inner wings are permuted
        int[] exchanges = new int[]{Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
        Integer[] perm = permutations.get(permutation).get(INNERWING);
        for (int i = 0; i < 24; i++) if (perm[i] != Z) exchanges[perm[i]] = innerWings[i];
        for (int i = 0; i < 24; i++) if (exchanges[i] != Z) innerWings[i] = exchanges[i];
        // Inner XCenters are permuted
        exchanges = new int[]{Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
        perm = permutations.get(permutation).get(INNERXCENTER);
        for (int i = 0; i < 24; i++) if (perm[i] != Z) exchanges[perm[i]] = innerXCenters[i];
        for (int i = 0; i < 24; i++) if (exchanges[i] != Z) innerXCenters[i] = exchanges[i];
        // Right obliques are permuted
        exchanges = new int[]{Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
        perm = permutations.get(permutation).get(RIGHTOBLIQUE);
        for (int i = 0; i < 24; i++) if (perm[i] != Z) exchanges[perm[i]] = rightObliques[i];
        for (int i = 0; i < 24; i++) if (exchanges[i] != Z) rightObliques[i] = exchanges[i];
        // Left obliques are permuted
        exchanges = new int[]{Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
        perm = permutations.get(permutation).get(LEFTOBLIQUE);
        for (int i = 0; i < 24; i++) if (perm[i] != Z) exchanges[perm[i]] = leftObliques[i];
        for (int i = 0; i < 24; i++) if (exchanges[i] != Z) leftObliques[i] = exchanges[i];
    }

    // Finds a BLD solution for the cube in its current state
    @Override
    protected void solveCube() {
        System.arraycopy(this.corners, 0, this.scrambledStateCorners, 0, 24);
        System.arraycopy(this.wings, 0, this.scrambledStateWings, 0, 24);
        System.arraycopy(this.innerWings, 0, this.scrambledStateInnerWings, 0, 24);
        System.arraycopy(this.xCenters, 0, this.scrambledStateXCenters, 0, 24);
        System.arraycopy(this.innerXCenters, 0, this.scrambledStateInnerXCenters, 0, 24);
        System.arraycopy(this.rightObliques, 0, this.scrambledStateRightObliques, 0, 24);
        System.arraycopy(this.leftObliques, 0, this.scrambledStateLeftObliques, 0, 24);
        if (this.optimizeCenters) reorientCube();
        solveCorners();
        solveWings();
        solveInnerWings();
        solveXCenters();
        solveInnerXCenters();
        solveRightObliques();
        solveLeftObliques();
    }

    @Override
    protected void reorientCube() {
        this.centerRotations = "";
        String[] possRotations = {"", "y'", "y", "y2", "z y", "z", "z y2", "z y'", "x y2", "x y'", "x y", "x", "z' y'", "z'", "z' y2", "z' y", "x'", "x' y'", "x' y", "x' y2", "x2 y'", "z2", "x2 y", "x2"};
        double max = Double.MIN_VALUE;
        int maxIndex = 0;
        int[] copyXCenters = new int[24], copyInnerXCenters = new int[24], copyRightObliques = new int[24], copyLeftObliques = new int[24];
        for (int i = 0; i < possRotations.length; i++) {
            System.arraycopy(this.xCenters, 0, copyXCenters, 0, 24);
            System.arraycopy(this.innerXCenters, 0, copyInnerXCenters, 0, 24);
            System.arraycopy(this.rightObliques, 0, copyRightObliques, 0, 24);
            System.arraycopy(this.leftObliques, 0, copyLeftObliques, 0, 24);
            if (i > 0) for (String rotation : possRotations[i].split("\\s")) {
                int[] exchanges = new int[]{Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
                Integer[] perm = this.permutations.get(rotation).get(XCENTER);
                for (int j = 0; j < 24; j++) if (perm[j] != Z) exchanges[perm[j]] = copyXCenters[j];
                for (int j = 0; j < 24; j++) if (exchanges[j] != Z) copyXCenters[j] = exchanges[j];
                exchanges = new int[]{Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
                perm = this.permutations.get(rotation).get(INNERXCENTER);
                for (int j = 0; j < 24; j++) if (perm[j] != Z) exchanges[perm[j]] = copyInnerXCenters[j];
                for (int j = 0; j < 24; j++) if (exchanges[j] != Z) copyInnerXCenters[j] = exchanges[j];
                exchanges = new int[]{Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
                perm = this.permutations.get(rotation).get(RIGHTOBLIQUE);
                for (int j = 0; j < 24; j++) if (perm[j] != Z) exchanges[perm[j]] = copyLeftObliques[j];
                for (int j = 0; j < 24; j++) if (exchanges[j] != Z) copyLeftObliques[j] = exchanges[j];
                exchanges = new int[]{Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
                perm = this.permutations.get(rotation).get(LEFTOBLIQUE);
                for (int j = 0; j < 24; j++) if (perm[j] != Z) exchanges[perm[j]] = copyRightObliques[j];
                for (int j = 0; j < 24; j++) if (exchanges[j] != Z) copyRightObliques[j] = exchanges[j];
            }

            double solvedCenters = 0, solvedBadCenters = 0;
            for (int j = 0; j < copyXCenters.length; j++) {
                if (copyXCenters[j] / 4 == j / 4) {
                    solvedCenters++;
                    if (j > 15) solvedBadCenters++;
                }
                if (copyInnerXCenters[j] / 4 == j / 4) {
                    solvedCenters++;
                    if (j > 15) solvedBadCenters++;
                }
                if (copyRightObliques[j] / 4 == j / 4) {
                    solvedCenters++;
                    if (j > 15) solvedBadCenters++;
                }
                if (copyLeftObliques[j] / 4 == j / 4) {
                    solvedCenters++;
                    if (j > 15) solvedBadCenters++;
                }
            }
            solvedCenters /= 96.;
            solvedBadCenters /= 32.;
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

    public void avoidInnerXBreakIns(boolean avoid) {
        this.avoidInnerXBreakIns = avoid;
        this.parseScramble(this.getScramble());
    }

    public void avoidRightObliqueBreakIns(boolean avoid) {
        this.avoidRightObliqueBreakIns = avoid;
        this.parseScramble(this.getScramble());
    }

    public void avoidLeftObliqueBreakIns(boolean avoid) {
        this.avoidLeftObliqueBreakIns = avoid;
        this.parseScramble(this.getScramble());
    }

    // Solves all 24 wings in the cube
    protected void solveInnerWings() {
        if (!innerWingsSolved())
            System.arraycopy(solvedInnerWings, 0, scrambledStateSolvedInnerWings, 0, solvedInnerWings.length);
        else
            this.scrambledStateSolvedInnerWings = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
        this.resetCube(true);
        innerWingCycles.clear();

        this.innerWingCycleNum = 0;
        while (!innerWingsSolved()) cycleInnerWingBuffer();
    }

    // Replaces the wing buffer with another wing
    private void cycleInnerWingBuffer() {
        boolean innerWingCycled = false;

        // If the buffer is solved, replace it with an unsolved wing
        if (solvedInnerWings[0]) {
            this.innerWingCycleNum++;
            int[] wingPref = {1, 10, 12, 2, 4, 18, 20, 21, 23, 9, 5, 6, 7, 13, 14, 15, 19, 11, 3, 8, 16, 17, 22};
            // First unsolved wing is selected
            for (int i = 0; i < 23 && !innerWingCycled; i++) {
                int j = this.innerWingCubies[0] == U ? wingPref[i] : i;
                if (!solvedInnerWings[j]) {
                    // Buffer is placed in a... um... buffer
                    int tempWing = innerWings[innerWingCubies[0]];

                    // Buffer wing is replaced with wing
                    innerWings[innerWingCubies[0]] = innerWings[innerWingCubies[j]];

                    // wing is replaced with buffer
                    innerWings[innerWingCubies[j]] = tempWing;

                    // wing cycle is inserted into solution array
                    innerWingCycles.add(innerWingCubies[j]);
                    innerWingCycled = true;
                }
            }
        }
        // If the buffer is not solved, swap it to the position where the wing belongs
        else {
            for (int i = 0; i < 24 && !innerWingCycled; i++) {
                if (innerWings[innerWingCubies[0]] == innerWingCubies[i]) {
                    // Buffer wing is replaced with wing
                    innerWings[innerWingCubies[0]] = innerWings[innerWingCubies[i]];

                    // wing is solved
                    innerWings[innerWingCubies[i]] = innerWingCubies[i];

                    // wing cycle is inserted into solution array
                    innerWingCycles.add(innerWingCubies[i]);
                    innerWingCycled = true;
                }
            }
        }
    }

    // Checks if all 12 wings are already solved
    // Ignores orientation
    private boolean innerWingsSolved() {
        boolean innerWingsSolved = true;

        // Check if corners marked as unsolved haven't been solved yet
        for (int i = 0; i < 24; i++) {
            if (i == 0 || !solvedInnerWings[i]) {
                // wing is solved in correct orientation
                if (innerWings[innerWingCubies[i]] == innerWingCubies[i]) solvedInnerWings[i] = true;
                else {
                    // Found at least one unsolved wing
                    solvedInnerWings[i] = false;
                    innerWingsSolved = false;
                }
            }
        }
        return innerWingsSolved;
    }

    protected void solveInnerXCenters() {
        if (!innerXCentersSolved())
            System.arraycopy(solvedInnerXCenters, 0, scrambledStateSolvedInnerXCenters, 0, solvedInnerXCenters.length);
        else
            scrambledStateSolvedInnerXCenters = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
        this.resetCube(true);
        innerXCenterCycles.clear();
        this.innerXCenterCycleNum = 0;
        while (!innerXCentersSolved()) cycleInnerXCenterBuffer();
    }

    private void cycleInnerXCenterBuffer() {
        boolean innerXCenterCycled = false;

        // If the buffer is solved, replace it with an unsolved corner
        if (solvedInnerXCenters[0]) {
            this.innerXCenterCycleNum++;
            // First unsolved corner is selected
            for (int i = 0; i < 23 && !innerXCenterCycled; i++) {
                if (!solvedInnerXCenters[i]) {
                    int centerIndex = i;
                    if (avoidInnerXBreakIns && innerXCenters[innerXCenterCubies[i / 4][i % 4]] / 4 == innerXCenterCubies[0][0] / 4)
                        for (int j = i; j < (i + 4) - (i % 4); j++)
                            if (!solvedInnerXCenters[j] && innerXCenters[innerXCenterCubies[j / 4][j % 4]] / 4 != innerXCenterCubies[0][0] / 4) {
                                centerIndex = j;
                                break;
                            }
                    // Buffer is placed in a... um... buffer
                    int tempXCenter = innerXCenters[innerXCenterCubies[0][0]];

                    // Buffer corner is replaced with corner
                    innerXCenters[innerXCenterCubies[0][0]] = innerXCenters[innerXCenterCubies[centerIndex / 4][centerIndex % 4]];

                    // Corner is replaced with buffer
                    innerXCenters[innerXCenterCubies[centerIndex / 4][centerIndex % 4]] = tempXCenter;

                    // Corner cycle is inserted into solution array
                    innerXCenterCycles.add(innerXCenterCubies[centerIndex / 4][centerIndex % 4]);
                    innerXCenterCycled = true;
                }
            }
        }
        // If the buffer is not solved, swap it to the position where the corner belongs
        else {
            for (int i = 0; i < 6 && !innerXCenterCycled; i++) {
                for (int j = 0; j < 4 && !innerXCenterCycled; j++) {
                    if (innerXCenters[innerXCenterCubies[0][0]] == innerXCenterCubies[i][j]) {
                        int centerIndex = j;
                        for (int k = ((i * 4) + j) - (j % 4); k < (((i + 1) * 4) + j) - (j % 4); k++)
                            if (!solvedInnerXCenters[k]) {
                                centerIndex = k % 4;
                                break;
                            }
                        if (avoidInnerXBreakIns && innerXCenters[innerXCenterCubies[i][centerIndex]] / 4 == innerXCenterCubies[0][0] / 4)
                            for (int l = (i * 4) + centerIndex; l < (i + 1) * 4; l++)
                                if (!solvedInnerXCenters[l] && innerXCenters[innerXCenterCubies[l / 4][l % 4]] / 4 != innerXCenterCubies[0][0] / 4) {
                                    centerIndex = l % 4;
                                    break;
                                }
                        // Buffer corner is replaced with corner
                        innerXCenters[innerXCenterCubies[0][0]] = innerXCenters[innerXCenterCubies[i][centerIndex]];

                        // Corner is solved
                        innerXCenters[innerXCenterCubies[i][centerIndex]] = innerXCenterCubies[i][centerIndex];

                        // Corner cycle is inserted into solution array
                        innerXCenterCycles.add(innerXCenterCubies[i][centerIndex]);
                        innerXCenterCycled = true;
                    }
                }
            }
        }
    }

    private boolean innerXCentersSolved() {
        boolean innerXCentersSolved = true;

        // Check if xCenters marked as unsolved haven't been solved yet
        for (int i = 0; i < 24; i++) {
            if (i == 0 || !solvedInnerXCenters[i]) {
                // XCenter is solved in correct orientation
                int j = i / 4;
                if (innerXCenters[innerXCenterCubies[j][i % 4]] == innerXCenterCubies[j][i % 4]
                        || innerXCenters[innerXCenterCubies[j][i % 4]] == innerXCenterCubies[j][(i + 1) % 4]
                        || innerXCenters[innerXCenterCubies[j][i % 4]] == innerXCenterCubies[j][(i + 2) % 4]
                        || innerXCenters[innerXCenterCubies[j][i % 4]] == innerXCenterCubies[j][(i + 3) % 4]) {
                    solvedInnerXCenters[i] = true;
                } else {
                    // Found at least one unsolved wing
                    solvedInnerXCenters[i] = false;
                    innerXCentersSolved = false;
                }
            }
        }
        return innerXCentersSolved;
    }

    protected void solveRightObliques() {
        if (!rightObliquesSolved())
            System.arraycopy(solvedRightObliques, 0, scrambledStateSolvedRightObliques, 0, solvedRightObliques.length);
        else
            scrambledStateSolvedRightObliques = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
        this.resetCube(true);
        rightObliqueCycles.clear();
        this.rightObliqueCycleNum = 0;
        while (!rightObliquesSolved()) cycleRightObliqueBuffer();
    }

    private void cycleRightObliqueBuffer() {
        boolean rightObliqueCycled = false;

        // If the buffer is solved, replace it with an unsolved corner
        if (solvedRightObliques[0]) {
            this.rightObliqueCycleNum++;
            // First unsolved corner is selected
            for (int i = 0; i < 23 && !rightObliqueCycled; i++) {
                if (!solvedRightObliques[i]) {
                    int centerIndex = i;
                    if (avoidRightObliqueBreakIns && rightObliques[rightObliqueCubies[i / 4][i % 4]] / 4 == rightObliqueCubies[0][0] / 4)
                        for (int j = i; j < (i + 4) - (i % 4); j++)
                            if (!solvedRightObliques[j] && rightObliques[rightObliqueCubies[j / 4][j % 4]] / 4 != rightObliqueCubies[0][0] / 4) {
                                centerIndex = j;
                                break;
                            }
                    // Buffer is placed in a... um... buffer
                    int tempRightOblique = rightObliques[rightObliqueCubies[0][0]];

                    // Buffer corner is replaced with corner
                    rightObliques[rightObliqueCubies[0][0]] = rightObliques[rightObliqueCubies[centerIndex / 4][centerIndex % 4]];

                    // Corner is replaced with buffer
                    rightObliques[rightObliqueCubies[centerIndex / 4][centerIndex % 4]] = tempRightOblique;

                    // Corner cycle is inserted into solution array
                    rightObliqueCycles.add(rightObliqueCubies[centerIndex / 4][centerIndex % 4]);
                    rightObliqueCycled = true;
                }
            }
        }
        // If the buffer is not solved, swap it to the position where the corner belongs
        else {
            for (int i = 0; i < 6 && !rightObliqueCycled; i++) {
                for (int j = 0; j < 4 && !rightObliqueCycled; j++) {
                    if (rightObliques[rightObliqueCubies[0][0]] == rightObliqueCubies[i][j]) {
                        int centerIndex = j;
                        for (int k = ((i * 4) + j) - (j % 4); k < (((i + 1) * 4) + j) - (j % 4); k++)
                            if (!solvedRightObliques[k]) {
                                centerIndex = k % 4;
                                break;
                            }
                        if (avoidInnerXBreakIns && rightObliques[rightObliqueCubies[i][centerIndex]] / 4 == rightObliqueCubies[0][0] / 4)
                            for (int l = (i * 4) + centerIndex; l < (i + 1) * 4; l++)
                                if (!solvedRightObliques[l] && rightObliques[rightObliqueCubies[l / 4][l % 4]] / 4 != rightObliqueCubies[0][0] / 4) {
                                    centerIndex = l % 4;
                                    break;
                                }
                        // Buffer corner is replaced with corner
                        rightObliques[rightObliqueCubies[0][0]] = rightObliques[rightObliqueCubies[i][centerIndex]];

                        // Corner is solved
                        rightObliques[rightObliqueCubies[i][centerIndex]] = rightObliqueCubies[i][centerIndex];

                        // Corner cycle is inserted into solution array
                        rightObliqueCycles.add(rightObliqueCubies[i][centerIndex]);
                        rightObliqueCycled = true;
                    }
                }
            }
        }
    }

    private boolean rightObliquesSolved() {
        boolean rightObliquesSolved = true;

        // Check if xCenters marked as unsolved haven't been solved yet
        for (int i = 0; i < 24; i++) {
            if (i == 0 || !solvedRightObliques[i]) {
                // XCenter is solved in correct orientation
                int j = i / 4;
                if (rightObliques[rightObliqueCubies[j][i % 4]] == rightObliqueCubies[j][i % 4]
                        || rightObliques[rightObliqueCubies[j][i % 4]] == rightObliqueCubies[j][(i + 1) % 4]
                        || rightObliques[rightObliqueCubies[j][i % 4]] == rightObliqueCubies[j][(i + 2) % 4]
                        || rightObliques[rightObliqueCubies[j][i % 4]] == rightObliqueCubies[j][(i + 3) % 4]) {
                    solvedRightObliques[i] = true;
                } else {
                    // Found at least one unsolved wing
                    solvedRightObliques[i] = false;
                    rightObliquesSolved = false;
                }
            }
        }
        return rightObliquesSolved;
    }

    protected void solveLeftObliques() {
        if (!leftObliquesSolved())
            System.arraycopy(solvedLeftObliques, 0, scrambledStateSolvedLeftObliques, 0, solvedLeftObliques.length);
        else
            scrambledStateSolvedLeftObliques = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
        this.resetCube(true);
        leftObliqueCycles.clear();
        this.leftObliqueCycleNum = 0;
        while (!leftObliquesSolved()) cycleLeftObliqueBuffer();
    }

    private void cycleLeftObliqueBuffer() {
        boolean leftObliqueCycled = false;

        // If the buffer is solved, replace it with an unsolved corner
        if (solvedLeftObliques[0]) {
            this.leftObliqueCycleNum++;
            // First unsolved corner is selected
            for (int i = 0; i < 23 && !leftObliqueCycled; i++) {
                if (!solvedLeftObliques[i]) {
                    int centerIndex = i;
                    if (avoidLeftObliqueBreakIns && leftObliques[leftObliqueCubies[i / 4][i % 4]] / 4 == leftObliqueCubies[0][0] / 4)
                        for (int j = i; j < (i + 4) - (i % 4); j++)
                            if (!solvedLeftObliques[j] && leftObliques[leftObliqueCubies[j / 4][j % 4]] / 4 != leftObliqueCubies[0][0] / 4) {
                                centerIndex = j;
                                break;
                            }
                    // Buffer is placed in a... um... buffer
                    int tempLeftOblique = leftObliques[leftObliqueCubies[0][0]];

                    // Buffer corner is replaced with corner
                    leftObliques[leftObliqueCubies[0][0]] = leftObliques[leftObliqueCubies[centerIndex / 4][centerIndex % 4]];

                    // Corner is replaced with buffer
                    leftObliques[leftObliqueCubies[centerIndex / 4][centerIndex % 4]] = tempLeftOblique;

                    // Corner cycle is inserted into solution array
                    leftObliqueCycles.add(leftObliqueCubies[centerIndex / 4][centerIndex % 4]);
                    leftObliqueCycled = true;
                }
            }
        }
        // If the buffer is not solved, swap it to the position where the corner belongs
        else {
            for (int i = 0; i < 6 && !leftObliqueCycled; i++) {
                for (int j = 0; j < 4 && !leftObliqueCycled; j++) {
                    if (leftObliques[leftObliqueCubies[0][0]] == leftObliqueCubies[i][j]) {
                        int centerIndex = j;
                        for (int k = ((i * 4) + j) - (j % 4); k < (((i + 1) * 4) + j) - (j % 4); k++)
                            if (!solvedLeftObliques[k]) {
                                centerIndex = k % 4;
                                break;
                            }
                        if (avoidInnerXBreakIns && leftObliques[leftObliqueCubies[i][centerIndex]] / 4 == leftObliqueCubies[0][0] / 4)
                            for (int l = (i * 4) + centerIndex; l < (i + 1) * 4; l++)
                                if (!solvedLeftObliques[l] && leftObliques[leftObliqueCubies[l / 4][l % 4]] / 4 != leftObliqueCubies[0][0] / 4) {
                                    centerIndex = l % 4;
                                    break;
                                }
                        // Buffer corner is replaced with corner
                        leftObliques[leftObliqueCubies[0][0]] = leftObliques[leftObliqueCubies[i][centerIndex]];

                        // Corner is solved
                        leftObliques[leftObliqueCubies[i][centerIndex]] = leftObliqueCubies[i][centerIndex];

                        // Corner cycle is inserted into solution array
                        leftObliqueCycles.add(leftObliqueCubies[i][centerIndex]);
                        leftObliqueCycled = true;
                    }
                }
            }
        }
    }

    private boolean leftObliquesSolved() {
        boolean leftObliquesSolved = true;

        // Check if xCenters marked as unsolved haven't been solved yet
        for (int i = 0; i < 24; i++) {
            if (i == 0 || !solvedLeftObliques[i]) {
                // XCenter is solved in correct orientation
                int j = i / 4;
                if (leftObliques[leftObliqueCubies[j][i % 4]] == leftObliqueCubies[j][i % 4]
                        || leftObliques[leftObliqueCubies[j][i % 4]] == leftObliqueCubies[j][(i + 1) % 4]
                        || leftObliques[leftObliqueCubies[j][i % 4]] == leftObliqueCubies[j][(i + 2) % 4]
                        || leftObliques[leftObliqueCubies[j][i % 4]] == leftObliqueCubies[j][(i + 3) % 4]) {
                    solvedLeftObliques[i] = true;
                } else {
                    // Found at least one unsolved wing
                    solvedLeftObliques[i] = false;
                    leftObliquesSolved = false;
                }
            }
        }
        return leftObliquesSolved;
    }

    public String getInnerXCenterPairs() {
        String innerXCenterPairs = "";
        if (innerXCenterCycles.size() != 0) {
            for (int i = 0; i < innerXCenterCycles.size(); i++) {
                innerXCenterPairs += innerXCenterLettering[innerXCenterCycles.get(i)];
                if (i % 2 == 1) innerXCenterPairs += " ";
            }
        } else return "Solved";
        return innerXCenterPairs;
    }

    public String getInnerWingPairs() {
        String innerWingPairs = "";
        if (innerWingCycles.size() != 0) {
            for (int i = 0; i < innerWingCycles.size(); i++) {
                innerWingPairs += innerWingLettering[innerWingCycles.get(i)];
                if (i % 2 == 1) innerWingPairs += " ";
            }
        } else return "Solved";
        return innerWingPairs;
    }

    public String getRightObliquePairs() {
        String rightObliquePairs = "";
        if (rightObliqueCycles.size() != 0) {
            for (int i = 0; i < rightObliqueCycles.size(); i++) {
                rightObliquePairs += rightObliqueLettering[rightObliqueCycles.get(i)];
                if (i % 2 == 1) rightObliquePairs += " ";
            }
        } else return "Solved";
        return rightObliquePairs;
    }

    public String getLeftObliquePairs() {
        String leftObliquePairs = "";
        if (leftObliqueCycles.size() != 0) {
            for (int i = 0; i < leftObliqueCycles.size(); i++) {
                leftObliquePairs += leftObliqueLettering[leftObliqueCycles.get(i)];
                if (i % 2 == 1) leftObliquePairs += " ";
            }
        } else return "Solved";
        return leftObliquePairs;
    }

    @Override
    public String getSolutionPairs(boolean withRotation) {
        return (withRotation ? this.getRotations() + "\n" : "")
                + "Right Obliques: " + this.getRightObliquePairs()
                + "\nLeft Obliques: " + this.getLeftObliquePairs()
                + "\nXCenters: " + this.getXCenterPairs()
                + "\nInner XCenters: " + this.getInnerXCenterPairs()
                + "\nWings: " + this.getWingPairs()
                + "\nInner Wings: " + this.getInnerWingPairs()
                + "\nCorners: " + this.getCornerPairs();
    }

    @Override
    public String getStatstics() {
        return "Corners: " + this.getCornerLength() + "@" + this.getCornerBreakInNum() + " w/ " + this.getNumPreSolvedCorners() + "-" + this.getNumPreTwistedCorners() + " > " + this.hasCornerParity()
                + "\nWings: " + this.getWingLength() + "@" + this.getWingBreakInNum() + " w/ " + this.getNumPreSolvedWings() + " > " + this.hasWingParity()
                + "\nInner Wings: " + this.getInnerWingLength() + "@" + this.getInnerWingBreakInNum() + " w/ " + this.getNumPreSolvedInnerWings() + " > " + this.hasInnerWingParity()
                + "\nXCenters: " + this.getXCenterLength() + "@" + this.getXCenterBreakInNum() + " w/ " + this.getNumPreSolvedXCenters() + " > " + this.hasXCenterParity()
                + "\nInner XCenters: " + this.getInnerXCenterLength() + "@" + this.getInnerXCenterBreakInNum() + " w/ " + this.getNumPreSolvedInnerXCenters() + " > " + this.hasInnerXCenterParity()
                + "\nRight Obliques: " + this.getRightObliqueLength() + "@" + this.getRightObliqueBreakInNum() + " w/ " + this.getNumPreSolvedRightObliques() + " > " + this.hasRightObliqueParity()
                + "\nLeft Obliques: " + this.getLeftObliqueLength() + "@" + this.getLeftObliqueBreakInNum() + " w/ " + this.getNumPreSolvedLeftObliques() + " > " + this.hasLeftObliqueParity();
    }

    @Override
    public String getNoahtation() {
        return "C:" + this.getCornerNoahtation() + " / W:" + this.getWingNoahtation() + " / iW:" + this.getInnerWingNoahtation() + " / X:" + this.getXCenterNoahtation() + " / iX:" + this.getInnerXCenterNoahtation() + " / RO:" + this.getRightObliqueNoahtation() + " / LO:" + this.getLeftObliqueNoahtation();
    }

    public String getStatString(boolean spaced) {
        return "C: " + this.getCornerStatString(spaced) + " | W: " + this.getWingStatString(spaced) + " | iW: " + this.getInnerWingStatString(spaced) + " | X: " + this.getXCenterStatString(spaced) + " | iX: " + this.getInnerXCenterStatString(spaced) + " | RO: " + this.getRightObliqueStatString(spaced) + " | LO: " + this.getLeftObliqueStatString(spaced);
    }

    public String getInnerWingStatString(boolean spaced) {
        String innerWingStat = this.hasInnerWingParity() ? "_" : " ";
        innerWingStat += this.getInnerWingLength();
        innerWingStat += this.isInnerWingBufferSolved() ? "*" : " ";
        innerWingStat += spaced ? "\t" : " ";
        for (int i = 0; i < 11; i++) innerWingStat += i < this.getInnerWingBreakInNum() ? "#" : spaced ? " " : "";
        if (spaced || innerWingStat.endsWith("#")) innerWingStat += " ";
        for (int i = 0; i < 23; i++) innerWingStat += i < this.getNumPreSolvedInnerWings() ? "+" : spaced ? " " : "";
        return innerWingStat;
    }

    public String getInnerWingStatString() {
        return this.getInnerWingStatString(false);
    }

    public String getInnerXCenterStatString(boolean spaced) {
        String innerXCenterStat = this.hasInnerXCenterParity() ? "_" : " ";
        innerXCenterStat += this.getInnerXCenterLength();
        innerXCenterStat += this.isInnerXCenterBufferSolved() ? "*" : " ";
        innerXCenterStat += spaced ? "\t" : " ";
        for (int i = 0; i < 11; i++) innerXCenterStat += i < this.getInnerXCenterBreakInNum() ? "#" : spaced ? " " : "";
        if (spaced || innerXCenterStat.endsWith("#")) innerXCenterStat += " ";
        for (int i = 0; i < 23; i++) innerXCenterStat += i < this.getNumPreSolvedInnerXCenters() ? "+" : spaced ? " " : "";
        return innerXCenterStat;
    }

    public String getInnerXCenterStatString() {
        return this.getInnerXCenterStatString(false);
    }

    public String getRightObliqueStatString(boolean spaced) {
        String rightObliqueStat = this.hasRightObliqueParity() ? "_" : " ";
        rightObliqueStat += this.getRightObliqueLength();
        rightObliqueStat += this.isRightObliqueBufferSolved() ? "*" : " ";
        rightObliqueStat += spaced ? "\t" : " ";
        for (int i = 0; i < 11; i++) rightObliqueStat += i < this.getRightObliqueBreakInNum() ? "#" : spaced ? " " : "";
        if (spaced || rightObliqueStat.endsWith("#")) rightObliqueStat += " ";
        for (int i = 0; i < 23; i++) rightObliqueStat += i < this.getNumPreSolvedRightObliques() ? "+" : spaced ? " " : "";
        return rightObliqueStat;
    }

    public String getRightObliqueStatString() {
        return this.getRightObliqueStatString(false);
    }

    public String getLeftObliqueStatString(boolean spaced) {
        String leftObliqueStat = this.hasLeftObliqueParity() ? "_" : " ";
        leftObliqueStat += this.getLeftObliqueLength();
        leftObliqueStat += this.isLeftObliqueBufferSolved() ? "*" : " ";
        leftObliqueStat += spaced ? "\t" : " ";
        for (int i = 0; i < 11; i++) leftObliqueStat += i < this.getLeftObliqueBreakInNum() ? "#" : spaced ? " " : "";
        if (spaced || leftObliqueStat.endsWith("#")) leftObliqueStat += " ";
        for (int i = 0; i < 23; i++) leftObliqueStat += i < this.getNumPreSolvedLeftObliques() ? "+" : spaced ? " " : "";
        return leftObliqueStat;
    }

    public String getLeftObliqueStatString() {
        return this.getLeftObliqueStatString(false);
    }

    public boolean hasInnerXCenterParity() {
        return this.innerXCenterCycles.size() % 2 == 1;
    }

    public boolean isInnerXCenterBufferSolved() {
        return this.scrambledStateSolvedInnerXCenters[0];
    }

    public int getInnerXCenterLength() {
        return this.innerXCenterCycles.size();
    }

    public int getInnerXCenterBreakInNum() {
        return this.innerXCenterCycleNum;
    }

    public boolean isInnerXCenterSingleCycle() {
        return this.innerXCenterCycleNum == 0;
    }

    public int getNumPreSolvedInnerXCenters() {
        int preSolved = 0;
        for (int i = 1; i < scrambledStateSolvedInnerXCenters.length; i++)
            if (scrambledStateSolvedInnerXCenters[i]) preSolved++;
        return preSolved;
    }

    public String getPreSolvedInnerXCenters() {
        String solvedInnerXCenters = "";
        for (int i = 1; i < scrambledStateSolvedInnerXCenters.length; i++)
            if (scrambledStateSolvedInnerXCenters[i])
                solvedInnerXCenters += (solvedInnerXCenters.length() > 0 ? " " : "") + innerXCenterPositions[i];
        return solvedInnerXCenters;
    }

    public String getInnerXCenterNoahtation() {
        return this.getInnerXCenterLength() + "";
    }

    public boolean hasInnerWingParity() {
        return this.innerWingCycles.size() % 2 == 1;
    }

    public boolean isInnerWingBufferSolved() {
        return this.scrambledStateSolvedInnerWings[0];
    }

    public int getInnerWingLength() {
        return this.innerWingCycles.size();
    }

    public int getInnerWingBreakInNum() {
        return this.innerWingCycleNum;
    }

    public boolean isInnerWingSingleCycle() {
        return this.innerWingCycleNum == 0;
    }

    public int getNumPreSolvedInnerWings() {
        int preSolved = 0;
        for (int i = 1; i < scrambledStateSolvedInnerWings.length; i++)
            if (scrambledStateSolvedInnerWings[i]) preSolved++;
        return preSolved;
    }

    public String getPreSolvedInnerWings() {
        String solvedInnerWings = "";
        for (int i = 1; i < scrambledStateSolvedInnerWings.length; i++)
            if (scrambledStateSolvedInnerWings[i])
                solvedInnerWings += (solvedInnerWings.length() > 0 ? " " : "") + innerWingPositions[i];
        return solvedInnerWings;
    }

    public String getInnerWingNoahtation() {
        return this.getInnerWingLength() + "";
    }

    public boolean hasRightObliqueParity() {
        return this.rightObliqueCycles.size() % 2 == 1;
    }

    public boolean isRightObliqueBufferSolved() {
        return this.scrambledStateSolvedRightObliques[0];
    }

    public int getRightObliqueLength() {
        return this.rightObliqueCycles.size();
    }

    public int getRightObliqueBreakInNum() {
        return this.rightObliqueCycleNum;
    }

    public boolean isRightObliqueSingleCycle() {
        return this.rightObliqueCycleNum == 0;
    }

    public int getNumPreSolvedRightObliques() {
        int preSolved = 0;
        for (int i = 1; i < scrambledStateSolvedRightObliques.length; i++)
            if (scrambledStateSolvedRightObliques[i]) preSolved++;
        return preSolved;
    }

    public String getPreSolvedRightObliques() {
        String solvedRightObliques = "";
        for (int i = 1; i < scrambledStateSolvedRightObliques.length; i++)
            if (scrambledStateSolvedRightObliques[i])
                solvedRightObliques += (solvedRightObliques.length() > 0 ? " " : "") + rightObliquePositions[i];
        return solvedRightObliques;
    }

    public String getRightObliqueNoahtation() {
        return this.getRightObliqueLength() + "";
    }

    public boolean hasLeftObliqueParity() {
        return this.leftObliqueCycles.size() % 2 == 1;
    }

    public boolean isLeftObliqueBufferSolved() {
        return this.scrambledStateSolvedLeftObliques[0];
    }

    public int getLeftObliqueLength() {
        return this.leftObliqueCycles.size();
    }

    public int getLeftObliqueBreakInNum() {
        return this.leftObliqueCycleNum;
    }

    public boolean isLeftObliqueSingleCycle() {
        return this.leftObliqueCycleNum == 0;
    }

    public int getNumPreSolvedLeftObliques() {
        int preSolved = 0;
        for (int i = 1; i < scrambledStateSolvedLeftObliques.length; i++)
            if (scrambledStateSolvedLeftObliques[i]) preSolved++;
        return preSolved;
    }

    public String getPreSolvedLeftObliques() {
        String solvedLeftObliques = "";
        for (int i = 1; i < scrambledStateSolvedLeftObliques.length; i++)
            if (scrambledStateSolvedLeftObliques[i])
                solvedLeftObliques += (solvedLeftObliques.length() > 0 ? " " : "") + leftObliquePositions[i];
        return solvedLeftObliques;
    }

    public String getLeftObliqueNoahtation() {
        return this.getLeftObliqueLength() + "";
    }

    public void setInnerXCenterScheme(String scheme) {
        this.setInnerXCenterScheme(scheme.split(""));
    }

    public void setInnerXCenterScheme(String[] scheme) {
        if (scheme.length == 24) this.innerXCenterLettering = scheme;
    }

    public void setInnerXCenterBuffer(String bufferAsLetter) {
        if (arrayContains(this.innerXCenterLettering, bufferAsLetter)) {
            int speffz = arrayIndex(this.innerXCenterLettering, bufferAsLetter);
            int outer = deepArrayOuterIndex(this.innerXCenterCubies, speffz), inner = deepArrayInnerIndex(this.innerXCenterCubies, speffz);
            for (int i = 0; i < outer; i++) cycleArrayLeft(this.innerXCenterCubies);
            for (int i = 0; i < inner; i++) cycleArrayLeft(this.innerXCenterCubies[0]);
            this.parseScramble(this.getScramble());
        }
    }

    public void setInnerWingScheme(String scheme) {
        this.setInnerWingScheme(scheme.split(""));
    }

    public void setInnerWingScheme(String[] scheme) {
        if (scheme.length == 24) this.innerWingLettering = scheme;
    }

    public void setInnerWingBuffer(String bufferAsLetter) {
        if (arrayContains(this.innerWingLettering, bufferAsLetter)) {
            int speffz = arrayIndex(this.innerWingLettering, bufferAsLetter);
            int index = arrayIndex(this.innerWingCubies, speffz);
            for (int i = 0; i < index; i++) cycleArrayLeft(this.innerWingCubies);
            this.parseScramble(this.getScramble());
        }
    }

    public void setRightObliqueScheme(String scheme) {
        this.setRightObliqueScheme(scheme.split(""));
    }

    public void setRightObliqueScheme(String[] scheme) {
        if (scheme.length == 24) this.rightObliqueLettering = scheme;
    }

    public void setRightObliquesBuffer(String bufferAsLetter) {
        if (arrayContains(this.rightObliqueLettering, bufferAsLetter)) {
            int speffz = arrayIndex(this.rightObliqueLettering, bufferAsLetter);
            int outer = deepArrayOuterIndex(this.rightObliqueCubies, speffz), inner = deepArrayInnerIndex(this.rightObliqueCubies, speffz);
            for (int i = 0; i < outer; i++) cycleArrayLeft(this.rightObliqueCubies);
            for (int i = 0; i < inner; i++) cycleArrayLeft(this.rightObliqueCubies[0]);
            this.parseScramble(this.getScramble());
        }
    }

    public void setLeftObliqueScheme(String scheme) {
        this.setLeftObliqueScheme(scheme.split(""));
    }

    public void setLeftObliqueScheme(String[] scheme) {
        if (scheme.length == 24) this.leftObliqueLettering = scheme;
    }

    public void setLeftObliquesBuffer(String bufferAsLetter) {
        if (arrayContains(this.leftObliqueLettering, bufferAsLetter)) {
            int speffz = arrayIndex(this.leftObliqueLettering, bufferAsLetter);
            int outer = deepArrayOuterIndex(this.leftObliqueCubies, speffz), inner = deepArrayInnerIndex(this.leftObliqueCubies, speffz);
            for (int i = 0; i < outer; i++) cycleArrayLeft(this.leftObliqueCubies);
            for (int i = 0; i < inner; i++) cycleArrayLeft(this.leftObliqueCubies[0]);
            this.parseScramble(this.getScramble());
        }
    }
}
