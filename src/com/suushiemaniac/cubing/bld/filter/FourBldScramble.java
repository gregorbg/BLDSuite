package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.bld.analyze.cube.BldCube;
import com.suushiemaniac.cubing.bld.analyze.cube.FourBldCube;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.NoInspectionFourByFourCubePuzzle;

import static com.suushiemaniac.cubing.bld.filter.BooleanCondition.*;
import static com.suushiemaniac.cubing.bld.filter.IntCondition.*;

public class FourBldScramble extends ThreeBldScramble {
    protected BooleanCondition hasWingParity, hasXCenterParity, wingSingleCycle, xCenterSingleCycle;
    protected IntCondition wingBreakIns, xCenterBreakIns, wingTargets, solvedWings, xCenterTargets, solvedXCenters;

    public FourBldScramble(IntCondition cornerTargets,
                           IntCondition cornerBreakIns,
                           BooleanCondition hasCornerParity,
                           IntCondition solvedCorners,
                           IntCondition twistedCorners,
                           IntCondition wingTargets,
                           IntCondition wingBreakIns,
                           BooleanCondition hasWingParity,
                           IntCondition solvedWings,
                           IntCondition xCenterTargets,
                           IntCondition xCenterBreakIns,
                           BooleanCondition hasXCenterParity,
                           IntCondition solvedXCenters) {
        super(cornerTargets, cornerBreakIns, hasCornerParity, solvedCorners, twistedCorners, ANY(), ANY(), ANY(), ANY());
        this.setWingTargets(wingTargets);
        this.setWingBreakIns(wingBreakIns);
        this.setWingSingleCycle();
        this.setWingParity(hasWingParity);
        this.setSolvedWings(solvedWings);
        this.setXCenterTargets(xCenterTargets);
        this.setXCenterBreakIns(xCenterBreakIns);
        this.setXCenterSingleCycle();
        this.setXCenterParity(hasXCenterParity);
        this.setSolvedXCenters(solvedXCenters);
        this.bldAnalyzerCube = new FourBldCube("");
    }

    public void setWingTargets(IntCondition wingTargets) {
        wingTargets.capMin(0);
        wingTargets.capMax(34);
        this.wingTargets = wingTargets;
    }

    public void setWingBreakIns(IntCondition wingBreakIns) {
        wingBreakIns.capMin(Math.max(0, this.wingTargets.getMin() - 23));
        wingBreakIns.capMax(11);
        this.wingBreakIns = wingBreakIns;
    }

    public void setWingSingleCycle() {
        this.wingSingleCycle = this.wingBreakIns.getMax() == 0 ? YES() : this.wingBreakIns.getMin() == 0 ? UNIMPORTANT() : NO();
    }

    public void setWingParity(BooleanCondition hasWingParity) {
        if (this.wingTargets.getMin() == this.wingTargets.getMax()) hasWingParity.setValue(this.wingTargets.getMax() % 2 == 1);
        this.hasWingParity = hasWingParity;
    }

    public void setSolvedWings(IntCondition solvedWings) {
        solvedWings.capMin(Math.max(0, 23 + this.wingBreakIns.getMax() - this.wingTargets.getMax()));
        solvedWings.capMax(Math.max(0, 23 + this.wingBreakIns.getMin() - this.wingTargets.getMin()));
        this.solvedWings = solvedWings;
    }

    public void setXCenterTargets(IntCondition xCenterTargets) {
        xCenterTargets.capMin(0);
        xCenterTargets.capMax(34);
        this.xCenterTargets = xCenterTargets;
    }

    public void setXCenterBreakIns(IntCondition xCenterBreakIns) {
        xCenterBreakIns.capMin(Math.max(0, this.xCenterTargets.getMin() - 23));
        xCenterBreakIns.capMax(11);
        this.xCenterBreakIns = xCenterBreakIns;
    }

    public void setXCenterSingleCycle() {
        this.xCenterSingleCycle = this.xCenterBreakIns.getMax() == 0 ? YES() : this.xCenterBreakIns.getMin() == 0 ? UNIMPORTANT() : NO();
    }

    public void setXCenterParity(BooleanCondition hasXCenterParity) {
        if (this.xCenterTargets.getMin() == this.xCenterTargets.getMax()) hasXCenterParity.setValue(this.xCenterTargets.getMax() % 2 == 1);
        this.hasXCenterParity = hasXCenterParity;
    }

    public void setSolvedXCenters(IntCondition solvedXCenters) {
        solvedXCenters.capMin(Math.max(0, 23 + this.xCenterBreakIns.getMax() - this.xCenterTargets.getMax()));
        solvedXCenters.capMax(Math.max(0, 23 + this.xCenterBreakIns.getMin() - this.xCenterTargets.getMin()));
        this.solvedXCenters = solvedXCenters;
    }

    @Override
    protected <T extends BldCube> boolean matchingConditions(T inCube) {
        if (inCube instanceof FourBldCube) {
            FourBldCube randCube = (FourBldCube) inCube;
            return this.hasCornerParity.evaluatePositive(randCube.hasCornerParity())
                    && this.hasWingParity.evaluatePositive(randCube.hasWingParity())
                    && this.hasXCenterParity.evaluatePositive(randCube.hasXCenterParity())
                    && this.cornerSingleCycle.evaluatePositive(randCube.isCornerSingleCycle())
                    && this.cornerBreakIns.evaluate(randCube.getCornerBreakInNum())
                    && this.wingSingleCycle.evaluatePositive(randCube.isWingSingleCycle())
                    && this.wingBreakIns.evaluate(randCube.getWingBreakInNum())
                    && this.xCenterSingleCycle.evaluatePositive(randCube.isXCenterSingleCycle())
                    && this.xCenterBreakIns.evaluate(randCube.getXCenterBreakInNum())
                    && this.cornerTargets.evaluate(randCube.getCornerLength())
                    && this.wingTargets.evaluate(randCube.getWingLength())
                    && this.xCenterTargets.evaluate(randCube.getXCenterLength())
                    && this.solvedCorners.evaluate(randCube.getNumPreSolvedCorners())
                    && this.solvedWings.evaluate(randCube.getNumPreSolvedWings())
                    && this.solvedXCenters.evaluate(randCube.getNumPreSolvedXCenters())
                    && this.twistedCorners.evaluate(randCube.getNumPreTwistedCorners());
        } else return false;
    }

    @Override
    protected Puzzle getScramblingPuzzle() {
        return new NoInspectionFourByFourCubePuzzle();
    }
}