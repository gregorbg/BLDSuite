package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.bld.analyze.cube.BldCube;
import net.gnehzr.tnoodle.scrambles.Puzzle;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class BldScramble {
    protected BldCube bldAnalyzerCube;

    public void findScrambleThreadModel(int numScrambles, int numThreads) {
        BlockingQueue<String> scrambleQueue = new ArrayBlockingQueue<>(50);
        ScrambleProducer producer = new ScrambleProducer(scrambleQueue);
        ScrambleConsumer consumer = new ScrambleConsumer(numScrambles, this.bldAnalyzerCube, scrambleQueue);
        for (int i = 0; i < numThreads; i++) {
            Thread genThread = new Thread(producer, "Producer " + (i + 1));
            genThread.setDaemon(true);
            genThread.start();
        }
        new Thread(consumer, "Consumer").start();
    }

    protected final class ScrambleProducer implements Runnable {
        private final Puzzle puzzle;
        private final BlockingQueue<String> scrambleQueue;

        public ScrambleProducer(BlockingQueue<String> queue) {
            this.puzzle = BldScramble.this.getScramblingPuzzle();
            this.scrambleQueue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    this.scrambleQueue.put(this.puzzle.generateScramble());
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    protected final class ScrambleConsumer implements Runnable {
        private final int numScrambles;
        private final BldCube testCube;
        private final BlockingQueue<String> scrambleQueue;

        public ScrambleConsumer(int numScrambles, BldCube testCube, BlockingQueue<String> queue) {
            this.numScrambles = numScrambles;
            this.testCube = testCube;
            this.scrambleQueue = queue;
        }

        @Override
        public void run() {
            int count = 0;
            do {
                try {
                    this.testCube.parseScramble(this.scrambleQueue.take());
                    if (matchingConditions(this.testCube)) {
                        System.out.println((this.numScrambles > 1 ? (count + 1) + "\t" : "") + this.testCube.getScramble());
                        count++;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            } while (count < this.numScrambles);
        }
    }

    protected abstract <T extends BldCube> boolean matchingConditions(T inCube);

    protected abstract Puzzle getScramblingPuzzle();

    protected static int getNumInStatArray(int[] stat, int pos, int offset, int scale) {
        int mem = 0;
        for (int i = 0; i < stat.length; i++) {
            mem += stat[i];
            if (pos < mem)
                return offset + scale * i;
        }
        return 0;
    }

    protected static int getNumInStatArray(int[] stat, int pos) {
        return getNumInStatArray(stat, pos, 0, 1);
    }
}