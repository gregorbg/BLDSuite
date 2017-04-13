package com.suushiemaniac.cubing.bld.filter;

import static com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition.*;
import static com.suushiemaniac.cubing.bld.filter.condition.IntCondition.*;

public final class ScrambleLib {
    public static final BldScramble lolScrambleCorner = new ThreeBldScramble(MAXIMUM(4), NONE(), UNIMPORTANT(), ANY(), NONE(), NO(), ANY(), ANY(), ANY(), NONE(), UNIMPORTANT());
    public static final BldScramble veryLolScrambleCorner = new ThreeBldScramble(MAXIMUM(2), NONE(), UNIMPORTANT(), ANY(), NONE(), NO(), ANY(), ANY(), ANY(), NONE(), UNIMPORTANT());
    public static final BldScramble lolScrambleEdge = new ThreeBldScramble(ANY(), ANY(), UNIMPORTANT(), ANY(), NONE(), UNIMPORTANT(), MAXIMUM(6), NONE(), ANY(), NONE(), NO());
    public static final BldScramble veryLolScrambleEdge = new ThreeBldScramble(ANY(), ANY(), UNIMPORTANT(), ANY(), NONE(), UNIMPORTANT(), MAXIMUM(4), NONE(), ANY(), NONE(), NO());
    public static final BldScramble easyScramble = new ThreeBldScramble(ANY(), NONE(), NO(), ANY(), NONE(), UNIMPORTANT(), ANY(), NONE(), ANY(), NONE(), UNIMPORTANT());
    public static final BldScramble superEasyScramble = new ThreeBldScramble(MAXIMUM(4), NONE(), NO(), ANY(), NONE(), NO(), MAXIMUM(8), NONE(), ANY(), NONE(), NO());
    public static final BldScramble avgScramble = new ThreeBldScramble(INTERVAL(6, 7), INTERVAL(0, 1), UNIMPORTANT(), INTERVAL(0, 1), INTERVAL(0, 1), UNIMPORTANT(), EXACT(11), INTERVAL(1, 2), INTERVAL(0, 1), INTERVAL(0, 1), UNIMPORTANT());
    public static final BldScramble hardScramble = new ThreeBldScramble(MINIMUM(7), MINIMUM(1), YES(), ANY(), ANY(), YES(), MINIMUM(11), MINIMUM(2), ANY(), ANY(), YES());
    public static final BldScramble threeSingleCycleScramble = new ThreeBldScramble(ANY(), NONE(), UNIMPORTANT(), ANY(), ANY(), UNIMPORTANT(), ANY(), NONE(), ANY(), ANY(), UNIMPORTANT());
    public static final BldScramble threeSingleCycleNoParityScramble = new ThreeBldScramble(ANY(), NONE(), NO(), ANY(), ANY(), UNIMPORTANT(), ANY(), NONE(), ANY(), ANY(), UNIMPORTANT());
    public static final BldScramble fourSingleCycleScramble = new FourBldScramble(ANY(), NONE(), UNIMPORTANT(), ANY(), ANY(), UNIMPORTANT(), ANY(), NONE(), UNIMPORTANT(), ANY(), UNIMPORTANT(), ANY(), NONE(), UNIMPORTANT(), ANY(), UNIMPORTANT());
    public static final BldScramble fiveSingleCycleScramble = new FiveBldScramble(ANY(), NONE(), UNIMPORTANT(), ANY(), ANY(), UNIMPORTANT(), ANY(), NONE(), ANY(), ANY(), UNIMPORTANT(), ANY(), NONE(), UNIMPORTANT(), ANY(), UNIMPORTANT(), ANY(), NONE(), UNIMPORTANT(), ANY(), UNIMPORTANT(), ANY(), NONE(), UNIMPORTANT(), ANY(), UNIMPORTANT());
    public static final BldScramble fourTrainingScramble = new FourBldScramble(ANY(), ANY(), UNIMPORTANT(), ANY(), ANY(), NO(), ANY(), NONE(), NO(), ANY(), NO(), ANY(), ANY(), UNIMPORTANT(), ANY(), NO());
    public static final BldScramble fiveTrainingScramble = new FiveBldScramble(ANY(), ANY(), UNIMPORTANT(), ANY(), ANY(), NO(), ANY(), ANY(), ANY(), ANY(), NO(), ANY(), NONE(), NO(), ANY(), NO(), ANY(), ANY(), UNIMPORTANT(), ANY(), NO(), ANY(), ANY(), UNIMPORTANT(), ANY(), NO());
    public static final BldScramble twoLolScramble = new TwoBldScramble(MAXIMUM(1), NONE(), YES(), ANY(), NONE(), NO());
    public static final BldScramble rileyScramble = new ThreeBldScramble(EXACT(3), NONE(), YES(), EXACT(4), NONE(), NO(), EXACT(10), EXACT(1), EXACT(2), NONE(), NO());
    public static final BldScramble threeAnyScramble = new ThreeBldScramble(ANY(), ANY(), UNIMPORTANT(), ANY(), ANY(), UNIMPORTANT(), ANY(), ANY(), ANY(), ANY(), UNIMPORTANT());
    public static final BldScramble threeNoParityScramble = new ThreeBldScramble(ANY(), ANY(), NO(), ANY(), ANY(), UNIMPORTANT(), ANY(), ANY(), ANY(), ANY(), UNIMPORTANT());
    public static final BldScramble threeNoMisorientScramble = new ThreeBldScramble(ANY(), ANY(), UNIMPORTANT(), ANY(), NONE(), UNIMPORTANT(), ANY(), ANY(), ANY(), NONE(), UNIMPORTANT());
}
