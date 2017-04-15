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

        this.writeProperties(CORNER,
                cornerTargets,
                cornerBreakIns,
                hasCornerParity,
                solvedCorners,
                twistedCorners,
                cornerBufferSolved
        );

        this.writeProperties(WING,
                wingTargets,
                wingBreakIns,
                hasWingParity,
                solvedWings,
                ANY(), // FIXME
                wingBufferSolved
        );

        this.writeProperties(XCENTER,
                xCenterTargets,
                xCenterBreakIns,
                hasXCenterParity,
                solvedXCenters,
                ANY(), // FIXME
                xCenterBufferSolved
        );
    }
}