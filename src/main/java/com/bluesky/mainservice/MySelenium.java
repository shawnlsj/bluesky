package com.bluesky.mainservice;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.formula.functions.Vlookup;
import org.apache.poi.ss.usermodel.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MySelenium {
    static WebDriver driver;
    static Sheet sheet;
    static int rowIndex = 1;
    static int cellIndex = 0;
    static Row row;
    static Cell cell;
    static LocalDate limitDate = LocalDate.parse("2021-12-02");

    public static void main(String[] args) throws Exception {
        //엑셀 읽어오기
        File file = new File("C:/Users/lsj/Desktop/calendar.xlsx");
        Workbook excel = WorkbookFactory.create(new FileInputStream(file));
        sheet = excel.getSheetAt(0);

        System.setProperty("webdriver.chrome.driver", "D:/dev/chromedriver.exe");
        driver = new ChromeDriver();
        driver.get("https://www.g2b.go.kr/pt/menu/selectSubFrame.do?framesrc=https://www.g2b.go.kr:8340/search.do?category=TGONG&kwd=%B4%DE%B7%C2");
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        driver.switchTo().frame(1);

        //페이지 게시글 리스트 가져오기
        List<WebElement> liList = driver.findElements(By.cssSelector("#contents > div.search_area > ul > li"));

        //페이지에 있는 게시글 목록 하나씩 순회하기
        for (int i = 0; i < liList.size(); i++) {
            WebElement li = liList.get(i);

            //게시글 리스트에서 날짜구하기
            String createDateString = li.findElement(By.className("m2")).findElement(By.tagName("span")).getText();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            LocalDate createDate = LocalDate.parse(createDateString, formatter);
            Info info = new Info();
            info.getDate().setValue(createDate);

            //limitDate 보다 이전 날짜면 write 호출 하고 프로그램 종료
            if (createDate.isBefore(limitDate)) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                excel.write(fileOutputStream);
                fileOutputStream.close();
                return;
            }


            li.findElement(By.cssSelector("strong > a")).sendKeys(Keys.ENTER);
            driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);

            driver.switchTo().defaultContent();
            driver.switchTo().frame(1).switchTo().frame("bodyFrame");

            //모든 테이블을 가져와서
            //테이블 당 모든 tr 을 순회하며 원하는 th 찾기
            List<WebElement> tableList = driver.findElements(By.tagName("table"));
            try {
                info = tableProcess(tableList, info);
                //모든 테이블을 순회했으면 유효한 info 를 메모리에 write 하기
                writeToMemory(info);
            } catch (Exception e) {
            }
            //    System.out.println("======");

            //페이지 분석이 끝났으면 뒤로가서 새로 글 리스트 받아오기
            driver.navigate().back();
            driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
            driver.switchTo().defaultContent();
            driver.switchTo().frame(1);
            liList = driver.findElements(By.cssSelector("#contents > div.search_area > ul > li"));
        }
    }

    private static void writeToMemory(Info info) {
        Row row = sheet.createRow(rowIndex);

        //공고날짜(년,월,일)	공고기관	배정예산	수량	 계약방법 예가방법 공고명
        LocalDate date = info.getDate().getValue();

        Cell cell = row.createCell(cellIndex);
        cell.setCellValue(date.getYear());
        cellIndex++;

        cell = row.createCell(cellIndex);
        cell.setCellValue(date.getMonthValue());
        cellIndex++;

        cell = row.createCell(cellIndex);
        cell.setCellValue(date.getDayOfMonth());
        cellIndex++;

        if (info.getCompany().isNotEmpty()) {
            cell = row.createCell(cellIndex);
            cell.setCellValue(info.getCompany().getValue());
        }
        cellIndex++;

        if (info.getBudget().isNotEmpty()) {
            cell = row.createCell(cellIndex);
            cell.setCellValue(info.getBudget().value);
        }
        cellIndex++;

        // 4번째 셀
        if (info.getQuantity().isNotEmpty()) {
            cell = row.createCell(cellIndex);
            cell.setCellValue(info.getQuantity().value);
        }
        cellIndex++;

        if (info.getContractMethod().isNotEmpty()) {
            cell = row.createCell(cellIndex);
            cell.setCellValue(info.getContractMethod().value);
        }
        cellIndex++;

        if (info.getEstimationMethod().isNotEmpty()) {
            cell = row.createCell(cellIndex);
            cell.setCellValue(info.getEstimationMethod().value);
        }
        cellIndex++;

        // 마지막 셀
        if (info.getTitle().isNotEmpty()) {
            cell = row.createCell(cellIndex);
            cell.setCellValue(info.getTitle().value);
        }
        rowIndex++;
        cellIndex = 0;
    }

    private static Info tableProcess(List<WebElement> tableList, Info info) throws Exception {
        //테이블별로 순회
        for (WebElement table : tableList) {
            String className = table.getAttribute("class");
            // System.out.println("className = " + className);
            if (className.equals("table_list_bidPurchaseTbl table_list")) {
                List<WebElement> trList = table.findElements(By.tagName("tr"));
                WebElement tr = trList.get(trList.size() - 1);
                List<WebElement> tdList = tr.findElements(By.cssSelector("td"));
                info.save("수량", tdList.get(0).getText());
                info.save("단위", tdList.get(1).getText());
                continue;
            }

            List<WebElement> trList = table.findElements(By.tagName("tr"));

            //뽑은 테이블에서 tr 태그별로 순회
            for (WebElement tr : trList) {
                boolean isValid = false;
                List<WebElement> trChildrenList = tr.findElements(By.cssSelector("th, td"));
                String key = "";
                String value = "";
                isValid = false;

                //tr의 자식태그별로 순회
                for (WebElement trChildren : trChildrenList) {
                    WebElement element = null;
                    try {
                        element = trChildren.findElement(By.cssSelector("p, div"));
                        if (isValid) {
                            isValid = false;
                            value = element.getText();
                            info.save(key, value);
                            continue;
                        }
                        if (element.getText().equals("배정예산")
                                || element.getText().equals("공고기관")
                                || element.getText().equals("공고명")
                                || element.getText().equals("계약방법")
                                || element.getText().equals("예가방법")) {
                            key = element.getText();
                            isValid = true;
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        return info;
    }

    @Getter
    @Setter
    static class Info {
        static final long TARGET_POINT = 200_000_000L;
        Value<Long> budget = new Value<>(); //배정예산
        Value<String> company = new Value<>(); //공고기관
        Value<String> title = new Value<>(); //공고명

        Value<LocalDate> date = new Value<>(); //공고날짜
        Value<String> contractMethod = new Value<>(); //계약방법
        Value<String> estimationMethod = new Value<>(); //예가방법

        Value<Long> quantity = new Value<>(); //수량
        Value<String> unit = new Value<>(); //단위

        Map<String, Value> map;

        Info() {
            map = new HashMap();
            map.put("배정예산", budget);
            map.put("공고기관", company);
            map.put("공고명", title);

            map.put("공고날짜", date);
            map.put("계약방법", contractMethod);
            map.put("예가방법", estimationMethod);

            map.put("수량", quantity);
            map.put("단위", unit);
        }

        @Getter
        static class Value<T> {
            T value;
            boolean empty = true;

            public void setValue(T value) {
                this.value = value;
                empty = false;
            }

            public boolean isNotEmpty() {
                return !empty;
            }
        }

        void save(String key, String value) {
            //배정예산이 들어올 경우 숫자로 바꾸고 setValue() 호출
            if (key.equals("배정예산")) {
                map.get(key).setValue(Long.valueOf(value.replaceAll("원|,", "")));
            } else if (key.equals("수량")) {
                map.get(key).setValue(Long.valueOf(value.replace(",", "")));
            } else {
                map.get(key).setValue(value);
            }
        }
    }
}

