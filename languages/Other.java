package languages;

import dictionaries.GenericDictionary;
import dictionaries.OtherDictionary;
import javax.swing.JFileChooser;

public class Other extends GenericLanguage implements Language {

    GenericDictionary d = null;

    @Override
    public String dictionaryFileName() {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        return defaultFolder + "\\Tatoeba\\OtherDictionary.txt";
    }

    @Override
    public GenericDictionary dictionary() {
         if (d == null) {
            d = new OtherDictionary(this);
            d.readDictionaryFromFile(dictionaryFileName());
        }       
        return d;
    }

}
