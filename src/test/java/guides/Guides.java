package guides;

import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.Configuration;
import com.codeborne.xlstest.XLS;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

public class Guides {
  ClassLoader cl = Guides.class.getClassLoader();

  //Test downloaded PDF and test that there is Analyst Briefing name inside.

  @Test
  void guidesDownload() throws Exception {

    Configuration.holdBrowserOpen = true;
    Configuration.browserSize = "2560x1140";

    open("https://pivotcx.io/collateral/");

    File downloadedFile = $("a[href='https://pivotcx.io/short-hr-tech-analyst-deck/']").download();

    // Load and extract text from the PDF using PDFBox
    try (PDDocument document = PDDocument.load(downloadedFile)) {
      PDFTextStripper pdfStripper = new PDFTextStripper();
      String textContent = pdfStripper.getText(document);

      assertThat(textContent).contains("Analyst Briefing");
    }
  }

  //Test downloaded zip and what it contains inside

    @Test
    void readFileFromArchive() throws Exception {
      try (
              InputStream resource = cl.getResourceAsStream("example/xxx.zip");
              ZipInputStream zis = new ZipInputStream(resource, Charset.forName("windows-1251"));
      ) {
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
          if (entry.getName().contains(".xlsx")) {
            XLS content = new XLS(zis);
            assertThat(
                    content.excel.getSheetAt(0).getRow(0).getCell(0)
                            .getStringCellValue()).contains("REST API");
          } else if (entry.getName().endsWith(".pdf")) {
            PDF content = new PDF(zis);
            assertThat(content.text)
                    .contains("Reynalda Rees");
          }
        }
      }
    }

  }


