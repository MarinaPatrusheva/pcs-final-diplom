import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private ArrayList<String> fileNames = new ArrayList<>();
    private HashMap<String, ArrayList<PageEntry>> wordsCount = new HashMap<>();
    private ArrayList<String> ignoreList = new ArrayList<>();
    private HashMap<String, ArrayList<PageEntry>> wordsCountMod = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        setWordsFromPdfToMap(pdfsDir);
        createIgnoreList();
        wordsCountMod = setModWordsCount();
    }

    @Override
    public List<PageEntry> search(String word) {
        List<String> listWords = getSearchWithoutIgnoreWords(word);
        ArrayList<PageEntry> pageEntriesMod = new ArrayList<>();
        for (int i = 0; i < listWords.size(); i++) {
            if (wordsCountMod.get(listWords.get(i)) != null) {
                ArrayList<PageEntry> pageEntries = wordsCountMod.get(listWords.get(i));
                pageEntriesMod.addAll(pageEntries);
            }
        }
        Collections.sort(pageEntriesMod);
        return pageEntriesMod;
    }

    private HashMap<String, ArrayList<PageEntry>> setModWordsCount() {
        HashMap<String, ArrayList<PageEntry>> modWordsCount = new HashMap<>();
        for (int i = 0; i < wordsCount.size(); i++) {
            String word = getKey(wordsCount, i);
            ArrayList<PageEntry> pageEntries = wordsCount.get(word);
            modWordsCount.put(word, createCopyWithMod(pageEntries, getCount(pageEntries)));
        }
        return modWordsCount;
    }

    private void setWordsFromPdfToMap(File pdfsDir) throws IOException {
        File[] files = getFileArray(pdfsDir);
        for (int i = 0; i < files.length; i++) {
            PdfDocument pdfDocument = new PdfDocument(new PdfReader(files[i]));
            wordsCount.putAll(getMapPageEntryFromDocument(pdfDocument, files[i].getName()));
        }
    }

    private HashMap<String, ArrayList<PageEntry>> getMapPageEntryFromDocument(PdfDocument pdfDocument, String name) {
        HashMap<String, ArrayList<PageEntry>> pageEntryHashMap = new HashMap<>();
        int numberPages = pdfDocument.getNumberOfPages();
        for (int i = 0; i < numberPages; i++) {
            PdfPage page = pdfDocument.getPage(i + 1);
            HashMap<String, Integer> countWordsInPage = getCountWordsFromPage(page);
            for (int j = 0; j < countWordsInPage.size(); j++) {
                String word = getKey(countWordsInPage, j);
                ArrayList<PageEntry> pageEntries;
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

    private HashMap<String, Integer> getCountWordsFromPage(PdfPage page) {
        String textPage = PdfTextExtractor.getTextFromPage(page);
        String[] textPageArray = textPage.split("\\P{IsAlphabetic}+");
        HashMap<String, Integer> mapWordsCount = new HashMap<>();
        for (int i = 0; i < textPageArray.length; i++) {
            String word = textPageArray[i];
            if (mapWordsCount.containsKey(word.toLowerCase())) {
                mapWordsCount.put(word.toLowerCase(), mapWordsCount.get(word.toLowerCase()) + 1);
            } else {
                mapWordsCount.put(word.toLowerCase(), 1);
            }
        }
        return mapWordsCount;
    }

    private String getKey(HashMap map, int i) {
        return map.entrySet().toArray()[i].toString().split("=")[0];
    }

    private File[] getFileArray(File directory) {
        File[] files = directory.listFiles();
        return files;
    }

    private void createIgnoreList() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("stop-ru.txt"));
        StringBuilder textIgnore = new StringBuilder();
        boolean flag = true;
        while (flag) {
            String text = reader.readLine();
            if (text != null) {
                textIgnore.append(text);
            } else {
                flag = false;
            }
        }
        String[] arrayIgnoreList = textIgnore.toString().split("\\P{IsAlphabetic}+");
        for (int i = 0; i < arrayIgnoreList.length; i++) {
            ignoreList.add(arrayIgnoreList[i]);
        }
    }

    private List<String> getSearchWithoutIgnoreWords(String word) {
        List searchWords = Arrays.asList(word.split("\\P{IsAlphabetic}+"));
        for (int i = 0; i < searchWords.size(); i++) {
            if (ignoreList.contains(searchWords.get(i))) {
                searchWords.remove(i);
            }
        }
        return searchWords;
    }

    private int getCount(ArrayList<PageEntry> pageEntries) {
        int count = 0;
        for (int j = 0; j < pageEntries.size(); j++) {
            count += pageEntries.get(j).getCount();
        }
        return count;
    }

    private ArrayList<PageEntry> createCopyWithMod(ArrayList<PageEntry> pageEntries, int count) {
        ArrayList<PageEntry> pageEntriesWithMod = new ArrayList<>();
        for (int j = 0; j < pageEntries.size(); j++) {
            PageEntry pageEntry = pageEntries.get(j);
            pageEntriesWithMod.add(new PageEntry(pageEntry.getPdfName(), pageEntry.getPage(), count));
        }
        return pageEntriesWithMod;
    }
}
