import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Matrix {

    public static final String RESOURCES_DIR = "src/main/resources";

    public static final String TEST_RESOURCES_DIR = "src/main/resources/test";

    public static final String MATRIX_FILE = "src/main/resources/output/matrix.txt";

    public List<List<String>> createMatrix(TreeMap<String, Integer> dict) {
        List<String> fileNames = getFileNames();
        List<List<String>> incidenceMatrix = makeEdges(fileNames);
        List<List<String>> matrix = null;
        try {
            matrix = initMatrix(incidenceMatrix, fileNames, dict.keySet());
            System.out.println(incidenceMatrix);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matrix;
    }

    private List<List<String>> initMatrix(List<List<String>> matrix, List<String> filenames, Set<String> words) throws IOException {
        Dictionary dict = new Dictionary();
        int partCounter = 1;
        matrix.get(0).addAll(words);
        for (String name : filenames) {
            TreeMap<String, Integer> fileDictionary = dict.getFileMap(name);
            int finalPartCounter = partCounter;
            words.forEach(word -> {
                              if (fileDictionary.containsKey(word)) {
                                  matrix.get(finalPartCounter).add("1");
                              } else {
                                  matrix.get(finalPartCounter).add("0");
                              }
                          }
            );
            partCounter++;
        }
        flushMatrix(matrix);
        return matrix;
    }

    private void flushMatrix(List<List<String>> toFlush) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MATRIX_FILE))) {
            writer.write(toFlush.toString());
        }
    }

    private List<List<String>> makeEdges(List<String> fileNames) {
        List<List<String>> matrix = new ArrayList<>();
        List<String> words = new ArrayList<>();
        words.add(" ");
        matrix.add(words);
        for (String file : fileNames) {
            List<String> fileList = new ArrayList<>();
            fileList.add(file);
            matrix.add(fileList);
        }
        return matrix;
    }

    private List<String> getFileNames() {
        File dir = new File(TEST_RESOURCES_DIR);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        return Arrays.stream(files).map(File::toString).collect(Collectors.toList());
    }

}
