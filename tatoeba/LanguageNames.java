package tatoeba;


import java.util.ArrayList;
import java.util.HashMap;

public class LanguageNames {

    static HashMap<String, Language> languagesByShortName = new HashMap<String, Language>();
    static HashMap<String, Language> languagesByLongName = new HashMap<String, Language>();

    public static ArrayList<String> languageShortNames() {
        return new ArrayList<String>(languagesByShortName.keySet());
    }

    public static ArrayList<String> languageLongNames() {
        return new ArrayList<String>(languagesByLongName.keySet());
    }

    public static void addLanguage(Language l) {
        languagesByShortName.put(l.shortName, l);
        languagesByLongName.put(l.longName, l);
    }

    public static String longToShort(String longName) {
        // if it can not be translated, assume that longName is a short name returned by shortToLong()
        Language l;
        l = languagesByLongName.get(longName);
        if (l == null) {
            return longName;
        } else {
            return l.shortName;
        }
    }
    
    
    
        public static String shortToLong(String shortName) { 
        // if shortName can not be translated, return shortName 
        Language l;
        l = languagesByShortName.get(shortName);
        if (l == null) {
            return shortName;
        } else {
            return l.longName;
        }
    }

}
