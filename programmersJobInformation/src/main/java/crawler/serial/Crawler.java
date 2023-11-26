package crawler.serial;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Crawler {

    private final WebDriver driver;
    private final ChromeOptions options;
    WebDriverWait wait;

    public Crawler() {
        options = new ChromeOptions();
        options.addArguments("--headless");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void exe() {
        long startTime = System.currentTimeMillis();
        try {
            int totalPage = findTotalPage();
            List<List<String>> dataList = new ArrayList<>();
            for (int page = 1; page <= totalPage; page++) {
                System.out.println("현재 크롤링 페이지 : " + page);
                String url = "https://career.programmers.co.kr/job?page=" + page;
                driver.get(url);

                wait.until(ExpectedConditions.jsReturnsValue("return document.readyState == 'complete'"));

                WebElement webElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("list-positions")));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("list-position-item")));

                List<WebElement> elements = webElement.findElements(By.className("list-position-item"));
                for (WebElement element : elements) {
                    String information = element.getText();
                    List<String> rowData = new ArrayList<>(List.of(information.split("\n")));

                    dataList.add(rowData);
                }
            }

            writeToExcel(dataList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.close();
            long endTime = System.currentTimeMillis();
            long elapsedTimeMillis = endTime - startTime;
            System.out.println("크롤링 시간 (밀리초): " + elapsedTimeMillis);
        }
    }

    private void writeToExcel(List<List<String>> dataList) {
        String fileName = LocalDate.now() + ".xlsx";

        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream outputStream = new FileOutputStream(fileName)) {
            Sheet sheet = workbook.createSheet("Programmers Job Information");

            for (int rowNum = 0; rowNum < dataList.size(); rowNum++) {
                Row row = sheet.createRow(rowNum);
                List<String> rowData = dataList.get(rowNum);

                for (int column = 0; column < rowData.size(); column++) {
                    row.createCell(column).setCellValue(rowData.get(column));
                }
            }

            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private int findTotalPage() {
        String url = "https://career.programmers.co.kr/job";
        WebDriver pageDriver = new ChromeDriver(options);
        pageDriver.get(url);
        WebElement element = pageDriver.findElement(By.xpath("//*[@id=\"list-positions-wrapper\"]/div/div[1]/h6"));

        String text = element.getText();
        pageDriver.close();
        String numberText = text.replaceAll("\\D", "");
        System.out.println("전체 채용 정보 수 : " + numberText);
        int infoNumber = Integer.parseInt(numberText);
        return (infoNumber + 23) / 24;
    }
}
