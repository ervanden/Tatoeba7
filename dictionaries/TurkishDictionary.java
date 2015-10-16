
package dictionaries;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import languages.Language;
import utils.MsgTextPane;


public class TurkishDictionary extends GenericDictionary implements Dictionary {
    
    public TurkishDictionary(Language language){
        super(language);
    }
 
    @Override
    public String runDictionaryOnWord(String word, boolean wordLookup, boolean stemLookup) {
System.out.println("runDictionaryOnWord |"+word+"|");
        int nextpos = 0;
        char nextchar;
        boolean iMode = false;  // turkify i
        boolean uMode = false;  // turkify u
        String stem = "";
        String newword = "";

        boolean debug = false;

        // word is expected to be lowercase and deturkified
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
            // if the word is not in the dictionary try stem correction
            // Longest match prevails
            stem = findStem(word, wordLookup, stemLookup);
            nextpos = stem.length();
            newword = stem;

            if (stem.equals("")) {
                newword = word;
            } else {

                char lastVowel = ' ';
                for (int i = 0; i <= stem.length() - 1; i++) {
                    char c = newword.charAt(i);
                    if (WordUtils.isVowel(c)) {
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
}
