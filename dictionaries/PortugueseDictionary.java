package dictionaries;

import languages.Language;



public class PortugueseDictionary extends GenericDictionary implements Dictionary {

    public PortugueseDictionary(Language language) {
        super(language);
    }

    @Override
    public String correctWordByRules(String word) {
        // word is expected to be lowercase and diacritics removed
        word = word.replaceAll("cao$", "ção");
        return word;
    }
}
