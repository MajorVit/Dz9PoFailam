import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import domain.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class FileTest {
    ClassLoader classLoader = FileTest.class.getClassLoader();
    String zipName = "files.zip";
    String pdfName = "file_example_PDF.pdf";
    String cvcName = "file_example_CSV.csv";
    String xlsName = "file_example_XLS.xls";


    private InputStream getFileFromArchive(String fileName) throws Exception {
        File zipFile = new File("src/test/resources/" + zipName);
        ZipFile zip = new ZipFile(zipFile);
        return zip.getInputStream(zip.getEntry(fileName));
    }


    @DisplayName("Check pdf from zip")
    @Test
    void parseZipPdfTest() throws Exception {
        try (InputStream pdfFileStream = getFileFromArchive(pdfName)) {
            PDF pdf = new PDF(pdfFileStream);
            assertThat(pdf.numberOfPages).isEqualTo(2);
            assertThat(pdf.text).containsAnyOf("A Simple PDF File");
        }
    }


    @DisplayName("Check csv from zip")
    @Test
    void parseZipCsvTest() throws Exception {
        try (InputStream csvFileStream = getFileFromArchive(cvcName)) {
            CSVReader csvReader = new CSVReader(new InputStreamReader(csvFileStream, UTF_8));
            List<String[]> csv = csvReader.readAll();
            assertThat(csv).contains(
                    new String[]{"2", "Vitaliy", "Mayorskiy", "Male", "RUS", "30", "02/01/1992", "1234"});
        }
    }


    @DisplayName("Check xls from zip")
    @Test
    void parseZipXlsTest() throws Exception {
        try (InputStream xlsFileStream = getFileFromArchive(xlsName)) {
            XLS xls = new XLS(xlsFileStream);
            assertThat(xls.excel.getSheetAt(0).getRow(2).getCell(6).getStringCellValue()).contains("16/11/199");
            assertThat(xls.excel.getSheetAt(0).getRow(2).getCell(0).getNumericCellValue()).isEqualTo(2);
            assertThat(xls.excel.getSheetAt(0).getRow(2).getCell(1).getStringCellValue()).contains("Svytoslav");
            assertThat(xls.excel.getSheetAt(0).getRow(2).getCell(2).getStringCellValue()).contains("Chikunov");
            assertThat(xls.excel.getSheetAt(0).getRow(2).getCell(3).getStringCellValue()).contains("Male");
            assertThat(xls.excel.getSheetAt(0).getRow(2).getCell(4).getStringCellValue()).contains("RUS");
            assertThat((xls.excel.getSheetAt(0).getRow(2).getCell(5).getNumericCellValue())).isEqualTo(31);
            assertThat((xls.excel.getSheetAt(0).getRow(2).getCell(7).getNumericCellValue())).isEqualTo(123);
            assertThat(xls.excel.getSheetAt(0).getPhysicalNumberOfRows()).isEqualTo(3);
        }
    }

    @DisplayName("Check json test")
    @Test
    void parseJsonTest() throws IOException {
        try (InputStream is = classLoader.getResourceAsStream("student.json")) {
            ObjectMapper mapper = new ObjectMapper();
            Student student = mapper.readValue(is, Student.class);
            assertThat(student.getName()).isEqualTo("Vital");
            assertThat(student.isBadStudent());
            assertThat(student.getTel()).isEqualTo("71234567890");
            assertThat(student.getAge()).isEqualTo(30);
        }
    }
}