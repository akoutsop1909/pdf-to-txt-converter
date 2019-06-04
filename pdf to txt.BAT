for %%f in ("%cd%") do SET LOCALPATH=%%~sf
SET CLASSPATH=%LOCALPATH%;%LOCALPATH%\itextpdf-5.4.1.jar
java PDFToTextConverter
pause