import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

class ConversionTest {

    @Test
    public void testConvertPdfs_skipsInvalidFiles(@TempDir File inputDir, @TempDir File outputDir) throws Exception {
        // ---- ✅ PDF with matching conditions ----
        File matchingPdf = new File(inputDir, "matching_file.PDF");
        createDummyPdf(matchingPdf, "Matching conditions. Should be converted.");
        matchingPdf.setLastModified(System.currentTimeMillis());

        // ---- 🚫 PDF with non-matching name ----
        File nonMatchingNamePdf = new File(inputDir, "file_non-matching.pdf");
        createDummyPdf(nonMatchingNamePdf, "Non-matching name. Should be skipped.");
        nonMatchingNamePdf.setLastModified(System.currentTimeMillis());

        // ---- 🚫 PDF with old modified date  ----
        File oldPdf = new File(inputDir, "old_file.pdf");
        createDummyPdf(oldPdf, "Old file. Should be skipped.");
        oldPdf.setLastModified(100000); // Very old timestamp (1970)

        // ---- 🚫 Non-PDF file ----
        File notPdf = new File(inputDir, "non-pdf_file.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(notPdf))) {
            bw.write("TXT file. Should be skipped.");
        }
        notPdf.setLastModified(System.currentTimeMillis());

        // ---- 🚫 Corrupted PDF file ----
        File corruptedPdf = new File(inputDir, "corrupted_file.pdf");
        createCorruptedPdf(corruptedPdf);
        corruptedPdf.setLastModified(System.currentTimeMillis());

        // Define pattern and date range
        Pattern pattern = Pattern.compile("^.*file\\.pdf$", Pattern.CASE_INSENSITIVE); // match *file.pdf only
        Date minDate = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-2020");
        Date maxDate = new Date();  // now

        // Run the actual conversion method
        Pdf2Txt.convertPdfs(inputDir, outputDir, pattern, minDate, maxDate);

        // ---- ✅ Check that only the matching PDF was converted ----
        File txtFile = new File(outputDir, "matching_file.txt");
        assertTrue(txtFile.exists(), "Expected TXT file not created.");

        String content = Files.readString(txtFile.toPath()).trim();
        assertEquals("Matching conditions. Should be converted.", content);

        // ---- ✅ Make sure no other TXT files were created ----
        File[] outputFiles = outputDir.listFiles((dir, name) -> name.endsWith(".txt"));
        assertNotNull(outputFiles);
        assertEquals(1, outputFiles.length, "Only one file should have been converted.");
    }

    // Utility to generate dummy PDF file with text
    private void createDummyPdf(File file, String text) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText(text);
                contentStream.endText();
            }

            document.save(file);
        }
    }

    // Utility to generate corrupted PDF file (invalid PDF content)
    private void createCorruptedPdf(File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(new byte[]{0x25, 0x50, 0x44, 0x46, 0x2D}); // "%PDF-" header truncated
            fos.write("corrupted content that is not a valid PDF structure".getBytes());
        }
    }
}