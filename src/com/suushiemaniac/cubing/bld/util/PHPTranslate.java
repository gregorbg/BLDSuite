package com.suushiemaniac.cubing.bld.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PHPTranslate {
	public static String[] permute(String[] values, int length, boolean repetitive, boolean ordered, boolean inclusive, int bottom) {
		if (length <= 1 || length > 7) {
			List<String> shortResList = new ArrayList<>();
			Collections.addAll(shortResList, values);

			return shortResList.toArray(new String[shortResList.size()]);
		}

		List<String> permuted = new ArrayList<>();
		String[] subPermuted = permute(values, length - 1, repetitive, ordered, false, length);

		for (String value : values) {
			outer: for (String subValue : subPermuted) {
				if (repetitive || !value.contains(subValue)) {
					String newValue = value + subValue;

					if (!ordered) {
						for (String oldValue : permuted) {
							if (oldValue.compareTo(newValue) == 0) { //TODO sort strings internally here before?
								continue outer;
							}
						}
					}

					permuted.add(newValue);
				}
			}
		}

		if (inclusive && length > bottom)
			Collections.addAll(permuted, permute(values, length - 1, repetitive, ordered, true, bottom));
		return permuted.toArray(new String[permuted.size()]);
	}

	public static String[] permute(String[] values, int length, boolean repetitive, boolean ordered, boolean inclusive) {
		return permute(values, length, repetitive, ordered, inclusive, 1);
	}

	public static String[] permute(String[] values, int length, boolean repetitive, boolean ordered) {
		return permute(values, length, repetitive, ordered, false);
	}

	public static String[] permute(String[] values, int length, boolean repetitive) {
		return permute(values, length, repetitive, true);
	}

	public static String[] permute(String[] values, int length) {
		return permute(values, length, false);
	}
}
