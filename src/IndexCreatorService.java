import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IndexCreatorService {
    Path file;
    Index index= new Index();


    public IndexCreatorService(Path file) {
        this.file=file;
    }

    public Index createIndexForDocument() {


        List<String> linesList = null;
        try {
            linesList = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }



        Stream<String> lines = linesList.stream().filter(IndexCreatorService::replaceEmptyLines);



        Stream<String> linesToGetWords = linesList.stream();



        long lineCount = lines.count();
        index.setNumberOfDocuments(lineCount);


        Stream<String> words = linesToGetWords.filter(IndexCreatorService::replaceEmptyLines).flatMap(line -> Stream.of(line.split(" ")));



     long wordCount = words.count();



        index.setNumberOfTotalWords(wordCount);


        Stream<String> wordsAsDistinct = linesList.stream().filter(IndexCreatorService::replaceEmptyLines).flatMap(
                line -> Stream.of(line.split(" ")).map(IndexCreatorService::eliminateSpecialChars));




        Stream<String> vocabularyStringNormalizedToLowerCase = wordsAsDistinct.map(IndexCreatorService::allToLowerCase).distinct();




        List<String> vocabularyList = vocabularyStringNormalizedToLowerCase.sorted().collect(Collectors.toList());



        List<String>  listOfLinesNotEmptyAndNoSpecialChars = linesList.stream().filter(IndexCreatorService::replaceEmptyLines).map(IndexCreatorService::eliminateSpecialChars).map(IndexCreatorService::allToLowerCase).collect(Collectors.toList());



        index.setNumberOfDifferentWords(vocabularyList.size());



        int allAppereances=0;


        int i=1;

            for (String line : listOfLinesNotEmptyAndNoSpecialChars) {

               List<String> wordsOfNormalizedLines = Arrays.asList(line.split(" "));


                for (String word: wordsOfNormalizedLines) {




                    for (String term : vocabularyList) {


                        if (word.equals(term)) {




                                if (index.getSearchTermByName(term) == null) {
                                    Searchterm newTerm = new Searchterm(term);
                                    index.getSearchtermList().add(newTerm);



                                    newTerm.addToDocumentsWehreTermAppears(i);
                                 //   newTerm.setNumberOfAppereances(newTerm.getDocumentsByNumberWhereTermAppears().size());
                                     index.getSearchTermByName(term).calculateNumberOfTermAppereances();
                                } else {



                                    index.getSearchTermByName(term).addToDocumentsWehreTermAppears(i);
                                   // index.getSearchTermByName(term).setNumberOfAppereances(index.getSearchTermByName(term).getDocumentsByNumberWhereTermAppears().size());
                                    index.getSearchTermByName(term).calculateNumberOfTermAppereances();

                                }
                            }


                        }
                }
                i++;
        }

        int result = 0;
        for (Searchterm term: index.getSearchtermList()) {

                result += term.getNumberOfAppereances();
        }

            index.setNumberOfTermDocumentAssociations(result);

return index;
    }




    private static String allToLowerCase(String s) {
        s=s.toLowerCase();
        return s;
    }

    private static boolean replaceEmptyLines(String s) {
      if(s.trim().matches(".*[0-9].*") || s.trim().matches(".*[a-y].*") || s.trim().matches(".*[A-Y].*") ) {
        return true;
      }
      return false;
    }

    private static String eliminateSpecialChars(String s) {
       s= s.replaceAll("[^a-zA-Z0-9 ]", "");
        return s;
    }


}
