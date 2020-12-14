import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {


    public static void main(String[] args) {

        if (args.length != 3) {
            System.err.println("Something went wrong with the arguments, please just a path to the file you want to have indexed");
            System.exit(-1);
        }


        final String filePathAsString = args[0];
        final String type = args[1];
        final String dgaps = args[2];

        if (!dgaps.equals("true") && !dgaps.equals("false")) {
            System.out.println(dgaps);
            System.out.println("Sry please just type true or false to indicate if you want to display dgaps or just document numbers");
            System.exit(-1);
        }

        boolean gaps;
        if (dgaps.equals("true")) {
            gaps=true;
        } else {
            gaps=false;
        }


        if (!type.equals("line") && !type.equals("verse")) {


            System.out.println(type);
            System.out.println("Sry please just type in line or verse as second argument to decide if documents should be lines or verses");
            System.exit(-1);
        }

        Path file = Paths.get(filePathAsString);

        if(Files.isRegularFile(file)) {
            boolean fileExtensionCorrect = filePathAsString.endsWith("txt") || filePathAsString.endsWith("docx") ;
            if (fileExtensionCorrect){
                IndexCreatorService indexCreator = new IndexCreatorService(file, type);
                Index resultIndex = indexCreator.createIndexForDocument();

                PrintService printService = new PrintService(resultIndex);


                printService.printParametersAkaKenngrößen();



                printService.printInvertedList(gaps);
            }
               

        }

    }

}