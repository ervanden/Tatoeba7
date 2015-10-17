package dictionaries;

import languages.Language;

public class PortugueseDictionary extends GenericDictionary implements Dictionary {

    public PortugueseDictionary(Language language) {
        super(language);
    }

    @Override
    public String runDictionaryOnWord(String word, boolean wordLookup) {

        // word is expected to be lowercase and diacritics removed
        if (wordLookup && words.containsKey(word)) {
            String correctedWord = words.get(word);
            if (matchInfo) {
                dictFrame.writeDictArea(word + " >> ", false);
                dictFrame.writeSelectDictArea(correctedWord);
                dictFrame.writeDictArea("\n", false);
                dictFrame.scrollEnd();
            }
            return correctedWord;
        } else {
            word=word.replaceAll("cao$", "ção");
            return word;
        }
    }
}
