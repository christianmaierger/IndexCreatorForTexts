import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

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
        StringBuilder builder = new StringBuilder();
        builder.append("N is: " + index.getNumberOfDocuments() + " (Number of " + granularity + "s)" + System.lineSeparator());
        builder.append("F is: " + index.getNumberOfTotalWords() + " (Number of Words in total)" + System.lineSeparator());
        builder.append("n is: " + index.getNumberOfDifferentWords() + " (Number of different Words)" + System.lineSeparator());
        builder.append("f is: " + index.getNumberOfTermDocumentAssociations() + " (Number of Term to Document Associations)" + System.lineSeparator());
        builder.append("Verweisdichte is: " + index.getVerweisdichte() + " (Associations/(Documents * totalWords))" + System.lineSeparator());
        this.printToPopUp(builder);
    }

    public void printInvertedList() {
      Map<String, List<Integer>> resultMap = index.getSearchTermMap();
      // get keys from HashMap as List to sort
        List<String>  resultList = new ArrayList<>(resultMap.keySet());
        // alphabetically sort the terms/keys from the map
                resultList.sort(String::compareTo);
        StringBuilder builder2 = new StringBuilder();
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

            builder2.append(lineCounter + " " + searchTerm + " [" + noOfAppearances + "; " + text + "]" + System.lineSeparator());
            lineCounter++;
        }
        this.printToPopUp(builder2);
    }

    private void printToPopUp(StringBuilder builder) {
        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        textArea.setText(builder.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JOptionPane.showMessageDialog(null, scrollPane, "Inverted List", JOptionPane.INFORMATION_MESSAGE);
    }
}
