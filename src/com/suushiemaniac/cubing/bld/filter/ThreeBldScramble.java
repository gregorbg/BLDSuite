package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.bld.analyze.ThreeBldCube;
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition;
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition;
import puzzle.NoInspectionThreeByThreeCubePuzzle;

import java.util.Random;

import static com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition.*;
import static com.suushiemaniac.cubing.bld.filter.condition.IntCondition.ANY;
import static com.suushiemaniac.cubing.bld.filter.condition.IntCondition.EXACT;
import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.CORNER;
import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.EDGE;

public class ThreeBldScramble extends BldScramble {
    // TODO buffer preSolved
    public static ThreeBldScramble averageScramble() {
        Random rand = new Random();
        int size = 100000;
        boolean[] parity = new boolean[size];

        for (int i = 0; i < parity.length; i++) {
            parity[i] = i < 50270;
        }

        //corner-edge num
        int[] cTargets = {5, 12, 100, 551, 2548, 8496, 20713, 32608, 25658, 8599, 710};
        int[] eTargets = {6, 184, 3911, 34275, 55169, 6432, 23}; //offset +4, times 2
        int[] cBreakIn = {34138, 48629, 16226, 1007};
        int[] eBreakIn = {19416, 50274, 22156, 7761, 370, 23};
        int[] cSolved = {65901, 27681, 5617, 736, 61, 4};
        int[] eSolved = {58120, 31491, 8623, 1544, 198, 22, 2};
        int[] cMisOrient = {55683, 32807, 9443, 1796, 250, 19, 2};
        int[] eMisOrient = {63173, 28982, 6676, 1031, 126, 12};

        return new ThreeBldScramble(
                EXACT(getNumInStatArray(cTargets, rand.nextInt(size))),
                EXACT(getNumInStatArray(cBreakIn, rand.nextInt(size))),
                parity[rand.nextInt(size)] ? YES() : NO(),
                EXACT(getNumInStatArray(cSolved, rand.nextInt(size))),
                EXACT(getNumInStatArray(cMisOrient, rand.nextInt(size))),
                UNIMPORTANT(), // TODO
                EXACT(getNumInStatArray(eTargets, rand.nextInt(size), 4, 2)),
                EXACT(getNumInStatArray(eBreakIn, rand.nextInt(size))),
                EXACT(getNumInStatArray(eSolved, rand.nextInt(size))),
                EXACT(getNumInStatArray(eMisOrient, rand.nextInt(size))),
                UNIMPORTANT() // TODO
        );
    }

    public static ThreeBldScramble mostCommonScramble() {
        return new ThreeBldScramble(
                EXACT(8),
                EXACT(1),
                NO(),
                EXACT(0),
                EXACT(0),
                UNIMPORTANT(), // TODO
                EXACT(12),
                EXACT(1),
                EXACT(0),
                EXACT(0),
                UNIMPORTANT() // TODO
        );
    }

    public static ThreeBldScramble mostCommonEdge() {
        return new ThreeBldScramble(
                ANY(),
                ANY(),
                UNIMPORTANT(),
                ANY(),
                ANY(),
                UNIMPORTANT(), // TODO
                EXACT(12),
                EXACT(1),
                EXACT(0),
                EXACT(0),
                UNIMPORTANT() // TODO
        );
    }

    public static ThreeBldScramble mostCommonCorner() {
        return new ThreeBldScramble(
                EXACT(8),
                EXACT(1),
                NO(),
                EXACT(0),
                EXACT(0),
                UNIMPORTANT(), // TODO
                ANY(),
                ANY(),
                ANY(),
                ANY(),
                UNIMPORTANT() // TODO
        );
    }

    public static BldScramble levelScramble(int level) {
        level = Math.min(11, level);
        level = Math.max(0, level);

        String[] cLevel = {"8 #", "_7", "_7 # ~", "6 ~", "_9 ##", "_7 # +", "8 #", "8 #", "8 #", "8 #", "6 +", "8 #"};
        String[] eLevel = {"12 #", "12 #", "12 #", "12 #", "12 #", "12 #", "12 ## +", "10 ~", "10 +", "12 ## ~", "12 #", "10 # ~ +"};

        return BldScramble.fromStatString("C: " + cLevel[level] + " | E: " + eLevel[level], new ThreeBldCube(), true);
    }

    public ThreeBldScramble(IntCondition cornerTargets,
                            IntCondition cornerBreakIns,
                            BooleanCondition hasCornerParity,
                            IntCondition solvedCorners,
                            IntCondition twistedCorners,
                            BooleanCondition cornerBufferSolved,
                            IntCondition edgeTargets,
                            IntCondition edgeBreakIns,
                            IntCondition solvedEdges,
                            IntCondition flippedEdges,
                            BooleanCondition edgeBufferSolved) {
        super(new ThreeBldCube(), NoInspectionThreeByThreeCubePuzzle::new);

        this.writeProperties(CORNER,
                cornerTargets,
                cornerBreakIns,
                hasCornerParity,
                solvedCorners,
                twistedCorners,
                cornerBufferSolved
        );

        this.writeProperties(EDGE,
                edgeTargets,
                edgeBreakIns,
                hasCornerParity, // FIXME only preliminary
                solvedEdges,
                flippedEdges,
                edgeBufferSolved
        );
    }
}
