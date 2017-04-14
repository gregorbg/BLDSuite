package com.suushiemaniac.cubing.bld.verify;

import com.suushiemaniac.cubing.alglib.alg.SubGroup;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.alglib.util.ParseUtils;
import com.suushiemaniac.cubing.bld.model.AlgSource;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.util.BruteForceUtil;

import java.util.*;

public class Verificator {
    protected static String[] fullLetterPairs = BruteForceUtil.genBlockString(BruteForceUtil.ALPHABET, 2, false);

    private NotationReader reader;
    private AlgSource source;

    public Verificator(NotationReader reader, AlgSource source) {
        this.reader = reader;
        this.source = source;
    }

    public Map<String, Map<String, Boolean>> verifyAll(PieceType type) {
        Map<String, Map<String, Boolean>> fullSolutionMap = new HashMap<>();
        for (String possPair : fullLetterPairs) fullSolutionMap.put(possPair, this.verifySingleCase(type, possPair));
        return fullSolutionMap;
    }

    public Map<String, Boolean> verifySingleCase(PieceType type, String letterPair) {
        Map<String, Boolean> solutionMap = new HashMap<>();
        Set<String> algStringList = this.source.getRawAlg(type, letterPair);
        if (algStringList != null)
            for (String alg : algStringList)
                solutionMap.put(alg, ParseUtils.isParseable(alg, this.reader));
                        //&& FiveBldCube.solves(type, this.reader.parse(alg).plain().toFormatString(), letterPair));
        return solutionMap;
    }

    public Map<String, List<String>> findMatchingSubGroup(PieceType type, SubGroup group) {
        Map<String, List<String>> sameGroupMap = new HashMap<>();
        for (String possPair : fullLetterPairs) {
            sameGroupMap.put(possPair, new ArrayList<>());
            Set<String> algStringList = this.source.getRawAlg(type, possPair);
            if (algStringList != null)
                for (String alg : algStringList)
                    if (ParseUtils.isParseable(alg, this.reader) && this.reader.parse(alg).getSubGroup().sameOrLargerSubGroup(group))
                        sameGroupMap.get(possPair).add(alg);
        }
        return sameGroupMap;
    }

    public Map<String, List<String>> checkParseable(PieceType type) {
        Map<String, List<String>> unparseableMap = new HashMap<>();
        for (String possPair : fullLetterPairs) {
            unparseableMap.put(possPair, new ArrayList<>());
            Set<String> algStringList = this.source.getRawAlg(type, possPair);
            if (algStringList != null)
                for (String alg : algStringList)
                    if (!ParseUtils.isParseable(alg, this.reader))
                        unparseableMap.get(possPair).add(alg);
        }
        return unparseableMap;
    }
}