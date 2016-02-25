package com.suushiemaniac.cubing.bld.analyze.cube;

import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class BldCube {
    protected static String invAlg(String moves) {
        String inverse = "";
        String[] singleMoves = moves.split("\\s");
        for (int i = singleMoves.length - 1; i >= 0; i--) {
            String move = singleMoves[i];
            if (move.endsWith("'"))
                move = move.replace("'", "");
            else if (!move.endsWith("2"))
                move += "'";
            inverse += move + " ";
        }
        return inverse.trim();
    }

    protected static final String NO_SCRAMBLE = "None";

    //Currently loaded scramble and (if set) solving orientation premoves
    protected String scramble = NO_SCRAMBLE;
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

    public String getStatString() {
        return this.getStatString(false, false);
    }

    public String getStatString(boolean newLine) {
        return this.getStatString(false, newLine);
    }

    public abstract String getPuzzleString();

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

    abstract void resetCube(boolean orientationOnly);

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

    protected String invertMoves(String moves) {
        String[] singleMoves = moves.split("\\s");
        ArrayList<String> invertedMoves = new ArrayList<>(singleMoves.length);
        for (int i = singleMoves.length; i > 0; i--) {
            String move = singleMoves[i - 1];
            if (move.endsWith("'")) move = move.replace("'", "");
            else if (!move.endsWith("2")) move += "'";
            invertedMoves.add(move);
        }
        return String.join(" ", invertedMoves);
    }

    protected <T> void cycleArrayLeft(T[] toCycle) {
        T tempStore = toCycle[0];
        System.arraycopy(toCycle, 1, toCycle, 0, toCycle.length - 1);
        toCycle[toCycle.length - 1] = tempStore;
    }

    protected <T> void cycleArrayRight(T[] toCycle) {
        T tempStore = toCycle[toCycle.length - 1];
        System.arraycopy(toCycle, 0, toCycle, 1, toCycle.length - 1);
        toCycle[0] = tempStore;
    }

    protected <T> boolean arrayEquals(T[] arrayOne, T[] arrayTwo) {
        boolean equals = arrayOne.length == arrayTwo.length;
        for (int i = 0; i < (arrayOne.length & arrayTwo.length); i++)
            equals = equals && arrayOne[i].equals(arrayTwo[i]);
        return equals;
    }

    protected <T> boolean arrayContains(T[] array, T searchObject) {
        for (T element : array) if (element.equals(searchObject)) return true;
        return false;
    }

    protected <T> int arrayIndex(T[] array, T searchObject) {
        for (int i = 0; i < array.length; i++) if (array[i].equals(searchObject)) return i;
        return -1;
    }

    protected <T> int deepArrayOuterIndex(T[][] array, T searchObject) {
        for (int i = 0; i < array.length; i++) for (T element : array[i]) if (element.equals(searchObject)) return i;
        return -1;
    }

    protected <T> int deepArrayInnerIndex(T[][] array, T searchObject) {
        for (T[] subarray : array)
            for (int i = 0; i < subarray.length; i++) if (subarray[i].equals(searchObject)) return i;
        return -1;
    }

    protected Integer[] autoboxArray(int[] source) {
        Integer[] boxedUp = new Integer[source.length];
        for (int i = 0; i < boxedUp.length; i++) boxedUp[i] = source[i];
        return boxedUp;
    }

    protected int[] autoboxArray(Integer[] source) {
        int[] boxedDown = new int[source.length];
        for (int i = 0; i < boxedDown.length; i++) boxedDown[i] = source[i];
        return boxedDown;
    }

    protected Character[] autoboxArray(char[] source) {
        Character[] boxedUp = new Character[source.length];
        for (int i = 0; i < boxedUp.length; i++) boxedUp[i] = source[i];
        return boxedUp;
    }

    protected char[] autoboxArray(Character[] source) {
        char[] boxedDown = new char[source.length];
        for (int i = 0; i < boxedDown.length; i++) boxedDown[i] = source[i];
        return boxedDown;
    }
}
