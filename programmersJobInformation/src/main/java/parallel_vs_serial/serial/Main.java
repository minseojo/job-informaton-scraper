package parallel_vs_serial.serial;
/**
 * 직렬 처리 (밀리초)
 * 80개의 페이지, 1600개의 정보
 * 단일 스레드: 76000, 77874, 966686, 102263
 *
 */
public class Main {
    // 드라이버 설치 경로
    private static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
    private static final String WEB_DRIVER_PATH = "/Users/minseojo/Desktop/chromedriver-mac-arm64/chromedriver";
    public static void main(String[] args) {
        Crawler crawler = new Crawler(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        crawler.exe();
    }
}
