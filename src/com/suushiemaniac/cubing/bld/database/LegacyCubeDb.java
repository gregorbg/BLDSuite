package com.suushiemaniac.cubing.bld.database;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.util.ParseUtils;
import com.suushiemaniac.cubing.bld.analyze.FiveBldCube;
import com.suushiemaniac.cubing.bld.model.source.AlgSource;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import com.suushiemaniac.cubing.bld.model.source.DatabaseAlgSource;
import com.suushiemaniac.cubing.bld.util.SpeffzUtil;
import javafx.scene.chart.PieChart;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
public class LegacyCubeDb extends DatabaseAlgSource {
    private Connection conn;

    public void setRefCube(FiveBldCube refCube) {
        this.refCube = refCube;
    }

    private FiveBldCube refCube;

    public LegacyCubeDb(File dbFile) throws SQLException {
    	super(null);

        boolean isOldH2 = dbFile.getAbsolutePath().endsWith(".h2.db");
        String pathString = dbFile.getAbsolutePath().replace(isOldH2 ? ".h2.db" : ".mv.db", "");
        String connString = "jdbc:h2:file:" + pathString;

        if (isOldH2) connString += ";TRACE_LEVEL_FILE=0;TRACE_LEVEL_SYSTEM_OUT=0;MV_STORE=FALSE;MVCC=FALSE";
        else connString += ";MV_STORE=TRUE;MVCC=TRUE";

        this.conn = DriverManager.getConnection(connString);

		try {
			this.createTables();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		this.refCube = new FiveBldCube();
    }

    public LegacyCubeDb(String connString) throws SQLException {
    	super(DriverManager.getConnection(connString));

        try {
            this.createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.refCube = new FiveBldCube();
    }

    private void createTables() throws SQLException {
        Statement stat = conn.createStatement();
        for (PieceType type : this.refCube.getPieceTypes(true)) {
            stat.execute("create table if not exists " + type.name() + "s(letterpair char(2), alg varchar(255) primary key, score int)");
            stat.execute("create table if not exists " + type.name() + "scheme(letterpair char(2), alg varchar(255) primary key)");
        }
        stat.execute("create table if not exists lpis(letterpair char(2), alg varchar(255), score int)");
        stat.execute("create table if not exists colorscheme(colorlist varchar(255))");
    }

    public void setConnection(String connUrl) throws SQLException {
        this.conn = DriverManager.getConnection(connUrl);
    }

    public boolean isFirstStart() throws SQLException {
        Statement stat = conn.createStatement();
        ResultSet search;
        stat.execute("create table if not exists firstStart(isFirstStart char(1) primary key)");
        search = stat.executeQuery("SELECT * FROM FIRSTSTART");
        String temp = "";
        while (search.next()) temp = search.getString("isFirstStart");
        if (temp.equals("")) temp = "1";
        if (temp.equals("1")) {
            stat.execute("delete from FirstStart");
            stat.execute("insert into firstStart (isFirstStart) values (0)");
            return true;
        } else {
            stat.execute("delete from FirstStart");
            stat.execute("insert into FirstStart (isFirstStart) values (0)");
            return false;
        }
    }

    private String[] getSchemeTypesArray() {
        String[] pieceTypeNames = CubicPieceType.nameArray();
        String[] types = Arrays.copyOf(pieceTypeNames, pieceTypeNames.length + 1);
        types[pieceTypeNames.length] = "color";
        return types;
    }

    public void setCompleteSchemes() throws SQLException {
        PreparedStatement stat;
        for (String type : this.getSchemeTypesArray()) {
            String[] scheme = this.refCube.getLetteringScheme(CubicPieceType.valueOf(type));
            String joinedScheme = String.join(",", scheme);
            String typeColumn = type.equals("color") ? "color" : "letter";
            stat = conn.prepareStatement("delete from " + type + "scheme");
            stat.execute();
            stat = conn.prepareStatement("insert into " + type + "scheme (" + typeColumn + "list) values (?)");
            stat.setString(1, joinedScheme);
            stat.execute();
        }
    }

    public void readCompleteSchemes() throws SQLException {
        String letterSearch = "";
        for (String type : this.getSchemeTypesArray()) {
            String typeColumn = type.equals("color") ? "color" : "letter";
            ResultSet search = conn.createStatement().executeQuery("select * from " + type + "scheme where " + typeColumn + "list != 'null'");
            while (search.next()) letterSearch = search.getString(typeColumn + "list");
            if (letterSearch.length() > 0 && letterSearch.contains(","))
                this.refCube.setLetteringScheme(CubicPieceType.valueOf(type), letterSearch.split(","));
        }
    }

    private void addAlgorithm(String speffz, String alg, String table) throws SQLException {
        boolean duplicate = readAlgorithm(speffz, table).contains(alg);
        if (!duplicate) {
            PreparedStatement stat = conn.prepareStatement("insert into " + table.toUpperCase() + "S (letterpair, alg) values (?, ?)");
            stat.setString(1, speffz);
            stat.setString(2, alg);
            stat.execute();
        }
    }

    public void addAlgorithm(String letterPair, String alg, PieceType type) throws SQLException {
        String speffz = SpeffzUtil.normalize(letterPair, this.refCube.getLetteringScheme(type));
        this.addAlgorithm(speffz, alg, type.name());
    }

    public void addLpi(String letterPair, String image) throws SQLException {
        this.addAlgorithm(letterPair, image, "lpi");
    }

    private Set<String> readAlgorithm(String speffz, String table) throws SQLException {
        PreparedStatement stat = conn.prepareStatement("select distinct alg from " + table.toUpperCase() + "S where letterpair=?");
        stat.setString(1, speffz);
        ResultSet search = stat.executeQuery();
        Set<String> temp = new HashSet<>();
        while (search.next()) temp.add(search.getString("alg"));
        return temp;
    }

    public Set<String> readAlgorithm(String letterPair, PieceType type) throws SQLException {
        String speffz = SpeffzUtil.normalize(letterPair, this.refCube.getLetteringScheme(type));
        return this.readAlgorithm(speffz, type.name());
    }

    public Set<String> readLpi(String letterPair) throws SQLException {
        return this.readAlgorithm(letterPair, "lpi");
    }

    private void resetScore(String content, String table) throws SQLException {
        PreparedStatement stat = this.conn.prepareStatement("update " + table.toUpperCase() + "S set score = 0 where alg = ?");
        stat.setString(1, content);
        stat.execute();
    }

    public void resetScore(String alg, PieceType type) throws SQLException {
        this.resetScore(alg, type.name());
    }

    public void resetScore(String lpi) throws SQLException {
        this.resetScore(lpi, "lpi");
    }

    private void increaseScore(String content, String table) throws SQLException {
        PreparedStatement stat = this.conn.prepareStatement("update " + table.toUpperCase() + "S set score = score + 1 where alg = ?");
        stat.setString(1, content);
        stat.execute();
    }

    public void increaseScore(String alg, PieceType type) throws SQLException {
        this.increaseScore(alg, type.name());
    }

    public void increaseScore(String lpi) throws SQLException {
        this.increaseScore(lpi, "lpi");
    }

    public Map<String, List<String>> getAllLpiWords() throws SQLException {
        ResultSet res = this.conn.createStatement().executeQuery("SELECT * FROM LPIS");
        Map<String, List<String>> words = new HashMap<>();

        while (res.next()) {
            String lpKey = res.getString("LETTERPAIR");

            List<String> otherLps = words.get(lpKey);
            if (otherLps == null) otherLps = new ArrayList<>();

            otherLps.add(res.getString("ALG"));

            words.put(lpKey, otherLps);
        }

        return words;
    }

    private void removeAlgorithm(String speffz, String alg, String table) throws SQLException {
        PreparedStatement stat = conn.prepareStatement("delete from " + table.toUpperCase() + "S where alg=? and letterpair=?");
        stat.setString(1, alg);
        stat.setString(2, speffz);
        stat.execute();
    }

    public void removeAlgorithm(String letterPair, String alg, PieceType type) throws SQLException {
        String speffz = SpeffzUtil.normalize(letterPair, this.refCube.getLetteringScheme(type));
        this.removeAlgorithm(speffz, alg, type.name());
    }

    public void removeLpi(String letterPair, String image) throws SQLException {
        this.removeAlgorithm(letterPair, image, "lpi");
    }

    private void updateAlgorithm(String speffz, String oldAlg, String newAlg, String table) throws SQLException {
        removeAlgorithm(speffz, oldAlg, table);
        addAlgorithm(speffz, newAlg, table);
    }

    public void updateAlgorithm(String letterPair, String oldAlg, String newAlg, PieceType pieceType) throws SQLException {
        String speffz = SpeffzUtil.normalize(letterPair, this.refCube.getLetteringScheme(pieceType));
        this.updateAlgorithm(speffz, oldAlg, newAlg, pieceType.name());
    }

    public void updateLpi(String letterPair, String oldImage, String newImage) throws SQLException {
        this.updateAlgorithm(letterPair, oldImage, newImage, "lpi");
    }

    public void closeConnection() throws SQLException {
        conn.close();
    }

    public String getDatabaseConnString() throws SQLException {
        return this.conn.getMetaData().getURL();
    }

    @Override
    public Set<String> getRawAlgorithms(PieceType type, String letterPair) {
        try {
            return this.readAlgorithm(letterPair, type);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean addAlgorithms(PieceType type, String letterPair, Set<Algorithm> algorithms) {
		try {
			for (Algorithm alg : algorithms) {
				this.addAlgorithm(letterPair, alg.toFormatString(), type);
			}

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
    }

    @Override
    public boolean updateAlgorithm(PieceType type, Algorithm oldAlg, Algorithm newAlg) {
		try {
			this.updateAlgorithm("", oldAlg.toFormatString(), newAlg.toFormatString(), type); // FIXME
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

    @Override
    public boolean deleteAlgorithm(PieceType type, Algorithm algorithm) {
		try {
			this.removeAlgorithm("", algorithm.toFormatString(), type); // FIXME
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
    }

    @Override
    public boolean deleteAlgorithms(PieceType type, String letterPair) {
		try {
			this.removeAlgorithm(letterPair, "", type); // FIXME
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
    }
}