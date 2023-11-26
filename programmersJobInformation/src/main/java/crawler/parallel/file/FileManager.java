package crawler.parallel.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class FileManager {
    public static final String MOCK_DIRECTORY_PATH = "mock";

    public static Path createMockDirectory() throws IOException {
        File mockDirectory = new File(MOCK_DIRECTORY_PATH);
        Files.createDirectories(mockDirectory.toPath());

        return mockDirectory.toPath();
    }

    public static void deleteMockDirectory() {
        File mockDirectory = new File(MOCK_DIRECTORY_PATH);

        if (mockDirectory.exists() && mockDirectory.isDirectory()) {
            File[] files = mockDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.delete()) {
                        System.err.println("파일 삭제에 실패했습니다: " + file.getName());
                    }
                }
            }

            // 폴더 삭제
            if (!mockDirectory.delete()) {
                System.err.println("폴더 삭제에 실패했습니다.");
            }
        }
    }

    public static Path createDirectory(LocalDate date) throws IOException {
        File directory = new File(date.toString());
        Files.createDirectories(directory.toPath());

        return directory.toPath();
    }
}
