package langeditor;

import java.util.HashMap;
import languages.Generic;
import languages.Language;
import languages.Polish;
import languages.Turkish;


public class LanguageContext {

    static public String language = "not initialized";
    static Language ops = null;
    
    static HashMap<String, Language> opsMap = new HashMap<>();

    public static Language get() {
        return ops;
    }

    public static void set(String lang, String origin) {

        language = lang;
        
        System.out.println(" set language context to " + language + "  (" + origin + ")");

        ops = opsMap.get(language);
        if (ops == null) {
            if (language.equals("tur")) {
                ops = new Turkish();
                opsMap.put(language, ops);
            } else 
            if (language.equals("pol")) {
                ops = new Polish();
                opsMap.put(language, ops);
            } else {
                language="generic";
                ops = new Generic();
                opsMap.put(language, ops);              
            }
        }

    }
}
