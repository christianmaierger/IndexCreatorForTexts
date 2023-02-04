import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * The class `IndexCreatorService` provides a service to create an index for a given file.
 * It takes a file path and a type of document (term from linguistics/library science),
 * which could be either "line" or "verse".
 * Based on the type of document, it processes the file, splits the content into words,
 * and adds each word as a search term to the index.
 */
public class IndexCreatorService {
    Path file;
    Index index;
    String type;

    /**
     * Constructs a new `IndexCreatorService` instance.
     * @param file The file to create an index for.
     * @param type The type of document, either "line" or "verse".
     */
    public IndexCreatorService(Path file, String type) {
        this.file=file;
        this.type=type;
        this.index = new Index();
    }

    /**
     * Creates the index for the given file.
     * @return The created index.
     */
    public Index createIndexForDocument() throws IOException {
            List<String> linesList = readFile(file);
            // List of verses or lines
            List<String> document = getDocument(linesList, type);
            // list of all distinct words from the text file
            List<String> vocabularyList = calculateSearchTerms(linesList);
            // calculates a so called inverted list that holds all words and their occurances in the text
            calculateInvertedList(vocabularyList, document);

            // stream calculates total no of appereances of one term in textfile
            index.setNumberOfTermDocumentAssociations(index.getSearchtermList().stream()
                    .mapToInt(Searchterm::getNumberOfAppereances)
                    .sum());
            // ratio of term-document associations to the product of the number of documents and number of different words.
            index.calculateVerweisdichte();
            return index;

    }

    /**
     * Reads the file into a list of strings.
     * @param file The file to read.
     * @return The list of strings.
     */
    private List<String> readFile(Path file) {
        try {
            String fileExtension = file.getFileName().toString().toLowerCase().substring(file.getFileName().toString().lastIndexOf(".") + 1);
            if (fileExtension.equals("txt")) {
                return Files.readAllLines(file, StandardCharsets.UTF_8);
            } else if (fileExtension.equals("doc") || fileExtension.equals("docx")) {
                System.out.println("will now read word");
                return readWordFile(file);

            } else {
                System.out.println("Unsupported file format.");
                return Collections.emptyList();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    private List<String> readWordFile(Path file) {
        List<String> lines = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file.toFile())) {
            XWPFDocument docx = new XWPFDocument(fis);
            StringBuilder lineBuilder = new StringBuilder();
            for (XWPFParagraph paragraph : docx.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.toString();
                    int index = text.indexOf("\n");
                    if (index == -1) {
                        lineBuilder.append(text);
                    } else {
                        lineBuilder.append(text.substring(0, index));
                        lines.add(lineBuilder.toString());
                        lineBuilder = new StringBuilder();
                        lineBuilder.append(text.substring(index + 1));
                    }
                }
                lines.add(lineBuilder.toString());
                lineBuilder = new StringBuilder();
            }
            System.out.println("Word read without exc");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }



    /**
     * Gets the document as a list of lines or verses, based on the type.
     * @param linesList The list of strings to process.
     * @param type The type of document, either "line" or "verse".
     * @return The list of lines or verses.
     */
    private List<String> getDocument(List<String> linesList, String type) {
        if (type.equals("line")) {
            return getLines(linesList);
        } else if (type.equals("verse")) {
            return getVerses(linesList);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     *Get the document as a list of lines based on the input list of strings.
     *@param linesList The list of strings to process.
     *@return The list of lines.
     */
    private List<String> getLines(List<String> linesList) {
        Stream<String> lines = linesList.stream().filter(IndexCreatorService::replaceEmptyLines);
        index.setNumberOfDocuments((int) lines.count());

        return linesList.stream().filter(IndexCreatorService::replaceEmptyLines)
                .map(IndexCreatorService::eliminateSpecialChars)
                .map(IndexCreatorService::allToLowerCase)
                .collect(Collectors.toList());
    }

    /**
     * This method takes a list of lines and returns a list of verses by removing blank lines and
     * aggregating non-blank lines. It also sets the number of documents (here meaning verses) in the index.
     *
     * @param linesList the list of lines to be processed.
     * @return the list of verses.
     */
    private List<String> getVerses(List<String> linesList) {
        List<String> verseList = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        for (String line : linesList) {
            if (!line.isBlank()) {
                sb.append(line).append(" ");
            } else {
                verseList.add(sb.toString());
                sb = new StringBuilder();
            }
        }
        index.setNumberOfDocuments(verseList.size());
        return verseList.stream().filter(IndexCreatorService::replaceEmptyLines)
                .map(IndexCreatorService::eliminateSpecialChars)
                .map(IndexCreatorService::allToLowerCase)
                .collect(Collectors.toList());
    }

    /**
     * This method calculates the search terms from the lines by counting the number of words,
     * counting the number of distinct words, and normalizing the words to lowercase.
     * It also sets the number of total words and the number of different words in the index.
     *
     * @param linesList the list of lines to be processed.
     * @return the list of search terms.
     */
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

    /**
     * This method calculates the inverted list for a given vocabulary list and list of lines.
     * The inverted list maps each search term to the documents (meaining line or verse) where it appears.
     *
     * @param vocabularyList A list of search terms.
     * @param listOfLinesNotEmptyAndNoSpecialChars A list of lines without special characters.
     */
    private void calculateInvertedList(List<String> vocabularyList, List<String> listOfLinesNotEmptyAndNoSpecialChars) {
        // no of line or verse
        int documentNumber=1;
        List<String> wordsOfNormalizedLines = new LinkedList<>();

        // unfortunatelly three nested loops are neccessary as contains() has the same complexity
        // and would miss double occurences, also it is an easy way to keep trak of line/verse number no
        // as it has to be mapped to the terms in the text file

        // split lines/verses into words
        for (String line : listOfLinesNotEmptyAndNoSpecialChars) {
            wordsOfNormalizedLines = Arrays.asList(line.split(" "));
            // now match all words from the lines and verses with the searchTerms from index and add them if not already
            //saved as term or otherwise add count and line for the term matched with a word
            for (String word: wordsOfNormalizedLines) {
                for (String term : vocabularyList) {
                    if (word.equals(term)) {
                            if (index.getSearchTermByName(term) == null) {
                                Searchterm newTerm = new Searchterm(term);
                                index.getSearchtermList().add(newTerm);
                                newTerm.addToDocumentsWehreTermAppears(documentNumber);
                                index.getSearchTermByName(term).calculateNumberOfTermAppereances();
                            } else {
                                index.getSearchTermByName(term).addToDocumentsWehreTermAppears(documentNumber);
                                index.getSearchTermByName(term).calculateNumberOfTermAppereances();
                            }
                        }
                    }
            }
            documentNumber++;
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
