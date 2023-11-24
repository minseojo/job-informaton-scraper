package info_to_excel.serial;

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
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Crawler {

    private final WebDriver driver;
    WebDriverWait wait;
    private final ChromeOptions options;
    private final List<List<String>> dataList;

    public Crawler() {
        options = new ChromeOptions();
        options.addArguments("headless");
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        dataList = new ArrayList<>();
    }

    public void exe() {
        long startTime = System.currentTimeMillis();
        try {
            int lastPage = findLastPage();
            for (int page = 1; page <= lastPage; page++) {
                System.out.println("크롤링 페이지 : " + page + ", 마지막 페이지 : " + lastPage);

                String url = "https://career.programmers.co.kr/job?page=" + page;
                driver.get(url);

                WebElement webElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("list-positions")));
                Thread.sleep(400);

                List<WebElement> elements = webElement.findElements(By.className("list-position-item"));
                for (WebElement element : elements) {
                    String information = element.getText();
                    List<String> rowData = new ArrayList<>(List.of(information.split("\n")));
                    dataList.add(rowData);
                }
            }

            writeToExcel("크롤링_데이터.xlsx");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.close();
            long endTime = System.currentTimeMillis();
            long elapsedTimeMillis = endTime - startTime;
            System.out.println("경과 시간 (밀리초): " + elapsedTimeMillis);
        }
    }

    private void writeToExcel(String fileName) {
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Programmers Job Information");

            int rowNum = 0;
            for (List<String> rowData : dataList) {
                Row row = sheet.createRow(rowNum++);
                for (int column = 0; column < rowData.size(); column++) {
                    row.createCell(column).setCellValue(rowData.get(column));
                }
            }
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int findLastPage() {
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
