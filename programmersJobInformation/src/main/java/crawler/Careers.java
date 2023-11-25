package crawler;

import java.util.LinkedHashMap;
import java.util.Map;

public class Careers {
    private static final String query = "&min_career=%d";
    public static final Integer NOT_FOUND = -1;

    private final Map<String, Integer> careers;

    public Careers(final Map<Integer, String> careers) {
        this.careers = convertTypeSequence(careers);
    }

    public Map<String, Integer> convertTypeSequence(Map<Integer, String> careers) {
        Map<String, Integer> convertedCareers = new LinkedHashMap<>();

        convertedCareers.put("전체", -1);
        for (Map.Entry<Integer, String> entry : careers.entrySet()) {
            convertedCareers.put(entry.getValue(), entry.getKey());
        }
        return convertedCareers;
    }

    public String createSql(int careerId) {
        return String.valueOf(String.format(query, careerId));
    }

    public Integer findCareer(Integer careerId) {
        for (Integer value : careers.values()) {
            if (value.equals(careerId)) {
                return value;
            }
        }

        return NOT_FOUND;
    }

    public Map<String, Integer> getCareers() {
        return careers;
    }
}
