import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Searchterm {
  String term;
  int numberOfAppereances=0;
  List<Integer> documentsByNumberWhereTermAppears= new LinkedList<>();
   List<Integer> dGaps= new LinkedList<>();

    /**
     * A class representing a search term in a collection of documents.
     *
     * The class stores information about the term, the number of times it appears,
     * the documents where it appears, and if wanted by the User the gaps between those documents.
     */
    public Searchterm(String term) {
        this.term = term;
    }


    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getNumberOfAppereances() {
        return numberOfAppereances;
    }

    public void setNumberOfAppereances(int numberOfAppereances) {
        this.numberOfAppereances = numberOfAppereances;
    }

    public List<Integer> getDocumentsByNumberWhereTermAppears() {
        return documentsByNumberWhereTermAppears;
    }

    public void addToDocumentsWehreTermAppears(int documentNumber) {
        if (!this.documentsByNumberWhereTermAppears.contains(documentNumber)) {
            this.documentsByNumberWhereTermAppears.add(documentNumber);
        }

    }


    public void calculateNumberOfTermAppereances() {
            this.setNumberOfAppereances(getDocumentsByNumberWhereTermAppears().size());
    }

    public List<Integer> getdGaps() {
        return dGaps;
    }

    public void setdGaps(List<Integer> dGaps) {
        this.dGaps = dGaps;
    }


    /**
     * Calculates the difference in document numbers between each appearance of the term.
     * The difference is stored in the `dGaps` list.
     */
    public void calculateDGaps () {

            Integer[] documentNumbers = new Integer[this.getDocumentsByNumberWhereTermAppears().size()];
             documentNumbers = this.getDocumentsByNumberWhereTermAppears().toArray(documentNumbers);

                Integer[] tmp = new Integer[this.getDocumentsByNumberWhereTermAppears().size()];


        tmp = this.getDocumentsByNumberWhereTermAppears().toArray(documentNumbers);

        Integer[] tmp2 = Arrays.copyOf(tmp, tmp.length);

             for (int i=1; i< documentNumbers.length; i++) {
                 documentNumbers[i] = tmp2[i] - tmp2[i - 1];
             }
             dGaps = Arrays.asList(documentNumbers);
    }

}

