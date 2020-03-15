import com.google.common.collect.Sets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dictionary {

    public static final String FILE_PATTERN = "src/main/resources/Part%s.txt";

    public static final String STATISTIC_FILE = "src/main/resources/output/statistics.txt";

    public static final String DICTIONARY_FILE = "src/main/resources/output/dictionary.txt";

    public static final String SERIALIZATION_FILE = "src/main/resources/output/dict.ser";

    public static final String DICT_INFO = "Dict from %s has %s words and it's size is approximately %s bytes. \n";

    public void createDictionary() {
        try {
            TreeMap<String, Integer> dict = new TreeMap<>();
            for (int i = 1; i < 11; i++) {
                TreeMap<String, Integer> fileMap = getFileMap(String.format(FILE_PATTERN, i));
                TreeMap<String, Integer> dictCopy = new TreeMap<>(dict);
                dict = mergeMaps(dictCopy, fileMap);
            }
            writeDictAsText(dict);
            writeStatistics("all files", dict.size(), 40 * dict.size());
            writeBinary(dict);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TreeMap<String, Integer> getFileMap(String path) throws IOException {
        TreeMap<String, Integer> wordToOccurence = new TreeMap<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {
            String line;
            Pattern pattern = Pattern.compile("\\w+");
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String word = matcher.group();
                    wordToOccurence.put(word.toLowerCase(), wordToOccurence.containsKey(word) ? wordToOccurence.get(word) + 1 : 1);
                }
            }
            int mapSize = wordToOccurence.size();
            writeStatistics(path, mapSize, 40 * mapSize);
        }
        return wordToOccurence;
    }

    private void writeStatistics(String file, int size, int bytes) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STATISTIC_FILE, true))) {
            writer.write(String.format(DICT_INFO, file, size, bytes));
        }
    }

    private TreeMap<String, Integer> mergeMaps(TreeMap<String, Integer> m1, TreeMap<String, Integer> m2) {
        TreeMap<String, Integer> result = new TreeMap<>();
        Set<String> secondMap = Sets.difference(m2.keySet(), m1.keySet());
        for (String word : m1.keySet()) {
            if (m2.containsKey(word)) {
                result.put(word, m1.get(word) + m2.get(word));
            } else {
                result.put(word, m1.get(word));
            }
        }
        for (String word : secondMap) {
            if (!result.containsKey(word)) {
                result.put(word, m2.get(word));
            }
        }
        return result;
    }

    private void writeDictAsText(TreeMap<String, Integer> map) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DICTIONARY_FILE))) {
            writer.write(String.valueOf(map));
        }
    }

    private void writeBinary(TreeMap<String, Integer> map) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(SERIALIZATION_FILE)) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
        }
    }

}
