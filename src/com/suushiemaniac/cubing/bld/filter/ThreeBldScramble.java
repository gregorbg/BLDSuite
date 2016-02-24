package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.bld.analyze.cube.BldCube;
import com.suushiemaniac.cubing.bld.analyze.cube.ThreeBldCube;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.NoInspectionThreeByThreeCubePuzzle;

import java.util.Random;

import static com.suushiemaniac.cubing.bld.filter.BooleanCondition.*;
import static com.suushiemaniac.cubing.bld.filter.IntCondition.*;

public class ThreeBldScramble extends TwoBldScramble {
    public static ThreeBldScramble averageScramble() {
        Random rand = new Random();
        int size = 100000;
        boolean[] parity = new boolean[size];
        for (int i = 0; i < parity.length; i++) parity[i] = i < 50270;
        //corner-edge num
        int[] cTargets = {5, 12, 100, 551, 2548, 8496, 20713, 32608, 25658, 8599, 710};
        int[] eTargets = {6, 184, 3911, 34275, 55169, 6432, 23}; //offset +4, times 2
        int[] cBreakIn = {34138, 48629, 16226, 1007};
        int[] eBreakIn = {19416, 50274, 22156, 7761, 370, 23};
        int[] cSolved = {65901, 27681, 5617, 736, 61, 4};
        int[] eSolved = {58120, 31491, 8623, 1544, 198, 22, 2};
        int[] cMisOrient = {55683, 32807, 9443, 1796, 250, 19, 2};
        int[] eMisOrient = {63173, 28982, 6676, 1031, 126, 12};
        return new ThreeBldScramble(
                EXACT(getNumInStatArray(cTargets, rand.nextInt(size))),
                EXACT(getNumInStatArray(cBreakIn, rand.nextInt(size))),
                parity[rand.nextInt(size)] ? YES() : NO(),
                EXACT(getNumInStatArray(cSolved, rand.nextInt(size))),
                EXACT(getNumInStatArray(cMisOrient, rand.nextInt(size))),
                EXACT(getNumInStatArray(eTargets, rand.nextInt(size), 4, 2)),
                EXACT(getNumInStatArray(eBreakIn, rand.nextInt(size))),
                EXACT(getNumInStatArray(eSolved, rand.nextInt(size))),
                EXACT(getNumInStatArray(eMisOrient, rand.nextInt(size)))
        );
    }

    public static ThreeBldScramble mostCommonScramble() {
        return new ThreeBldScramble(
                EXACT(7),
                EXACT(1),
                YES(),
                EXACT(0),
                EXACT(0),
                EXACT(12),
                EXACT(1),
                EXACT(0),
                EXACT(0)
        );
    }

    protected BooleanCondition edgeSingleCycle;
    protected IntCondition edgeTargets, edgeBreakIns, solvedEdges, flippedEdges;

    public ThreeBldScramble(IntCondition cornerTargets,
                            IntCondition cornerBreakIns,
                            BooleanCondition hasCornerParity,
                            IntCondition solvedCorners,
                            IntCondition twistedCorners,
                            IntCondition edgeTargets,
                            IntCondition edgeBreakIns,
                            IntCondition solvedEdges,
                            IntCondition flippedEdges) {
        super(cornerTargets, cornerBreakIns, hasCornerParity, solvedCorners, twistedCorners);
        this.setEdgeTargets(edgeTargets);
        this.setEdgeBreakIns(edgeBreakIns);
        this.setEdgeSingleCycle();
        this.setSolvedEdges(solvedEdges, false);
        this.setFlippedEdges(flippedEdges, false);
        this.balanceLeftOverEdges();
        this.bldAnalyzerCube = new ThreeBldCube("");
    }

    public void setEdgeTargets(IntCondition edgeTargets) {
        edgeTargets.capMin(0);
        edgeTargets.capMax(16);

        if (edgeTargets.getMin() == edgeTargets.getMax() && edgeTargets.getMin() % 2 == 1) {
            edgeTargets.setMin(edgeTargets.getMin() - 1);
            edgeTargets.setMax(edgeTargets.getMax() + 1);
        }

        this.edgeTargets = edgeTargets;
    }

    public void setEdgeBreakIns(IntCondition edgeBreakIns) {
        edgeBreakIns.capMin(Math.max(0, this.edgeTargets.getMin() - 11));
        edgeBreakIns.capMax(5);
        this.edgeBreakIns = edgeBreakIns;
    }

    public void setEdgeSingleCycle() {
        this.edgeSingleCycle = edgeBreakIns.getMax() == 0 ? YES() : edgeBreakIns.getMin() == 0 ? UNIMPORTANT() : NO();
    }

