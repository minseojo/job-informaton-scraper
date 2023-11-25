package crawler.parallel.filter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Filter {
    public static final String OPTION_ALL_NAME = "전체";
    public static final int OPTION_ALL_VALUE = 777;
    public static final String JOBS = "Jobs";
    public static final String CAREERS = "Careers";
    public static final String SALARIES = "Salaries";

    protected final Map<String, Integer> categories;
    protected final String query;

    public Filter(final Map<Integer, String> categories, String query) {
        this.categories = convertTypeSequence(categories);
        this.query = query;
    }

    public Map<String, Integer> convertTypeSequence(Map<Integer, String> inputCategories) {
        Map<String, Integer> convertedCategories = new LinkedHashMap<>();
        convertedCategories.put(OPTION_ALL_NAME, OPTION_ALL_VALUE);

        for (Map.Entry<Integer, String> entry : inputCategories.entrySet()) {
            convertedCategories.put(entry.getValue(), entry.getKey());
        }

        return convertedCategories;
    }

    public String createQuery(List<Integer> ids) {
        StringBuilder result = new StringBuilder();
        for (Integer id : ids) {
            result.append(String.format(query, id));
        }

        return String.valueOf(result);
    }

    public String createQuery(Integer filterId) {
        StringBuilder result = new StringBuilder();
        result.append(String.format(query, filterId));

        return String.valueOf(result);
    }

    public Integer findFilter(Integer filterId) {
        for (Integer value : categories.values()) {
            if (value.equals(filterId)) {
                return value;
            }
        }

        throw new IllegalArgumentException("\n❌ 존재하지 않는 번호 입니다. 다시 선택해 주세요.");
    }

    public boolean isAllFilter(Integer categoryId) {
        if (categoryId.equals(OPTION_ALL_VALUE)) {
            return true;
        }

        return false;
    }

    public Map<String, Integer> getFilters() {
        return categories;
    }
}