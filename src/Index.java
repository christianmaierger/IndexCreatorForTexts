import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Index {

    private long numberOfDocuments=0;
    private long numberOfTotalWords=0;
    private long numberOfDifferentWords=0;
    private long numberOfTermDocumentAssociations=0;
    List<Searchterm> searchtermList = new LinkedList<>();



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

