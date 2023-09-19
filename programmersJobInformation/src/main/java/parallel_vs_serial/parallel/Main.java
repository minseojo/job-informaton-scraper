package parallel_vs_serial.parallel;

/**
 * 병렬 처리 (밀리초)
 * 80개의 페이지, 1600개의 정보
 * 스레드 2개: 61900 62000 67000
 * 스레드 4개: 39000, 40000, 43000
 * 스레드 8개: 39000, 41900,  42500
 *
 */
public class Main {
    // 드라이버 설치 경로
    private static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
    private static final String WEB_DRIVER_PATH = "/Users/minseojo/Desktop/chromedriver-mac-arm64/chromedriver";
    public static void main(String[] args) {
        int numberOfThreads = Runtime.getRuntime().availableProcessors(); // m1 air 기준 8개
        Crawler crawler = new Crawler(WEB_DRIVER_ID, WEB_DRIVER_PATH, numberOfThreads);
        crawler.exe();
    }
}
