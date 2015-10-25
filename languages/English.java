package languages;

import dictionaries.EnglishDictionary;
import javax.swing.JFileChooser;

public class English extends GenericLanguage implements Language {

    EnglishDictionary d = null;

    @Override
    public String dictionaryFileName() {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        return defaultFolder + "\\Tatoeba\\EnglishDictionary.txt";
    }

    @Override
    public EnglishDictionary dictionary() {
         if (d == null) {
            d = new EnglishDictionary(this);
            d.readDictionaryFromFile(dictionaryFileName());
        }       
        return d;
    }
            
    public String color(String color){
        // this method is always called with the argument 'color' in english
        return color;
    }

}
