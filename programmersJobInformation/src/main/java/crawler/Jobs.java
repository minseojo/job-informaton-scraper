package crawler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static crawler.Careers.NOT_FOUND;

public class Jobs {
    private static final String query = "&job_category_ids=%d";

    private final Map<String, Integer> jobs;

    public Jobs(final Map<Integer, String> jobs) {
        this.jobs = convertTypeSequence(jobs);
    }

    public static Map<String, Integer> convertTypeSequence(Map<Integer, String> careers) {
        Map<String, Integer> convertedCareers = new LinkedHashMap<>();

        convertedCareers.put("전체", -1);
        for (Map.Entry<Integer, String> entry : careers.entrySet()) {
            convertedCareers.put(entry.getValue(), entry.getKey());
        }
        return convertedCareers;
    }

    public String createSql(List<Integer> ids) {
        StringBuilder result = new StringBuilder();
        for (Integer id : ids) {
            result.append(String.format(query, id));
        }

        return String.valueOf(result);
    }

    public Integer findJob(Integer ids) {
        for (Integer value : jobs.values()) {
            if (value.equals(ids)) {
                return value;
            }
        }

        return NOT_FOUND;
    }

    public Map<String, Integer> getJobs() {
        return jobs;
    }
}
