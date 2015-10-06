package langoperations;

import javax.swing.JFileChooser;
import langeditor.Dictionary;

public class DummyOperations implements LanguageOperations {

    Dictionary d = null;

    public String dictionaryFileName() {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        return defaultFolder + "\\Tatoeba\\DummyDictionary.txt";
    }

    public Dictionary dictionary() {
        return d;
    }

    public void initialize() {
        if (d == null) {
            d = new Dictionary();
            d.readDictionaryFromFile(dictionaryFileName());
        }
    }

    public String invertDiacritics(String word) {
        return word;
    }

    public String removeDiacritics(String word) {
        return word;
    }

    public String number(int n) {
        return (String.format("%s", n));
    }

}
