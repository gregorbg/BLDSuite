package com.suushiemaniac.cubing.bld.analyze.cube;

import java.util.ArrayList;

public class SkewbBldCube {
    protected static final int CORNERS = 0;
    protected static final int CENTERS = 1;

    protected int[] corners = new int[24];
    protected int[] centers = new int[6];

    protected int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5, G = 6, H = 7, I = 8, J = 9, K = 10, L = 11, M = 12, N = 13, O = 14, P = 15, Q = 16, R = 17, S = 18, T = 19, U = 20, V = 21, W = 22, X = 23, Z = -1;
    protected int UP = 0, LEFT = 1, FRONT = 2, RIGHT = 3, BACK = 4, DOWN = 5;
    //TODO Corner position lettering
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

}