    protected void setSolvedEdges(IntCondition solvedEdges, boolean balaceAfter) {
        solvedEdges.capMin(Math.max(0, 11 + this.edgeBreakIns.getMax() - this.edgeTargets.getMax()));
        solvedEdges.capMax(Math.max(0, 11 + this.edgeBreakIns.getMin() - this.edgeTargets.getMin()));
        this.solvedEdges = solvedEdges;
        if (balaceAfter) this.balanceLeftOverEdges();
    }

    public void setSolvedEdges(IntCondition solvedEdges) {
        this.setSolvedEdges(solvedEdges, true);
    }

    protected void setFlippedEdges(IntCondition flippedEdges, boolean balanceAfter) {
        flippedEdges.capMin(Math.max(0, 11 + edgeBreakIns.getMax() - this.edgeTargets.getMax()));
        flippedEdges.capMax(Math.max(0, 11 + edgeBreakIns.getMin() - this.edgeTargets.getMin()));
        this.flippedEdges = flippedEdges;
        if (balanceAfter) this.balanceLeftOverEdges();
    }

    public void setFlippedEdges(IntCondition flippedEdges) {
        this.setFlippedEdges(flippedEdges, true);
    }

    protected void balanceLeftOverEdges() {
        int leftOverMin = Math.max(0, 11 + this.edgeBreakIns.getMax() - this.edgeTargets.getMax());
        int pieceMinSum = this.flippedEdges.getMin() + this.solvedEdges.getMin();
        while (pieceMinSum > leftOverMin) {
            this.flippedEdges.setMin(Math.max(0, this.flippedEdges.getMin() - pieceMinSum + leftOverMin));
            this.solvedEdges.setMin(Math.max(0, this.solvedEdges.getMin() - pieceMinSum + leftOverMin));
            pieceMinSum = this.flippedEdges.getMin() + this.solvedEdges.getMin();
        }
    }

    public static ThreeBldScramble cloneFrom(String scramble, boolean strict) {
        ThreeBldCube refCube = new ThreeBldCube(scramble);
        BooleanCondition hasCornerParity = refCube.hasCornerParity() ? strict ? YES() : UNIMPORTANT() : NO();
        IntCondition cornerBreakIns = strict ? EXACT(refCube.getCornerBreakInNum()) : MAXIMUM(refCube.getCornerBreakInNum());
        IntCondition cornerTargets = strict ? EXACT(refCube.getCornerLength()) : MAXIMUM(refCube.getCornerLength());
        IntCondition preCorners = strict ? EXACT(refCube.getNumPreSolvedCorners()) : MINIMUM(refCube.getNumPreSolvedCorners());
        IntCondition preTwisted = strict ? EXACT(refCube.getNumPreTwistedCorners()) : MAXIMUM(refCube.getNumPreTwistedCorners());
        IntCondition edgeBreakIns = strict ? EXACT(refCube.getEdgeBreakInNum()) : MAXIMUM(refCube.getEdgeBreakInNum());
        IntCondition edgeTargets = strict ? EXACT(refCube.getEdgeLength()) : MAXIMUM(refCube.getEdgeLength());
        IntCondition preEdges = strict ? EXACT(refCube.getNumPreSolvedEdges()) : MINIMUM(refCube.getNumPreSolvedEdges());
        IntCondition preFlipped = strict ? EXACT(refCube.getNumPreFlippedEdges()) : MAXIMUM(refCube.getNumPreFlippedEdges());
        return new ThreeBldScramble(cornerTargets, cornerBreakIns, hasCornerParity, preCorners, preTwisted, edgeTargets, edgeBreakIns, preEdges, preFlipped);
    }

    @Override
    protected <T extends BldCube> boolean matchingConditions(T inCube) {
        if (inCube instanceof ThreeBldCube) {
            ThreeBldCube randCube = (ThreeBldCube) inCube;
            return this.hasCornerParity.evaluatePositive(randCube.hasCornerParity())
                    && this.cornerSingleCycle.evaluatePositive(randCube.isCornerSingleCycle())
                    && this.cornerBreakIns.evaluate(randCube.getCornerBreakInNum())
                    && this.edgeSingleCycle.evaluatePositive(randCube.isEdgeSingleCycle())
                    && this.edgeBreakIns.evaluate(randCube.getEdgeBreakInNum())
                    && this.cornerTargets.evaluate(randCube.getCornerLength())
                    && this.edgeTargets.evaluate(randCube.getEdgeLength())
                    && this.solvedCorners.evaluate(randCube.getNumPreSolvedCorners())
                    && this.solvedEdges.evaluate(randCube.getNumPreSolvedEdges())
                    && this.twistedCorners.evaluate(randCube.getNumPreTwistedCorners())
                    && this.flippedEdges.evaluate(randCube.getNumPreFlippedEdges());
        } else return false;
    }

    @Override
    protected Puzzle getScramblingPuzzle() {
        return new NoInspectionThreeByThreeCubePuzzle();
    }
}
