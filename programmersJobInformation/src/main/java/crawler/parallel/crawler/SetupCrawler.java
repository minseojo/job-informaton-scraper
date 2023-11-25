package crawler.parallel.crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static crawler.parallel.filter.Filter.OPTION_ALL_NAME;
import static crawler.parallel.filter.Filter.OPTION_ALL_VALUE;

public class SetupCrawler {
    WebDriver driver = new ChromeDriver();
    public Map<Integer, String> crawlJobs() {
        return crawlData("//*[@id='search-form']/div[2]/div[1]/button",
                "//*[@id=\"search-form\"]/div[2]/div[1]/div/ul", "dropdown-item");
    }

    public Map<Integer, String> crawlCareers() {
        return crawlData("//*[@id=\"search-form\"]/div[3]/div[1]/div[1]/button",
                "//*[@id=\"search-form\"]/div[3]/div[1]/div[1]/div/div/div", "min_career_label");
    }

    public Map<Integer, String> crawlSalary() {
        Map<Integer, String> crawlData =  crawlData("//*[@id=\"search-form\"]/div[3]/div[1]/div[2]/button",
                "//*[@id=\"search-form\"]/div[3]/div[1]/div[2]/div/div/div", "min_salary_label");

        driver.close();
        return crawlData;
    }

    private Map<Integer, String> crawlData(String buttonXPath, String wrapperXpathName, String elementsXpathName) {
        driver.get("https://career.programmers.co.kr/job");

        WebElement buttonElement = driver.findElement(By.xpath(buttonXPath));
        buttonElement.click();

        WebElement wrapperElement = driver.findElement(By.xpath(wrapperXpathName));
        List<WebElement> elements = wrapperElement.findElements(By.className(elementsXpathName));

        Map<Integer, String> crawlResult = new TreeMap<>(); // id, name, (트리셋 -> id 오름차순 )
        for (WebElement element : elements) {
            String ids = element.findElement(By.tagName("input")).getAttribute("value").trim();
            String name = element.getText().trim();

            if (name.equals(OPTION_ALL_NAME) || ids.equals(OPTION_ALL_VALUE)) {
                continue;
            }

            crawlResult.put(Integer.valueOf(ids), name);
        }

        return crawlResult;
    }
}
