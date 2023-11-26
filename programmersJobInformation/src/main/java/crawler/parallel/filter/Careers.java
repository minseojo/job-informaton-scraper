package crawler.parallel.filter;

import java.util.Map;

public class Careers extends Filter {
    public Careers(final Map<Integer, String> careers) {
        super(careers, "&min_career=%d");
    }
}
