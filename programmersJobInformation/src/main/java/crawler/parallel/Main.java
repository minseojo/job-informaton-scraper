package crawler.parallel;

public class Main {

    public static void main(String[] args) {
        int numberOfThreads = Runtime.getRuntime().availableProcessors(); // m1 air 기준 8개
        Controller controller = new Controller(new InitCrawler(), new View());
        controller.run();

//        Crawler crawler = new Crawler(1650, 1050, appendSql, numberOfThreads); // 해상도 wdith, 해상도 height, 스레드 개수
       // crawler.exe();
    }
}
