import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

public class PathParsingTest {

    @Test
    void testExistingDirectory(@TempDir Path tempDir) {
        // ---- Source is an existing directory ----
        File inputPath = tempDir.toFile();

        Object[] result = parseSourcePath(inputPath.getAbsolutePath());
        File source = (File) result[0];
        String wildcard = (String) result[1];

        assertEquals(inputPath.getAbsolutePath(), source.getAbsolutePath(), "Source should be the existing directory.");
        assertEquals("*.pdf", wildcard, "Should use default wildcard (*.pdf).");
    }

    @Test
    void testValidParentDirectory(@TempDir Path tempDir) {
        // ---- Source has a valid parent directory and a wildcard pattern ----
        File subDir = new File(tempDir.toFile(), "sub");
        assertTrue(subDir.mkdir(), "Subdirectory should be created.");

        Object[] result = parseSourcePath(subDir.getAbsolutePath()+"/file_?.pdf");
        File source = (File) result[0];
        String wildcard = (String) result[1];

        assertEquals(subDir.getAbsolutePath(), source.getAbsolutePath(), "Source should be the parent directory.");
        assertEquals("file_?.pdf", wildcard, "Should extract the correct wildcard pattern.");
    }

    @Test
    void testInvalidParentDirectory() {
        // ---- Source has an invalid parent directory and a wildcard pattern ----
        String invalidPath = "no_such_dir/file.pdf";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parseSourcePath(invalidPath);
        });

        assertTrue(exception.getMessage().contains("Parent directory does not exist"), "Should throw exception for invalid parent directory.");
    }

    @Test
    void testJustFilenameOrWildcard() {
        // ---- Source is a filename or a wildcard pattern ----
        String input = "test*.pdf";

        Object[] result = parseSourcePath(input);
        File source = (File) result[0];
        String wildcard = (String) result[1];

        assertEquals(new File(".").getAbsolutePath(), source.getAbsolutePath(), "Source should be default directory.");
        assertEquals("test*.pdf", wildcard, "Should extract the filename/wildcard correctly.");
    }

    // This method mimics the main() logic for parsing the source path
    private Object[] parseSourcePath(String input) {
        File source = new File("."); // current working directory
        String wildcard = "*.pdf"; // default wildcard

        File inputPath = new File(input);

        // Check if user provided an existing directory without wildcard
        if (inputPath.exists() && inputPath.isDirectory()) {
            source = inputPath;
            // Use default wildcard (*.pdf)
        } else if (inputPath.getParent() != null) {
            // Separate filename or wildcard from source path
            source = new File(inputPath.getParent());
            if (!source.exists() || !source.isDirectory()) {
                throw new IllegalArgumentException("Parent directory does not exist or is invalid: " + source.getAbsolutePath());
            }
            // Extract wildcard
            wildcard = inputPath.getName();
        } else {
            // Only filename or wildcard pattern is passed (or it could be a single folder with a typo)
            System.err.println("Warning: source path '" + inputPath.getAbsolutePath() + "' does not exist.\n" +
                    "Assuming '" + inputPath.getName() + "' is a filename or wildcard pattern in the current directory.\n");
            // Extract wildcard
            wildcard = inputPath.getName();
        }

        return new Object[]{source, wildcard};
    }
}
