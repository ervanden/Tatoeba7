package languages;

import java.util.HashMap;

public class LanguageContext {

    // static object that translates a language name to its corresponding language object
    static HashMap<String, Language> languageMap = new HashMap<>();

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
            } else if (lang.equals("ita")) {
                language = new Italian();
            } else if (lang.equals("eng")) {
                language = new English();
            } else if (lang.equals("fra")) {
                language = new French();
            } else if (lang.equals("nld")) {
                language = new Dutch();
            } else if (lang.equals("ind")) {
                language = new Indonesian();
            } else {
                language = new Other();
            }
            languageMap.put(lang, language);
        }
        return language;
    }

}
