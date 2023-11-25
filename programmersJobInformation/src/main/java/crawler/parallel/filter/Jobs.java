package crawler.parallel.filter;

import java.util.Map;

public class Jobs extends Filter {
    public Jobs(final Map<Integer, String> jobs) {
        super(jobs, "&job_category_ids=%d");
    }
}