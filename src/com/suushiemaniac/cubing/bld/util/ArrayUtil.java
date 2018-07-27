package com.suushiemaniac.cubing.bld.util;

import java.util.Arrays;
import java.util.stream.IntStream;

public class ArrayUtil {
	public static <S, T> S mutualIndex(T element, T[] source, S[] dest) {
		return dest[ArrayUtil.index(source, element)];
	}

	public static <T> void swap(T[] data, int from, int to) {
		T elem = data[to];

		data[to] = data[from];
		data[from] = elem;
	}

	public static <T> void cycleLeft(T[] toCycle) {
		T tempStore = toCycle[0];
		System.arraycopy(toCycle, 1, toCycle, 0, toCycle.length - 1);
		toCycle[toCycle.length - 1] = tempStore;
	}

	public static <T> void cycleRight(T[] toCycle) {
		T tempStore = toCycle[toCycle.length - 1];
		System.arraycopy(toCycle, 0, toCycle, 1, toCycle.length - 1);
		toCycle[0] = tempStore;
	}

	public static <T> boolean equals(T[] arrayOne, T[] arrayTwo) {
		boolean equals = arrayOne.length == arrayTwo.length;

		equals &= IntStream.range(0, (arrayOne.length & arrayTwo.length))
				.allMatch(i -> arrayOne[i].equals(arrayTwo[i]));

		return equals;
	}

	public static <T> boolean contains(T[] array, T searchObject) {
		return Arrays.stream(array)
				.anyMatch(element -> element.equals(searchObject));
	}

	public static <T> int index(T[] array, T searchObject) {
		return IntStream.range(0, array.length)
				.filter(i -> array[i].equals(searchObject))
				.findFirst()
				.orElse(-1);

	}

	public static <T> int deepOuterIndex(T[][] array, T searchObject) {
		for (int i = 0; i < array.length; i++)
			for (T element : array[i])
				if (element.equals(searchObject)) return i;

		return -1;
	}

	public static <T> int deepInnerIndex(T[][] array, T searchObject) {
		for (T[] subarray : array)
			for (int i = 0; i < subarray.length; i++)
				if (subarray[i].equals(searchObject)) return i;

		return -1;
	}

	public static Integer[] autobox(int[] source) {
		Integer[] boxedUp = new Integer[source.length];
		for (int i = 0; i < boxedUp.length; i++) boxedUp[i] = source[i];
		return boxedUp;
	}

	public static int[] autobox(Integer[] source) {
		int[] boxedDown = new int[source.length];
		for (int i = 0; i < boxedDown.length; i++) boxedDown[i] = source[i];
		return boxedDown;
	}

	public static Character[] autobox(char[] source) {
		Character[] boxedUp = new Character[source.length];
		for (int i = 0; i < boxedUp.length; i++) boxedUp[i] = source[i];
		return boxedUp;
	}

	public static char[] autobox(Character[] source) {
		char[] boxedDown = new char[source.length];
		for (int i = 0; i < boxedDown.length; i++) boxedDown[i] = source[i];
		return boxedDown;
	}

	public static int[] fill(int length) {
		return IntStream.range(0, length).toArray();
	}

	public static <T> void fillWith(T[] array, T element) {
		Arrays.fill(array, element);
	}

	public static <T> int count(T[] array, T element) {
		return (int) Arrays.stream(array)
				.filter(elem -> elem.equals(element))
				.count();
	}

	public static <T> int deepCount(T[][] array, T element) {
		return Arrays.stream(array)
				.mapToInt(anArray -> count(anArray, element))
				.sum();
	}
}