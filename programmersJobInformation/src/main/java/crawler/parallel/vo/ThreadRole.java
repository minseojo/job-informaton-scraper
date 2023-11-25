package crawler.parallel.vo;

/**
 * 병렬 처리에서, 각각의 스레드가 맡을 크롤링 시작, 종료 페이지\
 * 예시 : 스레드 8개, 전체 페이지 80개
 * 0번 스레드: startPage = 1, endPage = 11
 * 1번 스레드: startPage = 11, endPage = 21
 * ...
 * 7번 스레드: startPage = 71, endPage = 81
 *
 * 탐색 범위: [startPage, endPage)
 * 즉, for (int page = startPage; page < endPage; page++)
 */

public final class ThreadRole {
    private final Integer startPage;
    private final Integer endPage;

    public ThreadRole(Integer startPage, Integer endPage) {
        this.startPage = startPage;
        this.endPage = endPage;
    }

    public int startPage() {
        return startPage;
    }

    public int endPage() {
        return endPage;
    }
}
