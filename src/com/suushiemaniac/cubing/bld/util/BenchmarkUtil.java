package com.suushiemaniac.cubing.bld.util;

public class BenchmarkUtil {
	public static long benchmark(Runnable script) {
		long before = System.currentTimeMillis();

		script.run();

		long after = System.currentTimeMillis();
		return after - before;
	}
}
