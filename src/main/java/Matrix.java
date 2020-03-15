import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Matrix {

    public void createMatrix() {
        List<String> fileNames = getFileNames();
        List<List<String>> incidenceMatrix = makeEdges(fileNames);
        System.out.println(incidenceMatrix);
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
        File dir = new File("src/main/resources");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        return Arrays.stream(files).map(File::toString).collect(Collectors.toList());
    }

}
