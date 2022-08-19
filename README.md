# PdfToTxtConverter
Πρόγραμμα μαζικής μετατροπής αρχείων pdf σε txt που δημιούργησα κατά τη διάρκεια της πρακτικής μου.\
Τρέχει μέσω της γραμμής εντολών, πληκτρολογώντας ```java -jar "PDFToTextConverter.jar"``` με (ή χωρίς) παραμέτρους. Προς ευκολία χρήσης, τροποποιούμε και εκτελούμε το αρχείο ```conv.bat```.\
Το πιο απλό παράδειγμα είναι να τοποθετίσουμε τα αρχεία pdf στον ίδιο φάκελο με το jar και να τρέξουμε το bat.\
Συμπεριλαμβάνεται ο κώδικας java (αρχείο PDFToTextConverter.java).

## Παράμετροι command line arguments (args)
* 1η παράμετρος: φάκελος πηγή (source folder).
* 2η παράμετρος: φάκελος προορισμού (destination folder).
* 3η παράμετρος: μικρότερη ημερομηνία τελευταίας τροποποήσης των αρχείων.
* 4η παράμετρος: μεγαλύτερη ημερομηνία τελευταίας τροποποίησης των αρχείων.

## Οδηγίες εκτέλεσης με παραμέτρους
Δίνουμε absolute ή relative path για τον φάκελο πηγής και προορισμού. Αν δεν υπάρχει ο φάκελος προορισμού που δώσαμε, δημιουργείται αυτόματα. Σε περίπτωση που δεν δώσουμε ούτε πηγή ούτε προορισμό, ορίζονται αυτόματα ως το current working directory.\
Το πρόγραμμα υποστηρίζει και wildcards στην πρώτη παράμετρο, ώστε να επιλέξουμε συγκεκριμένα pdf προς μετατροπή. Το wildcard για το αστεράκι είναι το ```(.\*)```, πχ. ```myFolder/myOtherFolder/wild(.\*)```. Αν τα αρχεία προς μετατροπή βρίσκονται στο current working directory, τότε δίνουμε απλώς το wildcard χωρίς path.\
Η τρίτη παράμετρος είναι επίσης προαιρετική. Αν δεν οριστεί, ισούται με 1-1-1900 προς σιγουριά εύρεσης αρχείων με μεγαλύτερη τιμή ημερομηνίας τροποποίησης.\
Η τέταρτη παράμετρος, επίσης προαιρετική, αν δεν οριστεί, ισούται με την τρέχουσα ημερομηνία προς σιγουριά εύρεσης αρχείων με μικρότερη τιμή ημερομηνίας τροποποίησης.\
Σημειώνεται ότι δέχεται ημερομηνίες της μορφής dd-MM-yyyy, πχ. 14-11-2019.

## Παραδείγματα
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
