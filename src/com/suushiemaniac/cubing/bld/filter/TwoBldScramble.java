package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.bld.analyze.TwoBldCube;
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition;
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition;
import puzzle.TwoByTwoCubePuzzle;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.CORNER;

public class TwoBldScramble extends BldScramble {
    public TwoBldScramble(IntCondition cornerTargets,
                          IntCondition cornerBreakIns,
                          BooleanCondition hasCornerParity,
                          IntCondition solvedCorners,
                          IntCondition twistedCorners,
                          BooleanCondition cornerBufferSolved) {
        super(new TwoBldCube(), TwoByTwoCubePuzzle::new);

        this.writeProperties(CORNER,
                cornerTargets,
                cornerBreakIns,
                hasCornerParity,
                solvedCorners,
                twistedCorners,
                cornerBufferSolved
        );
    }
}
