package crawler.parallel.file;

import crawler.parallel.vo.FileName;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static crawler.parallel.file.FileManager.MOCK_DIRECTORY_PATH;

public class ExcelMerger implements Merger{
    public static final String FILE_EXTENSION = ".xlsx";

    public void mergeFiles(FileName outputFileName) {
        List<FileName> fileNamesToMerge = addMockDirectoryFiles();

        // 결과를 저장할 워크북 생성
        try (Workbook mergedWorkbook = new XSSFWorkbook()) {
            Sheet mergedSheet = mergedWorkbook.createSheet("Programmers Job Information");
            try {
                int rowNum = 0;
                // 각 엑셀 파일을 읽어와서 메모리에 추가
                for (FileName fileName : fileNamesToMerge) {
                    FileInputStream fileInputStream = new FileInputStream(fileName.toString());
                    Workbook workbook = WorkbookFactory.create(fileInputStream);

                    // 모든 시트를 합치기
                    for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                        Sheet sheet = workbook.getSheetAt(i);

                        // 각 행 탐색
                        for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {
                            Row row = sheet.getRow(j);
                            Row combinedRow = mergedSheet.createRow(rowNum++);

                            // 각 행에 대한 열 데이터 복사
                            for (int k = 0; k < row.getPhysicalNumberOfCells(); k++) {
                                Cell cell = row.getCell(k);
                                Cell combinedCell = combinedRow.createCell(k);

                                // 셀 유형에 따라 값을 복사
                                switch (cell.getCellType()) {
                                    case STRING:
                                        combinedCell.setCellValue(cell.getStringCellValue());
                                        break;
                                    case NUMERIC:
                                        combinedCell.setCellValue(cell.getNumericCellValue());
                                        break;
                                    // 다른 셀 유형에 대한 처리를 추가할 수 있음
                                    default:
                                        break;
                                        // 처리하지 않는 셀 유형에 대한 기본 동작
                                }
                            }
                        }
                    }
                }

                writeFile(mergedWorkbook, outputFileName.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<FileName>  addMockDirectoryFiles() {
        List<FileName> fileNamesToMerge = new ArrayList<>();

        File mockDirectory = new File(MOCK_DIRECTORY_PATH);
        if (mockDirectory.exists() && mockDirectory.isDirectory()) {
            File[] files = mockDirectory.listFiles();
            for (File file : Objects.requireNonNull(files)) {
                if (file.toPath().toString().endsWith(ExcelMerger.FILE_EXTENSION)) {
                    fileNamesToMerge.add(new FileName(file.toPath().toString()));
                }
            }
        } else {
            throw new IllegalArgumentException(MOCK_DIRECTORY_PATH + " 폴더가 존재하지 않습니다.");
        }

        return fileNamesToMerge;
    }

    private void writeFile(Workbook mergedWorkbook, String outputFileName) throws IOException {
        LocalDate nowDate = LocalDate.now();
        Path directoryPath = FileManager.createDirectory(nowDate);

        Path outputFilePath = Paths.get(directoryPath.toString(), outputFileName + FILE_EXTENSION);

        // 파일 병합
        try (FileOutputStream outputStream = new FileOutputStream(outputFilePath.toString())) {
            mergedWorkbook.write(outputStream);
            System.out.println(outputFilePath + " 을 생성했습니다.");
        }
    }
}
