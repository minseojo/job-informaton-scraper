package crawler.parallel;

import crawler.Careers;
import crawler.Jobs;
import crawler.Salaries;

import java.util.Map;

public class Controller {
    private final InitCrawler initCrawler;
    private final View view;

    public Controller(InitCrawler initCrawler, View view) {
        this.initCrawler = initCrawler;
        this.view = view;
    }

    public void run() {
        Map<Integer, String> crawlJobs = initCrawler.crawlJobs();
        Jobs jobs = new Jobs(crawlJobs);

        Map<Integer, String> crawlCareers = initCrawler.crawlCareers();
        Careers careers = new Careers(crawlCareers);

        Map<Integer, String> crawlSalaries = initCrawler.crawlSalary();
        Salaries salaries = new Salaries(crawlSalaries);

        String inputJobs = view.readJobs(jobs.getJobs());
        String inputCareer = view.readCareers(careers.getCareers());
        String inputSalary = view.readSalary(salaries.getSalaries());

        SqlGenerator sqlGenerator = new SqlGenerator(jobs, careers, salaries);
        sqlGenerator.generateJobsSql(inputJobs);
        sqlGenerator.generateCareerSql(inputCareer);
        sqlGenerator.generateSalarySql(inputSalary);



    }
}
