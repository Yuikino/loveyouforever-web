package untitymeow;

import toolgood.words.StringSearch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WordUtil {
    private static StringSearch stringSearch;

    public static StringSearch getStringSearch() throws IOException {
        if (stringSearch == null) {
            reload();
        }
        return stringSearch;
    }

    public static void reload() throws IOException {
        stringSearch = new StringSearch();
        stringSearch.SetKeywords(Files.readAllLines(Paths.get("filternames.csv")));
    }
}
