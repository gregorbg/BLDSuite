package com.suushiemaniac.cubing.bld.analyze.stat;

import java.util.*;

public abstract class MassAnalyzer {
    public abstract void analyzeProperties(int numCubes);

    public abstract void analyzeScrambleDist(int numCubes);

    protected static void numericMapPrint(Map<Integer, Integer> toPrint) {
        List<Integer> sortedKeys = new ArrayList<Integer>(toPrint.keySet());
        Collections.sort(sortedKeys, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        for (int i : sortedKeys) System.out.println(i + ": " + toPrint.get(i));
        System.out.println("Average: " + mapAverage(toPrint));
    }

    protected static void stringMapPrint(Map<String, Integer> toPrint) {
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<Map.Entry<String, Integer>>(toPrint.entrySet());
        Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
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

    protected class DefaultHashMap<K, V> extends HashMap<K, V> {
        public V getOrDefault(K key, V defaultValue) {
            V mapContent = this.get(key);
            return mapContent == null ? defaultValue : mapContent;
        }
    }
}