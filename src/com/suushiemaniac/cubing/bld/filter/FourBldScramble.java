package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.bld.analyze.FourBldCube;
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition;
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition;
import puzzle.NoInspectionFourByFourCubePuzzle;

import static com.suushiemaniac.cubing.bld.filter.condition.IntCondition.ANY;
import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.CORNER;
import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.WING;
import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.XCENTER;

public class FourBldScramble extends BldScramble {
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
        super(new FourBldCube(), NoInspectionFourByFourCubePuzzle::new);

        this.setTargets(CORNER, cornerTargets);
        this.setBreakIns(CORNER, cornerBreakIns);
        this.setParity(CORNER, hasCornerParity);
        this.setSolvedMisOriented(CORNER, solvedCorners, twistedCorners);
        this.setBufferSolved(CORNER, cornerBufferSolved);

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
    }
}