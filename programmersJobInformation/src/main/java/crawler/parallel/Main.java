package crawler.parallel;

public class Main {
    public static void main(String[] args) {
        Controller crawlerController = new Controller(new Input());
        crawlerController.run();
    }
}
