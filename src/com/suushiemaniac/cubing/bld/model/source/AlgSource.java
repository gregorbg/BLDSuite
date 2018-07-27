package com.suushiemaniac.cubing.bld.model.source;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;

import java.net.URI;
import java.util.Set;

public interface AlgSource {
    boolean isReadable();

    boolean isWritable();

    URI getSourceURI();

    boolean mayRead();

    Set<Algorithm> getAlgorithms(PieceType type, String letterPair);

    Set<String> getRawAlgorithms(PieceType type, String letterPair);

    boolean mayWrite();

    boolean addAlgorithm(PieceType type, String letterPair, Algorithm algorithm);

    boolean addAlgorithms(PieceType type, String letterPair, Set<Algorithm> algorithms);

    boolean mayUpdate();

    boolean updateAlgorithm(PieceType type, Algorithm oldAlg, Algorithm newAlg);

    boolean mayDelete();

    boolean deleteAlgorithm(PieceType type, Algorithm algorithm);

    boolean deleteAlgorithms(PieceType type, String letterPair);
}
