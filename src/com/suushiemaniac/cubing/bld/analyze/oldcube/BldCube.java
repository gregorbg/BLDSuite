package com.suushiemaniac.cubing.bld.analyze.oldcube;

import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.lang.json.JSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class BldCube {
    protected static final String NO_SCRAMBLE = "None";

    //Currently loaded scramble and (if set) solving orientation premoves
    protected String scramble = BldCube.NO_SCRAMBLE;
    protected String solvingOrPremoves = "";
    protected String centerRotations = "";

    // Definitions of available permutations and rotations
    protected HashMap<String, HashMap<PieceType, Integer[]>> permutations = new HashMap<>();

    abstract void initPermutations();

    abstract void permute(String permutation);

    abstract void solveCube();

    abstract String getRotationsFromOrientation(int top, int front, int[] checkArray);

    public abstract String getRotations();

    public abstract String getSolutionPairs(boolean withRotation);

    public abstract String getStatstics();

    public abstract String getScramble();

    public abstract String getNoahtation();

    public abstract String getStatString(boolean spaced, boolean newLine);

    public abstract String getPuzzleString();

    abstract void resetCube(boolean orientationOnly);

    public String getStatString() {
        return this.getStatString(false, false);
    }

    public String getStatString(boolean newLine) {
        return this.getStatString(false, newLine);
    }

    /**
     * Parse a new scramble to be analyzed
     *
     * @param scrambleString The scramble to be parsed. Supports full WCA notation including rotations
     */
    public void parseScramble(String scrambleString) {
        // Cube is scrambled
        scrambleCube(scrambleString);

        // Solve the cube
        solveCube();
    }

    // Sets the cube to solved state and applies scramble
    protected void scrambleCube(String scrambleString) {
        resetCube(false);
        this.centerRotations = "";

        // unrecognized moves are ignored
        ArrayList<String> validPermutations = new ArrayList<>();
        String[] scramble = scrambleString.split("\\s+?");
        for (String scrambleSeq : scramble)
            if (this.permutations.keySet().contains(scrambleSeq)) validPermutations.add(scrambleSeq);

        this.scramble = String.join(" ", validPermutations);

        if (this.solvingOrPremoves.length() > 0)
            validPermutations.addAll(0, Arrays.asList(this.solvingOrPremoves.split("\\s")));
        // Permutations are applied to the solved cube
        for (String permutation : validPermutations) this.permute(permutation);
    }

    public JSON jsonPermutations() {
        return JSON.fromNative(this.permutations);
    }
}
