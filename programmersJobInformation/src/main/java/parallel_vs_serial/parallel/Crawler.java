package parallel_vs_serial.parallel;

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

public class Crawler {
    private WebDriver driver;
    private final int numberOfThreads;

    public Crawler(String WEB_DRIVER_ID, String WEB_DRIVER_PATH, int numberOfThreads) {
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        this.numberOfThreads = numberOfThreads;
        this.exe();
    }

    public void exe() {
        long startTime = System.currentTimeMillis();
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

            for (int thread = 0; thread < 4; thread++) {
                final int currentThread = thread;
                Callable<Void> task = () -> {
                    crawlPage(currentThread);
                    return null;
                };
                executorService.submit(task);
            }

            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.MINUTES);
        } catch (Exception error) {
            error.printStackTrace();
        } finally {
            driver.close();
            long endTime = System.currentTimeMillis();
            long elapsedTimeMillis = endTime - startTime;
            System.out.println("경과 시간 (밀리초): " + elapsedTimeMillis);
        }
    }

    private void crawlPage(int threadNum) {
        try {
            int lastPage = 80; // 병렬 직렬 80 페이지로 비교
            int totalPage = lastPage / numberOfThreads;
            int startPage = threadNum * totalPage;
            int endPage = (threadNum + 1) * totalPage;

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
        }
    }
}
