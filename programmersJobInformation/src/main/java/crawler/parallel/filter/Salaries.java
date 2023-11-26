package crawler.parallel.filter;

import java.util.Map;

public class Salaries extends Filter {
    public Salaries(final Map<Integer, String> salaries) {
        super(salaries, "&min_salary=%d");
    }
}
