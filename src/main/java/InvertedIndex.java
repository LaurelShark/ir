import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InvertedIndex {

    public List<String> indexSearch(String toSearch, TreeMap<String, Integer> dictionary) {
        Map<String, Integer> docToId = createDocumentIdsMap();
        Map<Map<String, Integer>, List<Integer>> invertedIndex = new HashMap<>();
        Dictionary dict = new Dictionary();
        // lexicographical order is saved
        docToId.forEach((docPath, docId) -> {
            try {
                Map<String, Integer> fileMap = dict.getFileMap(docPath);
                List<String> mapIntersection = dictionary.keySet().stream()
                        .filter(fileMap.keySet()::contains)
                        .collect(Collectors.toList());
                mapIntersection.forEach(commonWord -> {
                    HashMap<String, Integer> wordToFreq = new HashMap<>();
                    wordToFreq.put(commonWord, dictionary.get(commonWord));
                    if (invertedIndex.keySet().stream().noneMatch(map -> map.containsKey(commonWord))) {
                        List<Integer> docIdsList = new ArrayList<>();
                        docIdsList.add(docId);
                        invertedIndex.put(wordToFreq, docIdsList);
                    } else {
                        invertedIndex.get(wordToFreq).add(docId);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        FileUtils fileUtils = new FileUtils();
        List<String> searchWords = fileUtils.getWords(toSearch);
        Set<Integer> resultDocIds = searchInIndex(searchWords, invertedIndex);
        return docToId.entrySet().stream()
                .filter(entry -> resultDocIds.contains(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private Set<Integer> searchInIndex(List<String> words, Map<Map<String, Integer>, List<Integer>> index) {
        Set<Integer> total = new HashSet<>();
        Map<String, List<Integer>> termToDocIds = createTermDocIdsMap(words, index);
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            if (!(word.equals("OR") || word.equals("AND") || word.equals("NOT"))) {
                total.addAll(termToDocIds.values().stream()
                                     .flatMap(list -> list.stream())
                                     .collect(Collectors.toList()));
            }
            if (word.equals("OR")) {
                if (!words.get(i + 1).equals("NOT")) {
                    String nextTerm = words.get(i = i + 1).toLowerCase();
                    total = Sets.union(termToDocIds.entrySet().stream()
                    .filter(elem -> elem.getKey().equals(nextTerm))
                    .flatMap(entry -> entry.getValue().stream())
                    .collect(Collectors.toSet()), total);
                } else {
                    String termToNegate = words.get(i = i + 2).toLowerCase();
                    Set<Integer> notNegatedDocIds = termToDocIds.entrySet().stream()
                            .filter(elem -> elem.getKey().equals(termToNegate))
                            .flatMap(entry -> entry.getValue().stream())
                            .collect(Collectors.toSet());
                    Set<Integer> negated = Sets.difference(index.values().stream()
                                                                   .flatMap(entry -> entry.stream())
                                                                   .collect(Collectors.toSet()), notNegatedDocIds);
                    total = Sets.union(negated, total);
                }
            }
            if (word.equals("AND")) {
                if (!words.get(i + 1).equals("NOT")) {
                    String nextTerm = words.get(i = i + 1).toLowerCase();
                    total = Sets.intersection(termToDocIds.entrySet().stream()
                                               .filter(elem -> elem.getKey().equals(nextTerm))
                                               .flatMap(entry -> entry.getValue().stream())
                                               .collect(Collectors.toSet()), total);
                } else {
                    String termToNegate = words.get(i = i + 2).toLowerCase();
                    Set<Integer> notNegatedDocIds = termToDocIds.entrySet().stream()
                            .filter(elem -> elem.getKey().equals(termToNegate))
                            .flatMap(entry -> entry.getValue().stream())
                            .collect(Collectors.toSet());
                    Set<Integer> negated = Sets.difference(index.values().stream()
                                                                   .flatMap(entry -> entry.stream())
                                                                   .collect(Collectors.toSet()), notNegatedDocIds);
                    total = Sets.intersection(negated, total);
                }
            }
        }
        return total;
    }

    private Map<String, List<Integer>> createTermDocIdsMap(List<String> words, Map<Map<String, Integer>, List<Integer>> index) {
        Map<String, List<Integer>> res = new HashMap<>();
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            if (!(word.equals("OR") || word.equals("AND") || word.equals("NOT"))) {
                res.put(word.toLowerCase(), index.entrySet().stream()
                        .filter(indexEntry -> indexEntry.getKey().containsKey(word.toLowerCase()))
                        .flatMap(indexEntry -> indexEntry.getValue().stream())
                        .collect(Collectors.toList()));
            }
        }
        return res;
    }

    private Map<String, Integer> createDocumentIdsMap() {
        FileUtils fileUtils = new FileUtils();
        List<String> docs = fileUtils.getFileNames().stream().sorted().collect(Collectors.toList());
        List<Integer> docIds = IntStream.rangeClosed(1, docs.size()).boxed().collect(Collectors.toList());
        return IntStream.range(0, docs.size()).boxed().collect(Collectors.toMap(docs::get, docIds::get));
    }

}
