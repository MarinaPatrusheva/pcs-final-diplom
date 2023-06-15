import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatingModWordSearchMap {
    private CreatingWordSearchMap searchMap;
    private List<String> keys;

    public CreatingModWordSearchMap(File fileDirectory) {
        searchMap = new CreatingWordSearchMap(fileDirectory);
        keys = searchMap.getKeys();
    }

    public Map<String, List<PageEntry>> getModWordsCount() throws IOException {
        return setModWordsCount();
    }

    private Map<String, List<PageEntry>> setModWordsCount() throws IOException {
        Map<String, List<PageEntry>> modWordsCount = new HashMap<>();
        Map<String, List<PageEntry>> wordsCount = searchMap.getWordsCount();
        for (int i = 0; i < keys.size(); i++) {
            String word = keys.get(i);
            List<PageEntry> pageEntries = wordsCount.get(word);
            modWordsCount.put(word, createCopyWithMod(pageEntries, getCount(pageEntries)));
        }
        return modWordsCount;
    }

    private List<PageEntry> createCopyWithMod(List<PageEntry> pageEntries, int count) {
        List<PageEntry> pageEntriesWithMod = new ArrayList<>();
        for (int j = 0; j < pageEntries.size(); j++) {
            PageEntry pageEntry = pageEntries.get(j);
            pageEntriesWithMod.add(new PageEntry(pageEntry.getPdfName(), pageEntry.getPage(), count));
        }
        return pageEntriesWithMod;
    }

    private int getCount(List<PageEntry> pageEntries) {
        int count = 0;
        for (int j = 0; j < pageEntries.size(); j++) {
            count += pageEntries.get(j).getCount();
        }
        return count;
    }
}
