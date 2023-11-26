package crawler.parallel.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class FileManager {
    public static final String MOCK_DIRECTORY_PATH = "mock";

    public static Path createMockDirectory() throws IOException {
        File directory = new File(MOCK_DIRECTORY_PATH);
        Files.createDirectories(directory.toPath());

        return directory.toPath();
    }

    public static Path createDirectory(LocalDate date) throws IOException {
        File directory = new File(date.toString());
        Files.createDirectories(directory.toPath());

        return directory.toPath();
    }
}
