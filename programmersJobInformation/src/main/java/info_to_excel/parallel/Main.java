package info_to_excel.parallel;

public class Main {
    public static void main(String[] args) {
        int numberOfThreads = Runtime.getRuntime().availableProcessors(); // m1 air 기준 8개
        Crawler crawler = new Crawler(1650, 1050, numberOfThreads); // 해상도 wdith, 해상도 height, 스레드 개수
        crawler.exe();
    }
}
