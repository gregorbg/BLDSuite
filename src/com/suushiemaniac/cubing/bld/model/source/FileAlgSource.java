package com.suushiemaniac.cubing.bld.model.source;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;

import java.io.File;
import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class FileAlgSource implements AlgSource {
	protected File file;

	protected FileAlgSource(File file) {
		this.file = file;
	}

	@Override
	public boolean isReadable() {
		return true;
	}

	@Override
	public boolean isWritable() {
		return false;
	}

	@Override
	public URI getSourceURI() {
		return this.file.toURI();
	}

	@Override
	public boolean mayRead() {
		return this.file.canRead();
	}

	@Override
	public Set<Algorithm> getAlgorithms(PieceType type, String letterPair) {
		return this.getRawAlgorithms(type, letterPair).stream().map(algString -> type.getReader().parse(algString)).collect(Collectors.toSet());
	}

	@Override
	public boolean mayWrite() {
		return false;
	}

	@Override
	public boolean addAlgorithm(PieceType type, String letterPair, Algorithm algorithm) {
		return false;
	}

	@Override
	public boolean addAlgorithms(PieceType type, String letterPair, Set<Algorithm> algorithms) {
		return false;
	}

	@Override
	public boolean mayUpdate() {
		return false;
	}

	@Override
	public boolean updateAlgorithm(PieceType type, Algorithm oldAlg, Algorithm newAlg) {
		return false;
	}

	@Override
	public boolean mayDelete() {
		return false;
	}

	@Override
	public boolean deleteAlgorithm(PieceType type, Algorithm algorithm) {
		return false;
	}

	@Override
	public boolean deleteAlgorithms(PieceType type, String letterPair) {
		return false;
	}
}
