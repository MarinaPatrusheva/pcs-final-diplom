import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchMap {
    private File fileDirectory;

    public SearchMap(File fileDirectory) {
        this.fileDirectory = fileDirectory;
    }

    public Map<String, List<PageEntry>> getSearchMap() {
        CreatingWordSearchMap searchMap = new CreatingWordSearchMap(fileDirectory);
        try {
            return searchMap.getWordsCount();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, List<PageEntry>> getModSearchMap() {
        CreatingModWordSearchMap searchMap = new CreatingModWordSearchMap(fileDirectory);
        try {
            return searchMap.getModWordsCount();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
