package com.suushiemaniac.cubing.bld.util;

public class ArrayUtil {
    public static <T> T mutualIndex(T element, T[] source, T[] dest) {
        return dest[ArrayUtil.binarySearch(element, source)];
    }

    public static <T> int binarySearch(T element, T[] arr) {
        for (int i = 0; i < arr.length; i++) if (arr[i].equals(element)) return i;
        return -1;
    }

    public static <T> void cycleLeft(T[] toCycle) {
        T tempStore = toCycle[0];
        System.arraycopy(toCycle, 1, toCycle, 0, toCycle.length - 1);
        toCycle[toCycle.length - 1] = tempStore;
    }

    public static <T> void cycleRight(T[] toCycle) {
        T tempStore = toCycle[toCycle.length - 1];
        System.arraycopy(toCycle, 0, toCycle, 1, toCycle.length - 1);
        toCycle[0] = tempStore;
    }

    public static <T> boolean equals(T[] arrayOne, T[] arrayTwo) {
        boolean equals = arrayOne.length == arrayTwo.length;
        for (int i = 0; i < (arrayOne.length & arrayTwo.length); i++)
            equals &= arrayOne[i].equals(arrayTwo[i]);
        return equals;
    }

    public static <T> boolean contains(T[] array, T searchObject) {
        for (T element : array) if (element.equals(searchObject)) return true;
        return false;
    }

    public static <T> int index(T[] array, T searchObject) {
        for (int i = 0; i < array.length; i++) if (array[i].equals(searchObject)) return i;
        return -1;
    }

    public static <T> int deepOuterIndex(T[][] array, T searchObject) {
        for (int i = 0; i < array.length; i++) for (T element : array[i]) if (element.equals(searchObject)) return i;
        return -1;
    }

    public static <T> int deepInnerIndex(T[][] array, T searchObject) {
        for (T[] subarray : array)
            for (int i = 0; i < subarray.length; i++) if (subarray[i].equals(searchObject)) return i;
        return -1;
    }

    public static Integer[] autobox(int[] source) {
        Integer[] boxedUp = new Integer[source.length];
        for (int i = 0; i < boxedUp.length; i++) boxedUp[i] = source[i];
        return boxedUp;
    }

    public static int[] autobox(Integer[] source) {
        int[] boxedDown = new int[source.length];
        for (int i = 0; i < boxedDown.length; i++) boxedDown[i] = source[i];
        return boxedDown;
    }

    public static Character[] autobox(char[] source) {
        Character[] boxedUp = new Character[source.length];
        for (int i = 0; i < boxedUp.length; i++) boxedUp[i] = source[i];
        return boxedUp;
    }

    public static char[] autobox(Character[] source) {
        char[] boxedDown = new char[source.length];
        for (int i = 0; i < boxedDown.length; i++) boxedDown[i] = source[i];
        return boxedDown;
    }

    public static int[] fill(int length) {
        int[] fill = new int[length];

		for (int i = 0; i < length; i++)
			fill[i] = i;

		return fill;
    }
}