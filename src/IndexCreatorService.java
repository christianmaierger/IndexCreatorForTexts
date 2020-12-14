import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IndexCreatorService {
    Path file;
    Index index= new Index();
    String type;


    public IndexCreatorService(Path file, String type) {
        this.file=file;
        this.type=type;

    }

    public Index createIndexForDocument() {


        List<String> linesList = null;
        try {
            linesList = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }


        List<String> verses = new LinkedList<>();
        if(type.equals("verse")) {

            List<String> verseList = new LinkedList<>();
            String string = "";

            for (String line : linesList) {


                if (!line.isBlank()) {
                    string = string.concat(line + " ");
                }
                if (line.isBlank()) {
                    verseList.add(string);
                    string = "";
                }
            }

             verses = verseList.stream().filter
                    (IndexCreatorService::replaceEmptyLines).map(IndexCreatorService::eliminateSpecialChars).map
                    (IndexCreatorService::allToLowerCase).collect(Collectors.toList());

            long verseCount = verses.size();
            index.setNumberOfDocuments(verseCount);

        }


        List<String> listOfLinesNotEmptyAndNoSpecialChars = new LinkedList<>();
        if (type.equals("line")) {

            // list for documet as lines
            Stream<String> lines = linesList.stream().filter(IndexCreatorService::replaceEmptyLines);

            long lineCount = lines.count();
            index.setNumberOfDocuments(lineCount);


             listOfLinesNotEmptyAndNoSpecialChars = linesList.stream().filter
                    (IndexCreatorService::replaceEmptyLines).map(IndexCreatorService::eliminateSpecialChars).map
                    (IndexCreatorService::allToLowerCase).collect(Collectors.toList());

        }


        List<String> vocabularyList = calculateSearchTerms(linesList);


        if (type.equals("line")) {
            calculateInvertedList(vocabularyList, listOfLinesNotEmptyAndNoSpecialChars);
        }

        if (type.equals("verse")) {
            calculateInvertedList(vocabularyList, verses);
        }



        int result = 0;
        for (Searchterm term: index.getSearchtermList()) {

                result += term.getNumberOfAppereances();
        }

            index.setNumberOfTermDocumentAssociations(result);
            index.calculateVerweisdichte();

return index;
    }


    private List<String> calculateSearchTerms(List<String> linesList) {
        Stream<String> linesToGetWords = linesList.stream();

        Stream<String> words = linesToGetWords.filter(IndexCreatorService::replaceEmptyLines).flatMap(line -> Stream.of(line.split(" ")));

        long wordCount = words.count();
        index.setNumberOfTotalWords(wordCount);

        Stream<String> wordsAsDistinct = linesList.stream().filter(IndexCreatorService::replaceEmptyLines).flatMap(
                line -> Stream.of(line.split(" ")).map(IndexCreatorService::eliminateSpecialChars));

        Stream<String> vocabularyStringNormalizedToLowerCase = wordsAsDistinct.map(IndexCreatorService::allToLowerCase).distinct();

        List<String> vocabularyList = vocabularyStringNormalizedToLowerCase.sorted().collect(Collectors.toList());

        index.setNumberOfDifferentWords(vocabularyList.size());
        return vocabularyList;
    }


    private void calculateInvertedList(List<String> vocabularyList, List<String> listOfLinesNotEmptyAndNoSpecialChars) {
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
