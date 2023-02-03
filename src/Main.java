import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;


/**
 * The Main class is the entry point for the application. It handles input from the user to retrieve the
 * file to be indexed and the granularity of the documents. The result is printed to the console in the form
 * of a list of parameters and an inverted list.
 *
 * @author Christian Maier
 */
public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Path file = getPathFromConsole(scanner);
        String type = getGranularityFromConsole(scanner);
        boolean dgap= getIfUserWantsDgapFromConsole(scanner);

        IndexCreatorService indexCreator = new IndexCreatorService(file, type);
        Index resultIndex = indexCreator.createIndexForDocument();

        PrintService printService = new PrintService(resultIndex);
        printService.printParametersAkaKenngrößen();
        printService.printInvertedList(dgap);
        }


    /**
     * This method prompts the user to type in "true" or "false" to determine if they want the inverted list
     * to contain dgaps or just document numbers.
     *
     * @param scanner Scanner object used to retrieve input from the user
     * @return true if the user wants the inverted list to contain dgaps, false if they want just document numbers
     */
    private static boolean getIfUserWantsDgapFromConsole(Scanner scanner) {
        System.out.println("Please type in true if you want index with dgaps or false if you just want document numbers in inverted list!");
        boolean dgap = false;
        boolean dgapAcceptable=false;

        while (dgapAcceptable == false) {
            String input = scanner.nextLine();
            if (input.equals("true")) {
                dgap = true;
                dgapAcceptable=true;
            } else if (input.equals("false")) {
                dgap = false;
                dgapAcceptable=true;
            } else {
                System.out.println("Please just type in true or false.");
            }
        }
        return dgap;
    }

    /**
     * This method prompts the user to type in "line" or "verse" to determine the granularity of the documents
     * (docuements is a scientific term here refereing to the size of textual units).
     *
     * @param scanner Scanner object used to retrieve input from the user
     * @return a string that is either "line" or "verse" indicating the granularity of the documents
     */
    private static String getGranularityFromConsole(Scanner scanner) {
        System.out.println("Please type line or verse to set document granularity!");
        String type = "";

        while (!type.equals("line") && !type.equals("verse")) {
            type = scanner.nextLine();
            if (!type.equals("line") && !type.equals("verse")) {
                System.out.println("Please just type line or verse to decide if documents should be lines or verses.");
            }
        }
        return type;
    }

    /**
     * This method prompts the user to type in the path to the file they want to have indexed.
     *
     * @param scanner Scanner object used to retrieve input from the user
     * @return a Path object representing the file to be indexed
     */
    private static Path getPathFromConsole(Scanner scanner) {
        System.out.println("Please type in path to file you want to have indexed: ");
        Path file = null;
        while (file == null) {
            String input = scanner.nextLine();
            try {
                file = Paths.get(input);
                if (!Files.isRegularFile(file) || !(input.endsWith("txt") || input.endsWith("docx"))) {
                    System.out.println("Please enter a path to a txt or docx file.");
                    file = null;
                }
            } catch (Exception e) {
                System.out.println("Please enter a valid path to a file.");
            }
        }
        return file;
    }

}

