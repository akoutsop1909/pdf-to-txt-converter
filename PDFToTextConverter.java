import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFToTextConverter {
    public static void main(String[] args) throws ParseException, IOException {
        //to programma den ekteleitai an den uparxoun parametroi
        if(args.length == 0) {
            System.out.println("Please give input and output folder");
            System.exit(0);
        }
        
        //gia arxiki timi gia to min date dinoume mia timi pou kseroume oti tha broume megaluteri
        //gia arxiki timi gia to max date dinoume tin torini imerominia
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        Date min_date = sdf1.parse("01-01-1900");
        Date max_date = Calendar.getInstance().getTime();  
        
        //an uparxei 3i parametros tote to min_date pairnei tin timi tis 3is parametrou
        if (args.length == 3) {
            try {
                min_date = sdf1.parse(args[2]);
            } catch (ParseException e) {e.printStackTrace(); System.exit(0);}
        }

        //an uparxei kai 4i parametros tote to max_date pairnei tin timi tis 4is parametrou
        if (args.length == 4) {
            try {
                min_date = sdf1.parse(args[2]);
                max_date = sdf1.parse(args[3]);
            } catch (ParseException e) {e.printStackTrace(); System.exit(0);}
        }
        
        //arxiki timi gia to wildcard
        String wildcard = "(.*)pdf";
        
        //lista me ta arxeia tou fakelou tis 1is parametrou
        //se periptosi pou dothei path pou teleionei se arxeio (i wildcard),
        //pairnoume to wildcard se metabliti me auto ton tropo
        File source = new File(args[0]);
        if (!source.isDirectory()) {
            if (source.getParent() == null) {
                source = new File (source.getAbsolutePath());
            }
            wildcard = source.getName();
            source = new File (source.getParent());
        }
        
        //to output folder brisketai sti 2i parametro
        File dest_folder = new File(args[1]);
        if (!dest_folder.isDirectory()) new File(args[1]).mkdirs();
        //me to count metrame posa arxeia exoun metatrapei se txt
        int count = 0;

        if (source.list().length > 0) {
            File[] listOfFiles = source.listFiles();
            for (File file : listOfFiles) {
                if (file.getName().matches(wildcard) && file.getName().endsWith(".pdf")) {
                    try {
                        //diabazoume tin imerominia tropopoiisis tou arxeiou kai ti sugkrinoume me to min kai to max date
                        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);                      
                        Date file_date = sdf2.parse(attr.lastModifiedTime().toString());
                        if(file_date.compareTo(min_date) >= 0 && file_date.compareTo(max_date) <= 0) {
                            FileWriter fw = new FileWriter(dest_folder + "/" + file.getName().substring(0, file.getName().length()-3) + "txt");
                            BufferedWriter bw = new BufferedWriter(fw);
                            PdfReader pr = new PdfReader(source + "/" + file.getName());
                            int pNum = pr.getNumberOfPages();
                            for(int page = 1; page <= pNum; page++) {
                                String text = PdfTextExtractor.getTextFromPage(pr, page);
                                text = text.replaceAll("\n", "\r\n");
                                bw.write(text);
                            }
                            bw.flush();
                            bw.close();
                            //emfanizei to onoma tou arxeiou pou exei metatrapei
                            System.out.println("Converted file " + file.getName());
                            count++;
                        }
                    } catch(Exception e){e.printStackTrace();}
                }
            }
            //an to count einai 0, tote den brethikan pdf arxeia sto fakelo
            if (count == 0) System.out.println("No pdf files to convert");
            //allios emfanizoume to sunoliko arithmo ton arxeion pou exoun metatrapei
            else {
                System.out.println(" ");
                System.out.println("Successfully converted " + count + " file(s)");
            }
        }
        else System.out.println("The folder is empty");
    }
}