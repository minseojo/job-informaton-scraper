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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Crawler {
    private final int numberOfThreads;
    private final List<ThreadRole> threadRoles;

    public Crawler(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        this.threadRoles = new ArrayList<>();
    }

    public void exe() {
        long startTime = System.currentTimeMillis();
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            int totalPage = findTotalPage();
            splitPage(totalPage);
            System.out.println("전체 페이지 : " + totalPage);
            for (int thread = 0; thread < numberOfThreads; thread++) {
                final int currentThread = thread;
                Callable<Void> task = () -> {
                    try (Workbook workbook = new XSSFWorkbook()) {
                        Sheet sheet = workbook.createSheet("Programmers Job Information");
                        WebDriver driver = new ChromeDriver();
                        crawlPage(currentThread, driver, workbook, sheet, totalPage);
                    }
                    return null;
                };
                executorService.submit(task);
            }

            executorService.shutdown();
            executorService.awaitTermination(30, TimeUnit.MINUTES);
        } catch (Exception error) {
            error.printStackTrace();
        } finally {
            long endTime = System.currentTimeMillis();
            long elapsedTimeMillis = endTime - startTime;
            System.out.println("경과 시간 (밀리초): " + elapsedTimeMillis);
        }
    }

    private void splitPage(int totalPages) {
        int pagesPerThread = totalPages / numberOfThreads; // 각 스레드가 처리할 기본 페이지 수
        int remainingPages = totalPages % numberOfThreads; // 나머지 페이지 수

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
        int endPage = threadPages[0];
        for (int thread = 0; thread < numberOfThreads; thread++) {
            if (thread == numberOfThreads - 1) {
                endPage--;
            }
            threadRoles.add(new ThreadRole(startPage, endPage));
            startPage += threadPages[thread];
            endPage += threadPages[thread];
        }
    }

    private void crawlPage(int threadNum, WebDriver driver, Workbook workbook, Sheet sheet, int lastPage) {
        try {
            int startPage = threadRoles.get(threadNum).getStartPage();
            int endPage = threadRoles.get(threadNum).getEndPage();

            System.out.println(String.format("%d번 스레드, 시작 페이지 : %d, 마지막 페이지 : %d", threadNum, startPage, endPage));

            int rowNum = 0;
            for (int page = startPage; page <= endPage; page++) {
                String url = "https://career.programmers.co.kr/job?page=" + page;
                // 해상도 기준
                // 브라우저 창 크기 변경(360, 450)
                Dimension browserSize = new Dimension(675, 780);
                driver.manage().window().setSize(browserSize);
                // 브라우저 창 위치 설정
                if (threadNum < 4) {
                    Point browserPosition = new Point(threadNum * 645, 0);
                    driver.manage().window().setPosition(browserPosition);
                } else {
                    Point browserPosition = new Point((threadNum - 4) * 645, 800);
                    driver.manage().window().setPosition(browserPosition);
                }
                driver.get(url);

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement webElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("list-positions")));
                Thread.sleep(400);
                List<WebElement> elements = webElement.findElements(By.className("list-position-item"));

                for (WebElement e : elements) {
                    String information = e.getText();
                    String link = e.findElement(By.className("position-link")).getAttribute("href");
                    Row row = sheet.createRow(rowNum++);
                    String[] type = information.split("\n");
                    for (int column = 0; column < type.length; column++) {
                        row.createCell(column).setCellValue(type[column]);
                    }
                    row.createCell(6).setCellValue(link);
                }
            }

            // 실제 엑셀에 쓰기
            try (FileOutputStream outputStream = new FileOutputStream("크롤링_데이터_" + startPage + ".xlsx")) {
                workbook.write(outputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.close();
        }
    }

    private int findTotalPage() {
        String url = "https://career.programmers.co.kr/job";
        WebDriver pageDriver = new ChromeDriver();
        pageDriver.get(url);
        WebElement element = pageDriver.findElement(By.xpath("//*[@id=\"list-positions-wrapper\"]/div/div[1]/h6"));

        String text = element.getText();
        pageDriver.close();
        String numberText = text.replaceAll("\\D", "");
        int infoNumber = Integer.parseInt(numberText);
        return (infoNumber + 23) / 24;
    }
}
