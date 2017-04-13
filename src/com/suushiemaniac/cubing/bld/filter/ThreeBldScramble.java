package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.exception.InvalidNotationException;
import com.suushiemaniac.cubing.bld.analyze.cube.BldCube;
import com.suushiemaniac.cubing.bld.analyze.cube.ThreeBldCube;
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition;
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition;
import com.suushiemaniac.cubing.bld.model.AlgSource;
import com.suushiemaniac.cubing.bld.util.BruteForceUtil;
import com.suushiemaniac.cubing.bld.util.SpeffzUtil;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.NoInspectionThreeByThreeCubePuzzle;

import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition.*;
import static com.suushiemaniac.cubing.bld.filter.condition.IntCondition.*;
import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.CORNER;
import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.EDGE;

public class ThreeBldScramble extends TwoBldScramble {
    // TODO buffer solved
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

    public static ThreeBldScramble fromStatString(String statString) {
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

    protected BooleanCondition edgeSingleCycle, edgeBufferSolved;
    protected IntCondition edgeTargets, edgeBreakIns, solvedEdges, flippedEdges;
    protected String edgeMemoRegex, edgePredicateRegex;

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
        super(cornerTargets, cornerBreakIns, hasCornerParity, solvedCorners, twistedCorners, cornerBufferSolved);
        this.setEdgeTargets(edgeTargets);
        this.setEdgeBreakIns(edgeBreakIns);
        this.setEdgeSingleCycle();
        this.setSolvedFlippedEdges(solvedEdges, flippedEdges);
        this.setEdgeBufferSolved(edgeBufferSolved);

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

    public void setEdgeBufferSolved(BooleanCondition edgeBufferSolved) {
        this.edgeBufferSolved = edgeBufferSolved;
    }

    public void setEdgeMemoRegex(String regex) {
        this.edgeMemoRegex = regex;
    }

    public void filterEdgeExecution(AlgSource algSource, Predicate<Algorithm> filter) {
        SortedSet<String> matches = new TreeSet<>();
        String[] possPairs = BruteForceUtil.genBlockString(SpeffzUtil.FULL_SPEFFZ, 2, false);

        for (String pair : possPairs) {
            try {
                matches.addAll(algSource.getAlg(EDGE, pair).stream().filter(filter).map(alg -> pair).collect(Collectors.toList()));
            } catch (InvalidNotationException e) {
                continue;
            }
        }

        if (matches.size() > 0) {
			this.edgePredicateRegex = "(" + String.join("|", matches) + ")*";
		}
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

    public static ThreeBldScramble cloneFrom(Algorithm scramble, boolean strict) {
        ThreeBldCube refCube = new ThreeBldCube(scramble);
        BooleanCondition hasCornerParity = refCube.hasParity(CORNER) ? strict ? YES() : UNIMPORTANT() : NO();
        IntCondition cornerBreakIns = strict ? EXACT(refCube.getBreakInCount(CORNER)) : MAXIMUM(refCube.getBreakInCount(CORNER));
        IntCondition cornerTargets = strict ? EXACT(refCube.getStatLength(CORNER)) : MAXIMUM(refCube.getStatLength(CORNER));
        IntCondition preCorners = strict ? EXACT(refCube.getPreSolvedCount(CORNER)) : MINIMUM(refCube.getPreSolvedCount(CORNER));
        IntCondition preTwisted = strict ? EXACT(refCube.getMisOrientedCount(CORNER)) : MAXIMUM(refCube.getMisOrientedCount(CORNER));
        BooleanCondition isCornerBufferSolved = refCube.isBufferSolved(CORNER) ? strict ? YES() : UNIMPORTANT() : NO();
        IntCondition edgeBreakIns = strict ? EXACT(refCube.getBreakInCount(EDGE)) : MAXIMUM(refCube.getBreakInCount(EDGE));
        IntCondition edgeTargets = strict ? EXACT(refCube.getStatLength(EDGE)) : MAXIMUM(refCube.getStatLength(EDGE));
        IntCondition preEdges = strict ? EXACT(refCube.getPreSolvedCount(EDGE)) : MINIMUM(refCube.getPreSolvedCount(EDGE));
        IntCondition preFlipped = strict ? EXACT(refCube.getMisOrientedCount(EDGE)) : MAXIMUM(refCube.getMisOrientedCount(EDGE));
        BooleanCondition isEdgeBufferSolved = refCube.isBufferSolved(EDGE) ? strict ? YES() : UNIMPORTANT() : NO();
        return new ThreeBldScramble(cornerTargets, cornerBreakIns, hasCornerParity, preCorners, preTwisted, isCornerBufferSolved, edgeTargets, edgeBreakIns, preEdges, preFlipped, isEdgeBufferSolved);
    }

    @Override
    protected <T extends BldCube> boolean matchingConditions(T inCube) {
        if (inCube instanceof ThreeBldCube) {
            ThreeBldCube randCube = (ThreeBldCube) inCube;
            return this.hasCornerParity.evaluatePositive(randCube.hasParity(CORNER))
                    && this.cornerSingleCycle.evaluatePositive(randCube.isSingleCycle(CORNER))
                    && this.cornerBreakIns.evaluate(randCube.getBreakInCount(CORNER))
                    && this.edgeSingleCycle.evaluatePositive(randCube.isSingleCycle(EDGE))
                    && this.edgeBreakIns.evaluate(randCube.getBreakInCount(EDGE))
                    && this.cornerTargets.evaluate(randCube.getStatLength(CORNER))
                    && this.edgeTargets.evaluate(randCube.getStatLength(EDGE))
                    && this.solvedCorners.evaluate(randCube.getPreSolvedCount(CORNER))
                    && this.solvedEdges.evaluate(randCube.getPreSolvedCount(EDGE))
                    && this.twistedCorners.evaluate(randCube.getMisOrientedCount(CORNER))
                    && this.flippedEdges.evaluate(randCube.getMisOrientedCount(EDGE))
                    && this.cornerBufferSolved.evaluatePositive(randCube.isBufferSolved(CORNER))
                    && this.edgeBufferSolved.evaluatePositive(randCube.isBufferSolved(EDGE))
                    && randCube.getSolutionPairs(CORNER).replaceAll("\\s*", "").matches(this.cornerMemoRegex)
                    && randCube.getSolutionPairs(CORNER).replaceAll("\\s*", "").matches(this.cornerPredicateRegex)
                    && randCube.getSolutionPairs(EDGE).replaceAll("\\s*", "").matches(this.edgeMemoRegex)
                    && randCube.getSolutionPairs(EDGE).replaceAll("\\s*", "").matches(this.edgePredicateRegex);
        } else return false;
    }

    @Override
    protected Puzzle getScramblingPuzzle() {
        return new NoInspectionThreeByThreeCubePuzzle();
    }

    @Override
    protected BldCube getAnalyzingPuzzle() {
        return new ThreeBldCube();
    }
}
