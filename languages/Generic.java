package languages;

import dictionary.GenericDictionary;
import javax.swing.JFileChooser;

public class Generic implements Language {

    GenericDictionary d = null;

    public String dictionaryFileName() {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        return defaultFolder + "\\Tatoeba\\GenericDictionary.txt";
    }

    public GenericDictionary dictionary() {
         if (d == null) {
            d = new GenericDictionary();
            d.readDictionaryFromFile(dictionaryFileName());
        }       
        return d;
    }
    
    public void disposeDictionary(){
        d=null;
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
