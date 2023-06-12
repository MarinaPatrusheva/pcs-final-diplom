import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreatingIgnoreList {
    private List<String> ignoreList = new ArrayList<>();

    public List<String> getIgnoreList() {
        try {
            createIgnoreList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ignoreList;
    }

    private void createIgnoreList() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("stop-ru.txt"));
        boolean flag = true;
        while (flag) {
            String text = reader.readLine();
            if (text != null) {
                ignoreList.add(text);
            } else {
                flag = false;
            }
        }
    }
}
