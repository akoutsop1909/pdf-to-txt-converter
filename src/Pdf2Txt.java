import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Pdf2Txt {
    public static void main(String[] args) {
        // Ensure Java 11 or later is installed
        String version = System.getProperty("java.version");
        if (version.compareTo("11") < 0) {
            System.err.println("Error: Java version " + version + " found.\n" +
                    "This tool requires Java 11 or later to run.\n" +
                    "You can download it from the official Oracle website: https://www.oracle.com/java/technologies/downloads/#java11");
            System.exit(1);
        }

        // Set default values
        File source = new File(System.getProperty("user.dir")); // current directory
        File dest = new File(System.getProperty("user.dir")); // current directory
        String wildcard = "^.*\\.pdf$"; // accepts all PDF files
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false); // only accepts dd-MM-yyyy format
        Date minDate = new Date(0);  // 01-01-1970
        Date maxDate = new Date();   // current date

        // If args[0] is provided, separate wildcard from source path
        if (args.length >= 1) {
            // First, check if user has called for help
            switch (args[0]) {
                case "--help": callHelp("--help"); System.exit(0); break;
                case "--about": callHelp("--about"); System.exit(0); break;
            }

            File inputPath = new File(args[0]);

            // Check if user provided an existing directory without wildcard
            if (inputPath.exists() && inputPath.isDirectory()) {
                source = inputPath;
                // Use default wildcard (*.pdf)
            } else if (inputPath.getParent() != null) {
                // Separate filename or wildcard from source path
                source = new File(inputPath.getParent());
                if (!source.exists() || !source.isDirectory()) {
                    throw new IllegalArgumentException("\nThe parent directory of source path '" + inputPath.getAbsolutePath() + "' does not exist or is not a valid directory.\n" +
                            "Please ensure the directory exists and is accessible.");
                }
                // Convert wildcard to regex
                wildcard = convertWildcardToRegex(inputPath.getName());
            } else {
                // Only filename or wildcard pattern is passed (or it could be a single folder with a typo)
                System.err.println("Warning: source path '" + inputPath.getAbsolutePath() + "' does not exist.\n" +
                        "Assuming '" + inputPath.getName() + "' is a filename or wildcard pattern in the current directory.\n");
                // Convert wildcard to regex
                wildcard = convertWildcardToRegex(inputPath.getName());
            }
        }

        // Ensure wildcard is valid
        Pattern pattern;
        try {
            pattern = Pattern.compile(wildcard, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            System.err.println("Error: invalid wildcard pattern: '" + wildcard + "'.\n" +
                    "Please ensure the pattern is a valid regular expression.\n" +
                    "Example: 'test*.pdf' or 'report_?.pdf'.");
            System.exit(1);
            return; // added for compiler to ensure the method stops here
        }

        // If args[1] is provided, set it to destination path
        if (args.length >= 2) {
            dest = new File(args[1]);

            if (dest.exists()) {
                // Check if destination path is a file
                if (!dest.isDirectory()) {
                    throw new IllegalArgumentException("\nDestination path '" + dest.getAbsolutePath() + "' exists, but it is not a directory.\n" +
                            "Please provide a valid path for the destination directory.");
                }
            } else {
                // Try to create the directory and its parents
                if (!dest.mkdirs()) {
                    throw new IllegalArgumentException("\nFailed to create destination directory: " + dest.getAbsolutePath() + "\n" +
                            "Please ensure that the path is valid and you have write permissions.");
                }
                System.out.println("Successfully created destination directory: " + dest.getAbsolutePath());
            }
        }

        // If args[2] is provided, parse it as the minimum bound for the modified date
        if (args.length >= 3) {
            try {
                minDate = dateFormat.parse(args[2]);
            } catch (ParseException e) {
                throw new IllegalArgumentException("\nInvalid date format for min modified date: " + args[2] + "\n" +
                        "Expected format: dd-MM-yyyy (e.g., 01-01-2022)");
            }
        }

        // If args[3] is provided, parse it as the maximum bound for the modified date
        if (args.length >= 4) {
            try {
                maxDate = dateFormat.parse(args[3]);
            } catch (ParseException e) {
                throw new IllegalArgumentException("\nInvalid date format for max modified date: " + args[3] + "\n" +
                        "Expected format: dd-MM-yyyy (e.g., 01-01-2023)");
            }

            if (minDate.after(maxDate)) {
                throw new IllegalArgumentException("\nMax modified date cannot be earlier than min modified date.");
            }
        }
        // Run the conversion. If no args are provided, use defaults (current directory, all PDFs, full date range)
        convertPdfs(source, dest, pattern, minDate, maxDate);
    }

    public static String convertWildcardToRegex(String wildcard) {
        StringBuilder sb = new StringBuilder();

        // Escape all special characters except * and ?
        for (char c : wildcard.toCharArray()) {
            switch (c) {
                case '*':
                    sb.append(".*"); // * → zero or more characters
                    break;
                case '?':
                    sb.append('.'); // ? → single character
                    break;
                case '.':
                case '^':
                case '$':
                case '+':
                case '(':
                case ')':
                case '{':
                case '}':
                case '[':
                case ']':
                case '|':
                case '\\':
                    sb.append("\\").append(c);
                    break;
                default:
                    sb.append(c);
            }
        }

        return "^" + sb + "$"; // anchor wildcard
    }

    public static void convertPdfs(File source, File dest, Pattern pattern, Date minDate, Date maxDate) {
        // Count number of PDF files converted to TXT
        int countMatching = 0;
        int countConverted = 0;

        File[] listOfFiles = source.listFiles();

        // Check if source directory is not empty
        if (listOfFiles != null) {
            // Count total number of files in source directory
            int numFiles = 0;
            for (File entry : listOfFiles) { if (entry.isFile()) { numFiles++; }}

            System.out.println("Found a total of " + numFiles + " file(s) in: " + source.getAbsolutePath() + "\n");
            System.out.println("Entering conversion phase");
            System.out.println("===========================================");

            for (File file : listOfFiles) {
                String fileName = file.getName().trim();

                // Check if it's a PDF file that matches the wildcard
                if (file.isFile() && fileName.toLowerCase().endsWith(".pdf") && pattern.matcher(fileName).matches()) {
                    try {
                        // Check if file's modified date is within min and max modified date
                        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                        Date fileDate = new Date(attr.lastModifiedTime().toMillis());

                        if (!fileDate.before(minDate) && !fileDate.after(maxDate)) {
                            countMatching++;
                            // Create the new TXT file, same name as original PDF file
                            File txtFile = new File(dest, file.getName().substring(0, file.getName().length() - 4) + ".txt");

                            // Begin conversion here
                            try (
                                    PDDocument document = PDDocument.load(file);
                                    BufferedWriter bw = new BufferedWriter(new FileWriter(txtFile))
                            ) {
                                PDFTextStripper stripper = new PDFTextStripper();
                                String text = stripper.getText(document);
                                bw.write(text);

                                System.out.println("Converted file: " + file.getName());
                                countConverted++;
                            } catch (IOException e) {
                                System.err.println("Error converting file: " + file.getName());
                            }
                        }
                    } catch (IOException | UnsupportedOperationException | SecurityException e) {
                        System.err.println("Failed to read file attributes: " + file.getName());
                    }
                }
            }
            // Print number of files converted
            if (countConverted == 0) {
                System.out.println("===========================================\n");
                System.out.println("No PDF files were converted.\n" +
                        "Possible reasons: no matching files, date filters excluded all, or conversion failed.");

            } else {
                System.out.println("===========================================\n");
                System.out.println("Successfully converted " + countConverted + "/" + countMatching + " matching PDF file(s)");
                System.out.println("TXT file(s) saved to: " + dest.getAbsolutePath());
            }
        } else {
            throw new IllegalArgumentException("\nUnable to read contents of source directory: " + source + "\nExiting program.");
        }
    }

    public static void callHelp(String option) {
        if (option.equals("--help")) {
            System.out.println(
                    "PDF to TXT Converter - Help\n" +
                            "------------------------------------------------------------\n" +
                            "Usage: java -jar pdf2txt.jar [source] [dest] [minDate] [maxDate]\n" +
                            "\n" +
                            "Arguments:\n" +
                            "  source       - Path to the PDF files.\n" +
                            "                 You can optionally use a wildcard pattern in the filename part of the path.\n" +
                            "                 Defaults to the current working directory matching all PDF files (*.pdf).\n" +
                            "                 Examples:\n" +
                            "                   test.pdf           : PDF file 'test.pdf' in current directory\n" +
                            "                   test*.pdf          : PDF files starting with 'test' in current directory\n" +
                            "                   ./docs/            : all PDF files in './docs/'\n" +
                            "                   ./docs/report*.pdf : PDF files starting with 'report' in './docs/'\n" +
                            "                 Supported wildcards:\n" +
                            "                   *  - Matches zero or more characters (e.g., 'test*.pdf' matches 'test1.pdf', 'test_abc.pdf')\n" +
                            "                   ?  - Matches exactly one character(e.g., 'report_?.pdf' matches 'report_1.pdf', 'report_a.pdf')\n" +
                            "\n" +
                            "  dest         - Directory to save converted TXT files (it is created if it does not exist, e.g., ./output/).\n" +
                            "                 Defaults to the current working directory.\n" +
                            "\n" +
                            "  minDate      - Minimum modified date for PDFs (format: dd-MM-yyyy, e.g. 01-01-2022).\n" +
                            "                 Defaults to 01-01-1970.\n" +
                            "\n" +
                            "  maxDate      - Maximum modified date for PDFs (format: dd-MM-yyyy, e.g. 01-01-2023).\n" +
                            "                 Defaults to the current date.\n" +
                            "\n" +
                            "Example:\n" +
                            "From .jar file:   java -jar pdf2txt.jar ./input/report_?.pdf ./output 01-01-2022 01-01-2023\n" +
                            "From .bat file:   Windows users can run the .jar by double-clicking the .bat file.\n" +
                            "\n" +
                            "Run with '--help' or '--about' for usage and info."
            );
        } else {
            System.out.println(
                    "PDF to TXT Converter - About\n" +
                            "------------------------------------------------------------\n" +
                            "A simple Java CLI tool that converts PDF files to TXT format.\n" +
                            "\n" +
                            "Supports:\n" +
                            "  - Batch conversion of multiple PDF files.\n" +
                            "  - Wildcard-based filtering for filenames (using * and ?).\n" +
                            "  - Last modified date filtering (min and max).\n" +
                            "\n" +
                            "Limitations:\n" +
                            "  - Arguments must be provided in order (source, dest, minDate, maxDate).\n" +
                            "  - Only supports wildcard patterns (* and ?), not full regular expressions.\n" +
                            "  - Wildcards apply to filenames only, not to directories.\n" +
                            "  - The * wildcard is greedy (matches as many characters as possible).\n" +
                            "  - Date filtering uses the file's last modified timestamp.\n" +
                            "  - Recursive folder traversal is not supported.\n" +
                            "\n" +
                            "Version: 1.0.0 (Stable)\n" +
                            "License: MIT (Open source). See the LICENCE file for details.\n" +
                            "GitHub: https://github.com/akoutsop1909/pdf-to-txt-converter\n"
            );
        }
    }
}