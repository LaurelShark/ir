import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileUtils {

    public static final String TEST_RESOURCES_DIR = "src/main/resources/test";

    public List<String> getFileNames() {
        File dir = new File(TEST_RESOURCES_DIR);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        return Arrays.stream(files).map(File::toString).collect(Collectors.toList());
    }

    public List<String> getWords(String search) {
        List<String> words = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(search);
        while (matcher.find()) {
            String word = matcher.group();
            words.add(word);
        }
        return words;
    }
}
