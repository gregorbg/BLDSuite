package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.bld.analyze.FiveBldCube;
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition;
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition;
import puzzle.NoInspectionFiveByFiveCubePuzzle;

import static com.suushiemaniac.cubing.bld.filter.condition.IntCondition.ANY;
import static com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType.*;

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

        this.writeProperties(CORNER,
                cornerTargets,
                cornerBreakIns,
                hasCornerParity,
                solvedCorners,
                twistedCorners,
                cornerBufferSolved
        );

        this.writeProperties(EDGE,
                edgeTargets,
                edgeBreakIns,
                hasCornerParity, // FIXME only preliminary
                solvedEdges,
                flippedEdges,
                edgeBufferSolved
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

        this.writeProperties(TCENTER,
                tCenterTargets,
                tCenterBreakIns,
                hasTCenterParity,
                solvedTCenters,
                ANY(), // FIXME
                tCenterBufferSolved
        );
    }
}
