package com.suushiemaniac.cubing.bld.optim;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.model.AlgSource;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.*;
import java.util.stream.Collectors;

public class BreakInOptim {
    private AlgSource source;

    public BreakInOptim(AlgSource source) {
        this.source = source;
    }

    public List<String> optimizeBreakInsAfter(char target, PieceType type) {
        List<Algorithm> algList = new ArrayList<>();
		Map<Algorithm, String> targetMap = new HashMap<>();

        for (char c = 'A'; c < 'Y'; c++) {
            Set<Algorithm> sourceList = this.source.getAlg(type, ("" + target) + c);
            if (sourceList == null) continue;
            algList.addAll(sourceList);

			for (Algorithm alg : sourceList)
				targetMap.put(alg, "" + c);
        }

        Collections.sort(algList, (o1, o2) -> o1.getSubGroup().toFormatString().compareTo(o2.getSubGroup().toFormatString()));
        Collections.sort(algList, (o1, o2) -> Integer.compare(o1.getSubGroup().size(), o2.getSubGroup().size()));
        Collections.sort(algList, (o1, o2) -> Integer.compare(o1.algLength(), o2.algLength()));
        Collections.sort(algList, (o1, o2) -> Integer.compare(o1.moveLength(), o2.moveLength()));

    	return algList.stream().map(a -> targetMap.get(a) + ": " + a.toFormatString()).collect(Collectors.toList());
    }
}