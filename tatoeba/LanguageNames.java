package tatoeba;


import java.util.ArrayList;
import java.util.HashMap;

public class LanguageNames {

    static HashMap<String, LanguageName> languagesByShortName = new HashMap<String, LanguageName>();
    static HashMap<String, LanguageName> languagesByLongName = new HashMap<String, LanguageName>();

    public static ArrayList<String> languageShortNames() {
        return new ArrayList<String>(languagesByShortName.keySet());
    }

    public static ArrayList<String> languageLongNames() {
        return new ArrayList<String>(languagesByLongName.keySet());
    }

    public static void addLanguage(LanguageName l) {
        languagesByShortName.put(l.shortName, l);
        languagesByLongName.put(l.longName, l);
    }

    public static String longToShort(String longName) {
        // if it can not be translated, assume that longName is a short name returned by shortToLong()
        LanguageName l;
        l = languagesByLongName.get(longName);
        if (l == null) {
            return longName;
        } else {
            return l.shortName;
        }
    }
    
    
    
        public static String shortToLong(String shortName) { 
        // if shortName can not be translated, return shortName 
        LanguageName l;
        l = languagesByShortName.get(shortName);
        if (l == null) {
            return shortName;
        } else {
            return l.longName;
        }
    }

}
