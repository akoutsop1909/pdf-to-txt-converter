# PDF-to-TXT Converter
A command-line Java tool for batch-converting PDF files to TXT format using [Apache PDFBox](https://pdfbox.apache.org/). It supports filtering input files by filename wildcards (e.g., `*.pdf`, `report_??.pdf`) and last modified date ranges. Output `.txt` files are saved to a specified directory, which is created automatically if it doesn't exist.
> [!NOTE]
> I initially built this project during my internship, tailored to the company's internal document needs.  
> Code shared with permission.

## ⚙️ System Requirements
- **Java 11 or later** is required. You can download it from the [official Oracle website](https://www.oracle.com/java/technologies/downloads/#java11).
- Ensure that the `java` command is available in your system's `PATH`.  
You may also need to set the `JAVA_HOME` environment variable on some systems. Instructions [here](https://docs.oracle.com/cd/E19182-01/821-0917/inst_jdk_javahome_t/index.html).

## How to Use

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
