package crawler.parallel.generator;

import crawler.parallel.filter.Careers;
import crawler.parallel.filter.Filter;
import crawler.parallel.filter.Jobs;
import crawler.parallel.filter.Salaries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryGenerator {
    private static final String BLANK = "";

    private final Map<String, Filter> filters;

    public QueryGenerator(Map<String, Filter> filters) {
        this.filters = filters;
    }

    public String generateJobsQuery(String input, Jobs jobs) {
        List<Integer> jobIds = new ArrayList<>();

        List<String> inputIds = List.of(input.split(","));
        for (String id : inputIds) {
            Integer jobId = parseSingleInput(id);
            Integer findJobId = jobs.findFilter(jobId);
            jobIds.add(findJobId);

            if (jobs.isAllFilter(findJobId)) {
                return BLANK;
            }
        }

        return jobs.createQuery(jobIds);
    }

    public String generateCareerQuery(String input, Careers careers) {
        Integer careerId = parseSingleInput(input);
        Integer findCareerId = careers.findFilter(careerId);
        return careers.isAllFilter(findCareerId) ? BLANK : careers.createQuery(findCareerId);
    }

    public String generateSalaryQuery(String input, Salaries salaries) {
        Integer salaryId = parseSingleInput(input);
        Integer findSalaryId= salaries.findFilter(salaryId);
        return salaries.isAllFilter(findSalaryId) ? BLANK : salaries.createQuery(findSalaryId);
    }

    private int parseSingleInput(String input) {
        try {
            return Integer.parseInt(input.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("❌ 존재하지 않는 번호 입니다. 다시 선택해 주세요.");
        }
    }
}
