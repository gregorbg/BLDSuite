package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.bld.analyze.ThreeBldCube;
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition;
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition;
import puzzle.NoInspectionThreeByThreeCubePuzzle;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static ThreeBldScramble levelScramble(int level) {
        level = Math.min(11, level);
        level = Math.max(0, level);

        String[] cLevel = {"8 #", "_7", "_7 # ~", "6 ~", "_9 ##", "_7 # +", "8 #", "8 #", "8 #", "8 #", "6 +", "8 #"};
        String[] eLevel = {"12 #", "12 #", "12 #", "12 #", "12 #", "12 #", "12 ## +", "10 ~", "10 +", "12 ## ~", "12 #", "10 # ~ +"};

        return ThreeBldScramble.fromStatString("C: " + cLevel[level] + " | E: " + eLevel[level]);
    }

    public static ThreeBldScramble fromStatString(String statString) { // TODO move up to super class?
        Pattern statPattern = Pattern.compile("C:(_?)(0|[1-9][0-9]*)(\\*?)(#*)(~*)(\\+*)\\|E:(0|[1-9][0-9]*)(\\*?)(#*)(~*)(\\+*)");
        Matcher statMatcher = statPattern.matcher(statString.replaceAll("\\s", ""));

        if (statMatcher.find()) {
            boolean hasParity = statMatcher.group(1).length() > 0;
            int cornerLength = Integer.parseInt(statMatcher.group(2));
            boolean cornerBufferSolved = statMatcher.group(3).length() > 0;
            int cornerBreakIn = statMatcher.group(4).length();
            int cornerTwisted = statMatcher.group(5).length();
            int cornerSolved = statMatcher.group(6).length();

            int edgeLength = Integer.parseInt(statMatcher.group(7));
            boolean edgeBufferSolved = statMatcher.group(8).length() > 0;
            int edgeBreakIn = statMatcher.group(9).length();
            int edgeFlipped = statMatcher.group(10).length();
            int edgeSolved = statMatcher.group(11).length();

            return new ThreeBldScramble(
                    EXACT(cornerLength),
                    EXACT(cornerBreakIn),
                    hasParity ? YES() : NO(),
                    EXACT(cornerSolved),
                    EXACT(cornerTwisted),
                    cornerBufferSolved ? YES() : NO(),
                    EXACT(edgeLength),
                    EXACT(edgeBreakIn),
                    EXACT(edgeSolved),
                    EXACT(edgeFlipped),
                    edgeBufferSolved ? YES() : NO()
            );
        } else {
        	return null;
		}
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

        this.setTargets(CORNER, cornerTargets);
        this.setBreakIns(CORNER, cornerBreakIns);
        this.setParity(CORNER, hasCornerParity);
        this.setSolvedMisOriented(CORNER, solvedCorners, twistedCorners);
        this.setBufferSolved(CORNER, cornerBufferSolved);

        this.setTargets(EDGE, edgeTargets);
        this.setBreakIns(EDGE, edgeBreakIns);
        this.setParity(EDGE, hasCornerParity); // FIXME only preliminary
        this.setSolvedMisOriented(EDGE, solvedEdges, flippedEdges);
        this.setBufferSolved(EDGE, edgeBufferSolved);
    }
}
