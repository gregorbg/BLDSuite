package com.suushiemaniac.cubing.bld.util;

import java.util.Arrays;
import java.util.List;

public class StringUtil {
    public static boolean containsOneOf(String toCheck, String[] containPoss) {
        for (String aContainPoss : containPoss)
            if (toCheck.contains(aContainPoss)) return true;
        return false;
    }

    public static boolean containsAllOf(String toCheck, String[] containOblig) {
        for (String aContainOblig : containOblig)
            if (!toCheck.contains(aContainOblig)) return false;
        return true;
    }

    public static String guessRegExpRange(String row) {
        char init = row.charAt(0);

        for (int i = 0; i < row.length(); i++) {
            if (((char) (init + i)) != row.charAt(i)) {
                return row;
            }
        }

        return "[" + row.charAt(0) + "-" + row.charAt(row.length() - 1) + "]";
    }

    public static String reverse(String in) {
        return new StringBuilder(in).reverse().toString();
    }

    public static int charCount(String in, char c) {
        return (int) in.chars().filter(v -> v == c).count();
    }

    public static boolean contentEquals(String thisString, String thatString) {
        if (thisString.length() != thatString.length()) {
            return false;
        }

        List<String> thisParts = Arrays.asList(thisString.split(""));
        List<String> thatParts = Arrays.asList(thatString.split(""));

        return thisParts.containsAll(thatParts) && thatParts.containsAll(thisParts);
    }
}
