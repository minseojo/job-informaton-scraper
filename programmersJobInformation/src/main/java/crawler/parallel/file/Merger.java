package crawler.parallel.file;

import crawler.parallel.vo.FileName;

public interface Merger {
    void mergeFiles(FileName outputFileName);
}
