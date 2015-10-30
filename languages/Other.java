package languages;


import dictionaries.OtherDictionary;

public class Other extends GenericLanguage implements Language {

    OtherDictionary d = null;

        public Other(){
        languageName="Other";
    }
/*        
    @Override
    public String dictionaryFileName() {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        return defaultFolder + "\\Tatoeba\\OtherDictionary.txt";
    }
*/
    @Override
    public OtherDictionary dictionary() {
         if (d == null) {
            d = new OtherDictionary(this);
            d.readDictionaryFromFile(dictionaryFileName());
        }       
        return d;
    }

}
