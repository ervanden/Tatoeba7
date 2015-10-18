package dictionaries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import languages.Language;
import utils.MsgTextPane;

public class TurkishDictionary extends GenericDictionary implements Dictionary {

    public TurkishDictionary(Language language) {
        super(language);
    }

    @Override
    public String correctWordByRules(String word) {
        boolean debug = false;
        int nextpos = 0;
        char nextchar;
        boolean iMode = false;  // turkify i
        boolean uMode = false;  // turkify u
        String stem = "";
        String newword = "";

            // if the word is not in the dictionary try stem correction
        // Longest match prevails
        stem = findStem(word);
        nextpos = stem.length();
        newword = stem;

        if (stem.equals("")) {
            newword = word;
        } else {

            char lastVowel = ' ';
            for (int i = 0; i <= stem.length() - 1; i++) {
                char c = newword.charAt(i);
                if (isVowel(c)) {
                    lastVowel = c;
                }
            }

            if ((lastVowel == 'ü') || (lastVowel == 'ö')) {
                uMode = true;
            } else if ((lastVowel == 'a') || (lastVowel == 'ı')) {
                iMode = true;
            }

                // turkify vowels according to mode
            // cancel mode if e,i,o are encountered  (bil,yor,et,ed)
            for (int i = nextpos; i <= word.length() - 1; i++) {
                nextchar = word.charAt(i);

                // do the substitutions
                if (nextchar == 'i') {
                    char newchar = 'i';

                    if (iMode) {
                        newchar = 'ı';
                    }

                    newword = newword + newchar;

                } else if (uMode && (nextchar == 'u')) {
                    newword = newword + 'ü';

                } else if (nextchar == 'g') {
                    char newchar = 'g';
                    if (i >= 4) {
                        // extract the string 3 characters long, preceding the letter g
                        String s = (word.substring(i - 3, i));
                        Pattern pattern = Pattern.compile("^..[iuıü]|ece|aca$");
                        Matcher matcher = pattern.matcher(s);
                        if (matcher.find()) {
                            newchar = 'ğ';
                        }
                    }
                    newword = newword + newchar;

                } else if (nextchar == 'c') {
                    char newchar = 'c';
                    if (i >= 2) {
                        // extract the string 1 character long , preceding the c
                        String s = (word.substring(i - 1, i));
                        Pattern pattern = Pattern.compile("^[pfkths]$");
                        Matcher matcher = pattern.matcher(s);
                        if (matcher.find()) {
                            newchar = 'ç';
                        }
                    }
                    newword = newword + newchar;

                } else if (nextchar == 's') {
                    char newchar = 's';
                    if (i >= 4) {
                        // extract the string to match : 3 characters long, ending in s
                        String s = (word.substring(i - 2, i + 1));
                        // all patterns 4 characters long
                        Pattern pattern = Pattern.compile("m[iu]s");
                        Matcher matcher = pattern.matcher(s);
                        if (matcher.find()) {
                            newchar = 'ş';
                        }
                    }
                    newword = newword + newchar;

                } else {
                    newword = newword + nextchar;
                }

                // 'e' = end substitution  (hallederim)
                if (nextchar == 'e') {
                    iMode = false;
                    uMode = false;
                    if (debug) {
                        MsgTextPane.write(" 'e' ends substitution");
                    }
                }

                // 'bil' = end substitution  (-bilmek)
                if (nextchar == 'b') {
                    if ((i + 3) <= word.length()) {
                        if ((word.charAt(i + 1) == 'i') && (word.charAt(i + 2) == 'l')) {
                            iMode = false;
                            uMode = false;
                            if (debug) {
                                MsgTextPane.write(" 'bil' ends substitution");
                            }
                        }
                    }
                }

                // 'yor' = end substitution  (-yorum)
                if (nextchar == 'y') {
                    if ((i + 3) <= word.length()) {
                        if ((word.charAt(i + 1) == 'o') && (word.charAt(i + 2) == 'r')) {
                            iMode = false;
                            uMode = false;
                            if (debug) {
                                MsgTextPane.write(" 'yor' ends substitution");
                            }
                        }
                    }
                }

                    // 'a' = activate i-substitution
                // it needs be re-activated if it was cancelled by 'yor'  (yapıyorlardı)
                if (nextchar == 'a') {
                    iMode = true;
                    uMode = false;
                    if (debug) {
                        MsgTextPane.write(" 'a' starts i - substitution");
                    }
                }
            }
        }

        newword = newword.replaceAll("kı$", "ki");
        newword = newword.replaceAll("k[iı]n[iı]$", "kini");
        newword = newword.replaceAll("k[iı]ne$", "kine");
        newword = newword.replaceAll("k[iı]nden$", "kinden");
        newword = newword.replaceAll("k[iı]n[iı]n$", "kinin");
        newword = newword.replaceAll("kıler$", "kiler");
        newword = newword.replaceAll("kıleri$", "kileri");
        newword = newword.replaceAll("kılerin$", "kilerin");
        newword = newword.replaceAll("kılerini$", "kilerini");
        newword = newword.replaceAll("kılerine$", "kilerine");
        newword = newword.replaceAll("kılerinin$", "kilerinin");
        newword = newword.replaceAll("kılerinden$", "kilerinden");

        return newword;

    }
    
        public static boolean isVowel(char c) {
        if (c == 'e') {
            return true;
        } else if (c == 'i') {
            return true;
        } else if (c == 'a') {
            return true;
        } else if (c == 'u') {
            return true;
        } else if (c == 'o') {
            return true;
        } else if (c == 'ö') {
            return true;
        } else if (c == 'ü') {
            return true;
        } else if (c == 'ı') {
            return true;
        }
        return false;
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

        if (stem.equals("")) { // determine stem as all letters up to and including the first vowel
            i = 0;
            while ((i <= word.length() - 1) && !(isVowel(word.charAt(i)))) {
                i++;
            }
            if (i == word.length()) {  // no vowel found
                stem = "";
            } else {
                stem = word.substring(0, i + 1);
            }
        }
        return stem;
    }

    public void optimizeStems() {

        WordTree w = new WordTree();
        for (String key : words.keySet()) {
            String word = words.get(key);
            if (word.replaceFirst("[^a-zşçğıöü]", " ").equals(word)) {
                w.addWord(word, 1);
            } else {
                MsgTextPane.write("OptimizeStems: word contains invalid character : |" + word + "|");
            }
        }

        stems.clear();
        ArrayList<String> stemList;
        stemList = w.scanStems();
        for (String stem : stemList) {
            stems.put(language.removeDiacritics(stem), stem);
        }
        MsgTextPane.write(stemList.size() + " stems extracted");

        if (true) {
            String correctedWord;
            String correctStem;
            java.util.List<String> v = new ArrayList<String>(stems.keySet());
//        MsgTextPane.write("Applying stem reduction to Dictionary...");
            matchInfo = false;
            Collections.sort(v);
            int success = 0;
            int failed = 0;

            for (String str : v) {
                correctStem = stems.get(str);
                stems.remove(str);

                correctedWord = correctWordByRules(str);
                // no dictionary lookup because otherwise if stem happens to be in words it is removed
                if (correctedWord.equals(correctStem)) {
                    if (success < 100) {
                        dictFrame.writeDictArea("Redundant stem removed : " + correctedWord + "\n", false);
                    } else if (success == 100) {
                        dictFrame.writeDictArea("...", false);
                    }
                    success++;
                } else { // put it back
                    stems.put(str, correctStem);
                    failed++;
                }
            }
            MsgTextPane.write(success + " redundant stems removed\n");
            matchInfo = true;
        }
    }

}
