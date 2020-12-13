import java.util.LinkedList;
import java.util.List;

public class Searchterm {
  String term;
  int numberOfAppereances=0;
  List<Integer> documentsByNumberWhereTermAppears= new LinkedList<>();

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



}
