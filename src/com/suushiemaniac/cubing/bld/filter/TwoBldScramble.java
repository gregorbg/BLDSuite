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

    public TwoBldScramble(IntCondition cornerTargets,
                          IntCondition cornerBreakIns,
                          BooleanCondition hasCornerParity,
                          IntCondition solvedCorners,
                          IntCondition twistedCorners) {
        this.setCornerTargets(cornerTargets);
        this.setCornerBreakIns(cornerBreakIns);
        this.setCornerSingleCycle();
        this.setCornerParity(hasCornerParity);
        this.setSolvedCorners(solvedCorners, false);
        this.setTwistedCorners(twistedCorners, false);
        this.balanceLeftOverCorners();
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

    protected void setSolvedCorners(IntCondition solvedCorners, boolean balanceAfter) {
        solvedCorners.capMin(Math.max(0, 7 + this.cornerBreakIns.getMax() - this.cornerTargets.getMax()));
        solvedCorners.capMax(Math.max(0, 7 + this.cornerBreakIns.getMin() - this.cornerTargets.getMin()));
        this.solvedCorners = solvedCorners;
        if (balanceAfter) this.balanceLeftOverCorners();
    }

    public void setSolvedCorners(IntCondition solvedCorners) {
        this.setSolvedCorners(solvedCorners, true);
    }

    protected void setTwistedCorners(IntCondition twistedCorners, boolean balanceAfter) {
        twistedCorners.capMin(Math.max(0, 7 + this.cornerBreakIns.getMax() - this.cornerTargets.getMax()));
        twistedCorners.capMax(Math.max(0, 7 + this.cornerBreakIns.getMin() - this.cornerTargets.getMin()));
        this.twistedCorners = twistedCorners;
        if (balanceAfter) this.balanceLeftOverCorners();
    }

    public void setTwistedCorners(IntCondition twistedCorners) {
        this.setTwistedCorners(twistedCorners, true);
    }

    protected void balanceLeftOverCorners() {
        int leftOverMin = Math.max(0, 7 + this.cornerBreakIns.getMax() - this.cornerTargets.getMax());
        int pieceMinSum = this.twistedCorners.getMin() + this.solvedCorners.getMin();
        while (pieceMinSum > leftOverMin) {
            this.twistedCorners.setMin(Math.max(0, this.twistedCorners.getMin() - pieceMinSum + leftOverMin));
            this.solvedCorners.setMin(Math.max(0, this.solvedCorners.getMin() - pieceMinSum + leftOverMin));
            pieceMinSum = this.twistedCorners.getMin() + this.solvedCorners.getMin();
        }
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
                    && this.twistedCorners.evaluate(randCube.getNumPreTwistedCorners());
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
