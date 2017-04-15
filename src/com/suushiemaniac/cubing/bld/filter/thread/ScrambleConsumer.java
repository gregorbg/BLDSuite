package com.suushiemaniac.cubing.bld.filter.thread;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

public final class ScrambleConsumer implements Callable<List<Algorithm>> {
	private final int numScrambles;
	private final BldPuzzle testCube;
	private final Predicate<BldPuzzle> matchingConditions;
	private final BlockingQueue<Algorithm> scrambleQueue;

	protected List<Algorithm> algList;

	public ScrambleConsumer(BldPuzzle analyzingPuzzle, Predicate<BldPuzzle> matchingConditions, int numScrambles, BlockingQueue<Algorithm> queue) {
		this.numScrambles = numScrambles;
		this.testCube = analyzingPuzzle;
		this.matchingConditions = matchingConditions;
		this.scrambleQueue = queue;

		this.algList = new ArrayList<>();
	}

	public List<Algorithm> getAlgList() {
		return new ArrayList<>(this.algList);
	}

	@Override
	public List<Algorithm> call() throws Exception {
		do {
			try {
				this.testCube.parseScramble(this.scrambleQueue.take());

				if (this.matchingConditions.test(this.testCube)) {
					this.algList.add(this.testCube.getScramble());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		} while (this.algList.size() < this.numScrambles);

		return this.getAlgList();
	}
}