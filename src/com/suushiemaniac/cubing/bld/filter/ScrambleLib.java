package com.suushiemaniac.cubing.bld.filter;

import static com.suushiemaniac.cubing.bld.filter.BooleanCondition.*;
import static com.suushiemaniac.cubing.bld.filter.IntCondition.*;

public final class ScrambleLib {
    public static final BldScramble lolScrambleCorner = new ThreeBldScramble(MAXIMUM(4), NONE(), UNIMPORTANT(), ANY(), NONE(), ANY(), ANY(), ANY(), NONE());
    public static final BldScramble veryLolScrambleCorner = new ThreeBldScramble(MAXIMUM(2), NONE(), UNIMPORTANT(), ANY(), NONE(), ANY(), ANY(), ANY(), NONE());
    public static final BldScramble lolScrambleEdge = new ThreeBldScramble(ANY(), ANY(), UNIMPORTANT(), ANY(), NONE(), MAXIMUM(6), NONE(), ANY(), NONE());
    public static final BldScramble veryLolScrambleEdge = new ThreeBldScramble(ANY(), ANY(), UNIMPORTANT(), ANY(), NONE(), MAXIMUM(4), NONE(), ANY(), NONE());
    public static final BldScramble easyScramble = new ThreeBldScramble(ANY(), NONE(), NO(), ANY(), NONE(), ANY(), NONE(), ANY(), NONE());
    public static final BldScramble superEasyScramble = new ThreeBldScramble(MAXIMUM(4), NONE(), NO(), ANY(), NONE(), MAXIMUM(8), NONE(), ANY(), NONE());
    public static final BldScramble avgScramble = new ThreeBldScramble(INTERVAL(6, 7), INTERVAL(0, 1), UNIMPORTANT(), INTERVAL(0, 1), INTERVAL(0, 1), EXACT(11), INTERVAL(1, 2), INTERVAL(0, 1), INTERVAL(0, 1));
    public static final BldScramble hardScramble = new ThreeBldScramble(MINIMUM(7), MINIMUM(1), YES(), ANY(), ANY(), MINIMUM(11), MINIMUM(2), ANY(), ANY());
    public static final BldScramble threeSingleCycleScramble = new ThreeBldScramble(ANY(), NONE(), UNIMPORTANT(), ANY(), ANY(), ANY(), NONE(), ANY(), ANY());
    public static final BldScramble threeSingleCycleNoParityScramble = new ThreeBldScramble(ANY(), NONE(), NO(), ANY(), ANY(), ANY(), NONE(), ANY(), ANY());
    public static final BldScramble fourSingleCycleScramble = new FourBldScramble(ANY(), NONE(), UNIMPORTANT(), ANY(), ANY(), ANY(), NONE(), UNIMPORTANT(), ANY(), ANY(), NONE(), UNIMPORTANT(), ANY());
    public static final BldScramble fiveSingleCycleScramble = new FiveBldScramble(ANY(), NONE(), UNIMPORTANT(), ANY(), ANY(), ANY(), NONE(), ANY(), ANY(), ANY(), NONE(), UNIMPORTANT(), ANY(), ANY(), NONE(), UNIMPORTANT(), ANY(), ANY(), NONE(), UNIMPORTANT(), ANY());
    public static final BldScramble fourTrainingScramble = new FourBldScramble(ANY(), ANY(), UNIMPORTANT(), ANY(), ANY(), ANY(), NONE(), NO(), ANY(), ANY(), ANY(), UNIMPORTANT(), ANY());
    public static final BldScramble fiveTrainingScramble = new FiveBldScramble(ANY(), ANY(), UNIMPORTANT(), ANY(), ANY(), ANY(), ANY(), ANY(), ANY(), ANY(), NONE(), NO(), ANY(), ANY(), ANY(), UNIMPORTANT(), ANY(), ANY(), ANY(), UNIMPORTANT(), ANY());
    public static final BldScramble twoLolScramble = new TwoBldScramble(MAXIMUM(1), NONE(), YES(), ANY(), NONE());
    public static final BldScramble rileyScramble = new ThreeBldScramble(EXACT(3), NONE(), YES(), EXACT(4), NONE(), EXACT(10), EXACT(1), EXACT(2), NONE());
    public static final BldScramble threeAnyScramble = new ThreeBldScramble(ANY(), ANY(), UNIMPORTANT(), ANY(), ANY(), ANY(), ANY(), ANY(), ANY());
}
