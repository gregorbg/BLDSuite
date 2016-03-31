package com.suushiemaniac.cubing.bld.database;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.util.ParseUtils;
import com.suushiemaniac.cubing.bld.analyze.cube.FiveBldCube;
import com.suushiemaniac.cubing.bld.model.AlgSource;
import com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.util.SpeffzUtil;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
public class CubeH2 implements AlgSource {
    private Connection conn;

    public void setRefCube(FiveBldCube refCube) {
        this.refCube = refCube;
    }

    private FiveBldCube refCube;
    private boolean isOldH2;

    public CubeH2(File dbFile, boolean isOldH2) throws SQLException {
        String pathString = dbFile.getAbsolutePath().replace(isOldH2 ? ".h2.db" : ".mv.db", "");
        String connString = "jdbc:h2:file:" + pathString;
        if (isOldH2) connString += ";TRACE_LEVEL_FILE=0;TRACE_LEVEL_SYSTEM_OUT=0;MV_STORE=FALSE;MVCC=FALSE";
        else connString += ";MV_STORE=TRUE;MVCC=TRUE";
        this.isOldH2 = isOldH2;
        this.conn = DriverManager.getConnection(connString);

        try {
            Statement stat = conn.createStatement();
            for (PieceType type : FiveBldCube.getPieceTypeArray()) {
                stat.execute("create table if not exists " + type.name() + "s(letterpair char(2), alg varchar(255) primary key)");
                stat.execute("create table if not exists " + type.name() + "scheme(letterpair char(2), alg varchar(255) primary key)");
            }
            stat.execute("create table if not exists lpis(letterpair char(2), alg varchar(255))");
            stat.execute("create table if not exists colorscheme(colorlist varchar(255))");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.refCube = new FiveBldCube("");
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
            String[] scheme = this.refCube.getScheme(type);
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
                this.refCube.setScheme(type, letterSearch.split(","));
        }
    }

    private void addAlgorithm(String speffz, String alg, String table) throws SQLException {
        boolean duplicate = readAlgorithm(speffz, table).contains(alg);
        if (!duplicate) {
            PreparedStatement stat = conn.prepareStatement("insert into " + table + "s (letterpair, alg) values (?, ?)");
            stat.setString(1, speffz);
            stat.setString(2, alg);
            stat.execute();
        }
    }

    public void addAlgorithm(String letterPair, String alg, PieceType type) throws SQLException {
        String speffz = SpeffzUtil.normalize(letterPair, this.refCube.getScheme(type.name()));
        this.addAlgorithm(speffz, alg, type.name());
    }

    public void addLpi(String letterPair, String image) throws SQLException {
        this.addAlgorithm(letterPair, image, "lpi");
    }

    private List<String> readAlgorithm(String speffz, String table) throws SQLException {
        PreparedStatement stat = conn.prepareStatement("select distinct alg from " + table + "s where letterpair=?");
        stat.setString(1, speffz);
        ResultSet search = stat.executeQuery();
        ArrayList<String> temp = new ArrayList<>();
        while (search.next()) temp.add(search.getString("alg"));
        return temp;
    }

    public List<String> readAlgorithm(String letterPair, PieceType type) throws SQLException {
        String speffz = SpeffzUtil.normalize(letterPair, this.refCube.getScheme(type.name()));
        return this.readAlgorithm(speffz, type.name());
    }

    public List<String> readLpi(String letterPair) throws SQLException {
        return this.readAlgorithm(letterPair, "lpi");
    }

    private void removeAlgorithm(String speffz, String alg, String table) throws SQLException {
        PreparedStatement stat = conn.prepareStatement("delete from " + table + "s where alg=? and letterpair=?");
        stat.setString(1, alg);
        stat.setString(2, speffz);
        stat.execute();
    }

    public void removeAlgorithm(String letterPair, String alg, PieceType type) throws SQLException {
        String speffz = SpeffzUtil.normalize(letterPair, this.refCube.getScheme(type.name()));
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
        String speffz = SpeffzUtil.normalize(letterPair, this.refCube.getScheme(pieceType.name()));
        this.updateAlgorithm(speffz, oldAlg, newAlg, pieceType.name());
    }

    public void updateLpi(String letterPair, String oldImage, String newImage) throws SQLException {
        this.updateAlgorithm(letterPair, oldImage, newImage, "lpi");
    }

    public void closeConnection() throws SQLException {
        conn.close();
    }

    public File getDatabaseFile() throws SQLException {
        return new File(this.conn.getMetaData().getURL().replace("jdbc:h2:file:", "") + (this.isOldH2 ? ".h2.db" : ".mv.db"));
    }

    @Override
    public List<Algorithm> getAlg(PieceType type, String letterPair) {
        return this.getRawAlg(type, letterPair).stream().map(rawAlg -> ParseUtils.guessReaderForAlgString(rawAlg).parse(rawAlg)).collect(Collectors.toList());
    }

    @Override
    public List<String> getRawAlg(PieceType type, String letterPair) {
        try {
            return this.readAlgorithm(letterPair, type);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
