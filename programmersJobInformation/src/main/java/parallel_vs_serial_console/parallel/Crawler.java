package parallel_vs_serial_console.parallel;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// 출력 주석처리 - 72줄 (시간을 줄이기 위해 - 보려면 주석 없애기)
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
                    WebDriver driver = new ChromeDriver(options);
                    crawlPage(currentThread, driver);
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

    private void crawlPage(int threadNum, WebDriver driver) {
        try {
            int lastPage = 80; // 병렬 직렬 80 페이지로 비교
            int totalPage = lastPage / numberOfThreads; // 각 스레드가 담당할 전체 페이지 (마지막 페이지/ 스레드개수)
            int startPage = threadNum * totalPage; // 시작 페이지 = 전체 페이지 * 스레드 번호
            int endPage = (threadNum + 1) * totalPage; // 마지막 페이지
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
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
