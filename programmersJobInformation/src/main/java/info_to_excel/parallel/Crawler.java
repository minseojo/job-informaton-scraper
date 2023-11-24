package info_to_excel.parallel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileOutputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Crawler {
    private final int width; // 가로 해상도
    private final int height; // 세로 해상도
    private final int numberOfThreads;
    private final List<ThreadRole> threadRoles;

    public Crawler(int width, int height, int numberOfThreads) {
        this.width = width;
        this.height = height;
        this.numberOfThreads = numberOfThreads;
        this.threadRoles = new ArrayList<>();
    }

    public void exe() {
        long startTime = System.currentTimeMillis();
        int totalPage = findTotalPage();
        splitPage(totalPage);
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

            for (int thread = 0; thread < numberOfThreads; thread++) {
                final int currentThread = thread;
                Callable<Void> task = () -> {
                    try {
                        WebDriver driver = new ChromeDriver();
                        crawlPage(currentThread, driver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                };
                executorService.submit(task);
            }

            executorService.shutdown();
            executorService.awaitTermination(30, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            long endTime = System.currentTimeMillis();
            long elapsedTimeMillis = endTime - startTime;
            System.out.println("크롤링 시간 (밀리초): " + elapsedTimeMillis);
        }
    }

    private void crawlPage(int threadNumber, WebDriver driver) {
        try(Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Programmers Job Information " + threadNumber + "번");

            initDriver(driver, threadNumber);

            int startPage = threadRoles.get(threadNumber).startPage();
            int endPage = threadRoles.get(threadNumber).endPage();
            System.out.printf("%d번 스레드, 시작 페이지 : %d, 마지막 페이지 : %d%n", threadNumber, startPage, endPage - 1); // [startPage, endPage)

            int rowNum = 0;
            for (int page = startPage; page < endPage; page++) {
                String url = "https://career.programmers.co.kr/job?page=" + page;
                driver.get(url);
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
                wait.until(ExpectedConditions.jsReturnsValue("return document.readyState == 'complete'"));

                WebElement webElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("list-positions")));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("list-position-item")));
                List<WebElement> elements = webElement.findElements(By.className("list-position-item"));
//                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//                WebElement webElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("list-positions")));
//                Thread.sleep(400);
//                List<WebElement> elements = webElement.findElements(By.className("list-position-item"));

                for (WebElement e : elements) {
                    String information = e.getText();
                    Row row = sheet.createRow(rowNum++);
                    String[] type = information.split("\n");
                    for (int column = 0; column < type.length; column++) {
                        row.createCell(column).setCellValue(type[column]);
                    }
                }
            }


            writeToExcel(workbook, threadNumber);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.close();
        }
    }

    private int findTotalPage() {
        String url = "https://career.programmers.co.kr/job";
        WebDriver driver = new ChromeDriver();
        driver.get(url);
        WebElement element = driver.findElement(By.xpath("//*[@id=\"list-positions-wrapper\"]/div/div[1]/h6"));

        String text = element.getText();
        driver.close();

        String numberText = text.replaceAll("\\D", ""); // 숫자를 제외한 문자열 ""으로 치환
        int infoNumber = Integer.parseInt(numberText);
        int totalPage = (infoNumber + 23) / 24;

        System.out.println("전체 페이지 : " + totalPage);
        return totalPage;
    }

    private void splitPage(int totalPages) {
        int pagesPerThread = totalPages / numberOfThreads; // 각 스레드가 처리할 기본 페이지 수
        int remainingPages = totalPages % numberOfThreads; // 나머지 페이지 수
        // 예를 들어, 페이지가 71개고 스레드 개수가 8개면
        // 각 스레드는 {9, 9, 9, 9, 9, 9, 9, 8} 개의 페이지를 크롤링한다.

        int[] threadPages = new int[numberOfThreads];

        // 각 스레드에 할당될 페이지 수 계산
        for (int thread = 0; thread < numberOfThreads; thread++) {
            if (remainingPages > 0) {
                threadPages[thread] = pagesPerThread + 1;
                remainingPages--;
            } else {
                threadPages[thread] = pagesPerThread;
            }
        }

        // 각 스레드에게 할당된 페이지 범위로 스레드 생성 및 실행
        int startPage = 1;
        int endPage = 1 + threadPages[0];
        for (int thread = 0; thread < numberOfThreads; thread++) {
            threadRoles.add(new ThreadRole(startPage, endPage));
            startPage += threadPages[thread];
            endPage += threadPages[thread];
        }
    }

    private void initDriver(WebDriver driver, int threadNumber) {
        int widthCount = 4; // 화면 가로에 나타 낼 브라우저 개수 (4 x 2)
        int heightCount = 2;

        int browserWidth = width / widthCount;
        int browserHeight = height / heightCount;
        Dimension browserSize = new Dimension(browserWidth + 25, browserHeight);
        driver.manage().window().setSize(browserSize);

        if (threadNumber < widthCount) {
            Point browserPosition = new Point(threadNumber * browserWidth, 0);
            driver.manage().window().setPosition(browserPosition);
        } else {
            Point browserPosition = new Point((threadNumber - widthCount) * browserWidth, browserHeight);
            driver.manage().window().setPosition(browserPosition);
        }
    }

    private void writeToExcel(Workbook workbook, int threadNumber) {
        String fileName = LocalDate.now().toString();
        try (FileOutputStream outputStream = new FileOutputStream(fileName + "_" + threadNumber + ".xlsx")) {
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
