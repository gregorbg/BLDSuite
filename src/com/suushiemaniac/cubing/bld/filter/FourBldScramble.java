package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.bld.analyze.BldCube;
import com.suushiemaniac.cubing.bld.analyze.FourBldCube;
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition;
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.NoInspectionFourByFourCubePuzzle;

import static com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition.*;
import static com.suushiemaniac.cubing.bld.filter.condition.IntCondition.*;
import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.CORNER;
import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.WING;
import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.XCENTER;

public class FourBldScramble extends ThreeBldScramble {
    protected BooleanCondition hasWingParity, hasXCenterParity, wingSingleCycle, xCenterSingleCycle, wingBufferSolved, xCenterBufferSolved;
    protected IntCondition wingBreakIns, xCenterBreakIns, wingTargets, solvedWings, xCenterTargets, solvedXCenters;

    public FourBldScramble(IntCondition cornerTargets,
                           IntCondition cornerBreakIns,
                           BooleanCondition hasCornerParity,
                           IntCondition solvedCorners,
                           IntCondition twistedCorners,
                           BooleanCondition cornerBufferSolved,
                           IntCondition wingTargets,
                           IntCondition wingBreakIns,
                           BooleanCondition hasWingParity,
                           IntCondition solvedWings,
                           BooleanCondition wingBufferSolved,
                           IntCondition xCenterTargets,
                           IntCondition xCenterBreakIns,
                           BooleanCondition hasXCenterParity,
                           IntCondition solvedXCenters,
                           BooleanCondition xCenterBufferSolved) {
        super(cornerTargets, cornerBreakIns, hasCornerParity, solvedCorners, twistedCorners, cornerBufferSolved, ANY(), ANY(), ANY(), ANY(), UNIMPORTANT());
        this.setWingTargets(wingTargets);
        this.setWingBreakIns(wingBreakIns);
        this.setWingSingleCycle();
        this.setWingParity(hasWingParity);
        this.setSolvedWings(solvedWings);
        this.setWingBufferSolved(wingBufferSolved);
        this.setXCenterTargets(xCenterTargets);
        this.setXCenterBreakIns(xCenterBreakIns);
        this.setXCenterSingleCycle();
        this.setXCenterParity(hasXCenterParity);
        this.setSolvedXCenters(solvedXCenters);
        this.setXCenterBufferSolved(xCenterBufferSolved);
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

    public void setWingBufferSolved(BooleanCondition wingBufferSolved) {
        this.wingBufferSolved = wingBufferSolved;
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

    public void setXCenterBufferSolved(BooleanCondition xCenterBufferSolved) {
        this.xCenterBufferSolved = xCenterBufferSolved;
    }

    @Override
    protected <T extends BldCube> boolean matchingConditions(T inCube) {
        if (inCube instanceof FourBldCube) {
            FourBldCube randCube = (FourBldCube) inCube;
            return this.hasCornerParity.evaluatePositive(randCube.hasParity(CORNER))
                    && this.hasWingParity.evaluatePositive(randCube.hasParity(WING))
                    && this.hasXCenterParity.evaluatePositive(randCube.hasParity(XCENTER))
                    && this.cornerSingleCycle.evaluatePositive(randCube.isSingleCycle(CORNER))
                    && this.cornerBreakIns.evaluate(randCube.getBreakInCount(CORNER))
                    && this.wingSingleCycle.evaluatePositive(randCube.isSingleCycle(WING))
                    && this.wingBreakIns.evaluate(randCube.getBreakInCount(WING))
                    && this.xCenterSingleCycle.evaluatePositive(randCube.isSingleCycle(XCENTER))
                    && this.xCenterBreakIns.evaluate(randCube.getBreakInCount(XCENTER))
                    && this.cornerTargets.evaluate(randCube.getStatLength(CORNER))
                    && this.wingTargets.evaluate(randCube.getStatLength(WING))
                    && this.xCenterTargets.evaluate(randCube.getStatLength(XCENTER))
                    && this.solvedCorners.evaluate(randCube.getPreSolvedCount(CORNER))
                    && this.solvedWings.evaluate(randCube.getPreSolvedCount(WING))
                    && this.solvedXCenters.evaluate(randCube.getPreSolvedCount(XCENTER))
                    && this.twistedCorners.evaluate(randCube.getMisOrientedCount(CORNER))
                    && this.cornerBufferSolved.evaluatePositive(randCube.isBufferSolved(CORNER))
                    && this.wingBufferSolved.evaluatePositive(randCube.isBufferSolved(WING))
                    && this.xCenterBufferSolved.evaluatePositive(randCube.isBufferSolved(XCENTER));
        } else return false;
    }

    @Override
    protected Puzzle getScramblingPuzzle() {
        return new NoInspectionFourByFourCubePuzzle();
    }

    @Override
    protected BldCube getAnalyzingPuzzle() {
        return new FourBldCube();
    }
}