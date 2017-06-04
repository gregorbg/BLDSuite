package com.suushiemaniac.cubing.bld.model.source;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class DatabaseAlgSource implements AlgSource {
	protected Connection conn;

	protected DatabaseAlgSource(Connection conn) {
		this.conn = conn;
	}

	@Override
	protected void finalize() throws Throwable {
		this.conn.close();

		super.finalize();
	}

	public URI getSourceURI() {
		try {
			return new URI(this.conn.getMetaData().getURL());
		} catch (URISyntaxException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean isReadable() {
		return true;
	}

	@Override
	public boolean isWritable() {
		return true;
	}

	// TODO check SQL grants

	@Override
	public boolean mayRead() {
		return true;
	}

	@Override
	public boolean mayWrite() {
		return true;
	}

	@Override
	public boolean mayUpdate() {
		return true;
	}

	@Override
	public boolean mayDelete() {
		return true;
	}

	@Override
	public Set<Algorithm> getAlgorithms(PieceType type, String letterPair) {
		return this.getRawAlgorithms(type, letterPair).stream().map(algString -> type.getReader().parse(algString)).collect(Collectors.toSet());
	}

	@Override
	public boolean addAlgorithm(PieceType type, String letterPair, Algorithm algorithm) {
		return this.addAlgorithms(type, letterPair, Collections.singleton(algorithm));
	}
}
