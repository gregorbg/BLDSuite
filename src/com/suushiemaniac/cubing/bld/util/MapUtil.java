package com.suushiemaniac.cubing.bld.util;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MapUtil {
	public static <K extends Comparable<? super K>, V> void sortedMapPrint(Map<K, V> toPrint) {
		List<K> sortedKeys = new ArrayList<>(toPrint.keySet());
		sortedKeys.sort(Comparator.naturalOrder());

		for (K i : sortedKeys) {
			System.out.println(i.toString() + ": " + toPrint.get(i));
		}
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

	public static <T, V> Map<T, V> constantValueMap(Collection<T> keys, Supplier<V> value) {
		return keys
				.stream()
				.collect(Collectors.toMap(Function.identity(), k -> value.get(), (a, b) -> b));
	}
}
