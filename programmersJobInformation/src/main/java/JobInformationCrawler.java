import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.List;

public class JobInformationCrawler {

    private WebDriver driver;
    private WebElement element;
    private String url;

    public JobInformationCrawler(String WEB_DRIVER_ID, String WEB_DRIVER_PATH){
        // WebDriver 경로 설정
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);

        // WebDriver 옵션 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless"); // 크롬 브라우저 창 안뜨게 하기
        options.addArguments("--start-maximized");
        options.addArguments("--disable-popup-blocking");
        driver = new ChromeDriver(options);
    }

    public void exe() {
        try {
            long lastPage = findLastPage();

            for (int page = 1; page <= 1; page++) {
                url = "https://career.programmers.co.kr/job?page=" + page;
                driver.get(url);

                // 채용 정보를 리스트로 묶어서 한번에 가져온다.
                element = driver.findElement(By.className("list-positions"));

                // 한번에 가져온 채용 정보 리스트를 각각의 정보로 나눠서 가져온다.
                List<WebElement> elements = element.findElements(By.className("list-position-item"));

                for (WebElement e : elements) {
                    String information = e.getText();
                    String link = e.findElement(By.className("position-link")).getAttribute("href");
                    System.out.println(information+ "\n" + link + "\n");
                }
                Thread.sleep(500); // 페이지 로딩 대기 시간 (10초), 불필요한 시간
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.close(); // 브라우저 종료
        }
    }

    private long findLastPage() throws InterruptedException {
        url = "https://career.programmers.co.kr/job";
        driver.get(url);
        Thread.sleep(500);

        element = driver.findElement(By.xpath("//*[@id=\"list-positions-wrapper\"]/div/div[1]/h6"));
        String text = element.getText();

        String numberText = "";
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if ('0' <= c && c <= '9') numberText += c;
            else break;
        }
        return Long.parseLong(numberText);
    }
}