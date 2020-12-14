import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {


        Scanner scanner = new Scanner(System.in);
        Path file = null;

        System.out.println("Please type in path to file you want to have indexed: ");

        boolean isAcceptableFile = false;
        while(!isAcceptableFile) {
            String input = scanner.nextLine();


            try {
                file = Paths.get(input);
                if(Files.isRegularFile(file)) {
                    boolean fileExtensionCorrect = input.endsWith("txt") || input.endsWith("docx");
                    if (!fileExtensionCorrect) {
                        System.out.println("Please just type in a path to a txt or docx file!");
                    } else {
                        isAcceptableFile=true;
                        break;
                    }
                } else {
                    System.out.println("Sorry only paths to files allowed as input, you typed in: " + input);
                }
            } catch (Exception e) {
                System.out.println("Sorry only paths to files allowed as input, you typed in: " + input);
            }
        }




        String type = "";
        boolean typeAcceptable=false;
        while(!typeAcceptable) {
            System.out.println("Please type line or verse to set document granularity!");
            String input = scanner.nextLine();

            try {
                if (!input.equals("line") && !input.equals("verse")) {
                    System.out.println("Sry please just type in line or verse as second argument to decide if documents should be lines or verses");
                } else {
                    type=input;
                    typeAcceptable = true;
                    break;
                }
            } catch (Exception e) {
                System.out.println("Sry please just type in line or verse as second argument to decide if documents should be lines or verses");
            }
        }

        boolean dgapAcceptable=false;
        boolean dgap=false;
        while(!dgapAcceptable) {
            System.out.println("Please type in true if you want index with dgaps or false if you just want document numbers in inverted list!");
            String input = scanner.nextLine();

            try {
                if (!input.equals("true") && !input.equals("false")) {

                    System.out.println("Please just type in true or false!");
                } else {
                    if(input.equals("true")) {
                        dgapAcceptable=true;
                        dgap = true;
                        break;
                    } else {
                        dgapAcceptable=true;
                        dgap =false;
                        break;
                    }

                }
            } catch (Exception e) {
                System.out.println("Sorry only paths to files allowed as input, you typed in: " + input);
            }
        }



                IndexCreatorService indexCreator = new IndexCreatorService(file, type);
                Index resultIndex = indexCreator.createIndexForDocument();

                PrintService printService = new PrintService(resultIndex);


                printService.printParametersAkaKenngrößen();



                printService.printInvertedList(dgap);


        }

    }

