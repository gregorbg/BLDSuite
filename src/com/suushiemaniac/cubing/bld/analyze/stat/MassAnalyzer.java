package com.suushiemaniac.cubing.bld.analyze.stat;

import java.util.*;

public abstract class MassAnalyzer {
    public abstract void analyzeProperties(int numCubes);

    public abstract void analyzeScrambleDist(int numCubes);

    protected static void numericMapPrint(Map<Integer, Integer> toPrint) {
        List<Integer> sortedKeys = new ArrayList<>(toPrint.keySet());
        sortedKeys.sort(Comparator.naturalOrder());
        for (int i : sortedKeys) System.out.println(i + ": " + toPrint.get(i));
        System.out.println("Average: " + mapAverage(toPrint));
    }

    protected static void stringMapPrint(Map<String, Integer> toPrint) {
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(toPrint.entrySet());
        sortedEntries.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        for (Map.Entry<String, Integer> entry : sortedEntries)
            System.out.println(entry.getValue() + ":\t" + entry.getKey());
    }

    protected static <T extends Number> float mapAverage(Map<Integer, T> toAvg) {
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
