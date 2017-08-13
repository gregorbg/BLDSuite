package com.suushiemaniac.cubing.bld.util;

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
}
