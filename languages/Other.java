package languages;


import dictionaries.OtherDictionary;
import javax.swing.JFileChooser;

public class Other extends GenericLanguage implements Language {

    OtherDictionary d = null;

    @Override
    public String dictionaryFileName() {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        return defaultFolder + "\\Tatoeba\\OtherDictionary.txt";
    }

    @Override
    public OtherDictionary dictionary() {
         if (d == null) {
            d = new OtherDictionary(this);
            d.readDictionaryFromFile(dictionaryFileName());
        }       
        return d;
    }

}
