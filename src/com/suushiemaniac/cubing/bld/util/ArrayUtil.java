package com.suushiemaniac.cubing.bld.util;

public class ArrayUtil {
    public static <T> T mutualIndex(T element, T[] source, T[] dest) {
        return dest[ArrayUtil.binarySearch(element, source)];
    }

    public static <T> int binarySearch(T element, T[] arr) {
        for (int i = 0; i < arr.length; i++) if (arr[i].equals(element)) return i;
        return -1;
    }
}
