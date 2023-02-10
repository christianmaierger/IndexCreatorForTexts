import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
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
     * @param removeLines boolean to indicate if empty lines should be removed and not counted.
     * @return The created index.
     *
     */
    public Index createIndexForDocument(boolean removeLines, boolean dgaps ) throws IOException {
            List<String> linesList = readFile(file);
            // List of verses or lines with empty lines removed or not in case of lines
            List<String> documentList = getDocument(linesList, type, removeLines);
            // list of all distinct words from the text file
            calculateSearchTermsNumber(documentList);
            Map<String, List<Integer>> listOfWordsWithDocumentNumbersAppended = createMapOfWordsAndDocumentNumbers(documentList);
            // calculates a so called inverted list that holds all words and their occurances in the text
            calculateInvertedList(listOfWordsWithDocumentNumbersAppended, dgaps);

            return index;

    }

    /**
     * Reads a .txt file into a list of strings.
     * @param file The file to read.
     * @return The list of strings.
     */
    private List<String> readFile(Path file) {
        try {
            String fileExtension = file.getFileName().toString().toLowerCase().substring(file.getFileName().toString().lastIndexOf(".") + 1);
            if (fileExtension.equals("txt")) {
                return Files.readAllLines(file, StandardCharsets.UTF_8);
            } else if (fileExtension.equals("doc") || fileExtension.equals("docx")) {
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

    /**
     * Reads a word file (.docx/doc) into a list of strings.
     * @param file The file to read.
     * @return The list of strings.
     */
    private List<String> readWordFile(Path file) {
        List<String> lines = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file.toFile())) {
            XWPFDocument docx = new XWPFDocument(fis);
            StringBuilder lineBuilder = new StringBuilder();
            List<XWPFParagraph> paragraphs = docx.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                lineBuilder.append(paragraph.getText());
            }
            String[] splitLines = lineBuilder.toString().split("\n");
            lines = Arrays.asList(splitLines);
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
    private List<String> getDocument(List<String> linesList, String type, boolean removeEmptyLines) {

        linesList = removeTrailingEmptyLines(linesList);

        List<String> finalLinesList = linesList;
        Supplier<Stream<String>> linesStream = () -> finalLinesList.stream().map(IndexCreatorService::eliminateSpecialChars)
                .map(IndexCreatorService::allToLowerCase);

        List<String> resultList = new LinkedList<>();
        if (type.equals("Line")) {
            resultList = getLinesAndSetNumberOFDocuments(linesStream, removeEmptyLines);
        } else if (type.equals("Verse")) {
            resultList = getVersesAndSetNumberOfDocuments(linesStream);
        }

        return resultList;
    }



    /**
     *Get the document as a list of lines based on the input list of strings.
     *@param lines The list of strings to process.
     *@return The list of lines.
     */
    private List<String> getLinesAndSetNumberOFDocuments(Supplier<Stream<String>> lines, boolean removeEmptyLines) {

        if (removeEmptyLines) {
            index.setNumberOfDocuments((int) lines.get().filter(IndexCreatorService::replaceEmptyLines).count());
        } else {
            index.setNumberOfDocuments((int) lines.get().count());
        }
        return lines.get().collect(Collectors.toList());
    }

    /**
     * This method takes a list of lines and returns a list of verses by removing blank lines and
     * aggregating non-blank lines. It also sets the number of documents (here meaning verses) in the index.
     *
     * @param lines the list of lines to be processed.
     * @return the list of verses.
     */
    private List<String> getVersesAndSetNumberOfDocuments(Supplier<Stream<String>> lines) {

        List<String> verseList = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        // for each line it is checked if blank or not
        lines.get().forEach(line -> {
            // trim to remove trailing whitespaces
            line = line.trim();
            // if line is not blank it gets appended to stringbuilder delimetered by " "
            if (!line.isBlank()) {
                sb.append(line).append(" ");
                // if a blank line comes, it means verse is over and concattenated string will be added to verseList
            } else if (!sb.isEmpty()) {
                // trim again to get rid of last appended " " and add whole verse to verseList
                verseList.add(sb.toString().trim());
                // stringbuilder is deleted, so next verse can be put together starting with an empty string
                sb.delete(0, sb.length()-1);
            }
        });
        // if last line is not blank, the verse in the strinbuilder has to be still added to list
        if (sb.toString().trim().length() > 0) {
            verseList.add(sb.toString().trim());
        }

        index.setNumberOfDocuments((int) verseList.size());
        return verseList;
    }

    /**
     * This method calculates the search terms from the lines by counting the number of words,
     * counting the number of distinct words, and normalizing the words to lowercase.
     * It also sets the number of total words and the number of different words in the index.
     *
     * @param documentList the list of lines to be processed.
     * @return the list of search terms.
     */
    private void calculateSearchTermsNumber(List<String> documentList) {
        long wordCount = documentList.stream()
                .filter(IndexCreatorService::replaceEmptyLines)
                .flatMap(line -> Arrays.stream(line.split(" ")))
                .count();

        index.setNumberOfTotalWords(wordCount);
    }

    /**
     * Creates a list of words with document numbers appended.
     * @param documentList The list of lines or verses.
     * @return The list of words with document numbers appended.
     */
    private Map<String, List<Integer>> createMapOfWordsAndDocumentNumbers(List<String> documentList) {

        Map<String, List<Integer>> wordsAndDocumentNumbersMap = new HashMap<>();
        for (int lineIndex = 0; lineIndex < documentList.size(); lineIndex++) {
            String line = documentList.get(lineIndex);
            String[] words = line.split(" ");
            for (String word : words) {
                if (word != "" && word.length() !=0) {
                    List<Integer> lineNumbers = wordsAndDocumentNumbersMap.getOrDefault(word, new ArrayList<>());
                    if (!lineNumbers.contains(lineIndex + 1)) {
                        lineNumbers.add(lineIndex + 1);
                        wordsAndDocumentNumbersMap.put(word, lineNumbers);
                    }
                }
            }
        }
        index.setNumberOfDifferentWords(wordsAndDocumentNumbersMap.size());
        return wordsAndDocumentNumbersMap;
    }

    /**
     * This method calculates the inverted list for a given vocabulary list and list of lines.
     * The inverted list maps each search term to the documents (meaining line or verse) where it appears.
     *
     * @param documentMap A Map of terms without special characters and their document numbers, lines/verses where they can be found.
     */
    private void calculateInvertedList(Map<String, List<Integer>> documentMap, boolean dgaps) {

        // if user wants dgaps meaning distance between terms instead of their line/verse numbers
        // then here the first document number is left to be and then the indexes are subtracted to calculate "distance"
        if (dgaps) {
            for (Map.Entry<String, List<Integer>> entry : documentMap.entrySet()) {
                List<Integer> values = entry.getValue();
                for (int i = 1; i < values.size(); i++) {
                    values.set(i, values.get(i) - values.get(i - 1));
                }
            }

        }

        index.setSearchTermMap(documentMap);
        AtomicInteger i = new AtomicInteger();
        int sum =0;
        for (List <Integer> list : index.getSearchTermMap().values()) {
            sum+=  list.size();
        }
        // ratio of term-document associations to the product of the number of documents and number of different words.
        index.setNumberOfTermDocumentAssociations(sum);
        index.calculateVerweisdichte();
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

    private static List<String> removeTrailingEmptyLines(List<String> list) {
        int index = list.size() - 1;
        int indexFromStart = 0;
        while (index >= 0 && list.get(index).isEmpty()) {
            list.remove(index--);
        }
        while (indexFromStart < list.size() && list.get(index).isEmpty()) {
            list.remove(index++);
        }
        return list;
    }

}
