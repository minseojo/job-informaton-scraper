package crawler;

import java.util.LinkedHashMap;
import java.util.Map;

import static crawler.Careers.NOT_FOUND;

public class Salaries {

    private static final String query = "&min_salary==%d";


    private final Map<String, Integer> salaries;

    public Salaries(final Map<Integer, String> salaries) {
        this.salaries = convertTypeSequence(salaries);
    }

    public Map<String, Integer> convertTypeSequence(Map<Integer, String> salaries) {
        Map<String, Integer> convertedCareers = new LinkedHashMap<>();

        convertedCareers.put("전체", -1);
        for (Map.Entry<Integer, String> entry : salaries.entrySet()) {
            convertedCareers.put(entry.getValue(), entry.getKey());
        }
        return convertedCareers;
    }

    public String createSql(int salaryId) {
        return String.valueOf(String.format(query, salaryId));
    }

    public Integer findSalaryId(Integer salaryId) {
        for (Integer value : salaries.values()) {
            if (value.equals(salaryId)) {
                return value;
            }
        }

        return NOT_FOUND;
    }

    public Map<String, Integer> getSalaries() {
        return salaries;
    }
}
