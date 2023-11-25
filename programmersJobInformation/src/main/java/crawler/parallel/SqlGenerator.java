package crawler.parallel;

import crawler.Careers;
import crawler.Jobs;
import crawler.Salaries;

import java.util.ArrayList;
import java.util.List;

public class SqlGenerator {

    private final Jobs jobs;
    private final Careers careers;
    private final Salaries salaries;

    public SqlGenerator(Jobs jobs, Careers careers, Salaries salaries) {
        this.jobs = jobs;
        this.careers = careers;
        this.salaries = salaries;
    }

    public String generateJobsSql(String input) {
        List<Integer> jobIds = new ArrayList<>();

        List<String> inputNumbers = List.of(input.split(","));
        for (String number : inputNumbers) {
            int jobNumber;
            try {
                jobNumber = Integer.parseInt(number);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("숫자가 아닙니다.");
            }

            Integer findJobId = jobs.findJob(jobNumber);
            jobIds.add(findJobId);
        }

        return jobs.createSql(jobIds);
    }

    public String generateCareerSql(String input) {
        int careerNumber;
        try {
            careerNumber = Integer.parseInt(input);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("숫자가 아닙니다.");
        }

        Integer findCareerId = careers.findCareer(careerNumber);
        return careers.createSql(findCareerId);
    }

    public String generateSalarySql(String input) {
        int salaryNumber;
        try {
            salaryNumber = Integer.parseInt(input);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("숫자가 아닙니다.");
        }

        Integer findSalaryId = salaries.findSalaryId(salaryNumber);
        return salaries.createSql(findSalaryId);
    }
}
