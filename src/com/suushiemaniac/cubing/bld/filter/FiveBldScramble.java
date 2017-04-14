package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.bld.analyze.FiveBldCube;
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition;
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition;
import puzzle.NoInspectionFiveByFiveCubePuzzle;

import static com.suushiemaniac.cubing.bld.filter.condition.IntCondition.ANY;
import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public class FiveBldScramble extends BldScramble {
    public FiveBldScramble(IntCondition cornerTargets,
                           IntCondition cornerBreakIns,
                           BooleanCondition hasCornerParity,
                           IntCondition solvedCorners,
                           IntCondition twistedCorners,
                           BooleanCondition cornerBufferSolved,
                           IntCondition edgeTargets,
                           IntCondition edgeBreakIns,
                           IntCondition solvedEdges,
                           IntCondition flippedEdges,
                           BooleanCondition edgeBufferSolved,
                           IntCondition wingTargets,
                           IntCondition wingBreakIns,
                           BooleanCondition hasWingParity,
                           IntCondition solvedWings,
                           BooleanCondition wingBufferSolved,
                           IntCondition xCenterTargets,
                           IntCondition xCenterBreakIns,
                           BooleanCondition hasXCenterParity,
                           IntCondition solvedXCenters,
                           BooleanCondition xCenterBufferSolved,
                           IntCondition tCenterTargets,
                           IntCondition tCenterBreakIns,
                           BooleanCondition hasTCenterParity,
                           IntCondition solvedTCenters,
                           BooleanCondition tCenterBufferSolved) {
        super(new FiveBldCube(), NoInspectionFiveByFiveCubePuzzle::new);

        this.setTargets(CORNER, cornerTargets);
        this.setBreakIns(CORNER, cornerBreakIns);
        this.setParity(CORNER, hasCornerParity);
        this.setSolvedMisOriented(CORNER, solvedCorners, twistedCorners);
        this.setBufferSolved(CORNER, cornerBufferSolved);

        this.setTargets(EDGE, edgeTargets);
        this.setBreakIns(EDGE, edgeBreakIns);
        this.setParity(EDGE, hasCornerParity); // FIXME preliminary!
        this.setSolvedMisOriented(EDGE, solvedEdges, flippedEdges);
        this.setBufferSolved(EDGE, edgeBufferSolved);

        this.setTargets(WING, wingTargets);
        this.setBreakIns(WING, wingBreakIns);
        this.setParity(WING, hasWingParity);
        this.setSolvedMisOriented(WING, solvedWings, ANY()); // FIXME
        this.setBufferSolved(WING, wingBufferSolved);

        this.setTargets(XCENTER, xCenterTargets);
        this.setBreakIns(XCENTER, xCenterBreakIns);
        this.setParity(XCENTER, hasXCenterParity);
        this.setSolvedMisOriented(XCENTER, solvedXCenters, ANY()); // FIXME
        this.setBufferSolved(XCENTER, xCenterBufferSolved);

        this.setTargets(TCENTER, tCenterTargets);
        this.setBreakIns(TCENTER, tCenterBreakIns);
        this.setParity(TCENTER, hasTCenterParity);
        this.setSolvedMisOriented(TCENTER, solvedTCenters, ANY()); // FIXME
        this.setBufferSolved(TCENTER, tCenterBufferSolved);
    }
}
