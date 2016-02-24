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
}
