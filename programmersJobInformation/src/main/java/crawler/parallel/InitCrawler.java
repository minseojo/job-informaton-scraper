package crawler.parallel;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class InitCrawler {

    public Map<Integer, String>  crawlJobs() {
        WebDriver driver = new ChromeDriver();
        driver.get("https://career.programmers.co.kr/job");

        // 직무 버튼 클릭 (화면에 보이게 하기 위함)
        WebElement buttonElement = driver.findElement(By.xpath("//*[@id='search-form']/div[2]/div[1]/button"));
        buttonElement.click();

        WebElement wrapperElement = driver.findElement(By.className("_3Fb6jzPc2ceEcuAQu6GO7B"));
        List<WebElement> elements = wrapperElement.findElements(By.className("dropdown-item"));

        Map<Integer, String> crawlResult = new TreeMap<>(); // ids, name
        for (WebElement element : elements) {
            String ids = element.findElement(By.tagName("input")).getAttribute("value").trim(); // 직무 value 값 가져오기 (sql 쿼리에 이용)
            String name = element.getText().trim(); // 직무 이름 가져오기
            crawlResult.put(Integer.valueOf(ids), name);
        }

        driver.close();
        return crawlResult;
    }

    public Map<Integer, String> crawlCareers() {
        WebDriver driver = new ChromeDriver();
        driver.get("https://career.programmers.co.kr/job");

        // 경력 버튼 클릭 (화면에 보이게 하기 위함)
        WebElement buttonElement =  driver.findElement(By.xpath("//*[@id=\"search-form\"]/div[3]/div[1]/div[1]/button"));
        buttonElement.click();

        WebElement wrapperElement = driver.findElement(By.className("dropdown-item-wrapper"));
        List<WebElement> elements = wrapperElement.findElements(By.className("min_career_label"));

        Map<Integer, String> crawlResult = new TreeMap<>(); // ids, name
        for (WebElement element : elements) {
            String ids = element.findElement(By.tagName("input")).getAttribute("value").trim(); // 경력 value 값 가져오기 (sql 쿼리에 이용)
            String name = element.getText().trim(); // 경력 이름 가져오기

            if (ids.isBlank() || name.equals("전체")) {
                continue;
            }

            crawlResult.put(Integer.valueOf(ids), name);
        }

        driver.close();
        return crawlResult;
    }

    public Map<Integer, String> crawlSalary() {
        WebDriver driver = new ChromeDriver();
        driver.get("https://career.programmers.co.kr/job");

        // 연봉 버튼 클릭 (화면에 보이게 하기 위함)
        WebElement buttonElement =  driver.findElement(By.xpath("//*[@id=\"search-form\"]/div[3]/div[1]/div[2]/button"));
        buttonElement.click();

        WebElement wrapperElement = driver.findElement(By.xpath("//*[@id=\"search-form\"]/div[3]/div[1]/div[2]/div/div/div"));
        List<WebElement> elements = wrapperElement.findElements(By.className("min_salary_label"));

        Map<Integer, String> crawlResult = new TreeMap<>(); // ids, name
        for (WebElement element : elements) {
            String ids = element.findElement(By.tagName("input")).getAttribute("value").trim(); // 연봉 value 값 가져오기 (sql 쿼리에 이용)
            String name = element.getText().trim(); // 연봉 이름 가져오기

            if (ids.isBlank() || name.equals("전체")) {
                continue;
            }

            crawlResult.put(Integer.valueOf(ids), name);
        }

        driver.close();
        return crawlResult;
    }
}
