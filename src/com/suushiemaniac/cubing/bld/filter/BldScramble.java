package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle;
import com.suushiemaniac.cubing.bld.filter.thread.ScrambleConsumer;
import com.suushiemaniac.cubing.bld.filter.thread.ScrambleProducer;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import net.gnehzr.tnoodle.scrambles.Puzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition.*;
import static com.suushiemaniac.cubing.bld.filter.condition.IntCondition.*;

public class BldScramble {
    protected static boolean SHOW_DISCARDED = false;

	public static void setShowDiscarded(boolean showDiscarded) {
    	BldScramble.SHOW_DISCARDED = showDiscarded;
	}

	protected BldPuzzle analyzingPuzzle;

    protected Supplier<Puzzle> scramblingSupplier;

    protected List<ConditionsBundle> conditions;

	public BldScramble(BldPuzzle refPuzzle, List<ConditionsBundle> conditions) {
		this.analyzingPuzzle = refPuzzle;
		this.scramblingSupplier = refPuzzle.getModel().supplyScramblingPuzzle();

		this.conditions = conditions;
	}

	protected BldPuzzle getAnalyzingPuzzle() {
		return this.analyzingPuzzle;
	}

	public Supplier<Puzzle> supplyScramblingPuzzle() {
		return this.scramblingSupplier;
	}

	public Puzzle generateScramblingPuzzle() {
		return this.supplyScramblingPuzzle().get();
	}

	public List<ConditionsBundle> getConditions() {
		return this.conditions;
	}

	public Algorithm findScrambleOnThread() {
        BldPuzzle testCube = this.getAnalyzingPuzzle();
        Puzzle tNoodle = this.generateScramblingPuzzle();
        NotationReader reader = new CubicAlgorithmReader();

        do {
            String scrString = tNoodle.generateScramble();
            Algorithm scramble = reader.parse(scrString);
            testCube.parseScramble(scramble);
        } while (!this.matchingConditions(testCube));

        return testCube.getScramble();
    }

    public List<Algorithm> findScramblesOnThread(int num) {
        List<Algorithm> scrambles = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            if (i % (num / Math.min(100, num)) == 0) {
                System.out.println(i);
            }

            scrambles.add(this.findScrambleOnThread());
        }

        return scrambles;
    }

    public List<Algorithm> findScramblesThreadModel(int numScrambles, IntConsumer feedbackFunction) {
		int numThreads = Runtime.getRuntime().availableProcessors() + 1;
        BlockingQueue<Algorithm> scrambleQueue = new ArrayBlockingQueue<>(numScrambles * numThreads * numThreads);

		for (int i = 0; i < numThreads; i++) {
			ScrambleProducer producer = new ScrambleProducer(this.generateScramblingPuzzle(), scrambleQueue);
			Thread genThread = new Thread(producer, "Producer " + (i + 1));

            genThread.setDaemon(true);
            genThread.start();
        }

		ScrambleConsumer consumer = new ScrambleConsumer(this.getAnalyzingPuzzle().clone(), this::matchingConditions, numScrambles, scrambleQueue);

		if (feedbackFunction != null) {
			consumer.registerFeedbackFunction(feedbackFunction);
		}

		// TODO MAYBE have multiple threads to check conformity?
		FutureTask<List<Algorithm>> consumerFuture = new FutureTask<>(consumer);

        Thread consThread = new Thread(consumerFuture, "Consumer");
		consThread.setDaemon(true);
        consThread.start();

        try {
			return consumerFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return consumer.getAlgList();
		}
	}

	public List<Algorithm> findScramblesThreadModel(int numScrambles) {
		return this.findScramblesThreadModel(numScrambles, null);
	}

    protected boolean matchingConditions(BldPuzzle inCube) {
		for (ConditionsBundle bundle : this.getConditions()) {
			if (!bundle.matchingConditions(inCube)) {
				if (SHOW_DISCARDED) {
					System.out.println("Discarded " + inCube.getStatString(true));
				}

				return false;
			}
		}

		return true;
	}

	public String getStatString() {
		return this.getConditions().stream()
				.map(ConditionsBundle::getStatString)
				.collect(Collectors.joining(" | "));
	}

	@Override
	public String toString() {
		return this.getStatString();
	}

    public static BldScramble cloneFrom(BldPuzzle refCube, boolean isStrict) {
		List<ConditionsBundle> conditions = new ArrayList<>();

    	for (PieceType type : refCube.getPieceTypes()) {
    		ConditionsBundle condition = new ConditionsBundle(type);

			condition.writeProperties(
					isStrict ? EXACT(refCube.getStatLength(type)) : MAX(refCube.getStatLength(type)),
					isStrict ? EXACT(refCube.getBreakInCount(type)) : MAX(refCube.getBreakInCount(type)),
					refCube.hasParity(type) ? isStrict ? YES() : MAYBE() : NO(),
					isStrict ? EXACT(refCube.getPreSolvedCount(type)) : MIN(refCube.getPreSolvedCount(type)),
					isStrict ? EXACT(refCube.getMisOrientedCount(type)) : MAX(refCube.getMisOrientedCount(type)),
					refCube.isBufferSolved(type) ? isStrict ? YES() : MAYBE() : NO()
			);

			condition.setAllowTwistedBuffer(!isStrict || refCube.isBufferSolvedAndMisOriented(type));
			conditions.add(condition);
		}

		return new BldScramble(refCube, conditions);
	}

	public static BldScramble fromStatString(String statString, BldPuzzle refCube, boolean isStrict) {
		List<ConditionsBundle> conditions = new ArrayList<>();

		for (String pieceStatString : statString.split("\\|")) {
			Pattern statPattern = Pattern.compile("([A-Za-z]+?):(_?)(0|[1-9][0-9]*)(\\*?)(#*)(~*)(\\+*)");
			Matcher statMatcher = statPattern.matcher(pieceStatString.replaceAll("\\s", ""));

			if (statMatcher.find()) {
				String mnemonic = statMatcher.group(1);
				PieceType type = findTypeByMnemonic(refCube, mnemonic);

				ConditionsBundle condition = new ConditionsBundle(type);

				int targets = Integer.parseInt(statMatcher.group(3));
				int breakIns = statMatcher.group(5).length();
				boolean hasParity = statMatcher.group(2).length() > 0;
				int preSolved = statMatcher.group(7).length();
				int misOriented = statMatcher.group(6).length();
				boolean bufferSolved = statMatcher.group(4).length() > 0;

				condition.writeProperties(
						isStrict ? EXACT(targets) : MAX(targets),
						isStrict ? EXACT(breakIns) : MAX(breakIns),
						hasParity ? isStrict ? YES() : MAYBE() : NO(),
						isStrict ? EXACT(preSolved) : MIN(preSolved),
						isStrict ? EXACT(misOriented) : MAX(misOriented),
						bufferSolved ? isStrict ? YES() : MAYBE() : NO()
				);

				conditions.add(condition);
			}
		}

		return new BldScramble(refCube, conditions);
	}

	protected static PieceType findTypeByMnemonic(BldPuzzle refCube, String mnemonic) {
		for (PieceType type : refCube.getPieceTypes()) {
			if (type.mnemonic().equalsIgnoreCase(mnemonic)) {
				return type;
			}
		}

		return null;
	}
}