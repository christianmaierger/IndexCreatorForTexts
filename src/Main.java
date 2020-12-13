import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {


    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Something went wrong with the arguments, please just a path to the file you want to have indexed");
            System.exit(-1);
        }


        final String filePathAsString = args[0];

        Path file = Paths.get(filePathAsString);

        if(Files.isRegularFile(file)) {
            boolean fileExtensionCorrect = filePathAsString.endsWith("txt") || filePathAsString.endsWith("docx") ;
            if (fileExtensionCorrect){
                IndexCreatorService indexCreator = new IndexCreatorService(file);
                Index resultIndex = indexCreator.createIndexForDocument();

                PrintService printService = new PrintService(resultIndex);
                printService.printParametersAkaKenngrößen();



                printService.printInvertedList();
            }
               

        }

    }

}