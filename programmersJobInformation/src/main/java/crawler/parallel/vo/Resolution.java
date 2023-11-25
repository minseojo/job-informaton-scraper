package crawler.parallel.vo;

/**
 * 사용자 모니터 해상도
 * 예시: 1920*1080
 * width = 1920, height = 1080
 */

public class Resolution {
    private final Integer width;
    private final Integer height;

    public Resolution(String width, String height) {
        validate(width, height);
        this.width = Integer.valueOf(width);
        this.height = Integer.valueOf(height);
    }

    private void validate(String width, String height) {
        try {
            Integer.valueOf(width);
            Integer.valueOf(height);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("\n❌ 존재하지 않는 모니터 해상도입니다. 다시 입력해 주세요.");
        }
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }
}
