package info_to_excel.parallel;

public class Main {
    public static void main(String[] args) {
        int numberOfThreads = Runtime.getRuntime().availableProcessors(); // m1 air 기준 8개
        Crawler crawler = new Crawler(numberOfThreads);
        crawler.exe();
    }
}
