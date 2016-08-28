package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.analyze.cube.BldCube;
import com.suushiemaniac.cubing.bld.analyze.cube.TwoBldCube;
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition;
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition;
import com.suushiemaniac.cubing.bld.model.AlgSource;
import com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType;
import com.suushiemaniac.cubing.bld.util.BruteForceUtil;
import com.suushiemaniac.cubing.bld.util.SpeffzUtil;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.TwoByTwoCubePuzzle;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition.*;
import static com.suushiemaniac.cubing.bld.filter.condition.IntCondition.*;
import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.CORNER;

public class TwoBldScramble extends BldScramble {
    protected BooleanCondition hasCornerParity, cornerSingleCycle;
    protected IntCondition cornerTargets, cornerBreakIns, solvedCorners, twistedCorners;
    protected String cornerMemoRegex, cornerPredicateRegex;

    public TwoBldScramble(IntCondition cornerTargets,
                          IntCondition cornerBreakIns,
                          BooleanCondition hasCornerParity,
                          IntCondition solvedCorners,
                          IntCondition twistedCorners) {
        this.setCornerTargets(cornerTargets);
        this.setCornerBreakIns(cornerBreakIns);
        this.setCornerSingleCycle();
        this.setCornerParity(hasCornerParity);
        this.setSolvedTwistedCorners(solvedCorners, twistedCorners);

        this.cornerMemoRegex = BldScramble.REGEX_UNIV;
        this.cornerPredicateRegex = BldScramble.REGEX_UNIV;
    }

    public void setCornerTargets(IntCondition cornerTargets) {
        cornerTargets.capMin(0);
        cornerTargets.capMax(10);
        this.cornerTargets = cornerTargets;
    }

    public void setCornerBreakIns(IntCondition cornerBreakIns) {
        cornerBreakIns.capMin(Math.max(0, this.cornerTargets.getMin() - 7));
        cornerBreakIns.capMax(3);
        this.cornerBreakIns = cornerBreakIns;
    }

    public void setCornerSingleCycle() {
        this.cornerSingleCycle = this.cornerBreakIns.getMax() == 0 ? YES() : this.cornerBreakIns.getMin() == 0 ? UNIMPORTANT() : NO();
    }

    public void setCornerParity(BooleanCondition hasCornerParity) {
        if (this.cornerTargets.getMin() == this.cornerTargets.getMax()) hasCornerParity.setValue(this.cornerTargets.getMax() % 2 == 1);
        this.hasCornerParity = hasCornerParity;
    }

    public void setCornerMemoRegex(String regex) {
        this.cornerMemoRegex = regex;
    }

    public void filterCornerExecution(AlgSource algSource, Predicate<Algorithm> filter) {
        SortedSet<String> matches = new TreeSet<>();
        String[] possPairs = BruteForceUtil.genBlockString(SpeffzUtil.FULL_SPEFFZ, 2, false);

        for (String pair : possPairs) {
            matches.addAll(algSource.getAlg(CORNER, pair).stream().filter(filter).map(alg -> pair).collect(Collectors.toList()));
        }

        if (matches.size() > 0)
            this.cornerPredicateRegex = "(" + String.join("|", matches) + ")*" + (this.hasCornerParity.getPositive() ? "[A-Z]?" : "");
    }
    
    public void setSolvedTwistedCorners(IntCondition solvedCorners, IntCondition twistedCorners) {
        int leftOverMin = Math.max(0, 7 + this.cornerBreakIns.getMax() - this.cornerTargets.getMax());

        solvedCorners.capMin(0);
        twistedCorners.capMin(0);

        solvedCorners.capMax(8);
        twistedCorners.capMax(8);

        int sumMin = solvedCorners.getMin() + twistedCorners.getMin();
        int sumMax = solvedCorners.getMax() + twistedCorners.getMax();

        if (sumMin > leftOverMin) {
            if (solvedCorners.isPrecise() || !twistedCorners.isPrecise())
                twistedCorners.setMin(twistedCorners.getMin() - sumMin + leftOverMin);
            if (twistedCorners.isPrecise() || !solvedCorners.isPrecise())
                solvedCorners.setMin(solvedCorners.getMin() - sumMin + leftOverMin);
        } else if (sumMax < leftOverMin) {
            if (solvedCorners.isPrecise() || !twistedCorners.isPrecise())
                twistedCorners.setMax(twistedCorners.getMax() + leftOverMin - sumMax);
            if (twistedCorners.isPrecise() || !solvedCorners.isPrecise())
                solvedCorners.setMax(twistedCorners.getMax() + leftOverMin - sumMax);
        }

        this.solvedCorners = solvedCorners;
        this.twistedCorners = twistedCorners;
    }

    public static BldScramble cloneFrom(Algorithm scramble, boolean strict) {
        TwoBldCube refCube = new TwoBldCube(scramble);
        BooleanCondition hasCornerParity = refCube.hasParity(CORNER) ? strict ? YES() : UNIMPORTANT() : NO();
        IntCondition cornerBreakIns = strict ? EXACT(refCube.getBreakInCount(CORNER)) : MAXIMUM(refCube.getBreakInCount(CORNER));
        IntCondition cornerTargets = strict ? EXACT(refCube.getStatLength(CORNER)) : MAXIMUM(refCube.getStatLength(CORNER));
        IntCondition preCorners = strict ? EXACT(refCube.getPreSolvedCount(CORNER)) : MINIMUM(refCube.getPreSolvedCount(CORNER));
        IntCondition preTwisted = strict ? EXACT(refCube.getMisOrientedCount(CORNER)) : MAXIMUM(refCube.getMisOrientedCount(CORNER));
        return new TwoBldScramble(cornerTargets, cornerBreakIns, hasCornerParity, preCorners, preTwisted);
    }

    protected <T extends BldCube> boolean matchingConditions(T inCube) {
        if (inCube instanceof TwoBldCube) {
            TwoBldCube randCube = (TwoBldCube) inCube;
            return this.hasCornerParity.evaluatePositive(randCube.hasParity(CORNER))
                    && this.cornerSingleCycle.evaluatePositive(randCube.isSingleCycle(CORNER))
                    && this.cornerBreakIns.evaluate(randCube.getBreakInCount(CORNER))
                    && this.cornerTargets.evaluate(randCube.getStatLength(CORNER))
                    && this.solvedCorners.evaluate(randCube.getPreSolvedCount(CORNER))
                    && this.twistedCorners.evaluate(randCube.getMisOrientedCount(CORNER))
                    && randCube.getSolutionPairs(CORNER).replaceAll("\\s*", "").matches(this.cornerMemoRegex)
                    && randCube.getSolutionPairs(CORNER).replaceAll("\\s*", "").matches(this.cornerPredicateRegex);
        } else return false;
    }

    @Override
    protected Puzzle getScramblingPuzzle() {
        return new TwoByTwoCubePuzzle();
    }

    @Override
    protected BldCube getAnalyzingPuzzle() {
        return new TwoBldCube();
    }
}
