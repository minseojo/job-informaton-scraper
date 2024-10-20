# 셀레니움을 이용한 프로그래머스 채용정보 스크래퍼

## 프로젝트소개
[프로그래머스 채용 공고 사이트](https://career.programmers.co.kr/job)를 병렬 스크래핑하여, 그 결과를 엑셀 파일에 저장하는 프로그램 입니다.
원하는 필터 조건에 맞는 채용 공고 정보를 신속하게 수집하고 데이터화하기 위해 만들었습니다.

- 프로그래머스 사이트 Robots.txt

```
User-Agent: *

Disallow: /users
Disallow: /managers
Disallow: /cable
Disallow: /admin
Disallow: /start_trial
Disallow: /pr/*
Allow: /

Sitemap: https://programmers.co.kr/sitemaps/sitemap.xml
```

위 내용에 따라 [프로그래머스 채용 공고 사이트](https://career.programmers.co.kr/job)는 스크래핑, 크롤링이 가능하다.

</br>

## 사용한 기술 스택

### 프로그래밍 언어

- Java & JDK 11 이상

### 라이브러리

- selenium-java : 4.12.1
- selenium-support : 4.12.1
- apache-poi : 5.0.0
- apache-poi-ooxml : 5.0.0

</br>

## 프로그램 동작 과정
### 1. 필터 스크래핑 & 정보 입력

![Untitled (1)](https://github.com/user-attachments/assets/4e48c1b4-b018-4155-ab3d-1485512fc25e)

- 필터 예시
    
![Untitled (4)](https://github.com/user-attachments/assets/1e62191a-8ed2-4eac-8e66-117f4cc66659)
    
필터를 스크래핑해서 메모리에 저장 후, 사용자에게 필터 입력 제시
    
![Untitled (5)](https://github.com/user-attachments/assets/5711b60f-7ce7-49ee-922d-8af56b0f3dd9)

    
</br>

### 2. 필터링 된 URI로 채용 공고 마지막 페이지 스크래핑

![Untitled](https://github.com/user-attachments/assets/9547d040-9591-429e-860f-945afc0c7a3c)


- 필터링 된 URI 예시:
    - 연봉 3000 이상, 경력 1년 이상 공고: https://career.programmers.co.kr/job?page=1&min_career=1&min_salary=3000

### 스레드 개수 선정 방법: `Runtime.getRuntime().availableProcessors()`

### 각 스레드 별 페이지 분담 알고리즘 예시
![Untitled (2)](https://github.com/user-attachments/assets/6776348d-ae5e-4f31-aa67-ca2eab74b931)


</br>

### 3. 각 스레드가 분담받은 페이지 스크래핑 & 파일에 데이터 저장 & 파일 병합

![Untitled (3)](https://github.com/user-attachments/assets/ffc5682e-34ac-4db5-bc57-7f8850e68af4)


</br>

## 고민한 부분 및 해결방법
### 1. 웹 드라이버 호환성 문제 (호환성)

- **문제점:** 셀레니움과 웹 드라이버의 버전을 맞춰야 스크래핑이 가능했습니다. 하지만 사용자 컴퓨터의 웹 드라이버 버전은 모두 달랐고, 사용자마다 웹 드라이버 버전과 셀레니움 버전을 맞춰서 다운 받아야했습니다.
    - 예시) 크롬드라이버 121, 크롬드라이버 120, 크롬드라이버 119
- **해결 방법:** Selenium 4.6 이상 버전부터 지원하는 Selenium 자체에 내장된 WebDriver를 통해 해결했습니다.

### 2. 스크래핑 속도 향상 (성능)

- **문제점:** 스크래핑 속도가 느렸습니다.
- **원인:** 채용 공고는 약 60개 이상의 페이지 (2024.3.11 기준)
- **해결 방법:** 타이머 변경, 멀티 스레드 이용
    - 타이머 변경
        - 변경 전 코드: `Thread.sleep(1000)`
        - 변경 후 코드:
        
        ```java
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.jsReturnsValue("return document.readyState == 'complete'"));
        ```
        
    - 멀티 스레드 이용
        - **멀티스레드 이용 결과:**
            - **총 80개의 페이지,** **1,600개 정보**
            - **약 2분 걸리던 작업 → 약 40초**
        - **성능 향상:**  **약 2.1 ~ 2.4배 증가**
        - **성능 표**
            
            
            |  | 단일 스레드 | 멀티 스레드 (4개 또는 8개) |
            | --- | --- | --- |
            | 스크래핑 속도 | 76000ms ~ 120000ms | 36000ms ~ 50000ms |

### 3. 데이터 동기화 문제

- **문제점:** 여러 개의 스레드가 하나의 공유자원(엑셀 파일)에 접근하는 문제가 발생했습니다.
- **원인:** 기존에는 싱글 스레드라 상관없었지만, 성능 향상을 위해 병렬 처리를 수행한 것이 원인입니다.
- **해결 방법:**
    1. 각각의 스레드마다 스크래핑한 데이터를 자신의 스레드 번호를 붙인 엑셀 파일에 저장
        - ex) 0.xlsx, 1.xlsx, …, n.xlsx
    2. 모든 엑셀 파일이 저장된 후, 해당 파일들을 하나의 파일로 병합
        - 0.xlsx, 1.xlsx, …, n.xlsx → result.xlsx

### 4. 상태 모니터링 및 디버깅 (편의성)

- **문제점:** 스크래핑 정상 동작 여부를 알 수 없었습니다.
- **해결 방법:** 사용자화면 해상도를 입력받고, 해상도에 따른 스레드의 개수를 화면 비율에 맞춰 스크래핑 과정을 시각화했습니다.

</br>

## 시연 영상
- [시연 영상 링크](https://www.youtube.com/watch?v=a0PJ3KzdYwk)

</br>

## ✅ 스크래퍼 사용 방법
1. github clone
2. `build.gradle` 실행
3. parallel.main 실행
4. 희망 직무 선택 [입력]
5. 희망 경력 선택 [입력]
6. 희망 연봉 선택 [입력]
7. 모니터 해상도 입력 [입력]
8. 스크래핑한 데이터를 저장 할 파일 명 이름 입력 (확장자 제외) [입력]
9. 기다림....
10. `오늘날짜/입력한 파일 명` 엑셀 파일 생성, 예시 `2023-11-26/result.xlsx`

</br>
## ❗️ 주의
- 스크래핑 도중에 `mock` 디렉토리가 생기는데, 이 디렉토리를 도중에 열어놓으면 스크래핑 결과가 저장되지 않는다.
- 광고 내용으로 인해, 전체 채용 정보가 100개여도, 광고 내용을 포함한 120개가 스크래핑 될 수 있습니다.
- 스크래핑 할 전체 페이지가, 사용자 컴퓨터의 스레드 개수보다 적다면, 1개의 단일 스레드만 이용합니다. 즉 한개의 브라우저만 띄웁니다.
