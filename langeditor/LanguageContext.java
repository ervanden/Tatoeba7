package langeditor;

import java.util.HashMap;

public class LanguageContext {

    static LanguageOperations ops = null;
    static HashMap<String, LanguageOperations> opsMap = new HashMap<>();

    public static LanguageOperations get() {
        return ops;
    }

    public static void set(String language) {
        System.out.println(" set language context to " + language);

        ops = opsMap.get(language);
        if (ops == null) {
            if (language.equals("tur")) {
                ops = new TurkishOperations();
                ops.initialize();
                opsMap.put(language, ops);
            }
            if (language.equals("pol")) {
                ops = new PolishOperations();
                ops.initialize();
                opsMap.put(language, ops);
            }
        }

    }
}
