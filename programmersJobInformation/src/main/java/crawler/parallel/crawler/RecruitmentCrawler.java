package crawler.parallel.crawler;

import crawler.parallel.file.ExcelMerger;
import crawler.parallel.file.FileManager;
import crawler.parallel.vo.Resolution;
import crawler.parallel.vo.ThreadRole;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class RecruitmentCrawler {
    private final Resolution resolution; // 화면 해상도
    private final String appendQuery; //
    private int numberOfThreads;
    private final List<ThreadRole> threadRoles;

    public RecruitmentCrawler(Resolution resolution, String appendQuery, int numberOfThreads) {
        this.resolution = resolution;
        this.appendQuery = appendQuery;
        this.numberOfThreads = numberOfThreads;
        this.threadRoles = new ArrayList<>();
    }

    public void execute() {
        System.out.println(appendQuery);
        long startTime = System.currentTimeMillis();
        int totalPage = findTotalPage();
        splitPage(totalPage);
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

            IntStream.range(0, numberOfThreads)
                    .forEach(thread -> executorService
                            .submit(() -> crawlPage(thread)));

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

    private void crawlPage(int threadNumber) {
        WebDriver driver = new ChromeDriver();

        try(Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Programmers Job Information " + threadNumber + "번");
            setupDriver(driver, threadNumber);

            int startPage = threadRoles.get(threadNumber).startPage();
            int endPage = threadRoles.get(threadNumber).endPage();
            System.out.printf("%d번 스레드, 시작 페이지 : %d, 마지막 페이지 : %d%n", threadNumber, startPage, endPage - 1); // [startPage, endPage)

            int rowNum = 0;
            for (int page = startPage; page < endPage; page++) {
                String url = "https://career.programmers.co.kr/job?page=" + page + appendQuery;
                driver.get(url);

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
                wait.until(ExpectedConditions.jsReturnsValue("return document.readyState == 'complete'"));

                WebElement webElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("list-positions")));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("list-position-item")));
                List<WebElement> elements = webElement.findElements(By.className("list-position-item"));

                for (WebElement element : elements) {
                    String information = element.getText();
                    Row row = sheet.createRow(rowNum++);
                    List<String> types = List.of(information.split("\n"));
                    for (int column = 0; column < types.size(); column++) {
                        row.createCell(column).setCellValue(types.get(column));
                    }
                }
            }

            writeToMockExcel(workbook, threadNumber);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        } finally {
            driver.close();
        }
    }

    private int findTotalPage() {
        String url = "https://career.programmers.co.kr/job?page=1" + appendQuery;
        WebDriver driver = new ChromeDriver();
        driver.get(url);

        WebElement element = driver.findElement(By.xpath("//*[@id=\"list-positions-wrapper\"]/div/div[1]/h6"));
        String text = element.getText();
        driver.close();

        String numberText = text.replaceAll("\\D", ""); // 숫자를 제외한 문자열 ""으로 치환
        int infoNumber = Integer.parseInt(numberText);
        System.out.println("전체 채용 정보 : " + infoNumber + "개");
        int totalPage = (infoNumber + 23) / 24;

        System.out.println("전체 페이지 : " + totalPage + "페이지");
        return totalPage;
    }

    private void splitPage(int totalPages) {
        int pagesPerThread = totalPages / numberOfThreads; // 각 스레드가 처리할 기본 페이지 수
        int remainingPages = totalPages % numberOfThreads; // 나머지 페이지 수
        // 예를 들어, 페이지가 71개고 스레드 개수가 8개면
        // 각 스레드는 {9, 9, 9, 9, 9, 9, 9, 8} 개의 페이지를 크롤링한다.
        int[] threadPages = new int[numberOfThreads];

        // 스레드 개수는 8개인데 전체 페이지가 7개인 경우, 1개의 스레드만 이용
        if (pagesPerThread < 1) {
            numberOfThreads = 1;
            int startPage= 1;
            int endPage= remainingPages + 1;
            threadRoles.add(new ThreadRole(startPage, endPage));
        }

        // 각 스레드에 할당될 페이지 수 계산
        for (int thread = 0; thread < numberOfThreads; thread++) {
            if (remainingPages > 0) {
                threadPages[thread] = pagesPerThread + 1;
                remainingPages--;
            } else {
                threadPages[thread] = pagesPerThread;
            }
        }

        int startPage = 1;
        int endPage;
        for (int thread = 0; thread < numberOfThreads; thread++) {
            endPage = startPage + threadPages[thread];
            threadRoles.add(new ThreadRole(startPage, endPage));
            startPage = endPage;
        }
    }

    private void setupDriver(WebDriver driver, int threadNumber) {
        if (numberOfThreads < 2) numberOfThreads = 2;
        int widthCount = numberOfThreads / 2; // 화면 나타 낼 브라우저 비율 (numberOfThreads x 2), 스레드 개수가 8개면 4x2
        int heightCount = 2;
        int browserWidth = resolution.getWidth() / widthCount;
        int browserHeight = resolution.getHeight() / heightCount;
        Dimension browserSize = new Dimension(browserWidth + 25, browserHeight);
        driver.manage().window().setSize(browserSize);

        Point browserPosition;
        if (threadNumber < widthCount) {
            browserPosition = new Point(threadNumber * browserWidth, 0);
        } else {
            browserPosition = new Point((threadNumber - widthCount) * browserWidth, browserHeight);
        }
        driver.manage().window().setPosition(browserPosition);
    }

    private void writeToMockExcel(Workbook workbook, int threadNumber) throws IOException {
        Path mockDirectoryPath = FileManager.createMockDirectory();
        Path mcokFilePath = Paths.get(mockDirectoryPath.toString(), threadNumber + ExcelMerger.FILE_EXTENSION);

        try (FileOutputStream outputStream = new FileOutputStream(mcokFilePath.toString())) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
