package parallel_vs_serial.serial;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
public class Crawler {

    private final WebDriver driver; // 웹드라이버
    private final ChromeOptions options; // 웹드라이버 옵션
    private final WebDriverWait wait; // 타이머
    public Crawler(String WEB_DRIVER_ID, String WEB_DRIVER_PATH){
        // 웹드라이버 경로 설정
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        // 웹드라이버 옵션 설정
        options = new ChromeOptions();
        options.addArguments("headless"); // 크롬 브라우저 창 시각적으로 안보이게 하기 -> 속도 빨라짐
        options.addArguments("--start-maximized"); // 브라우저 창 최대화 -> 웹페이지 내용을 많이 표시 -> 스크래핑 속도 빨라짐
        // 드라이버에 옵션 넣기 (-> 속도 빨라짐)
        driver = new ChromeDriver(options);
        // 드라이버가 웹페이지 접속 대기 객체
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); //웹피이지 최대 10초 기다림, 로딩완료시 바로 실행
    }

    public void exe() {
        // 시간 측정시작
        long startTime = System.currentTimeMillis();
        try {
            // 크롤링할 사이트 마지막 페이지 찾기
            int lastPage = 80; // 병렬 직렬 80 페이지로 비교
            for (int page = 1; page <= lastPage; page++) {
                String url = "https://career.programmers.co.kr/job?page=" + page;
                driver.get(url);
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("list-positions")));
                List<WebElement> elements = element.findElements(By.className("list-position-item"));
                // 분류된 정보를 (회사명, 회사지역, 채용분야, 채용링크) 등의 타입으로 나눠 엑셀에 저장한다.
                for (WebElement e : elements) {
                    String information = e.getText(); // 분류된 정보 (회사명, 지역, 채용분야) 정보 가져오기
                    String link = e.findElement(By.className("position-link")).getAttribute("href"); //채용 링크 가져오기
                    //System.out.println(information + "\n" + link + "\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.close(); // 브라우저 종료
            long endTime = System.currentTimeMillis();
            long elapsedTimeMillis = endTime - startTime;
            System.out.println("경과 시간 (밀리초): " + elapsedTimeMillis);
        }
    }
}