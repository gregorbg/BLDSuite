package com.suushiemaniac.cubing.bld.analyze.cube;

import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.ArrayList;
import java.util.HashMap;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public class SevenBldCube extends SixBldCube {
    protected int[] innerTCenters = new int[24];

    protected String[] innerTCenterLettering = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X"};
    protected String[] innerTCenterPositions = {"Df", "Dr", "Db", "Dl", "Ub", "Ur", "Uf", "Ul", "Lu", "Lf", "Ld", "Lb", "Fu", "Fr", "Fd", "Fl", "Ru", "Rb", "Rd", "Rf", "Bu", "Bl", "Bd", "Br"};

    protected Integer[][] innerTCenterCubies = {{U, V, W, X}, {A, B, C, D}, {E, F, G, H}, {I, J, K, L}, {M, N, O, P}, {Q, R, S, T}};
    protected boolean[] solvedInnerTCenters = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
    protected boolean[] scrambledStateSolvedInnerTCenters = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
    protected int[] scrambledStateInnerTCenters = new int[24];
    protected int innerTCenterCycleNum = 0;
    protected ArrayList<Integer> innerTCenterCycles = new ArrayList<Integer>();

    private boolean avoidInnerTBreakIns = true;

    public SevenBldCube(String scramble) {
        initPermutations();
        this.parseScramble(scramble);
    }

    protected SevenBldCube() {
    }

    protected void initPermutations() {
        super.initPermutations();
        String[] faceNames = {
                "4Uw", "4Uw'", "4Uw2",
                "4Fw", "4Fw'", "4Fw2",
                "4Rw", "4Rw'", "4Rw2",
                "4Lw", "4Lw'", "4Lw2",
                "4Bw", "4Bw'", "4Bw2",
                "4Dw", "4Dw'", "4Dw2"
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
        Integer[][] tCenterFacePerms = {
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
        Integer[][] innerWingFacePerms = {
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
        Integer[][] innerXCenterFacePerms = {
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
        Integer[][] rightObliqueFacePerms = {
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
        Integer[][] leftObliqueFacePerms = {
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
        for (int i = 0; i < faceNames.length; i++) {
            HashMap<PieceType, Integer[]> tempMap = permutations.get(faceNames[i]);
            if (tempMap == null) tempMap = new HashMap<PieceType, Integer[]>();
            tempMap.put(CORNER, cornerFacePerms[i]);
            tempMap.put(EDGE, edgeFacePerms[i]);
            tempMap.put(WING, wingFacePerms[i]);
            tempMap.put(INNERWING, innerWingFacePerms[i]);
            tempMap.put(XCENTER, xCenterFacePerms[i]);
            tempMap.put(INNERXCENTER, innerXCenterFacePerms[i]);
            tempMap.put(RIGHTOBLIQUE, rightObliqueFacePerms[i]);
            tempMap.put(LEFTOBLIQUE, leftObliqueFacePerms[i]);
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

                "4Uw", "4Uw'", "4Uw2",
                "4Fw", "4Fw'", "4Fw2",
                "4Rw", "4Rw'", "4Rw2",
                "4Lw", "4Lw'", "4Lw2",
                "4Bw", "4Bw'", "4Bw2",
                "4Dw", "4Dw'", "4Dw2",

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
        Integer[][] innerTCenterFacePerms = {
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
            if (tempMap == null) tempMap = new HashMap<PieceType, Integer[]>();
            tempMap.put(INNERTCENTER, innerTCenterFacePerms[i]);
            permutations.put(faceNames[i], tempMap);
        }
    }

    // Sets cube to solved position
    @Override
    protected void resetCube(boolean orientationOnly) {
        super.resetCube(orientationOnly);
        // Corners and edges are initialized in solved position
        for (int i = 0; i < 24; i++) {
            if (!orientationOnly) this.innerTCenters[i] = i;
            solvedInnerTCenters[i] = false;
        }
    }

    // Perform a permutation on the cube
    @Override
    protected void permute(String permutation) {
        super.permute(permutation);
        // Inner XCenters are permuted
        int[] exchanges = new int[]{Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z};
        Integer[] perm = permutations.get(permutation).get(INNERTCENTER);
        for (int i = 0; i < 24; i++) if (perm[i] != Z) exchanges[perm[i]] = innerTCenters[i];
        for (int i = 0; i < 24; i++) if (exchanges[i] != Z) innerTCenters[i] = exchanges[i];
    }

    // Finds a BLD solution for the cube in its current state
    @Override
    protected void solveCube() {
        System.arraycopy(this.corners, 0, this.scrambledStateCorners, 0, 24);
        System.arraycopy(this.edges, 0, this.scrambledStateEdges, 0, 24);
        System.arraycopy(this.centers, 0, this.scrambledStateCenters, 0, 6);
        System.arraycopy(this.wings, 0, this.scrambledStateWings, 0, 24);
        System.arraycopy(this.innerWings, 0, this.scrambledStateInnerWings, 0, 24);
        System.arraycopy(this.xCenters, 0, this.scrambledStateXCenters, 0, 24);
        System.arraycopy(this.innerXCenters, 0, this.scrambledStateInnerXCenters, 0, 24);
        System.arraycopy(this.tCenters, 0, this.scrambledStateTCenters, 0, 24);
        System.arraycopy(this.innerTCenters, 0, this.scrambledStateInnerTCenters, 0, 24);
        System.arraycopy(this.rightObliques, 0, this.scrambledStateRightObliques, 0, 24);
        System.arraycopy(this.leftObliques, 0, this.scrambledStateLeftObliques, 0, 24);
        reorientCube();
        solveCorners();
        solveEdges();
        solveWings();
        solveInnerWings();
        solveXCenters();
        solveInnerXCenters();
        solveTCenters();
        solveInnerTCenters();
        solveRightObliques();
        solveLeftObliques();
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

    public void avoidInnerTBreakIns(boolean avoid) {
        this.avoidInnerTBreakIns = avoid;
        this.parseScramble(this.getScramble());
    }

    protected void solveInnerTCenters() {
        if (!innerTCentersSolved())
            System.arraycopy(solvedInnerTCenters, 0, scrambledStateSolvedInnerTCenters, 0, solvedInnerTCenters.length);
        else
            scrambledStateSolvedInnerTCenters = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
        this.resetCube(true);
        innerTCenterCycles.clear();
        this.innerTCenterCycleNum = 0;
        while (!innerTCentersSolved()) cycleInnerTCenterBuffer();
    }

    private void cycleInnerTCenterBuffer() {
        boolean innerTCenterCycled = false;

        // If the buffer is solved, replace it with an unsolved corner
        if (solvedInnerTCenters[0]) {
            this.innerTCenterCycleNum++;
            // First unsolved corner is selected
            for (int i = 0; i < 23 && !innerTCenterCycled; i++) {
                if (!solvedInnerTCenters[i]) {
                    int centerIndex = i;
                    if (avoidInnerTBreakIns && innerTCenters[innerTCenterCubies[i / 4][i % 4]] / 4 == innerTCenterCubies[0][0] / 4)
                        for (int j = i; j < (i + 4) - (i % 4); j++)
                            if (!solvedInnerTCenters[j] && innerTCenters[innerTCenterCubies[j / 4][j % 4]] / 4 != innerTCenterCubies[0][0] / 4) {
                                centerIndex = j;
                                break;
                            }
                    // Buffer is placed in a... um... buffer
                    int tempTCenter = innerTCenters[innerTCenterCubies[0][0]];

                    // Buffer corner is replaced with corner
                    innerTCenters[innerTCenterCubies[0][0]] = innerTCenters[innerTCenterCubies[centerIndex / 4][centerIndex % 4]];

                    // Corner is replaced with buffer
                    innerTCenters[innerTCenterCubies[centerIndex / 4][centerIndex % 4]] = tempTCenter;

                    // Corner cycle is inserted into solution array
                    innerTCenterCycles.add(innerTCenterCubies[centerIndex / 4][centerIndex % 4]);
                    innerTCenterCycled = true;
                }
            }
        }
        // If the buffer is not solved, swap it to the position where the corner belongs
        else {
            for (int i = 0; i < 6 && !innerTCenterCycled; i++) {
                for (int j = 0; j < 4 && !innerTCenterCycled; j++) {
                    if (innerTCenters[innerTCenterCubies[0][0]] == innerTCenterCubies[i][j]) {
                        int centerIndex = j;
                        for (int k = ((i * 4) + j) - (j % 4); k < (((i + 1) * 4) + j) - (j % 4); k++)
                            if (!solvedInnerTCenters[k]) {
                                centerIndex = k % 4;
                                break;
                            }
                        if (avoidInnerTBreakIns && innerTCenters[innerTCenterCubies[i][centerIndex]] / 4 == innerTCenterCubies[0][0] / 4)
                            for (int l = (i * 4) + centerIndex; l < (i + 1) * 4; l++)
                                if (!solvedInnerTCenters[l] && innerTCenters[innerTCenterCubies[l / 4][l % 4]] / 4 != innerTCenterCubies[0][0] / 4) {
                                    centerIndex = l % 4;
                                    break;
                                }
                        // Buffer corner is replaced with corner
                        innerTCenters[innerTCenterCubies[0][0]] = innerTCenters[innerTCenterCubies[i][centerIndex]];

                        // Corner is solved
                        innerTCenters[innerTCenterCubies[i][centerIndex]] = innerTCenterCubies[i][centerIndex];

                        // Corner cycle is inserted into solution array
                        innerTCenterCycles.add(innerTCenterCubies[i][centerIndex]);
                        innerTCenterCycled = true;
                    }
                }
            }
        }
    }

    private boolean innerTCentersSolved() {
        boolean innerTCentersSolved = true;

        // Check if xCenters marked as unsolved haven't been solved yet
        for (int i = 0; i < 24; i++) {
            if (i == 0 || !solvedInnerTCenters[i]) {
                // XCenter is solved in correct orientation
                int j = i / 4;
                if (innerTCenters[innerTCenterCubies[j][i % 4]] == innerTCenterCubies[j][i % 4]
                        || innerTCenters[innerTCenterCubies[j][i % 4]] == innerTCenterCubies[j][(i + 1) % 4]
                        || innerTCenters[innerTCenterCubies[j][i % 4]] == innerTCenterCubies[j][(i + 2) % 4]
                        || innerTCenters[innerTCenterCubies[j][i % 4]] == innerTCenterCubies[j][(i + 3) % 4]) {
                    solvedInnerTCenters[i] = true;
                } else {
                    // Found at least one unsolved wing
                    solvedInnerTCenters[i] = false;
                    innerTCentersSolved = false;
                }
            }
        }
        return innerTCentersSolved;
    }

    public String getInnerTCenterPairs() {
        String innerTCenterPairs = "";
        if (innerTCenterCycles.size() != 0) {
            for (int i = 0; i < innerTCenterCycles.size(); i++) {
                innerTCenterPairs += innerTCenterLettering[innerTCenterCycles.get(i)];
                if (i % 2 == 1) innerTCenterPairs += " ";
            }
        } else return "Solved";
        return innerTCenterPairs;
    }

    @Override
    public String getSolutionPairs(boolean withRotation) {
        return (withRotation ? this.getRotations() + "\n" : "")
                + "TCenters: " + this.getTCenterPairs()
                + "\nInner TCenters: " + this.getInnerTCenterPairs()
                + "\nRight Obliques: " + this.getRightObliquePairs()
                + "\nLeft Obliques: " + this.getLeftObliquePairs()
                + "\nXCenters: " + this.getXCenterPairs()
                + "\nInner XCenters: " + this.getInnerXCenterPairs()
                + "\nWings: " + this.getWingPairs()
                + "\nInner Wings: " + this.getInnerWingPairs()
                + "\nCorners: " + this.getCornerPairs()
                + "\nEdges: " + this.getEdgePairs();
    }

    @Override
    public String getStatstics() {
        return "Corners: " + this.getCornerLength() + "@" + this.getCornerBreakInNum() + " w/ " + this.getNumPreSolvedCorners() + "-" + this.getNumPreTwistedCorners() + " > " + this.hasCornerParity()
                + "\nEdges: " + this.getEdgeLength() + "@" + this.getEdgeBreakInNum() + " w/ " + this.getNumPreSolvedEdges() + " > " + this.hasCornerParity()
                + "\nWings: " + this.getWingLength() + "@" + this.getWingBreakInNum() + " w/ " + this.getNumPreSolvedWings() + " > " + this.hasWingParity()
                + "\nInner Wings: " + this.getInnerWingLength() + "@" + this.getInnerWingBreakInNum() + " w/ " + this.getNumPreSolvedInnerWings() + " > " + this.hasInnerWingParity()
                + "\nXCenters: " + this.getXCenterLength() + "@" + this.getXCenterBreakInNum() + " w/ " + this.getNumPreSolvedXCenters() + " > " + this.hasXCenterParity()
                + "\nInner XCenters: " + this.getInnerXCenterLength() + "@" + this.getInnerXCenterBreakInNum() + " w/ " + this.getNumPreSolvedInnerXCenters() + " > " + this.hasInnerXCenterParity()
                + "\nTCenters: " + this.getTCenterLength() + "@" + this.getTCenterBreakInNum() + " w/ " + this.getNumPreSolvedTCenters() + " > " + this.hasTCenterParity()
                + "\nInner TCenters: " + this.getInnerTCenterLength() + "@" + this.getInnerTCenterBreakInNum() + " w/ " + this.getNumPreSolvedInnerTCenters() + " > " + this.hasInnerTCenterParity()
                + "\nRight Obliques: " + this.getRightObliqueLength() + "@" + this.getRightObliqueBreakInNum() + " w/ " + this.getNumPreSolvedRightObliques() + " > " + this.hasRightObliqueParity()
                + "\nLeft Obliques: " + this.getLeftObliqueLength() + "@" + this.getLeftObliqueBreakInNum() + " w/ " + this.getNumPreSolvedLeftObliques() + " > " + this.hasLeftObliqueParity();
    }

    @Override
    public String getNoahtation() {
        return "C:" + this.getCornerNoahtation() + " / E:" + this.getEdgeNoahtation() + " / W:" + this.getWingNoahtation() + " / iW:" + this.getInnerWingNoahtation() + " / X:" + this.getXCenterNoahtation() + " / iX:" + this.getInnerXCenterNoahtation() + " / T:" + this.getTCenterNoahtation() + " / iT:" + this.getInnerTCenterNoahtation() + " / RO:" + this.getRightObliqueNoahtation() + " / LO:" + this.getLeftObliqueNoahtation();
    }

    public String getStatString(boolean spaced, boolean newLine) {
        return "C: " + this.getCornerStatString(spaced) + (newLine ? "\n" : " | ") + "E: " + this.getEdgeStatString(spaced) + (newLine ? "\n" : " | ") + "W: " + this.getWingStatString(spaced) + (newLine ? "\n" : " | ") + "iW: " + this.getInnerWingStatString(spaced) + (newLine ? "\n" : " | ") + "X: " + this.getXCenterStatString(spaced) + (newLine ? "\n" : " | ") + "iX: " + this.getInnerXCenterStatString(spaced) + (newLine ? "\n" : " | ") + "T: " + this.getTCenterStatString(spaced) + (newLine ? "\n" : " | ") + "iT: " + this.getInnerTCenterStatString(spaced) + (newLine ? "\n" : " | ") + "RO: " + this.getRightObliqueStatString(spaced) + (newLine ? "\n" : " | ") + "LO: " + this.getLeftObliqueStatString(spaced);
    }

    public String getInnerTCenterStatString(boolean spaced) {
        String innerTCenterStat = this.hasInnerTCenterParity() ? "_" : " ";
        innerTCenterStat += this.getInnerTCenterLength();
        innerTCenterStat += this.isInnerTCenterBufferSolved() ? "*" : " ";
        innerTCenterStat += spaced ? "\t" : " ";
        for (int i = 0; i < 11; i++) innerTCenterStat += i < this.getInnerTCenterBreakInNum() ? "#" : spaced ? " " : "";
        if (spaced || innerTCenterStat.endsWith("#")) innerTCenterStat += " ";
        for (int i = 0; i < 23; i++) innerTCenterStat += i < this.getNumPreSolvedInnerTCenters() ? "+" : spaced ? " " : "";
        return innerTCenterStat;
    }

    public String getInnerTCenterStatString() {
        return this.getInnerTCenterStatString(false);
    }

    public boolean hasInnerTCenterParity() {
        return this.innerTCenterCycles.size() % 2 == 1;
    }

    public boolean isInnerTCenterBufferSolved() {
        return this.scrambledStateSolvedInnerTCenters[0];
    }

    public int getInnerTCenterLength() {
        return this.innerTCenterCycles.size();
    }

    public int getInnerTCenterBreakInNum() {
        return this.innerTCenterCycleNum;
    }

    public boolean isInnerTCenterSingleCycle() {
        return this.innerTCenterCycleNum == 0;
    }

    public int getNumPreSolvedInnerTCenters() {
        int preSolved = 0;
        for (int i = 1; i < scrambledStateSolvedInnerTCenters.length; i++)
            if (scrambledStateSolvedInnerTCenters[i]) preSolved++;
        return preSolved;
    }

    public String getPreSolvedInnerTCenters() {
        String solvedInnerTCenters = "";
        for (int i = 1; i < scrambledStateSolvedInnerTCenters.length; i++)
            if (scrambledStateSolvedInnerTCenters[i])
                solvedInnerTCenters += (solvedInnerTCenters.length() > 0 ? " " : "") + innerTCenterPositions[i];
        return solvedInnerTCenters;
    }

    public String getInnerTCenterNoahtation() {
        return this.getInnerTCenterLength() + "";
    }

    public void setInnerTCenterScheme(String scheme) {
        this.setInnerTCenterScheme(scheme.split(""));
    }

    public void setInnerTCenterScheme(String[] scheme) {
        if (scheme.length == 24) this.innerTCenterLettering = scheme;
    }

    public void setInnerTCenterBuffer(String bufferAsLetter) {
        if (arrayContains(this.innerTCenterLettering, bufferAsLetter)) {
            int speffz = arrayIndex(this.innerTCenterLettering, bufferAsLetter);
            int outer = deepArrayOuterIndex(this.innerTCenterCubies, speffz), inner = deepArrayInnerIndex(this.innerTCenterCubies, speffz);
            for (int i = 0; i < outer; i++) cycleArrayLeft(this.innerTCenterCubies);
            for (int i = 0; i < inner; i++) cycleArrayLeft(this.innerTCenterCubies[0]);
            this.parseScramble(this.getScramble());
        }
    }
}
