package langoperations;

import javax.swing.JFileChooser;
import langeditor.Dictionary;

public class GenericOperations implements LanguageOperations {

    Dictionary d = null;

    public String dictionaryFileName() {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        return defaultFolder + "\\Tatoeba\\GenericDictionary.txt";
    }

    public Dictionary dictionary() {
         if (d == null) {
            d = new Dictionary();
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
