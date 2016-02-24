package com.suushiemaniac.cubing.bld.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MapUtil {
    public static <T extends Number> void freqMapPrint(Map<Integer, T> toPrint) {
        List<Integer> sortedKeys = new ArrayList<>(toPrint.keySet());
        sortedKeys.sort(Comparator.naturalOrder());
        for (int i : sortedKeys) System.out.println(i + ": " + toPrint.get(i));
        System.out.println("Average: " + freqMapAverage(toPrint));
    }

    public static <T extends Number> float freqMapAverage(Map<Integer, T> toAvg) {
        float sum = 0;
        int criteriaHit = 0;
        for (int i : toAvg.keySet()) {
            Number mapEntry = toAvg.get(i);
            criteriaHit += mapEntry.intValue();
            sum += i * mapEntry.floatValue();
        }
        return sum / criteriaHit;
    }
}
