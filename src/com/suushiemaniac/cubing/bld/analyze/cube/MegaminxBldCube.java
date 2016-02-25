package com.suushiemaniac.cubing.bld.analyze.cube;

import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.suushiemaniac.cubing.bld.model.enumeration.DodecahedronPieceType.*;

public class MegaminxBldCube extends BldCube {
    protected int[] corners = new int[60];
    protected int[] edges = new int[60];
    protected int[] centers = new int[12];

    protected int BA = 0, BE = 1, BI = 2, BO = 3, BU = 4, CA = 5, CE = 6, CI = 7, CO = 8, CU = 9, DA = 10, DE = 11, DI = 12, DO = 13, DU = 14, FA = 15, FE = 16, FI = 17, FO = 18, FU = 19, GA = 20, GE = 21, GI = 22, GO = 23, GU = 24, HA = 25, HE = 26, HI = 27, HO = 28, HU = 29, JA = 30, JE = 31, JI = 32, JO = 33, JU = 34, KA = 35, KE = 36, KI = 37, KO = 38, KU = 39, LA = 40, LE = 41, LI = 42, LO = 43, LU = 44, MA = 45, ME = 46, MI = 47, MO = 48, MU = 49, NA = 50, NE = 51, NI = 52, NO = 53, NU = 54, PA = 55, PE = 56, PI = 57, PO = 58, PU = 59, ZZ = -1;
    protected int UP = 0, UPLEFT = 1, FRONT = 2, UPRIGHT = 3, UPBACKRIGHT = 4, UPBACKLEFT = 5, DOWNBACKLEFT = 6, DOWNLEFT = 7, DOWNRIGHT = 8, DOWNBACKRIGHT = 9, BACK = 10, DOWN = 11;
    //TODO Position Lettering
    protected String[] cornerLettering = {"BA", "BE", "BI", "BO", "BU", "CA", "CE", "CI", "CO", "CU", "DA", "DE", "DI", "DO", "DU", "FA", "FE", "FI", "FO", "FU", "GA", "GE", "GI", "GO", "GU", "HA", "HE", "HI", "HO", "HU", "JA", "JE", "JI", "JO", "JU", "KA", "KE", "KI", "KO", "KU", "LA", "LE", "LI", "LO", "LU", "MA", "ME", "MI", "MO", "MU", "NA", "NE", "NI", "NO", "NU", "PA", "PE", "PI", "PO", "PU"};
    protected String[] cornerPositions = {};
    protected String[] edgeLettering = {"BA", "BE", "BI", "BO", "BU", "CA", "CE", "CI", "CO", "CU", "DA", "DE", "DI", "DO", "DU", "FA", "FE", "FI", "FO", "FU", "GA", "GE", "GI", "GO", "GU", "HA", "HE", "HI", "HO", "HU", "JA", "JE", "JI", "JO", "JU", "KA", "KE", "KI", "KO", "KU", "LA", "LE", "LI", "LO", "LU", "MA", "ME", "MI", "MO", "MU", "NA", "NE", "NI", "NO", "NU", "PA", "PE", "PI", "PO", "PU"};
    protected String[] edgePositions = {};

