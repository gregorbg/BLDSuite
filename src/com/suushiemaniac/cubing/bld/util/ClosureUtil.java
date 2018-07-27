package com.suushiemaniac.cubing.bld.util;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ClosureUtil {
	public static <T, U, R> Function<U, R> curry(BiFunction<T, U, R> unBound, T arg) {
		return fnArg -> unBound.apply(arg, fnArg);
	}

	public static <T, U, R> Function<T, R> curryRight(BiFunction<T, U, R> unBound, U arg) {
		return fnArg -> unBound.apply(fnArg, arg);
	}

	public static <T, R> Supplier<R> curry(Function<T, R> unBound, T arg) {
		return () -> unBound.apply(arg);
	}

	public static <T> Predicate<T> predicatize(Function<T, Boolean> origin) {
		return origin::apply;
	}

	public static <T, U, R> BiFunction<U, T, R> reversingArguments(BiFunction<T, U, R> original) {
		return (a, b) -> original.apply(b, a);
	}

	public static <T> Supplier<T> constant(T value) {
		return () -> value;
	}

	public static <T, R> Function<T, R> always(R value) {
		return (in) -> value;
	}

	public static <T, R> Function<T, R> always(Supplier<R> value) {
		return (in) -> value.get();
	}

	public static <T, R> Function<T, R> tryCatch(Function<T, R> throwing) {
		return (t) -> {
			try {
				return throwing.apply(t);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}
}