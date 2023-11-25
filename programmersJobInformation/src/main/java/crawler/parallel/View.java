package crawler.parallel;

import java.util.Map;
import java.util.Scanner;

public class View {
    private static final Scanner SCANNER = new Scanner(System.in);

    public String readJobs(Map<String, Integer> options) {
        System.out.println("\n희망 직무를 선택해 주세요. 예시: 0 또는 1, 4 등등 (중복 선택 가능)\n");
        printOptions(options);

        System.out.print("\n\n희망 직무 선택: ");
        return SCANNER.nextLine();
    }

    public String readCareers(Map<String, Integer> options) {
        System.out.println("\n\n\n\n희망 경력을 선택해 주세요. 예시: 0 또는 4 등등 (중복 선택 불가)\n");
        printOptions(options);

        System.out.print("\n\n희망 경력 선택: ");
        return SCANNER.nextLine();
    }

    public String readSalary(Map<String, Integer> options) {
        System.out.println("\n\n\n\n희망 연봉을 선택해 주세요. 예시: 3000 또는 5500 등등 (중복 선택 불가)\n");
        printOptions(options);

        System.out.print("\n\n희망 연봉 선택: ");
        return SCANNER.nextLine();
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
        if (sequence != 0 && sequence % 5 == 0) {
            System.out.println();
        }
    }
}