    protected Integer[][] cornerCubies = {{BA, CA, HE}, {BE, HA, GE}, {BI, GA, FE}, {BO, FA, DE}, {BU, DA, CE}, {CI, DU, KE}, {DI, FU, LE}, {FI, GU, ME}, {GI, HU, NE}, {HI, CU, JE}, {JA, NI, HO}, {KA, JI, CO}, {LA, KI, DO}, {MA, LI, FO}, {NA, MI, GO}, {PA, JO, KU}, {PI, KO, LU}, {PU, LO, MU}, {PE, MO, NU}, {PO, NO, JU}};
    protected boolean[] solvedCorners = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
    protected boolean[] scrambledStateSolvedCorners = {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    protected int[] scrambledStateCorners = new int[60];
    protected int cornerCycleNum = 0;
    protected ArrayList<Integer> cornerCycles = new ArrayList<>();
    protected ArrayList<Integer> cwCorners = new ArrayList<>();
    protected ArrayList<Integer> ccwCorners = new ArrayList<>();

    protected Integer[][] edgeCubies = {{BU, CA}, {BA, HA}, {BE, GA}, {BI, FA}, {BO, DA}, {CE, DU}, {CI, KA}, {CO, JE}, {CU, HE}, {DE, FU}, {DI, LA}, {DO, KE}, {FE, GU}, {FI, MA}, {FO, LE}, {GE, HU}, {GI, NA}, {GO, ME}, {HI, JA}, {HO, NE}, {JI, KU}, {JO, PU}, {JU, NI}, {KI, LU}, {KO, PA}, {LI, MU}, {LO, PE}, {MI, NU}, {MO, PI}, {NO, PO}};
    protected boolean[] solvedEdges = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
    protected boolean[] scrambledStateSolvedEdges = {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    protected int[] scrambledStateEdges = new int[60];
    protected int edgeCycleNum = 0;
    protected ArrayList<Integer> edgeCycles = new ArrayList<>();
    protected ArrayList<Integer> flippedEdges = new ArrayList<>();

    protected int[] centerCubies = {UP, UPLEFT, FRONT, UPRIGHT, UPBACKRIGHT, UPBACKLEFT, DOWNBACKLEFT, DOWNLEFT, DOWNRIGHT, DOWNBACKRIGHT, BACK, DOWN};
    protected int[] scrambledStateCenters = new int[12];
    protected String centerRotations = "";

    protected String scramble = NO_SCRAMBLE;
    protected String solvingOrPremoves = "";

    public MegaminxBldCube(String scramble) {
        initPermutations();
        this.parseScramble(scramble);
    }

    protected MegaminxBldCube() {
    }

    //TODO Permutation definitions
    protected void initPermutations() {
        String[] faceNames = {
                "R++", "R--",
                "D++", "D--",

                "U", "U'",

                "rx", "rx'", "rx2", "rx2'",
                "lx", "lx'", "lx2", "lx2'",
                "y", "y'", "y2", "y2'",
                "rbz", "rbz'", "rbz2", "rbz2'",
                "lbz", "lbz'", "lbz2", "lbz2'",
                "z", "z'", "z2", "z2'"
        };
        Integer[][] cornerFacePerms = {
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},

                {BE, BI, BO, BU, BA, HA, HE, ZZ, ZZ, ZZ, CA, CE, ZZ, ZZ, ZZ, DA, DE, ZZ, ZZ, ZZ, FA, FE, ZZ, ZZ, ZZ, GA, GE, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},

                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ}
        };
        Integer[][] edgeFacePerms = {
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},

                {BE, BI, BO, BU, BA, HA, ZZ, ZZ, ZZ, ZZ, CA, ZZ, ZZ, ZZ, ZZ, DA, ZZ, ZZ, ZZ, ZZ, FA, ZZ, ZZ, ZZ, ZZ, GA, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},

                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ}
        };
        Integer[][] centerFacePerms = {
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},

                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},

                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ},
                {ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ}
        };
        for (int i = 0; i < faceNames.length; i++) {
            HashMap<PieceType, Integer[]> tempMap = permutations.get(faceNames[i]);
            if (tempMap == null) tempMap = new HashMap<>();
            tempMap.put(CORNER, cornerFacePerms[i]);
            tempMap.put(EDGE, edgeFacePerms[i]);
            tempMap.put(CENTER, centerFacePerms[i]);
            permutations.put(faceNames[i], tempMap);
        }
    }

    @Override
    void resetCube(boolean orientationOnly) {
        for (int i = 0; i < 60; i++) {
            if (!orientationOnly) {
                corners[i] = i;
                edges[i] = i;
                if (i < 12) centers[i] = i;
            }
            if (i < 20) solvedCorners[i] = false;
            if (i < 30) solvedEdges[i] = false;
        }
    }

    @Override
    void permute(String permutation) {
        // Edges are permuted
        int[] exchanges = new int[]{ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ};
        Integer[] perm = permutations.get(permutation).get(EDGE);
        for (int i = 0; i < 60; i++) if (perm[i] != ZZ) exchanges[perm[i]] = edges[i];
        for (int i = 0; i < 60; i++) if (exchanges[i] != ZZ) edges[i] = exchanges[i];
        // Centers are permuted
        exchanges = new int[]{ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ};
        perm = permutations.get(permutation).get(CORNER);
        for (int i = 0; i < 60; i++) if (perm[i] != ZZ) exchanges[perm[i]] = corners[i];
        for (int i = 0; i < 60; i++) if (exchanges[i] != ZZ) corners[i] = exchanges[i];
        // Centers are permuted
        exchanges = new int[]{ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ, ZZ};
        perm = permutations.get(permutation).get(CENTER);
        for (int i = 0; i < 12; i++) if (perm[i] != ZZ) exchanges[perm[i]] = centers[i];
        for (int i = 0; i < 12; i++) if (exchanges[i] != ZZ) centers[i] = exchanges[i];
    }

    @Override
    void solveCube() {
        System.arraycopy(this.corners, 0, this.scrambledStateCorners, 0, 60);
        System.arraycopy(this.edges, 0, this.scrambledStateEdges, 0, 60);
        System.arraycopy(this.centers, 0, this.scrambledStateCenters, 0, 12);
        reorientCube();
        solveCorners();
        solveEdges();
    }

    @Override
    String getRotationsFromOrientation(int top, int front, int[] checkArray) {
        String[][] reorientation = {
                {"", "y'", "", "y", "y2", "y2'", "", "", "", "", "", ""},
                {"z y", "", "z", "", "", "z y2", "z y2'", "z y'", "", "", "", ""},
                {"rx y2", "rx y2'", "", "rx y", "", "", "", "rx y'", "rx", "", "", ""},
                {"z' y'", "", "z'", "", "z' y2'", "", "", "", "z' y", "z' y2", "", ""},
                {"rx'", "", "", "rx' y", "", "rx' y'", "rx' y2", "rx' y2'", "", "", "", ""},
                {"lx", "lx y'", "", "", "lx y", "", "lx y2'", "", "", "", "lx y2", ""},
                {"", "lx2 y'", "", "", "", "lx2", "", "lx2 y2'", "", "", "lx2 y", "lx2 y2"},
                {"", "z2 y", "z2", "", "", "", "z2 y2", "", "z2 y'", "", "", "z2 y2'"},
                {"", "", "z2'", "z2' y'", "", "", "", "z2' y", "", "z2' y2'", "", "z2' y2"},
                {"", "", "", "rx2' y", "rx2'", "", "", "", "rx2' y2", "", "rx2' y'", "rx2' y2'"},
                {"", "", "", "", "rbz2' y2", "rbz2' y", "rbz2'", "", "", "rbz2' y2'", "", "rbz2' y'"},
                {"", "", "", "", "", "", "lx2' z'", "lx' z2'", "rx z2", "rx2 z", "rx2' rbz'", ""},
        };
        int topPosition = -1, frontPosition = -1;
        for (int i = 0; i < 12; i++) {
            if (checkArray[i] == top && topPosition == -1) topPosition = i;
            if (checkArray[i] == front && frontPosition == -1) frontPosition = i;
        }
        if (topPosition == -1 || frontPosition == -1 || topPosition == frontPosition) return "";
        else return reorientation[topPosition][frontPosition];
    }

    protected void reorientCube() {
        this.centerRotations = "";
        String neededRotation = this.getRotationsFromOrientation(this.centerCubies[0], this.centerCubies[2], this.centers);

        if (neededRotation.length() > 0) {
            this.centerRotations = neededRotation;
            for (String rotation : neededRotation.split("\\s")) this.permute(rotation);
        }
    }

    public void setSolvingOrientation(int top, int front) {
        String neededRotation = this.getRotationsFromOrientation(top, front, this.centerCubies);
        if (neededRotation.length() > 0) {
            this.solvingOrPremoves = this.invertMoves(neededRotation);
            this.parseScramble(this.getScramble());
        }
    }

    // Solves all 8 corners in the cube
    // Ignores mis-oriented corners
    protected void solveCorners() {
        if (!cornersSolved()) System.arraycopy(solvedCorners, 0, scrambledStateSolvedCorners, 0, solvedCorners.length);
        else
            scrambledStateSolvedCorners = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
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
            for (int i = 0; i < 19 && !cornerCycled; i++) {
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
            for (int i = 0; i < 20 && !cornerCycled; i++) {
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
        for (int i = 0; i < 20; i++) {
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

    // Solves all 12 edges in the cube
    protected void solveEdges() {
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
            for (int i = 0; i < 29 && !edgeCycled; i++) {
                if (!solvedEdges[i]) {
                    // Buffer is placed in a... um... buffer
                    int[] tempEdge = {edges[edgeCubies[0][0]], edges[edgeCubies[0][1]]};

                    // Buffer edge is replaced with edge
                    edges[edgeCubies[0][0]] = edges[edgeCubies[i][0]];
                    edges[edgeCubies[0][1]] = edges[edgeCubies[i][1]];

                    // Edge is replaced with buffer
                    edges[edgeCubies[i][0]] = tempEdge[0];
                    edges[edgeCubies[i][1]] = tempEdge[1];

                    // Edge cycle is inserted into solution array
                    edgeCycles.add(edgeCubies[i][0]);
                    edgeCycled = true;
                }
            }
        }
        // If the buffer is not solved, swap it to the position where the edge belongs
        else {
            for (int i = 0; i < 30 && !edgeCycled; i++) {
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
        for (int i = 0; i < 30; i++) {
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

    @Override
    public String getRotations() {
        return this.centerRotations.length() > 0 ? this.centerRotations : "/";
    }

    @Override
    public String getSolutionPairs(boolean withRotation) {
        return (withRotation ? this.getRotations() + "\n" : "")
                + "Edges: " + this.getEdgePairs()
                + "\nCorners: " + this.getCornerPairs();
    }

    @Override
    public String getStatstics() {
        return "Corners: " + this.getCornerLength() + "@" + this.getCornerBreakInNum() + " w/ " + this.getNumPreSolvedCorners() + "-" + this.getNumPreTwistedCorners() + " > " + this.hasCornerParity()
                + "\nEdges: " + this.getEdgeLength() + "@" + this.getEdgeBreakInNum() + " w/ " + this.getNumPreSolvedEdges() + "-" + this.getNumPreFlippedEdges() + " > " + this.hasCornerParity();
    }

    @Override
    public String getNoahtation() {
        return "C:" + this.getCornerNoahtation() + " / E:" + this.getEdgeNoahtation();
    }

    public String getStatString(boolean spaced, boolean newLine) {
        return "C: " + this.getCornerStatString(spaced) + (newLine ? "\n" : " | ") + "E: " + this.getEdgeStatString(spaced);
    }

    public String getCornerStatString(boolean spaced) {
        String cornerStat = this.hasCornerParity() ? "_" : " ";
        cornerStat += this.getCornerLength();
        cornerStat += this.isCornerBufferSolved() ? "*" : " ";
        cornerStat += spaced ? "\t" : " ";
        for (int i = 0; i < 9; i++) cornerStat += i < this.getCornerBreakInNum() ? "#" : spaced ? " " : "";
        if (spaced || cornerStat.endsWith("#")) cornerStat += " ";
        for (int i = 0; i < 19; i++) cornerStat += i < this.getNumPreTwistedCorners() ? "~" : spaced ? " " : "";
        if (spaced || cornerStat.endsWith("~")) cornerStat += " ";
        for (int i = 0; i < 19; i++) cornerStat += i < this.getNumPreSolvedCorners() ? "+" : spaced ? " " : "";
        return cornerStat;
    }

    public String getCornerStatString() {
        return this.getCornerStatString(false);
    }

    private String getEdgeStatString(boolean spaced) {
        String edgeStat = "" + this.getEdgeLength();
        edgeStat += this.isEdgeBufferSolved() ? "*" : " ";
        edgeStat += spaced ? "\t" : " ";
        for (int i = 0; i < 9; i++) edgeStat += i < this.getEdgeBreakInNum() ? "#" : spaced ? " " : "";
        if (spaced || edgeStat.endsWith("#")) edgeStat += " ";
        for (int i = 0; i < 18; i++) edgeStat += i < this.getNumPreFlippedEdges() ? "~" : spaced ? " " : "";
        if (spaced || edgeStat.endsWith("~")) edgeStat += " ";
        for (int i = 0; i < 18; i++) edgeStat += i < this.getNumPreSolvedEdges() ? "+" : spaced ? " " : "";
        return edgeStat;
    }

    public String getEdgeStatString() {
        return this.getEdgeStatString(false);
    }

    @Override
    public String getScramble() {
        return this.scramble;
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
        } else return "Solved";
        return edgePairs;
    }

    public boolean hasCornerParity() {
        return this.cornerCycles.size() % 2 == 1 && this.edgeCycles.size() % 2 == 1;
    }

    public boolean isCornerBufferSolved() {
        return this.scrambledStateSolvedCorners[0];
    }

    public boolean isSingleCycle() {
        return this.isCornerSingleCycle() && this.isEdgeSingleCycle();
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

    public int getEdgeLength() {
        return this.edgeCycles.size();
    }

    public boolean isEdgeBufferSolved() {
        return this.scrambledStateSolvedEdges[0];
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

    public void setCornerScheme(String scheme) {
        this.setCornerScheme(scheme.split(""));
    }

    public void setCornerScheme(String[] scheme) {
        if (scheme.length == 60) this.cornerLettering = scheme;
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

    public void setEdgeScheme(String scheme) {
        this.setEdgeScheme(scheme.split(""));
    }

    public void setEdgeScheme(String[] scheme) {
        if (scheme.length == 60) this.edgeLettering = scheme;
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
}
