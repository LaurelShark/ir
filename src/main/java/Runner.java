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
        InvertedIndex index = new InvertedIndex();
        List<String> indexDocs = index.indexSearch("Cool OR is", dictionary);
        System.out.println(indexDocs);
        List<String> indexDocs1 = index.indexSearch("Is AND file", dictionary);
        System.out.println(indexDocs1);
        List<String> indexDocs2 = index.indexSearch("Is AND NOT file", dictionary);
        System.out.println(indexDocs2);
        List<String> indexDocs3 = index.indexSearch("Is OR NOT file", dictionary);
        System.out.println(indexDocs3);
    }
}
