import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private HashMap<String, List<PageEntry>> wordsCount;
    private List<String> ignoreList;

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        CreatingIgnoreList ignore = new CreatingIgnoreList();
        ignoreList = ignore.getIgnoreList();
        SearchMap searchMap = new SearchMap(pdfsDir);
        wordsCount = searchMap.getModSearchMap();
    }

    @Override
    public List<PageEntry> search(String word) {
        List<String> listWords = getSearchWithoutIgnoreWords(word);
        List<PageEntry> pageEntriesMod = new ArrayList<>();
        for (int i = 0; i < listWords.size(); i++) {
            if (wordsCount.containsKey((listWords.get(i)))) {
                List<PageEntry> pageEntries = wordsCount.get(listWords.get(i));
                pageEntriesMod.addAll(pageEntries);
            }
        }
        Collections.sort(pageEntriesMod);
        return pageEntriesMod;
    }

    private List<String> getSearchWithoutIgnoreWords(String word) {
        List<String> searchWords = Arrays.asList(word.split("\\P{IsAlphabetic}+"));
        for (int i = 0; i < searchWords.size(); i++) {
            if (ignoreList.contains(searchWords.get(i))) {
                searchWords.remove(i);
            }
        }
        return searchWords;
    }
}
