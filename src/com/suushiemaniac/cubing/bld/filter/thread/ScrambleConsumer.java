package com.suushiemaniac.cubing.bld.filter.thread;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle;

import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;

public final class ScrambleConsumer implements Runnable {
	private final int numScrambles;
	private int readScrambles;
	private final BldPuzzle testCube;
	private final Predicate<BldPuzzle> matchingConditions;
	private final BlockingQueue<Algorithm> scrambleQueue;

	public ScrambleConsumer(BldPuzzle analyzingPuzzle, Predicate<BldPuzzle> matchingConditions, int numScrambles, BlockingQueue<Algorithm> queue) {
		this.numScrambles = numScrambles;
		this.readScrambles = 0;
		this.testCube = analyzingPuzzle;
		this.matchingConditions = matchingConditions;
		this.scrambleQueue = queue;
	}

	@Override
	public void run() {
		int count = 0;

		do {
			try {
				this.testCube.parseScramble(this.scrambleQueue.take());
				this.readScrambles++;

				if (this.matchingConditions.test(this.testCube)) {
					String header = (this.numScrambles > 1 ? (count + 1) + "\t" : "");
					String findCount = this.readScrambles + "\t\t";

					System.out.println(header + findCount + this.testCube.getScramble().toFormatString());

					this.readScrambles = 0;
					count++;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		} while (count < this.numScrambles);
	}
}
