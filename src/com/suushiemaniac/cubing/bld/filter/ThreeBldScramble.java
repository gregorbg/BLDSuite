package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.exception.InvalidNotationException;
import com.suushiemaniac.cubing.bld.analyze.cube.BldCube;
import com.suushiemaniac.cubing.bld.analyze.cube.ThreeBldCube;
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition;
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition;
import com.suushiemaniac.cubing.bld.model.AlgSource;
import com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType;
import com.suushiemaniac.cubing.bld.util.BruteForceUtil;
import com.suushiemaniac.cubing.bld.util.SpeffzUtil;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.NoInspectionThreeByThreeCubePuzzle;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition.*;
import static com.suushiemaniac.cubing.bld.filter.condition.IntCondition.*;

public class ThreeBldScramble extends TwoBldScramble {
    public static ThreeBldScramble averageScramble() {
        Random rand = new Random();
        int size = 100000;
        boolean[] parity = new boolean[size];
        for (int i = 0; i < parity.length; i++) parity[i] = i < 50270;
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
                EXACT(getNumInStatArray(eTargets, rand.nextInt(size), 4, 2)),
                EXACT(getNumInStatArray(eBreakIn, rand.nextInt(size))),
                EXACT(getNumInStatArray(eSolved, rand.nextInt(size))),
                EXACT(getNumInStatArray(eMisOrient, rand.nextInt(size)))
        );
    }

    public static ThreeBldScramble mostCommonScramble() {
        return new ThreeBldScramble(
                EXACT(8),
                EXACT(1),
                NO(),
                EXACT(0),
                EXACT(0),
                EXACT(12),
                EXACT(1),
                EXACT(0),
                EXACT(0)
        );
    }

    public static ThreeBldScramble mostCommonEdge() {
        return new ThreeBldScramble(
                ANY(),
                ANY(),
                UNIMPORTANT(),
                ANY(),
                ANY(),
                EXACT(12),
                EXACT(1),
                EXACT(0),
                EXACT(0)
        );
    }

    public static ThreeBldScramble mostCommonCorner() {
        return new ThreeBldScramble(
                EXACT(8),
                EXACT(1),
                NO(),
                EXACT(0),
                EXACT(0),
                ANY(),
                ANY(),
                ANY(),
                ANY()
        );
    }

    public static ThreeBldScramble levelScramble(int level) {
        level = Math.min(11, level);
        level = Math.max(0, level);
        String[] cLevel = {"8 #", "_7", "_7 # ~", "6 ~", "_9 ##", "_7 # +", "8 #", "8 #", "8 #", "8 #", "6 +", "8 #"};
        String[] eLevel = {"12 #", "12 #", "12 #", "12 #", "12 #", "12 #", "12 ## +", "10 ~", "10 +", "12 ## ~", "12 #", "10 # ~ +"};
        return ThreeBldScramble.fromStatString("C: " + cLevel[level] + " | E: " + eLevel[level]);
    }

    public static ThreeBldScramble fromStatString(String statString) {
        Pattern statPattern = Pattern.compile("C:(_?)(0|[1-9][0-9]*)\\*?(#*)(~*)(\\+*)\\|E:(0|[1-9][0-9]*)\\*?(#*)(~*)(\\+*)");
        Matcher statMatcher = statPattern.matcher(statString.replaceAll("\\s", ""));
        if (statMatcher.find()) {
            boolean hasParity = statMatcher.group(1).length() > 0;
            int cornerLength = Integer.parseInt(statMatcher.group(2));
            int cornerBreakIn = statMatcher.group(3).length();
            int cornerTwisted = statMatcher.group(4).length();
            int cornerSolved = statMatcher.group(5).length();

            int edgeLength = Integer.parseInt(statMatcher.group(6));
            int edgeBreakIn = statMatcher.group(7).length();
            int edgeFlipped = statMatcher.group(8).length();
            int edgeSolved = statMatcher.group(9).length();

            return new ThreeBldScramble(
                    EXACT(cornerLength),
                    EXACT(cornerBreakIn),
                    hasParity ? YES() : NO(),
                    EXACT(cornerSolved),
                    EXACT(cornerTwisted),
                    EXACT(edgeLength),
                    EXACT(edgeBreakIn),
                    EXACT(edgeSolved),
                    EXACT(edgeFlipped)
            );
        } else return null;
    }

    protected BooleanCondition edgeSingleCycle;
    protected IntCondition edgeTargets, edgeBreakIns, solvedEdges, flippedEdges;
    protected String edgeMemoRegex, edgePredicateRegex;

    public ThreeBldScramble(IntCondition cornerTargets,
                            IntCondition cornerBreakIns,
                            BooleanCondition hasCornerParity,
                            IntCondition solvedCorners,
                            IntCondition twistedCorners,
                            IntCondition edgeTargets,
                            IntCondition edgeBreakIns,
                            IntCondition solvedEdges,
                            IntCondition flippedEdges) {
        super(cornerTargets, cornerBreakIns, hasCornerParity, solvedCorners, twistedCorners);
        this.setEdgeTargets(edgeTargets);
        this.setEdgeBreakIns(edgeBreakIns);
        this.setEdgeSingleCycle();
        this.setSolvedFlippedEdges(solvedEdges, flippedEdges);

        this.edgeMemoRegex = BldScramble.REGEX_UNIV;
        this.edgePredicateRegex = BldScramble.REGEX_UNIV;
    }

    public void setEdgeTargets(IntCondition edgeTargets) {
        edgeTargets.capMin(0);
        edgeTargets.capMax(16);

        if (edgeTargets.getMin() == edgeTargets.getMax() && edgeTargets.getMin() % 2 == 1) {
            edgeTargets.setMin(edgeTargets.getMin() - 1);
            edgeTargets.setMax(this.hasCornerParity.getNegative() ? edgeTargets.getMin() : edgeTargets.getMax() + 1);
        }

        this.edgeTargets = edgeTargets;
    }

    public void setEdgeBreakIns(IntCondition edgeBreakIns) {
        edgeBreakIns.capMin(Math.max(0, this.edgeTargets.getMin() - 11));
        edgeBreakIns.capMax(5);
        this.edgeBreakIns = edgeBreakIns;
    }

    public void setEdgeSingleCycle() {
        this.edgeSingleCycle = edgeBreakIns.getMax() == 0 ? YES() : edgeBreakIns.getMin() == 0 ? UNIMPORTANT() : NO();
    }

    public void setEdgeMemoRegex(String regex) {
        this.edgeMemoRegex = regex;
    }

    public void filterEdgeExecution(AlgSource algSource, Predicate<Algorithm> filter) {
        SortedSet<String> matches = new TreeSet<>();
        String[] possPairs = BruteForceUtil.genBlockString(SpeffzUtil.FULL_SPEFFZ, 2, false);

        for (String pair : possPairs) {
            try {
                matches.addAll(algSource.getAlg(CubicPieceType.EDGE, pair).stream().filter(filter).map(alg -> pair).collect(Collectors.toList()));
            } catch (InvalidNotationException e) {
                continue;
            }
        }

        if (matches.size() > 0)
            this.edgePredicateRegex = "(" + String.join("|", matches) + ")*";
    }

    public void setSolvedFlippedEdges(IntCondition solvedEdges, IntCondition flippedEdges) {
        int leftOverMin = Math.max(0, 11 + this.edgeBreakIns.getMax() - this.edgeTargets.getMax());

        solvedEdges.capMin(0);
        flippedEdges.capMin(0);

        solvedEdges.capMax(12);
        flippedEdges.capMax(12);

        int sumMin = solvedEdges.getMin() + flippedEdges.getMin();
        int sumMax = solvedEdges.getMax() + flippedEdges.getMax();

        if (sumMin > leftOverMin) {
            if (solvedEdges.isPrecise() || !flippedEdges.isPrecise()) flippedEdges.setMin(flippedEdges.getMin() - sumMin + leftOverMin);
            if (flippedEdges.isPrecise() || !solvedEdges.isPrecise()) solvedEdges.setMin(solvedEdges.getMin() - sumMin + leftOverMin);
        } else if (sumMax < leftOverMin) {
            if (solvedEdges.isPrecise() || !flippedEdges.isPrecise()) flippedEdges.setMax(flippedEdges.getMax() + leftOverMin - sumMax);
            if (flippedEdges.isPrecise() || !solvedEdges.isPrecise()) solvedEdges.setMax(flippedEdges.getMax() + leftOverMin - sumMax);
        }

        this.solvedEdges = solvedEdges;
        this.flippedEdges = flippedEdges;
    }

    public static ThreeBldScramble cloneFrom(String scramble, boolean strict) {
        ThreeBldCube refCube = new ThreeBldCube(scramble);
        BooleanCondition hasCornerParity = refCube.hasCornerParity() ? strict ? YES() : UNIMPORTANT() : NO();
        IntCondition cornerBreakIns = strict ? EXACT(refCube.getCornerBreakInNum()) : MAXIMUM(refCube.getCornerBreakInNum());
        IntCondition cornerTargets = strict ? EXACT(refCube.getCornerLength()) : MAXIMUM(refCube.getCornerLength());
        IntCondition preCorners = strict ? EXACT(refCube.getNumPreSolvedCorners()) : MINIMUM(refCube.getNumPreSolvedCorners());
        IntCondition preTwisted = strict ? EXACT(refCube.getNumPreTwistedCorners()) : MAXIMUM(refCube.getNumPreTwistedCorners());
        IntCondition edgeBreakIns = strict ? EXACT(refCube.getEdgeBreakInNum()) : MAXIMUM(refCube.getEdgeBreakInNum());
        IntCondition edgeTargets = strict ? EXACT(refCube.getEdgeLength()) : MAXIMUM(refCube.getEdgeLength());
        IntCondition preEdges = strict ? EXACT(refCube.getNumPreSolvedEdges()) : MINIMUM(refCube.getNumPreSolvedEdges());
        IntCondition preFlipped = strict ? EXACT(refCube.getNumPreFlippedEdges()) : MAXIMUM(refCube.getNumPreFlippedEdges());
        return new ThreeBldScramble(cornerTargets, cornerBreakIns, hasCornerParity, preCorners, preTwisted, edgeTargets, edgeBreakIns, preEdges, preFlipped);
    }

    @Override
    protected <T extends BldCube> boolean matchingConditions(T inCube) {
        if (inCube instanceof ThreeBldCube) {
            ThreeBldCube randCube = (ThreeBldCube) inCube;
            return this.hasCornerParity.evaluatePositive(randCube.hasCornerParity())
                    && this.cornerSingleCycle.evaluatePositive(randCube.isCornerSingleCycle())
                    && this.cornerBreakIns.evaluate(randCube.getCornerBreakInNum())
                    && this.edgeSingleCycle.evaluatePositive(randCube.isEdgeSingleCycle())
                    && this.edgeBreakIns.evaluate(randCube.getEdgeBreakInNum())
                    && this.cornerTargets.evaluate(randCube.getCornerLength())
                    && this.edgeTargets.evaluate(randCube.getEdgeLength())
                    && this.solvedCorners.evaluate(randCube.getNumPreSolvedCorners())
                    && this.solvedEdges.evaluate(randCube.getNumPreSolvedEdges())
                    && this.twistedCorners.evaluate(randCube.getNumPreTwistedCorners())
                    && this.flippedEdges.evaluate(randCube.getNumPreFlippedEdges())
                    && randCube.getCornerPairs(false).replaceAll("\\s*", "").matches(this.cornerMemoRegex)
                    && randCube.getCornerPairs(false).replaceAll("\\s*", "").matches(this.cornerPredicateRegex)
                    && randCube.getEdgePairs(false).replaceAll("\\s*", "").matches(this.edgeMemoRegex)
                    && randCube.getEdgePairs(false).replaceAll("\\s*", "").matches(this.edgePredicateRegex);
        } else return false;
    }

    @Override
    protected Puzzle getScramblingPuzzle() {
        return new NoInspectionThreeByThreeCubePuzzle();
    }

    @Override
    protected BldCube getAnalyzingPuzzle() {
        return new ThreeBldCube("");
    }
}
