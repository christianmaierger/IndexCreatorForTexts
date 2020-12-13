import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

        //index.setNumberOfDocuments(linesList.size());

        Stream<String> lines = linesList.stream().filter(IndexCreatorService::replaceEmptyLines);

        Stream<String> linesToGetWords = linesList.stream();

        Stream<String> linesToGetPolishedLineList = linesList.stream();



        long lineCount = lines.count();
        index.setNumberOfDocuments(lineCount);


        Stream<String> words = linesToGetWords.flatMap(line -> Stream.of(line.split(" ")));


     long wordCount = words.count();



        index.setNumberOfTotalWords(wordCount);


        Stream wordsAsDistinct = linesList.stream().filter(IndexCreatorService::replaceEmptyLines).flatMap(
                line -> Stream.of(line.split(" ")).map(IndexCreatorService::eliminateSpecialChars));


        Stream wordsAsDistinctForLineList = linesList.stream().filter(IndexCreatorService::replaceEmptyLines).flatMap(
                line -> Stream.of(line.split(" ")).map(IndexCreatorService::eliminateSpecialChars));

        Stream<String> vocabularyString = wordsAsDistinct.distinct();


        List<String> vocabularyList = vocabularyString.sorted().collect(Collectors.toList());


        List<String>  listOfLinesNotEmptyAndNoSpecialChars = linesList.stream().filter(IndexCreatorService::replaceEmptyLines).collect(Collectors.toList());



        index.setNumberOfDifferentWords(vocabularyList.size());


        int i=1;

            for (String line : listOfLinesNotEmptyAndNoSpecialChars) {


                for (String term: vocabularyList) {

                    term= " "+ term;

                if (line.contains(term)) {
                    index.setNumberOfTermDocumentAssociations((int) (index.getNumberOfTermDocumentAssociations()+1.0));
                    if(index.getSearchTermByName(term) == null){
                        Searchterm newTerm;
                        index.getSearchtermList().add(newTerm = new Searchterm(term));
                        newTerm.setNumberOfAppereances(newTerm.getNumberOfAppereances()+1);
                        newTerm.addToDocumentsWehreTermAppears(i);
                    }
                   index.getSearchTermByName(term).setNumberOfAppereances(index.getSearchTermByName(term).getNumberOfAppereances()+1);
                   index.getSearchTermByName(term).addToDocumentsWehreTermAppears(i);

                }

            }
                i++;
        }





return index;
    }

    private static boolean replaceEmptyLines(String s) {
      if(s.trim().matches(".*[0-9].*") || s.trim().matches(".*[a-y].*") || s.trim().matches(".*[A-Y].*") ) {
        return true;
      }
      return false;
    }

    private static String eliminateSpecialChars(String s) {
       s= s.replaceAll("[^a-zA-Z0-9]", "");
        return s;
    }


}
