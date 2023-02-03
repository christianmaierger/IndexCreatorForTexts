import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
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


    public void printParametersAkaKenngrößen() {
       System.out.println("N is: " + index.getNumberOfDocuments());
       System.out.println("F is: " + index.getNumberOfTotalWords());
       System.out.println("n is: " + index.getNumberOfDifferentWords());
       System.out.println("f is: " + index.getNumberOfTermDocumentAssociations());
       System.out.println("Verweisdichte is: " + index.getVerweisdichte());
    }

    public void printInvertedList(boolean gaps) {
      List<Searchterm> resultList = index.getSearchtermList();

        resultList.sort(new Comparator<>() {
            public int compare(Searchterm q1, Searchterm q2) {
                return q1.getTerm().compareTo(q2.getTerm());
            }
        } );

        List<Integer> numberList;

         for(Searchterm term: resultList) {
             if(!gaps) {
                 numberList = term.getDocumentsByNumberWhereTermAppears();
             } else {
                 term.calculateDGaps();
                 numberList = term.getdGaps();
             }

             StringBuilder builder = new StringBuilder();
             for (int value : numberList) {
                 builder.append(value + ", ");
             }
             // delete last ,
            builder.deleteCharAt(builder.length()-2);
            String text = builder.toString();
            text = text.trim();

            System.out.println(lineCounter + " " + term.getTerm() + " [" + term.getNumberOfAppereances() + "; " + text + "]");
            lineCounter++;
        }

    }
}
