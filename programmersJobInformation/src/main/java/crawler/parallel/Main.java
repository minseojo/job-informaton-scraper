package crawler.parallel;

import crawler.parallel.file.ExcelMerger;

public class Main {
    public static void main(String[] args) {
        Controller crawlerController = new Controller(new Input(), new ExcelMerger());
        crawlerController.run();
    }
}
