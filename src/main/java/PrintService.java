import java.util.*;

/**
 * This class is responsible for printing the parameters and inverted list of the given index.
 * The class is instantiated with the Index object and contains two methods for printing.
 *
 */
public class PrintService {
    Index index;
    int lineCounter;

    public PrintService(Index resultIndex) {
        this.index=resultIndex;
        lineCounter=1;
    }


    public void printParametersAkaKenngroessen(String granularity) {
       System.out.println("N is: " + index.getNumberOfDocuments() + " (Number of " + granularity + "s)");
       System.out.println("F is: " + index.getNumberOfTotalWords() + " (Number of Words in total)");
       System.out.println("n is: " + index.getNumberOfDifferentWords() + " (Number of different Words)");
       System.out.println("f is: " + index.getNumberOfTermDocumentAssociations() + " (Number of Term to Document Associations)");
       System.out.println("Verweisdichte is: " + index.getVerweisdichte() + "(  Associations/(Documents * totalWords) )" );
    }

    public void printInvertedList() {
      Map<String, List<Integer>> resultMap = index.getSearchTermMap();
      // get keys from HashMap as List to sort
        List<String>  resultList = new ArrayList<>(resultMap.keySet());
        // alphabetically sort the terms/keys from the map
                resultList.sort(String::compareTo);

        List<Integer> numberList;
         for(String searchTerm: resultList) {

             numberList = resultMap.get(searchTerm);
             int noOfAppearances = numberList.size();

             StringBuilder builder = new StringBuilder();
             for (int value : numberList) {
                 builder.append(value + ", ");
             }
             // delete last "," so it does not show in results printout
            builder.deleteCharAt(builder.length()-2);
            String text = builder.toString();
            text = text.trim();

            System.out.println(lineCounter + " " + searchTerm + " [" + noOfAppearances + "; " + text + "]");
            lineCounter++;
        }

    }
}
