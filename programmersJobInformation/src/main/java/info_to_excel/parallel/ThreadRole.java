package info_to_excel.parallel;

public class ThreadRole {
    private final int startPage;
    private final int endPage;

    public ThreadRole(int startPage, int endPage) {
        this.startPage = startPage;
        this.endPage = endPage;
    }

    public int getStartPage() {
        return startPage;
    }

    public int getEndPage() {
        return endPage;
    }
}
