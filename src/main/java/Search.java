import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Search {

    public List<String> matrixSearch(String toSearch, List<List<String>> matrix) {
        List<String> documents = new ArrayList<>();
        if (!Iterables.isEmpty(matrix.get(0))) {
            documents = getDocuments(toSearch, matrix);
        }
        return documents;
    }

    // presume that we have validation (operators isn't on first or last position)
    private List<String> getDocuments(String search, List<List<String>> matrix) {
        List<String> words = getWords(search);
        List<String> vectorsAndOr = getVectorsAndOr(words, matrix);
        String documentVector = reduceStatement(vectorsAndOr);
        return getDocumentList(documentVector, matrix);
    }

    private List<String> getDocumentList(String documentVector, List<List<String>> matrix) {
        List<String> docList = new ArrayList<>();
        for (int i = 0; i < documentVector.length(); i++) {
            if (documentVector.charAt(i) == '1') {
                docList.add(matrix.get(i + 1).get(0));
            }
        }
        return docList;
    }

    private String reduceStatement(List<String> statement) {
        String currentResult = statement.get(0);
        for (int i = 0; i < statement.size(); i++) {
            StringBuilder tempRes = new StringBuilder();
            if (statement.get(i).equals("AND")) {
                String nextVector = statement.get(i + 1);
                for (int j = 0; j < nextVector.length(); j++) {
                    if (!(currentResult.charAt(j) == '1' && nextVector.charAt(j) == '1')) {
                        tempRes.append('0');
                    } else {
                        tempRes.append('1');
                    }
                }
                currentResult = tempRes.toString();
            }
            if (statement.get(i).equals("OR")) {
                String nextVector = statement.get(i + 1);
                for (int j = 0; j < nextVector.length(); j++) {
                    if (!(currentResult.charAt(j) == '0' && nextVector.charAt(j) == '0')) {
                        tempRes.append('1');
                    } else {
                        tempRes.append('0');
                    }
                }
                currentResult = tempRes.toString();
            }
        }
        return currentResult;
    }

    private List<String> getVectorsAndOr(List<String> words, List<List<String>> matrix) {
        List<String> vectors = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            String vector = null;
            if (!(word.equals("OR") || word.equals("AND") || word.equals("NOT"))) {
                String lowerWord = word.toLowerCase();
                vector = buildVector(lowerWord, matrix);
            } else if ("NOT".equals(word)) {
                vector = inverseVector(buildVector(vectors.get(i = i + 1), matrix));
            } else {
                vector = word;
            }
            vectors.add(vector);
        }
        return vectors;
    }

    private String inverseVector(String vector) {
        StringBuilder inverse = new StringBuilder();
        for (int i = 0; i < vector.toCharArray().length; i++) {
            if (vector.charAt(i) == '1') {
                inverse.append('0');
            } else {
                inverse.append('1');
            }
        }
        return inverse.toString();
    }

    private List<String> getWords(String search) {
        List<String> words = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(search);
        while (matcher.find()) {
            String word = matcher.group();
            words.add(word);
        }
        return words;
    }

    private String buildVector(String word, List<List<String>> matrix) {
        int wordPosition = matrix.get(0).indexOf(word);
        StringBuilder vector = new StringBuilder();
        for (int i = 1; i < matrix.size(); i++) {
            vector.append(matrix.get(i).get(wordPosition));
        }
        return vector.toString();
    }
}
