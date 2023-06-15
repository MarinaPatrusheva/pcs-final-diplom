import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatingWordSearchMap {
    private Map<String, List<PageEntry>> wordsCount = new HashMap<>();
    private File[] files;
    private List<String> keys = new ArrayList<>();

    public CreatingWordSearchMap(File fileDirectory) {
        setFileDirectory(fileDirectory);
    }

    public Map<String, List<PageEntry>> getWordsCount() throws IOException {
        setWordsFromPdfToMap();
        return wordsCount;
    }

    public List<String> getKeys() {
        return keys;
    }

    private void setWordsFromPdfToMap() throws IOException {
        for (int i = 0; i < files.length; i++) {
            PdfDocument pdfDocument = new PdfDocument(new PdfReader(files[i]));
            putMap(getMapPageEntryFromDocument(pdfDocument, files[i].getName()));
        }
    }

    private Map<String, List<PageEntry>> getMapPageEntryFromDocument(PdfDocument pdfDocument, String name) {
        Map<String, List<PageEntry>> pageEntryHashMap = new HashMap<>();
        int numberPages = pdfDocument.getNumberOfPages();
        for (int i = 0; i < numberPages; i++) {
            PdfPage page = pdfDocument.getPage(i + 1);
            Map<String, Integer> countWordsInPage = getCountWordsFromPage(page);
            List<String> keys = getKeyPage(page);
            for (int j = 0; j < countWordsInPage.size(); j++) {
                String word = keys.get(j);
                List<PageEntry> pageEntries;
                if (pageEntryHashMap.containsKey(word)) {
                    pageEntries = pageEntryHashMap.get(word);
                } else {
                    pageEntries = new ArrayList<>();
                }
                int count = countWordsInPage.get(word);
                pageEntries.add(new PageEntry(name, i + 1, count));
                pageEntryHashMap.put(word, pageEntries);
            }
        }
        return pageEntryHashMap;
    }

    private Map<String, Integer> getCountWordsFromPage(PdfPage page) {
        String[] textPageArray = getTextPage(page);
        Map<String, Integer> mapWordsCount = new HashMap<>();
        for (int i = 0; i < textPageArray.length; i++) {
            String word = textPageArray[i];
            addKey(word);
            if (mapWordsCount.containsKey(word.toLowerCase())) {
                mapWordsCount.put(word.toLowerCase(), mapWordsCount.get(word.toLowerCase()) + 1);
            } else {
                mapWordsCount.put(word.toLowerCase(), 1);
            }
        }
        return mapWordsCount;
    }

    private void putMap(Map<String, List<PageEntry>> map) {
        for (int i = 0; i < keys.size(); i++) {
            String word = keys.get(i);
            if (wordsCount.containsKey(word) & map.containsKey(word)) {
                List<PageEntry> pageEntries = wordsCount.get(word);
                pageEntries.addAll(map.get(word));
                wordsCount.put(word, pageEntries);
            }
            if (!wordsCount.containsKey(word) & map.containsKey(word)) {
                wordsCount.put(word, map.get(word));
            }
        }
    }

    private List<String> getKeyPage(PdfPage page) {
        String[] textPageArray = getTextPage(page);
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < textPageArray.length; i++) {
            String word = textPageArray[i];
            if (!keys.contains(word.toLowerCase())) {
                keys.add(word.toLowerCase());
            }
        }
        return keys;
    }

    private void addKey(String key) {
        if (!keys.contains(key.toLowerCase())) {
            keys.add(key.toLowerCase());
        }
    }

    private String[] getTextPage(PdfPage page) {
        String textPage = PdfTextExtractor.getTextFromPage(page);
        String[] textPageArray = textPage.split("\\P{IsAlphabetic}+");
        return textPageArray;
    }

    private void setFileDirectory(File directory) {
        files = directory.listFiles();
    }
}
