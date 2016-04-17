package dictionaries;

import languages.Language;
import utils.MsgTextPane;

public class PolishDictionary extends GenericDictionary implements Dictionary {

    public PolishDictionary(Language language) {
        super(language);
    }

    @Override
    public String correctWordByRules(String word) {

        // for Polish, if the beginning of a word matches a stem, the corrected word is this stem
        // followed by the rest of the word. This is to cover all word endings of declensions and conjugations
        // that have no diacritics, e.g. cały cała całowa całym
        // If the declension or conjugation ending has diacritics, the complete declined or conjugated
        // word must be in the dictionary
        
        int nextpos = 0;
        char nextchar;

        String stem = "";
        String newword = "";

        stem = findStem(word);
        if (stem.equals("")) {
            newword = word;
        } else {
            nextpos = stem.length();
            newword = stem;
            for (int i = nextpos; i <= word.length() - 1; i++) {
                nextchar = word.charAt(i);
                newword=newword+nextchar;
            }
        }
        
//ąćęłńóśżź
                newword = newword.replaceAll("ow$", "ów");
                
//System.out.println("CorrectByRules word="+word+" stem="+stem+" newword="+newword);
        
        return newword;

    }

    public String findStem(String word) {
        String substring;
        String stem = "";
        int i;

        for (i = 1; i <= word.length(); i++) {
            substring = word.substring(0, i);
            if (stems.containsKey(substring)) {
                stem = stems.get(substring);
                if (matchInfo) {  // no output if called from optimizer
                    dictFrame.writeDictArea("[", false);
                    dictFrame.writeSelectDictArea(stem);
                    dictFrame.writeDictArea("]\n", false);
                    dictFrame.scrollEnd();
                }
            }
        }

        return stem;
    }

}
