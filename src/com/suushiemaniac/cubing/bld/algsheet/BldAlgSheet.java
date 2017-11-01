package com.suushiemaniac.cubing.bld.algsheet;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import com.suushiemaniac.cubing.bld.model.source.FileAlgSource;
import com.suushiemaniac.cubing.bld.util.BruteForceUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public abstract class BldAlgSheet extends FileAlgSource {
    protected Workbook workbook;
    private Map<PieceType, Map<String, Set<String>>> cache;

    public BldAlgSheet(File excelFile, boolean fullCache) {
        super(excelFile);

        this.workbook = this.getWorkbook();
        this.cache = fullCache ? this.readAll() : new HashMap<>();
    }

    public BldAlgSheet(File excelFile) {
        this(excelFile, true);
    }

    protected Workbook getWorkbook() {
        try (FileInputStream fis = new FileInputStream(this.file)) {
            return new XSSFWorkbook(fis);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void writeWorkbook() {
        try (FileOutputStream fos = new FileOutputStream(this.file)) {
            this.workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.workbook = this.getWorkbook();
    }

    protected Map<PieceType, Map<String, Set<String>>> readAll() {
        String[] possPairs = BruteForceUtil.genBlockString(BruteForceUtil.ALPHABET, 2, false);
        Map<PieceType, Map<String, Set<String>>> cache = new HashMap<>();

        for (PieceType type : this.getSupportedPieceTypes()) {
            Map<String, Set<String>> subCache = new HashMap<>();

            for (String pair : possPairs) {
                Set<String> current = this.fetchRawAlgorithmsFor(type, pair);

                if (current.size() > 0) {
                    subCache.put(pair, current);
                }
            }

            if (subCache.size() > 0) {
                cache.put(type, subCache);
            }

        }

        return cache;
    }

    protected Set<String> fetchRawAlgorithmsFor(PieceType type, String letterPair) {
        Set<String> current = new HashSet<>();

        Cell primaryCell = this.getPrimaryCell(type, letterPair);

        if (primaryCell != null) {
            current.add(primaryCell.getStringCellValue());
        }

        for (Cell c : this.getSecondaryCells(type, letterPair)) {
            current.add(c.getStringCellValue());
        }

        return current;
    }

    public boolean updateAlgorithm(PieceType pieceType, Algorithm oldAlg, Algorithm newAlg) {
        String[] possPairs = BruteForceUtil.genBlockString(BruteForceUtil.ALPHABET, 2, false);
        boolean updated = false;

        for (String pair : possPairs) {
            Cell c = this.getPrimaryCell(pieceType, pair);

            if (c != null && c.getStringCellValue().equals(oldAlg.toFormatString())) {
                updated = true;
                c.setCellValue(newAlg.toFormatString());
            } else {
                for (Cell sec : this.getSecondaryCells(pieceType, pair)) {
                    if (sec.getStringCellValue().equals(oldAlg.toFormatString())) {
                        updated = true;
                        sec.setCellValue(newAlg.toFormatString());
                    }
                }
            }

            if (this.cache.get(pieceType).get(pair).remove(oldAlg.toFormatString())) {
                this.cache.get(pieceType).get(pair).add(newAlg.toFormatString());
            }
        }

        if (updated) {
            this.writeWorkbook();
            return true;
        }

        return false;
    }

    protected abstract Cell getPrimaryCell(PieceType pieceType, String letterPair);

    protected abstract List<Cell> getSecondaryCells(PieceType pieceType, String letterPair);

    protected abstract PieceType[] getSupportedPieceTypes();

    @Override
    public Set<String> getRawAlgorithms(PieceType type, String letterPair) {
        return this.cache.computeIfAbsent(type, pieceType -> new HashMap<>()).computeIfAbsent(letterPair, s -> this.fetchRawAlgorithmsFor(type, s));
    }
}