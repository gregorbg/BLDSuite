package com.suushiemaniac.cubing.bld.util;

import java.util.HashMap;

public class CountingMap<K> extends HashMap<K, Integer> {
	public void increment(K key) {
		this.put(key, this.getOrDefault(key, 0) + 1);
	}
}
