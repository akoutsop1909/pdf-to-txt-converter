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
- **`[source]`** – Path to the PDF files. Supports wildcards (e.g., ./input/report_?.pdf).
- **`[dest]`** – Directory to save converted TXT files. (e.g., ./output/).
- **`[minDate]`** – Minimum modified date for PDFs. (format: dd-MM-yyyy, e.g., 01-01-2022).
- **`[maxDate]`** – Maximum modified date for PDFs. (format: dd-MM-yyyy, e.g., 01-01-2023).  

All arguments are optional, but must be provided in the order listed above. If one or more are omitted at the end, default values will be used for the missing ones:
- **`[source]`** → Current directory.
- **`[dest]`** → Current directory.
- **`[minDate]`** → 01-01-1970.
- **`[maxDate]`** → Current date.

### Limitations
- Arguments must be provided in order: `[source]`, `[dest]`, `[minDate]`, `[maxDate]`.
- Supports only wildcard patterns (`*` and `?`), not full regular expressions.
- Wildcards apply to filenames only, not to directory names.
- The `*` wildcard is greedy (matches as many characters as possible).
- Date filtering is based on the file's last modified timestamp.
- Recursive folder traversal is not supported.

### Example 
```
java -jar pdf2txt.jar ./input/report_?.pdf ./output 01-01-2022 01-01-2023
```

The above command will convert PDF files in './input' that were modified between Jan 1, 2022 and Jan 1, 2023 into TXT files, saving them in './output'. If no matching files are found, the tool will display an appropriate message.

> [!IMPORTANT]
> The tool was developed and tested using IntelliJ IDEA. However, when running it directly from the command line (e.g., cmd, PowerShell, bash), the shell may automatically expand wildcard characters before passing them to the tool, leading to unexpected behavior. Quoting the source path may still result in wildcard expansion. Running the tool from a Java IDE, such as IntelliJ IDEA, bypasses shell expansion and ensures the wildcard is passed correctly.
    
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


<!---
A bulk PDF to TXT converter that I developed during my internship.\
It is executed through the command line by typing ```java -jar "PDFToTextConverter.jar"``` with or without arguments. For ease of use, the ```conv.bat``` file can be modified and executed instead.\
The simplest conversion involves placing the PDF files in the same folder as the JAR file and executing the BAT file.\
The java code is included in the ```PDFToTextConverter.java``` file.

## Command line arguments (args)
* 1st argument: source folder (absolute or relative path).
* 2nd argument: destination folder (absolute or relative path).
* 3rd argument: last modified date (greater than set value).
* 4th argument: last modified date (less than set value).

## Execution with hyperparameters
If the destination folder does not exist, it is created automatically. In case both the source and destination folders are not provided, they are set to the current working directory.\
The first hyperparameter supports wildcards to select specific PDF files for conversion. The wildcard for the asterisk is ```(.\*)```, for example ```myFolder/myOtherFolder/wild(.\*)```. If the files to convert are in the current working directory, we can type the wildcard without the path.\
The third hyperparameter is also optional. If not set, it equals to 1-1-1900 to ensure searching for files with a higher last modified date.\
The fourth hyperparameter is also optional. If not set, it equals to the current date to ensure searching for files with a lower last modified date.\
Dates must be in dd-MM-yyyy format, for example 14-11-2019.

## Examples
```
java -jar "PDFToTextConverter.jar"
java -jar "PDFToTextConverter.jar" wi(.\*)
java -jar "PDFToTextConverter.jar" wi(.\*) new
java -jar "PDFToTextConverter.jar" folder1 folder2
java -jar "PDFToTextConverter.jar" folder1/subfolder folder2
java -jar "PDFToTextConverter.jar" folder1/wild(.\*) folder2
java -jar "PDFToTextConverter.jar" folder1 folder/subfolder2 1-1-2019
java -jar "PDFToTextConverter.jar" folder1/subfolder folder2 3-4-2018 3-4-2018
java -jar "PDFToTextConverter.jar" folder1/subfolder/wi(.\*) folder2/sub1/sub2 4-5-2018 5-10-2019
```
---!>
