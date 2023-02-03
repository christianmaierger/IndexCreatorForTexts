import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * The Index class holds information about the search terms, number of documents,
 * total number of words, number of different words, and number of term-document associations
 * in a search engine.
 * It also calculates the `verweisdichte` which is the ratio of term-document associations to the product of
 * the number of documents and number of different words.
 */
public class Index {

    private long numberOfDocuments=0;
    private long numberOfTotalWords=0;
    private long numberOfDifferentWords=0;
    private long numberOfTermDocumentAssociations=0;
    List<Searchterm> searchtermList = new LinkedList<>();
    private double Verweisdichte = 0;



    public Index() {
    }

    public long getNumberOfDocuments() {
        return numberOfDocuments;
    }

    public void setNumberOfDocuments(long numberOfDocuments) {
        this.numberOfDocuments = numberOfDocuments;
    }

    public long getNumberOfTotalWords() {
        return numberOfTotalWords;
    }

    public void setNumberOfTotalWords(long numberOfTotalWords) {
        this.numberOfTotalWords = numberOfTotalWords;
    }

    public long getNumberOfDifferentWords() {
        return numberOfDifferentWords;
    }

    public void setNumberOfDifferentWords(long numberOfDifferentWords) {
        this.numberOfDifferentWords = numberOfDifferentWords;
    }

    public long getNumberOfTermDocumentAssociations() {
        return numberOfTermDocumentAssociations;
    }

    public void setNumberOfTermDocumentAssociations(int numberOfTermDocumentAssociations) {
        this.numberOfTermDocumentAssociations = numberOfTermDocumentAssociations;
    }

    public void setNumberOfTermDocumentAssociations(long numberOfTermDocumentAssociations) {
        this.numberOfTermDocumentAssociations = numberOfTermDocumentAssociations;
    }


    public double getVerweisdichte() {
        return Verweisdichte;
    }

    public void setVerweisdichte(double verweisdichte) {
        Verweisdichte = verweisdichte;
    }

    public void calculateVerweisdichte() {
        long tmp = this.getNumberOfDocuments()*this.getNumberOfDifferentWords();
        long tmp2= this.getNumberOfTermDocumentAssociations();
        double temp3 = (double)tmp2/tmp;
        this.setVerweisdichte(temp3);
    }

    public Searchterm getSearchTermByName(String name) {
            for (Searchterm term: searchtermList) {
                if(term.getTerm().equals(name)) {
                    return term;
                }
            }
        return null;}

    public List<Searchterm> getSearchtermList() {
        return searchtermList;
    }

    public void setSearchtermList(List<Searchterm> searchtermList) {
        this.searchtermList = searchtermList;
    }
}

