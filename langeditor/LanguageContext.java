package langeditor;

import langoperations.LanguageOperations;
import langoperations.TurkishOperations;
import langoperations.PolishOperations;
import langoperations.GenericOperations;
import java.util.HashMap;


public class LanguageContext {

    static public String language = "not initialized";
    static LanguageOperations ops = null;
    static LanguageEditorFrame frame = null;
    
    static HashMap<String, LanguageOperations> opsMap = new HashMap<>();

    public static LanguageOperations get() {
        return ops;
    }

    public static void set(LanguageEditorFrame f, String lang, String origin) {

        language = lang;
        
        System.out.println(" set language context to " + language + "  (" + origin + ")");

        ops = opsMap.get(language);
        if (ops == null) {
            if (language.equals("tur")) {
                ops = new TurkishOperations();
                opsMap.put(language, ops);
            } else 
            if (language.equals("pol")) {
                ops = new PolishOperations();
                opsMap.put(language, ops);
            } else {
                language="generic";
                ops = new GenericOperations();
                opsMap.put(language, ops);              
            }
        }

    }
}
