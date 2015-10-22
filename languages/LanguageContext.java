package languages;

import java.util.ArrayList;
import java.util.HashMap;

public class LanguageContext {

    // static object that translates a language name to its corresponding language object
    
    static ArrayList<String> knownLanguages;
    static HashMap<String, Language> languageMap = new HashMap<>();
    
    static {
        knownLanguages=new ArrayList<>();
        knownLanguages.add("tur");
        knownLanguages.add("pol");
        knownLanguages.add("por");
    }

    public static Language get(String lang) {
        Language language;
        language = languageMap.get(lang);
        if (language == null) {
            if (lang.equals("tur")) {
                language = new Turkish();
            } else if (lang.equals("pol")) {
                language = new Polish();
            } else if (lang.equals("por")) {
                language = new Portuguese();
            } else {
                language = new Other();
            }
            languageMap.put(lang, language);
        }
        return language;
    }
    
    public static ArrayList<String> knownLanguages(){       
        return knownLanguages;
    }
}