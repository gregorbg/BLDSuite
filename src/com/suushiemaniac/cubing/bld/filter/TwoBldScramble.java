package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.bld.analyze.cube.BldCube;
import com.suushiemaniac.cubing.bld.analyze.cube.TwoBldCube;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.TwoByTwoCubePuzzle;

import static com.suushiemaniac.cubing.bld.filter.BooleanCondition.*;
import static com.suushiemaniac.cubing.bld.filter.IntCondition.*;

public class TwoBldScramble extends BldScramble {
    protected BooleanCondition hasCornerParity, cornerSingleCycle;
    protected IntCondition cornerTargets, cornerBreakIns, solvedCorners, twistedCorners;
    protected String cornerMemoRegex;

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

    public static BldScramble cloneFrom(String scramble, boolean strict) {
        TwoBldCube refCube = new TwoBldCube(scramble);
        BooleanCondition hasCornerParity = refCube.hasCornerParity() ? strict ? YES() : UNIMPORTANT() : NO();
        IntCondition cornerBreakIns = strict ? EXACT(refCube.getCornerBreakInNum()) : MAXIMUM(refCube.getCornerBreakInNum());
        IntCondition cornerTargets = strict ? EXACT(refCube.getCornerLength()) : MAXIMUM(refCube.getCornerLength());
        IntCondition preCorners = strict ? EXACT(refCube.getNumPreSolvedCorners()) : MINIMUM(refCube.getNumPreSolvedCorners());
        IntCondition preTwisted = strict ? EXACT(refCube.getNumPreTwistedCorners()) : MAXIMUM(refCube.getNumPreTwistedCorners());
        return new TwoBldScramble(cornerTargets, cornerBreakIns, hasCornerParity, preCorners, preTwisted);
    }

    protected <T extends BldCube> boolean matchingConditions(T inCube) {
        if (inCube instanceof TwoBldCube) {
            TwoBldCube randCube = (TwoBldCube) inCube;
            return this.hasCornerParity.evaluatePositive(randCube.hasCornerParity())
                    && this.cornerSingleCycle.evaluatePositive(randCube.isCornerSingleCycle())
                    && this.cornerBreakIns.evaluate(randCube.getCornerBreakInNum())
                    && this.cornerTargets.evaluate(randCube.getCornerLength())
                    && this.solvedCorners.evaluate(randCube.getNumPreSolvedCorners())
                    && this.twistedCorners.evaluate(randCube.getNumPreTwistedCorners())
                    && randCube.getCornerPairs(false).replaceAll("\\s*", "").matches(this.cornerMemoRegex);
        } else return false;
    }

    @Override
    protected Puzzle getScramblingPuzzle() {
        return new TwoByTwoCubePuzzle();
    }

    @Override
    protected BldCube getAnalyzingPuzzle() {
        return new TwoBldCube("");
    }
}
