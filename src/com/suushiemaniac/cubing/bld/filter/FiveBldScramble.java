package com.suushiemaniac.cubing.bld.filter;

import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.suushiemaniac.cubing.bld.analyze.cube.BldCube;
import com.suushiemaniac.cubing.bld.analyze.cube.FiveBldCube;
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition;
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition;
import com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.NoInspectionFiveByFiveCubePuzzle;

import static com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition.*;
import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

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
            return this.hasCornerParity.evaluatePositive(randCube.hasParity(CORNER))
                    && this.hasWingParity.evaluatePositive(randCube.hasParity(WING))
                    && this.hasXCenterParity.evaluatePositive(randCube.hasParity(XCENTER))
                    && this.hasTCenterParity.evaluatePositive(randCube.hasParity(TCENTER))
                    && this.cornerSingleCycle.evaluatePositive(randCube.isSingleCycle(CORNER))
                    && this.cornerBreakIns.evaluate(randCube.getBreakInCount(CORNER))
                    && this.edgeSingleCycle.evaluatePositive(randCube.isSingleCycle(EDGE))
                    && this.edgeBreakIns.evaluate(randCube.getBreakInCount(EDGE))
                    && this.wingSingleCycle.evaluatePositive(randCube.isSingleCycle(WING))
                    && this.wingBreakIns.evaluate(randCube.getBreakInCount(WING))
                    && this.xCenterSingleCycle.evaluatePositive(randCube.isSingleCycle(XCENTER))
                    && this.xCenterBreakIns.evaluate(randCube.getBreakInCount(XCENTER))
                    && this.tCenterSingleCycle.evaluatePositive(randCube.isSingleCycle(TCENTER))
                    && this.tCenterBreakIns.evaluate(randCube.getBreakInCount(TCENTER))
                    && this.cornerTargets.evaluate(randCube.getStatLength(CORNER))
                    && this.edgeTargets.evaluate(randCube.getStatLength(EDGE))
                    && this.wingTargets.evaluate(randCube.getStatLength(WING))
                    && this.xCenterTargets.evaluate(randCube.getStatLength(XCENTER))
                    && this.tCenterTargets.evaluate(randCube.getStatLength(TCENTER))
                    && this.solvedCorners.evaluate(randCube.getPreSolvedCount(CORNER))
                    && this.solvedEdges.evaluate(randCube.getPreSolvedCount(EDGE))
                    && this.solvedWings.evaluate(randCube.getPreSolvedCount(WING))
                    && this.solvedXCenters.evaluate(randCube.getPreSolvedCount(XCENTER))
                    && this.solvedTCenters.evaluate(randCube.getPreSolvedCount(TCENTER))
                    && this.twistedCorners.evaluate(randCube.getMisOrientedCount(CORNER))
                    && this.flippedEdges.evaluate(randCube.getMisOrientedCount(EDGE));
        } else return false;
    }

    @Override
    protected Puzzle getScramblingPuzzle() {
        return new NoInspectionFiveByFiveCubePuzzle();
    }

    @Override
    protected BldCube getAnalyzingPuzzle() {
        return new FiveBldCube();
    }
}
