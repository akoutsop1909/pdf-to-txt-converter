# PDF-to-TXT Converter
A command-line Java tool for batch-converting PDF files to TXT format using [Apache PDFBox](https://pdfbox.apache.org/). It supports filtering input files by filename wildcards (e.g., `*.pdf`, `report_??.pdf`) and last modified date ranges. Output `.txt` files are saved to a specified directory, which is created automatically if it doesn't exist.
> [!NOTE]
> I initially built this project during my internship, tailored to the company's internal document needs.  
> Code shared with permission.

## ⚙️ System Requirements
- **Java 11 or later** is required. You can download it from the [official Oracle website](https://www.oracle.com/java/technologies/downloads/#java11).
- Ensure that the `java` command is available in your system's `PATH`.  
You may also need to set the `JAVA_HOME` environment variable on some systems. Instructions [here](https://docs.oracle.com/cd/E19182-01/821-0917/inst_jdk_javahome_t/index.html).

## 📘 How to Use
You can download the latest release from the **Releases** section of this repository, which includes the executable `pdf2txt.jar` and a `run_pdf2txt.bat` script for Windows users. This batch script prompts you for input and runs the `pdf2txt.jar` with the parameters you provide. Make sure both files are in the same directory.

Alternatively, you can clone the repository and build the JAR manually using a Java IDE like [IntelliJ IDEA](https://www.jetbrains.com/idea/download/?section=windows), or the `jar` command.

To run the tool directly from the command line, use the following format:
```
java -jar pdf2txt.jar [source] [dest] [minDate] [maxDate]
```

### Arguments
- `[source]` – Path to the PDF files. Supports wildcards (e.g., ./input/report_?.pdf).
- `[dest]` – Directory to save converted TXT files. (e.g., ./output/).
- `[minDate]` – Min modified date for PDFs. (format: dd-MM-yyyy, e.g., 01-01-2022).
- `[maxDate]` – Max modified date for PDFs. (format: dd-MM-yyyy, e.g., 01-01-2023).  

All arguments are optional, but must be provided in the order listed above. If one or more are omitted at the end, default values will be used for the missing ones:
- `[source]` → Current directory.
- `[dest]` → Current directory.
- `[minDate]` → 01-01-1970.
- `[maxDate]` → Current date.

### Limitations
- Arguments must be provided in order: `[source]`, `[dest]`, `[minDate]`, `[maxDate]`.
- Supports only wildcard patterns (`*` and `?`), not full regular expressions.
- Wildcards apply to filenames only, not directory names.
- The `*` wildcard is greedy (matches as many characters as possible).
- Date filtering is based on the file's last modified timestamp.
- Recursive folder traversal is not supported.

### Example 
```
java -jar pdf2txt.jar ./input/report_?.pdf ./output 01-01-2022 01-01-2023
```

The above command will convert PDF files in the `./input` directory, modified between Jan 1, 2022, and Jan 1, 2023, into TXT files, saving them in the `.output` directory. If no matching files are found, an appropriate message will be displayed.

> [!IMPORTANT]
> The tool was developed and tested using IntelliJ IDEA. However, when running it directly from the command line (e.g., cmd, PowerShell, bash), the shell may automatically expand wildcard characters before passing them to the tool, which can lead to unexpected behavior. Quoting the source path may still not fully prevent the expansion. Running it from a Java IDE, such as IntelliJ IDEA, bypasses shell expansion and ensures the wildcard is passed correctly.

For more details and information, you can run:
```
java -jar pdf2txt.jar --help  # Displays detailed usage and arguments guide.
java -jar pdf2txt.jar --about # Displays general information and limitations.
```
    
## ⌨️ Demo Run
![pdf2txt demo](https://github.com/user-attachments/assets/ac940b3c-d88b-4312-836a-b9d1a46d8df8)

## 📂 Folder Structure
```
pdf-to-txt-converter/
├── lib/                     # Dependency JARs
│   └── pdfbox-app-2.0.30.jar
├── src/                     # Java source code
│   └── Pdf2Txt.java    
├── test/                    # JUnit tests
│   ├── ConversionTest.java
│   ├── PathParsingTest.java
│   └── WildcardTest.java
├── .gitignore               # Files/folders to ignore in Git
├── LICENSE                  # License file (MIT)
└── README.md                # This file
```
