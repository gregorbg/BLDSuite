package com.suushiemaniac.cubing.bld.optim;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.model.AlgSource;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BreakInOptim {
    private AlgSource source;

    public BreakInOptim(AlgSource source) {
        this.source = source;
    }

    public List<Algorithm> optimizeBreakInsAfter(char target, PieceType type) {
        List<Algorithm> algList = new ArrayList<Algorithm>();
        for (char c = 'A'; c < 'Y'; c++) algList.addAll(this.source.getAlg(type, "" + target + c));
        Collections.sort(algList, new Comparator<Algorithm>() {
            @Override
            public int compare(Algorithm o1, Algorithm o2) {
                //return Integer.compare(o1.getSubGroup().size(), o2.getSubGroup().size());
                return new Integer(o1.getSubGroup().size()).compareTo(o2.getSubGroup().size());
            }
        });
        Collections.sort(algList, new Comparator<Algorithm>() {
            @Override
            public int compare(Algorithm o1, Algorithm o2) {
                //return Integer.compare(o1.length(), o2.length());
                return new Integer(o1.length()).compareTo(o2.length());
            }
        });
        return algList;
    }
}
