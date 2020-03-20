import java.util.List;
import java.util.TreeMap;

public class Runner {
    public static void main(String[] args) {
        Dictionary dict = new Dictionary();
        TreeMap<String, Integer> dictionary = dict.createDictionary();
        Matrix matrix = new Matrix();
        List<List<String>> incMatrix =  matrix.createMatrix(dictionary);
        Search search = new Search();
        List<String> docs = search.matrixSearch("Cool OR is", incMatrix);
        System.out.println(docs);
    }
}
