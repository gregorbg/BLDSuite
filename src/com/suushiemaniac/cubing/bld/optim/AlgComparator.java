package com.suushiemaniac.cubing.bld.optim;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;

import java.util.Comparator;

public class AlgComparator implements Comparator<Algorithm> {
	protected static AlgComparator INST;

	public static AlgComparator INST() {
		if (INST == null) {
			INST = new AlgComparator();
		}

		return INST;
	}

	public static float scoreAlg(Algorithm alg) {
		return INST().score(alg);
	}

	protected AlgComparator() {}

	@Override
	public int compare(Algorithm alg, Algorithm otherAlg) {
		return Float.compare(this.score(otherAlg), this.score(alg));
	}

	public float score(Algorithm alg) {
		return (
			2 * this.lengthScore(alg)
			+ this.rotationScore(alg)
			+ this.subGroupScore(alg)
		) / 4f;
	}

	protected int lengthScore(Algorithm alg) {
		return -2 * alg.moveLength() + 26;
	}

	protected int rotationScore(Algorithm alg) {
		return -5 * alg.getRotationGroup().size() + 10;
	}

	protected int subGroupScore(Algorithm alg) {
		return -5 * alg.getSubGroup().size() + 25;
	}
}