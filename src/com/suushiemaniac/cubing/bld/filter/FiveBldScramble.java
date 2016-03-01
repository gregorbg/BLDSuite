package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.bld.analyze.cube.BldCube;
import com.suushiemaniac.cubing.bld.analyze.cube.FiveBldCube;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.NoInspectionFiveByFiveCubePuzzle;

import static com.suushiemaniac.cubing.bld.filter.BooleanCondition.*;

public class FiveBldScramble extends FourBldScramble {
    protected BooleanCondition hasTCenterParity, tCenterSingleCycle;
    protected IntCondition tCenterBreakIns, tCenterTargets, solvedTCenters;

    public FiveBldScramble(IntCondition cornerTargets,
                           IntCondition cornerBreakIns,
                           BooleanCondition hasCornerParity,
                           IntCondition solvedCorners,
                           IntCondition twistedCorners,
                           IntCondition edgeTargets,
                           IntCondition edgeBreakIns,
                           IntCondition solvedEdges,
                           IntCondition flippedEdges,
                           IntCondition wingTargets,
                           IntCondition wingBreakIns,
                           BooleanCondition hasWingParity,
                           IntCondition solvedWings,
                           IntCondition xCenterTargets,
                           IntCondition xCenterBreakIns,
                           BooleanCondition hasXCenterParity,
                           IntCondition solvedXCenters,
                           IntCondition tCenterTargets,
                           IntCondition tCenterBreakIns,
                           BooleanCondition hasTCenterParity,
                           IntCondition solvedTCenters) {
        super(cornerTargets, cornerBreakIns, hasCornerParity, solvedCorners, twistedCorners, wingTargets, wingBreakIns, hasWingParity, solvedWings, xCenterTargets, xCenterBreakIns, hasXCenterParity, solvedXCenters);
        this.setEdgeTargets(edgeTargets);
        this.setEdgeBreakIns(edgeBreakIns);
        this.setEdgeSingleCycle();
        this.setSolvedFlippedEdges(solvedEdges, flippedEdges);
        this.setTCenterTargets(tCenterTargets);
        this.setTCenterBreakIns(tCenterBreakIns);
        this.setTCenterSingleCycle();
        this.setTCenterParity(hasTCenterParity);
        this.setSolvedTCenters(solvedTCenters);
    }

    public void setTCenterTargets(IntCondition tCenterTargets) {
        tCenterTargets.capMin(0);
        tCenterTargets.capMax(34);
        this.tCenterTargets = tCenterTargets;
    }

    public void setTCenterBreakIns(IntCondition tCenterBreakIns) {
        tCenterBreakIns.capMin(Math.min(0, this.tCenterTargets.getMin() - 23));
        tCenterBreakIns.capMax(11);
        this.tCenterBreakIns = tCenterBreakIns;
    }

    public void setTCenterSingleCycle() {
        this.tCenterSingleCycle = this.tCenterBreakIns.getMax() == 0 ? YES() : this.tCenterBreakIns.getMin() == 0 ? UNIMPORTANT() : NO();
    }

    public void setTCenterParity(BooleanCondition hasTCenterParity) {
        if (this.tCenterTargets.getMin() == this.tCenterTargets.getMax()) hasTCenterParity.setValue(this.tCenterBreakIns.getMax() % 2 == 1);
        this.hasTCenterParity = hasTCenterParity;
    }

    public void setSolvedTCenters(IntCondition solvedTCenters) {
        solvedTCenters.capMin(Math.max(0, 23 + this.tCenterBreakIns.getMax() - this.tCenterTargets.getMax()));
        solvedTCenters.capMax(Math.max(0, 23 + this.tCenterBreakIns.getMin() - this.tCenterTargets.getMin()));
        this.solvedTCenters = solvedTCenters;
    }

    @Override
    protected <T extends BldCube> boolean matchingConditions(T inCube) {
        if (inCube instanceof FiveBldCube) {
            FiveBldCube randCube = (FiveBldCube) inCube;
            return this.hasCornerParity.evaluatePositive(randCube.hasCornerParity())
                    && this.hasWingParity.evaluatePositive(randCube.hasWingParity())
                    && this.hasXCenterParity.evaluatePositive(randCube.hasXCenterParity())
                    && this.hasTCenterParity.evaluatePositive(randCube.hasTCenterParity())
                    && this.cornerSingleCycle.evaluatePositive(randCube.isCornerSingleCycle())
                    && this.cornerBreakIns.evaluate(randCube.getCornerBreakInNum())
                    && this.edgeSingleCycle.evaluatePositive(randCube.isEdgeSingleCycle())
                    && this.edgeBreakIns.evaluate(randCube.getEdgeBreakInNum())
                    && this.wingSingleCycle.evaluatePositive(randCube.isWingSingleCycle())
                    && this.wingBreakIns.evaluate(randCube.getWingBreakInNum())
                    && this.xCenterSingleCycle.evaluatePositive(randCube.isXCenterSingleCycle())
                    && this.xCenterBreakIns.evaluate(randCube.getXCenterBreakInNum())
                    && this.tCenterSingleCycle.evaluatePositive(randCube.isTCenterSingleCycle())
                    && this.tCenterBreakIns.evaluate(randCube.getTCenterBreakInNum())
                    && this.cornerTargets.evaluate(randCube.getCornerLength())
                    && this.edgeTargets.evaluate(randCube.getEdgeLength())
                    && this.wingTargets.evaluate(randCube.getWingLength())
                    && this.xCenterTargets.evaluate(randCube.getXCenterLength())
                    && this.tCenterTargets.evaluate(randCube.getTCenterLength())
                    && this.solvedCorners.evaluate(randCube.getNumPreSolvedCorners())
                    && this.solvedEdges.evaluate(randCube.getNumPreSolvedEdges())
                    && this.solvedWings.evaluate(randCube.getNumPreSolvedWings())
                    && this.solvedXCenters.evaluate(randCube.getNumPreSolvedXCenters())
                    && this.solvedTCenters.evaluate(randCube.getNumPreSolvedTCenters())
                    && this.twistedCorners.evaluate(randCube.getNumPreTwistedCorners())
                    && this.flippedEdges.evaluate(randCube.getNumPreFlippedEdges());
        } else return false;
    }

    @Override
    protected Puzzle getScramblingPuzzle() {
        return new NoInspectionFiveByFiveCubePuzzle();
    }

    @Override
    protected BldCube getAnalyzingPuzzle() {
        return new FiveBldCube("");
    }
}
