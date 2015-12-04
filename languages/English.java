package languages;

import dictionaries.EnglishDictionary;

public class English extends GenericLanguage implements Language {

    EnglishDictionary d = null;

    public English() {
        languageName = "English";
    }

    @Override
    public EnglishDictionary dictionary() {
        if (d == null) {
            d = new EnglishDictionary(this);
            d.readDictionaryFromFile(dictionaryFileName());
        }
        return d;
    }

    public String color(String color) {
        // this method is always called with the argument 'color' in english
        return color;
    }

}
