package info_to_excel.serial;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.List;

public class Crawler {

    private final WebDriver driver; // 웹드라이버
    private final ChromeOptions options; // 웹드라이버 옵션
    private final WebDriverWait wait; // 타이머
    private final Workbook workbook; // 엑셀 생성 객체
    private final Sheet sheet; // 액셀 컨트롤 객체
    public Crawler(String WEB_DRIVER_ID, String WEB_DRIVER_PATH){
        // 웹드라이버 경로 설정
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        // 웹드라이버 옵션 설정
        options = new ChromeOptions();
        options.addArguments("headless"); // 크롬 브라우저 창 시각적으로 안보이게 하기 -> 속도 빨라짐
        options.addArguments("--start-maximized"); // 브라우저 창 최대화 -> 웹페이지 내용을 많이 표시 -> 스크래핑 속도 빨라짐
        // 액셀 생성
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Programmers Job Information");
        // 드라이버에 옵션 넣기 (-> 속도 빨라짐)
        driver = new ChromeDriver(options);
        // 드라이버가 웹페이지 접속 대기 객체
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); //웹피이지 최대 10초 기다림, 로딩완료시 바로 실행
    }

    public void exe() {
        // 시간 측정시작
        long startTime = System.currentTimeMillis();
        try {
            // 크롤링할 사이트 마지막 페이지 찾기
            int lastPage = findLastPage();
            // 헤더 행 생성
            int rowNum =0;
            for (int page = 1; page <= lastPage; page++) {
                // 프로그래머스 채용 URL 가져와서, 소프트웨어 드라이버에 연결
                String url = "https://career.programmers.co.kr/job?page=" + page;
                driver.get(url);
                // 한 페이지의 채용 정보를 리스트로 묶어서 한번에 가져온다.
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("list-positions")));
                // 한번에 가져온 채용 정보 리스트에 분류한다.
                List<WebElement> elements = element.findElements(By.className("list-position-item"));
                // 분류된 정보를 (회사명, 회사지역, 채용분야, 채용링크) 등의 타입으로 나눠 엑셀에 저장한다.
                for (WebElement e : elements) {
                    String information = e.getText(); // 분류된 정보 (회사명, 지역, 채용분야) 정보 가져오기
                    String link = e.findElement(By.className("position-link")).getAttribute("href"); //채용 링크 가져오기
                    Row row = sheet.createRow(rowNum++); // 열 생성
                    String type[] = information.split("\n"); //회사명, 지역, 채용분야, 채용 링크 분류 (엑셀 열을 나누기 위해)
                    for (int column = 0; column < type.length; column++) {
                        row.createCell(column).setCellValue(type[column]); // 분류된 정보들을 각 행에 저장
                    }
                    row.createCell(6).setCellValue(link); // 회사 링크 6행에 저장
                }
                // 실제 엑셀에 쓰기
                try (FileOutputStream outputStream = new FileOutputStream("크롤링_데이터.xlsx")) {
                    workbook.write(outputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.close(); // 브라우저 종료
            long endTime = System.currentTimeMillis();
            long elapsedTimeMillis = endTime - startTime;
            System.out.println("경과 시간 (밀리초): " + elapsedTimeMillis);
        }
    }

    private int findLastPage() {
        String url = "https://career.programmers.co.kr/job";
        WebDriver driver = new ChromeDriver(options);
        driver.get(url);
        WebElement element = driver.findElement(By.xpath("//*[@id=\"list-positions-wrapper\"]/div/div[1]/h6"));

        String text = element.getText();
        String numberText = "";
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if ('0' <= c && c <= '9') numberText += c; // 숫자인 부분만 연결
            else break; // 숫자가 아니면 반복문 종료
        }

        int infoNumber = Integer.parseInt(numberText); // 채용 정보 총 개수
        int lastPage = (infoNumber + 19) / 20; // 올림 처리하여 마지막 페이지 계산
        return lastPage; //한 페이지당 20개
    }
}

