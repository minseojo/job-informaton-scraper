package crawler.parallel;

import java.util.Map;
import java.util.Scanner;

public class Input {
    private static final Scanner SCANNER = new Scanner(System.in);

    public String readJobs(Map<String, Integer> options) {
        System.out.println("✅ 희망 직무를 선택해 주세요.  예시 ▶ 0 또는 1, 4 (중복 선택 가능)\n");
        printOptions(options);

        System.out.print("\n\n희망 직무 선택: ");

        String input = SCANNER.nextLine();
        System.out.println();
        return input;
    }

    public String readCareers(Map<String, Integer> options) {
        System.out.println("✅ 희망 경력을 선택해 주세요.  예시 ▶ 0 또는 4 (중복 선택 불가)\n");
        printOptions(options);

        System.out.print("\n\n희망 경력 선택: ");

        String input = SCANNER.nextLine();
        System.out.println();
        return input;
    }

    public String readSalary(Map<String, Integer> options) {
        System.out.println("✅ 희망 연봉을 선택해 주세요.  예시 ▶ 3000 또는 5500 (중복 선택 불가)\n");
        printOptions(options);

        System.out.print("\n\n희망 연봉 선택: ");
        String input = SCANNER.nextLine();
        System.out.println();
        return input;
    }

    public String readResolution() {
        System.out.println("✅ 모니터 해상도를 입력해 주세요. \n양식 : 1920*1080");
        System.out.print("\n모니터 해상도 입력:  ");

        String input = SCANNER.nextLine();
        System.out.println();
        if (!input.matches("^\\d+\\*\\d+$")) {
            throw new IllegalArgumentException("\n❌ 존재하지 않는 해상도 입니다. 다시 입력해 주세요.");
        }
        return input;
    }

    public String readOutputFileName() {
        System.out.println("✅ 수집한 데이터를 저장 할 파일 이름을 입력해 주세요.");
        System.out.println("\n파일 이름 입력 : ");

        String input = SCANNER.nextLine();
        System.out.println();
        if (!input.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("\n❌ 파일 이름은 소문자, 대문자, 숫자, 밑줄(_), 대시(-) 만 가능 합니다. 파일 이름을 다시 입력해 주세요,");
        }

        return input;
    }

    private void printOptions(Map<String, Integer> crawledData) {
        int sequence = 0;
        for (Map.Entry<String, Integer> data : crawledData.entrySet()) {
            printRow(sequence++, data.getKey(), data.getValue());
        }
    }

    private void printRow(int sequence, String name, int id) {
        printNewLineIfNeeded(sequence);
        String formattedOutput = String.format("%-20s", name + " : " + id);
        System.out.print(formattedOutput);
    }

    private void printNewLineIfNeeded(int sequence) {
        int columnsSize = 5;
        if (sequence != 0 && sequence % columnsSize == 0) {
            System.out.println();
        }
    }
}
