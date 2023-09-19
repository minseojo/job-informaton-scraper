package info_to_excle_parallel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileOutputStream;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Crawler {
    private final int numberOfThreads;
    private static final ChromeOptions options = new ChromeOptions();

    public Crawler(String WEB_DRIVER_ID, String WEB_DRIVER_PATH, int numberOfThreads) {
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        this.numberOfThreads = numberOfThreads;
        options.addArguments("headless");
        options.addArguments("--start-maximized");
    }

    public void exe() {
        long startTime = System.currentTimeMillis();
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

            for (int thread = 0; thread < numberOfThreads; thread++) {
                final int currentThread = thread;
                Callable<Void> task = () -> {
                    Workbook workbook = new XSSFWorkbook();
                    Sheet sheet = workbook.createSheet("Programmers Job Information");
                    WebDriver driver = new ChromeDriver(options);
                    crawlPage(currentThread, driver, workbook, sheet);
                    return null;
                };
                executorService.submit(task);
            }

            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.MINUTES);
        } catch (Exception error) {
            error.printStackTrace();
        } finally {
            long endTime = System.currentTimeMillis();
            long elapsedTimeMillis = endTime - startTime;
            System.out.println("경과 시간 (밀리초): " + elapsedTimeMillis);
        }
    }

    private void crawlPage(int threadNum, WebDriver driver, Workbook workbook, Sheet sheet) {
        try {
            int lastPage = findLastPage();
            int totalPage = lastPage / numberOfThreads; // 각 스레드가 담당할 전체 페이지 (마지막 페이지/ 스레드개수)
            int startPage = threadNum * totalPage; // 시작 페이지 = 전체 페이지 * 스레드 번호
            int endPage = (threadNum + 1) * totalPage; // 마지막 페이지
            int rowNum =0;
            for (int page = startPage; page < endPage; page++) {
                String url = "https://career.programmers.co.kr/job?page=" + page;
                driver.get(url);
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("list-positions")));
                List<WebElement> elements = element.findElements(By.className("list-position-item"));

                for (WebElement e : elements) {
                    String information = e.getText();
                    String link = e.findElement(By.className("position-link")).getAttribute("href");
                    //   System.out.println(information + "\n" + link + "\n");
                    Row row = sheet.createRow(rowNum++); // 열 생성
                    String type[] = information.split("\n"); //회사명, 지역, 채용분야, 채용 링크 분류 (엑셀 열을 나누기 위해)
                    for (int column = 0; column < type.length; column++) {
                        row.createCell(column).setCellValue(type[column]); // 분류된 정보들을 각 행에 저장
                    }
                    row.createCell(6).setCellValue(link); // 회사 링크 6행에 저장
                }
                // 실제 엑셀에 쓰기
                try (FileOutputStream outputStream = new FileOutputStream("크롤링_데이터" + startPage + ".xlsx")) {
                    workbook.write(outputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
    private int findLastPage(){
        String url = "https://career.programmers.co.kr/job";
        WebDriver driver = new ChromeDriver(options);
        driver.get(url);
        WebElement element = driver.findElement(By.xpath("//*[@id=\"list-positions-wrapper\"]/div/div[1]/h6"));

        String text = element.getText();
        String numberText = "";
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if ('0' <= c && c <= '9') numberText += c; // 숫자인 부분만 연결
            else break; // 숫자가 아니면 반복문 종료
        }

        int infoNumber = Integer.parseInt(numberText); // 채용 정보 총 개수
        int lastPage = (infoNumber + 19) / 20; // 올림 처리하여 마지막 페이지 계산
        return lastPage; //한 페이지당 20개
    }
}
