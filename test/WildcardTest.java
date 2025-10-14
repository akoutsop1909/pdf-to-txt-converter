import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.regex.Pattern;

public class WildcardTest {

    @Test
    void testConvertWildcardToRegex() {
        // ---- Basic wildcards ----
        testWildcard("*.pdf", "document.pdf", true);
        testWildcard("*.pdf", "image.png", false);

        testWildcard("file?.txt", "file1.txt", true);
        testWildcard("file?.txt", "file12.txt", false);

        testWildcard("report_202?.doc", "report_2021.doc", true);
        testWildcard("report_202?.doc", "report_20210.doc", false);

        testWildcard("data.*", "data.csv", true);
        testWildcard("data.*", "data_backup.csv", false);

        // ---- Edge cases ----
        testWildcard("*", "anything_goes_here", true);      // * alone matches everything
        testWildcard("?", "a", true);                        // single ? matches single char
        testWildcard("?", "", false);                        // ? does not match empty string
        testWildcard("file*", "file", true);                 // * matches zero or more chars, so 'file' matches
        testWildcard("file*", "file123.txt", true);
        testWildcard("*file", "myfile", true);
        testWildcard("*file", "file", true);
        testWildcard("*file", "afile.txt", false);

        // ---- Multiple wildcards ----
        testWildcard("file*?.txt", "file1a.txt", true);
        testWildcard("file*?.txt", "file123.txt", true); // * is greedy

        testWildcard("???.txt", "abc.txt", true);
        testWildcard("???.txt", "ab.txt", false);
        testWildcard("???.txt", "abcd.txt", false);

        // ---- Wildcards with dots and special chars (dots are literal) ----
        testWildcard("file.*.txt", "file.v1.txt", true);
        testWildcard("file.*.txt", "file..txt", true);
        testWildcard("file.*.txt", "filev1.txt", false);

        // ---- Patterns with no wildcards should match literally ----
        testWildcard("exactfile.txt", "exactfile.txt", true);
        testWildcard("exactfile.txt", "ExactFile.txt", true); // case-insensitive
        testWildcard("exactfile.txt", "exactfile.txt.bak", false);
        testWildcard("exactfile.txt", "my_exactfile.txt", false);

        // ---- Wildcards in the middle ----
        testWildcard("file*name.txt", "filename.txt", true);
        testWildcard("file*name.txt", "file123name.txt", true);
        testWildcard("file*name.txt", "file_name.txt", true);
        testWildcard("file*name.txt", "filenam.txt", false);

        // ---- Empty string and empty pattern ----
        testWildcard("", "", true);       // empty pattern matches empty string
        testWildcard("", "anything", false);
        testWildcard("*", "", true);      // * matches empty string
        testWildcard("?", "", false);

        // ---- Literal special regex characters should match literally since only * and ? are wildcards ----
        testWildcard("file+?.txt", "file+.txt", false);   // ? expects one char, so no match here
        testWildcard("file+?.txt", "file+1.txt", true);
        testWildcard("file(1).txt", "file(1).txt", true);
        testWildcard("file(1).txt", "filea.txt", false);
        testWildcard("file[1].txt", "file[1].txt", true);
        testWildcard("file{1}.txt", "file{1}.txt", true);
        testWildcard("file^1.txt", "file^1.txt", true);
        testWildcard("file$.txt", "file$.txt", true);
        testWildcard("file|alt.txt", "file|alt.txt", true);
        testWildcard("file\\.txt", "file\\.txt", true); // double escape for Java
    }

    private void testWildcard(String wildcard, String filename, boolean expected) {
        String regex = Pdf2Txt.convertWildcardToRegex(wildcard);
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        boolean matches = pattern.matcher(filename).matches();
        assertEquals(expected, matches,
                "Wildcard '" + wildcard + "' regex '" + regex + "' matching '" + filename + "' => ");
    }
}