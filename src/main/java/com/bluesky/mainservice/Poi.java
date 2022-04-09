package com.bluesky.mainservice;

import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Poi {
    public static void main(String[] args) throws Exception {
        File file = new File("C:/Users/lsj/Desktop/calendar.xlsx");
        Workbook excel = WorkbookFactory.create(new FileInputStream(file));

        Sheet sheet = excel.getSheetAt(0);

        Row row = sheet.createRow(0);

        Cell cell = row.createCell(0);
        cell.setCellValue("hello3");
        Cell cell1 = row.createCell(1);
        cell1.setCellValue("world3");
        cell.setCellValue(3L);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        excel.write(fileOutputStream);
        fileOutputStream.close();

    }
}
