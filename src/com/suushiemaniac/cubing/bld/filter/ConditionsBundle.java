package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle;
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition;
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import com.suushiemaniac.cubing.bld.model.source.AlgSource;
import com.suushiemaniac.cubing.bld.util.BruteForceUtil;
import com.suushiemaniac.cubing.bld.util.ClosureUtil;
import com.suushiemaniac.cubing.bld.util.SpeffzUtil;
import com.suushiemaniac.cubing.bld.util.StringUtil;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConditionsBundle {
	public static final String REGEX_UNIV = ".*";

	protected static boolean ALLOW_TWISTED_BUFFER = true;

	public static void setGlobalAllowTwistedBuffer(boolean allowTwistedBuffer) {
		ConditionsBundle.ALLOW_TWISTED_BUFFER = allowTwistedBuffer;
	}

	protected PieceType type;

	protected IntCondition targets;
	protected IntCondition breakIns;
	protected IntCondition preSolved;
	protected IntCondition misOriented;

	protected BooleanCondition parity;
	protected BooleanCondition bufferSolved;

	protected boolean allowTwistedBuffer;

	protected String memoRegExp;
	protected String predicateRegExp;
	protected String letterPairRegExp;

	protected Predicate<BldPuzzle> statisticalPredicate;

	public ConditionsBundle(PieceType type) {
		this.type = type;

		this.targets = IntCondition.ANY();
		this.breakIns = IntCondition.ANY();
		this.preSolved = IntCondition.ANY();
		this.misOriented = IntCondition.ANY();

		this.parity = BooleanCondition.MAYBE();
		this.bufferSolved = BooleanCondition.MAYBE();

		this.allowTwistedBuffer = ALLOW_TWISTED_BUFFER;

		this.memoRegExp = REGEX_UNIV;
		this.predicateRegExp = REGEX_UNIV;
		this.letterPairRegExp = REGEX_UNIV;

		this.statisticalPredicate = ClosureUtil.predicatize(ClosureUtil.always(true));
	}

	public PieceType getPieceType() {
		return this.type;
	}

	protected IntCondition getTargets() {
		return this.targets; // ANY()
	}

	private void writeTargets(IntCondition targets) {
		targets.capMin(0);
		// C=10 E=16 W=34 XC=34 TC=34
		targets.capMax(((type.getNumPiecesNoBuffer() / 2) * 3) + (type.getNumPiecesNoBuffer() % 2));

		this.targets = targets;
		// pre-solved
		// mis-orient
		// parity
	}

	public void setTargets(IntCondition targets) {
		this.writeProperties(
				targets,
				this.getBreakIns(),
				this.hasParity(),
				this.getPreSolved(),
				this.getMisOriented(),
				this.isBufferSolved()
		);
	}

	protected IntCondition getBreakIns() {
		return this.breakIns; // ANY()
	}

	private void writeBreakIns(IntCondition breakIns) {
		// C=7 E=11 W=23 XC=23 TC=23
		breakIns.capMin(Math.max(this.isBufferSolved().getNegative() ? 1 : 0, this.getTargets().getMin() - type.getNumPiecesNoBuffer()));
		// C=3 E=5 W=11 XC=11 TC=11
		breakIns.capMax(type.getNumPiecesNoBuffer() / 2);

		this.breakIns = breakIns;
		// targets
	}

	public void setBreakIns(IntCondition breakIns) {
		this.writeProperties(
				this.getTargets(),
				breakIns,
				this.hasParity(),
				this.getPreSolved(),
				this.getMisOriented(),
				this.isBufferSolved()
		);
	}

	protected BooleanCondition hasParity() {
		return this.parity; // MAYBE()
	}

	private void writeParity(BooleanCondition hasParity) {
		if (this.getTargets().isExact()) {
			hasParity.setValue(this.getTargets().getMax() % 2 == 1);
		}

		this.parity = hasParity;
	}

	public void setParity(BooleanCondition parity) {
		this.writeProperties(
				this.getTargets(),
				this.getBreakIns(),
				parity,
				this.getPreSolved(),
				this.getMisOriented(),
				this.isBufferSolved()
		);
	}

	protected BooleanCondition isBufferSolved() {
		return this.bufferSolved; // MAYBE()
	}

	private void writeBufferSolved(BooleanCondition bufferSolved) {
		this.bufferSolved = bufferSolved;
		// break-ins
	}

	public void setBufferSolved(BooleanCondition bufferSolved) {
		this.writeProperties(
				this.getTargets(),
				this.getBreakIns(),
				this.hasParity(),
				this.getPreSolved(),
				this.getMisOriented(),
				bufferSolved
		);
	}

	protected boolean isAllowTwistedBuffer() {
		return this.allowTwistedBuffer; // global constant
	}

	public void setAllowTwistedBuffer(boolean allowTwisted) {
		this.allowTwistedBuffer = allowTwisted;
	}

	protected IntCondition getPreSolved() {
		return this.preSolved; // ANY()
	}

	protected IntCondition getMisOriented() {
		return this.misOriented; // ANY()
	}

	private void writeSolvedMisOriented(IntCondition solved, IntCondition misOriented) {
		// C=7 E=11 W=23 XC=23 TC=23
		int leftOverMin = Math.max(0, type.getNumPiecesNoBuffer() + this.getBreakIns().getMax() - this.getTargets().getMax());
		int leftOverMax = Math.min(type.getNumPiecesNoBuffer(), type.getNumPiecesNoBuffer() + this.getBreakIns().getMin() - this.getTargets().getMin());

		solved.capMin(0);
		misOriented.capMin(0);

		// C=8 E=12 W=? XC=? TC=?
		solved.capMax(type.getNumPieces());
		// C=8 E=12 W=? XC=? TC=?
		misOriented.capMax(type.getNumPieces());

		int sumMin = solved.getMin() + misOriented.getMin();
		int sumMax = solved.getMax() + misOriented.getMax();

		if (sumMin > leftOverMax) {
			if (solved.isPrecise() || !misOriented.isPrecise()) {
				misOriented.setMin(misOriented.getMin() - sumMin + leftOverMax);
			}

			if (misOriented.isPrecise() || !solved.isPrecise()) {
				solved.setMin(solved.getMin() - sumMin + leftOverMax);
			}
		} else if (sumMax < leftOverMin) {
			if (solved.isPrecise() || !misOriented.isPrecise()) {
				misOriented.setMax(misOriented.getMax() + leftOverMin - sumMax);
			}

			if (misOriented.isPrecise() || !solved.isPrecise()) {
				solved.setMax(misOriented.getMax() + leftOverMin - sumMax);
			}
		}

		this.preSolved = solved;
		this.misOriented = misOriented;
		// targets
	}

	public void setSolved(IntCondition solved) {
		this.writeProperties(
				this.getTargets(),
				this.getBreakIns(),
				this.hasParity(),
				solved,
				this.getMisOriented(),
				this.isBufferSolved()
		);
	}

	public void setMisOriented(IntCondition misOriented) {
		this.writeProperties(
				this.getTargets(),
				this.getBreakIns(),
				this.hasParity(),
				this.getPreSolved(),
				misOriented,
				this.isBufferSolved()
		);
	}

	public void writeProperties(IntCondition targets, IntCondition breakIns, BooleanCondition parity, IntCondition solved, IntCondition misOriented, BooleanCondition bufferSolved) {
		this.writeTargets(targets);
		this.writeBufferSolved(bufferSolved);
		this.writeBreakIns(breakIns);
		this.writeParity(parity);
		this.writeSolvedMisOriented(solved, misOriented);
	}

	protected String getMemoRegex() {
		return this.memoRegExp; // REGEX_UNIV
	}

	public void setMemoRegex(String regex) {
		this.memoRegExp = regex;
	}

	protected String getLetterPairRegex() {
		return this.letterPairRegExp; // REGEX_UNIV
	}

	public void setLetterPairRegex(String[] letteringScheme, List<String> pairs, boolean conjunctive, boolean allowInverse) {
		String letters = String.join("", letteringScheme);
		String row = StringUtil.guessRegExpRange(letters);

		if (row.equals(letters)) {
			row = "[" + Pattern.quote(row) + "]";
		}

		String anyLP = row + "{2}";
		String kleeneLP = "(" + anyLP + ")*";

		if (allowInverse) {
			pairs = pairs.stream()
					.map(pair -> "([" + pair + "]{2})")
					.collect(Collectors.toList());
		}

		String glue = conjunctive ? kleeneLP : "|";
		List<String> pieces = conjunctive ? Collections.nCopies(pairs.size(), "(" + String.join("|", pairs) + ")") : pairs;
		String joined = String.join(glue, pieces);

		if (!conjunctive) {
			joined = "(" + joined + ")";
		}

		this.letterPairRegExp = kleeneLP + joined + kleeneLP;
	}

	public void setLetterPairRegex(String[] letteringScheme, List<String> pairs, boolean conjunctive) {
		this.setLetterPairRegex(letteringScheme, pairs, conjunctive, false);
	}

	public void setLetterPairRegex(String[] letteringScheme, List<String> pairs) {
		this.setLetterPairRegex(letteringScheme, pairs, true);
	}

	protected String getPredicateRegex() {
		return this.predicateRegExp; // REGEX_UNIV
	}

	public void setPredicateRegex(AlgSource algSource, Predicate<Algorithm> filter) {
		SortedSet<String> matches = new TreeSet<>();
		String[] possPairs = BruteForceUtil.genBlockString(SpeffzUtil.FULL_SPEFFZ, 2, false);

		for (String pair : possPairs) {
			matches.addAll(algSource.getAlgorithms(this.type, pair).stream()
					.filter(filter)
					.map(alg -> pair)
					.collect(Collectors.toList()));
		}

		if (matches.size() > 0) {
			this.predicateRegExp = "(" + String.join("|", matches) + ")*" + (this.hasParity().getPositive() ? "[A-Z]?" : "");
		}
	}

	protected Predicate<BldPuzzle> getStatisticalPredicate() {
		return this.statisticalPredicate; // (in) -> true
	}

	public void setStatisticalPredicate(Predicate<BldPuzzle> predicate) {
		this.statisticalPredicate = predicate;
	}

	public boolean matchingConditions(BldPuzzle inCube) {
		return inCube.getPieceTypes().contains(this.type)
				&& this.hasParity().evaluatePositive(inCube.hasParity(this.type))
				&& this.isBufferSolved().evaluatePositive(inCube.isBufferSolved(this.type, this.isAllowTwistedBuffer()))
				&& this.getBreakIns().evaluate(inCube.getBreakInCount(this.type))
				&& this.getTargets().evaluate(inCube.getStatLength(this.type))
				&& this.getPreSolved().evaluate(inCube.getPreSolvedCount(this.type))
				&& this.getMisOriented().evaluate(inCube.getMisOrientedCount(this.type))
				&& inCube.getSolutionRaw(this.type).matches(this.getMemoRegex()) // TODO have regular expressions comply w/ buffer floats!
				&& inCube.getSolutionRaw(this.type).matches(this.getLetterPairRegex())
				&& inCube.getSolutionRaw(this.type).matches(this.getPredicateRegex())
				&& this.getStatisticalPredicate().test(inCube);
	}

	public String getStatString() {
		return this.type.mnemonic() + ": " + (this.hasParity().getPositive() ? "_" : "") +
				(this.hasParity().isImportant() ? "! " : "? ") +
				this.getTargets().toString() +
				" " +
				(this.isBufferSolved().getPositive() ? (this.isAllowTwistedBuffer() ? "**" : "*") : "") +
				(this.isBufferSolved().isImportant() ? "! " : "? ") +
				this.getBreakIns().toString("#") +
				" " +
				this.getMisOriented().toString("~") +
				" " +
				this.getPreSolved().toString("+");
	}

	@Override
	public String toString() {
		return this.getStatString();
	}
}
