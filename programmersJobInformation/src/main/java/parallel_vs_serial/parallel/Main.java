package parallel_vs_serial.parallel;

public class Main {
    public static void main(String[] args) {
        String WEB_DRIVER_ID = "webdriver.chrome.driver";
        String WEB_DRIVER_PATH = "/Users/minseojo/Desktop/chromedriver-mac-arm64/chromedriver";
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        Crawler crawler = new Crawler(WEB_DRIVER_ID, WEB_DRIVER_PATH, numberOfThreads);
        crawler.exe();
    }
}
