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

    public static <T> String join(String glue, T[] elements) {
        String joined = "";
        for (T element : elements) joined += (joined.length() > 0 ? glue : "") + element.toString();
        return joined;
    }

    public static <T> String join(String glue, Iterable<T> elements) {
        String joined = "";
        for (T element : elements) joined += (joined.length() > 0 ? glue : "") + element.toString();
        return joined;
    }
}
