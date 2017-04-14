package com.suushiemaniac.cubing.bld.filter.thread;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import net.gnehzr.tnoodle.scrambles.Puzzle;

import java.util.concurrent.BlockingQueue;

public final class ScrambleProducer implements Runnable {
	private final Puzzle puzzle;
	private final NotationReader reader;
	private final BlockingQueue<Algorithm> scrambleQueue;

	public ScrambleProducer(Puzzle scramblingPuzzle, BlockingQueue<Algorithm> queue) {
		this.puzzle = scramblingPuzzle;
		this.reader = new CubicAlgorithmReader();
		this.scrambleQueue = queue;
	}

	@Override
	public void run() {
		while (true) {
			try {
				this.scrambleQueue.put(this.reader.parse(this.puzzle.generateScramble()));
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}
