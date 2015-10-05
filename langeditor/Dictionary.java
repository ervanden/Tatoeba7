package langeditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import utils.MsgTextPane;

public class Dictionary {

    public  Hashtable<String, String> words = new Hashtable<String, String>();
    public  Hashtable<String, String> stems = new Hashtable<String, String>();

     String dictionaryFileName = "?";
     String dictionaryPattern = "";

     Boolean markCorrection = false;
     Boolean matchInfo = true;

    public  void setMatchInfo(boolean m) {
        matchInfo = m;
    }

    public  void addWord(String word) {
        words.put(LanguageContext.get().removeDiacritics(word), word);
        DocUtils.writeDictArea("Word added: ", false);
        DocUtils.writeSelectDictArea(word);
        DocUtils.writeDictArea("\n", false);
        DocUtils.scrollEnd();
    }

    public  void addStem(String word) {
        stems.put(LanguageContext.get().removeDiacritics(word), word);
        DocUtils.writeDictArea("Stem added: ", false);
        DocUtils.writeSelectDictArea(word);
        DocUtils.writeDictArea("\n", false);
        DocUtils.scrollEnd();
    }

    public  void removeWord(String word) {
        words.remove(LanguageContext.get().removeDiacritics(word));
        DocUtils.writeDictArea("Word removed: ", false);
        DocUtils.writeDictArea(word, false);
        DocUtils.writeDictArea("\n", false);
        DocUtils.scrollEnd();
    }

    public  void removeStem(String word) {
        stems.remove(LanguageContext.get().removeDiacritics(word));
        DocUtils.writeDictArea("Stem removed: ", false);
        DocUtils.writeSelectDictArea(word);
        DocUtils.writeDictArea("\n", false);
        DocUtils.scrollEnd();
        stems.remove(LanguageContext.get().removeDiacritics(word));
    }

    public  String readDictionaryFromFile(String fileName) {

        // returns the dictionary file name
        // if the fileName argument is a fileName , this is returned
        // if the fileName argument is "", the file selected by the user is returned
        BufferedReader inputStream = null;
        int wcount = 0;
        int scount = 0;

        File f = new File(fileName);
        if (!f.exists() || f.isDirectory()) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chose a dictionary file");
            int retval = fileChooser.showOpenDialog(null);
            if (retval == JFileChooser.APPROVE_OPTION) {
                f = fileChooser.getSelectedFile();
                fileName = f.getAbsolutePath();
            }
        }

        try {

            File initialFile = new File(fileName);
            InputStream is = new FileInputStream(initialFile);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            MsgTextPane.write("read encoding = " + isr.getEncoding());
            inputStream = new BufferedReader(isr);

            String l;
            while ((l = inputStream.readLine()) != null) {
                if (l.charAt(0) == '[') {
                    l = l.substring(1, l.length() - 1);
                    stems.put(LanguageContext.get().removeDiacritics(l), l);
                    scount++;
                } else {
                    words.put(LanguageContext.get().removeDiacritics(l), l);
                    wcount++;
                }
            }
        } catch (FileNotFoundException fnf) {
        } catch (IOException io) {
        }
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException io) {
        }
        MsgTextPane.write(wcount + " words read from " + fileName);
        MsgTextPane.write(scount + " stems read from " + fileName);
        return fileName;
    }

    public  boolean saveDictionary(String fileName) {

        boolean confirm;
        ConfirmDialog cd = new ConfirmDialog();
        cd.popUp(LanguageEditor.languageEditorFrame,
                words.size() + "/"
                + stems.size() + " entries will be written to " + fileName, "Continue", "Cancel");
        confirm = cd.confirm;

        if (confirm) {
            try {

                File initialFile = new File(fileName);
                OutputStream is = new FileOutputStream(initialFile);
                OutputStreamWriter isr = new OutputStreamWriter(is, "UTF-8");
                MsgTextPane.write("write encoding = " + isr.getEncoding());
                BufferedWriter outputStream = new BufferedWriter(isr);

                java.util.List<String> v;

                v = new ArrayList<String>(words.keySet());
                for (String str : v) {
                    outputStream.write((String) words.get(str));
                    outputStream.newLine();
                }

                v = new ArrayList<String>(stems.keySet());

                for (String str : v) {
                    outputStream.write("[" + (String) stems.get(str) + "]");
                    outputStream.newLine();
                }
                outputStream.close();

                return true;

            } catch (IOException io) {
                MsgTextPane.write(" io exception during save dictionary");
                return false;
            }
        } else {
            return false;
        }
    }

    public  String findStem(String word, boolean wordLookup, boolean stemLookup) {
        String substring;
        String stem = "";
        int len, i;

        if (stemLookup) {
            for (i = 1; i <= word.length(); i++) {
                substring = word.substring(0, i);
                if (stems.containsKey(substring)) {
                    stem = stems.get(substring);
                    if (matchInfo) {  // no output if called from optimizer
                        DocUtils.writeDictArea("[", false);
                        DocUtils.writeSelectDictArea(stem);
                        DocUtils.writeDictArea("]\n", false);
                        DocUtils.scrollEnd();
                    }
                }
            }
        }

        if (stem.equals("")) { // determine stem as all letters up to and including the first vowel
            i = 0;
            while ((i <= word.length() - 1) && !(DocUtils.isVowel(word.charAt(i)))) {
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

    public  String runDictionaryOnWord(String word, boolean wordLookup, boolean stemLookup) {

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
                DocUtils.writeDictArea(word + " >> ", false);
                DocUtils.writeSelectDictArea(correctedWord);
                DocUtils.writeDictArea("\n", false);
                DocUtils.scrollEnd();
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
                    if (DocUtils.isVowel(c)) {
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

    public  void runDictionary(StyledDocument doc, int position, int length) {
        int startWordPosition = 0;
        int endWordPosition = 0;
        String wordorig, word, wordlc, wordnew, wordnewlc;

        int nrWords = 0;
        int nrCorrected = 0;
        if (markCorrection) {
            nrWords = 0;
            nrCorrected = 0;
        }

        position = DocUtils.startOfWord(doc, position);
        length = DocUtils.endOfWord(doc, position + length) - position;
        int endPosition = position + length;
        while (position < endPosition) {
            try {
                if (markCorrection) {
                    nrWords++;
                }

                startWordPosition = DocUtils.nextAlphabetic(doc, position);
                endWordPosition = DocUtils.nextNonAlphabetic(doc, startWordPosition);
                wordorig = doc.getText(startWordPosition, endWordPosition - startWordPosition);
                word = LanguageContext.get().removeDiacritics(wordorig);
//                MsgTextPane.write("word = "+word);
                wordlc = word.replaceAll("[İI]", "i").toLowerCase();
//  MsgTextPane.write("wordlc = "+wordlc);              
                wordnewlc = runDictionaryOnWord(wordlc, true, true);  // true = dictionary lookup for words and stems
//MsgTextPane.write("wordnewlc = "+wordnewlc);
                // make characters uppercase if they were originally
                wordnew = "";
                for (int i = 0; i < word.length(); i++) {
                    char nextChar = wordnewlc.charAt(i);
                    if (Character.isUpperCase(word.charAt(i))) {
                        wordnew = wordnew + DocUtils.toUpperCase(nextChar); // DocUtils does I correctly
                    } else {
                        wordnew = wordnew + nextChar;
                    }
                }
                //MsgTextPane.write("wordnew = "+wordnew);               
                if (!wordnew.equals(wordorig)) {
                    LanguageTextPane.finalInsert = true;
                    doc.remove(startWordPosition, endWordPosition - startWordPosition);
                    doc.insertString(startWordPosition, wordnew, null);
                    if (markCorrection) {
                        doc.setCharacterAttributes(startWordPosition, wordnew.length(), DocUtils.sas_red, false);
                        nrCorrected++;
                    }
                    LanguageTextPane.finalInsert = false;
                }
            } catch (BadLocationException ex) {
                MsgTextPane.write("Bad Location in runDictionary ");
                ex.printStackTrace();
                System.exit(1);
            };
            position = endWordPosition;
        }

        if (markCorrection) {
            MsgTextPane.write(nrWords + " words in selected text");
            if (nrWords > 0) {
                float percentage = (float) nrCorrected / (float) nrWords;
                percentage = 100 * (1 - percentage);
                MsgTextPane.write(nrCorrected + " words corrected");
                MsgTextPane.write(percentage + "% unchanged");
            }
            markCorrection = false;
        }
    }

    public  void optimizeWords() {
        String correctedWord;
        java.util.List<String> v = new ArrayList<String>(words.keySet());
//        MsgTextPane.write("Applying stem correction to Dictionary...");
        matchInfo = false;
        Collections.sort(v);
        int success = 0;
        int failed = 0;
        for (String str : v) {
            // str is lowercase and deturkified because it is in keySet
            correctedWord = runDictionaryOnWord(str, false, true); //  only stem based correction
            if (!correctedWord.equals(words.get(str))) {
                failed++;
            } else {
                if (success < 100) {
                    DocUtils.writeDictArea("Redundant word removed : " + correctedWord + "\n", false);
                } else if (success == 100) {
                    DocUtils.writeDictArea("...", false);
                }
                words.remove(str);
                success++;
            }
        }
        MsgTextPane.write("Purge on dictionary : " + success + " words removed\n");
        matchInfo = true;
    }

    public  void optimizeStems() {

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
            stems.put(LanguageContext.get().removeDiacritics(stem), stem);
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

                correctedWord = runDictionaryOnWord(str, false, true);
                // no dictionary lookup because otherwise if stem happens to be in words it is removed
                if (correctedWord.equals(correctStem)) {
                    if (success < 100) {
                        DocUtils.writeDictArea("Redundant stem removed : " + correctedWord + "\n", false);
                    } else if (success == 100) {
                        DocUtils.writeDictArea("...", false);
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

    public  void printAll() {

        Pattern pattern;
        Matcher matcher;
        java.util.List<String> v;
        int count;

        if (dictionaryPattern.equals("")) {
            dictionaryPattern = "^.*$";
        }
        pattern = Pattern.compile(dictionaryPattern);
        v = new ArrayList<String>(words.keySet());
        Collections.sort(v);
        DocUtils.writeDictArea("Searching Dictionary..." + "\n", true);
        count = 1;
        for (String str : v) {
            matcher = pattern.matcher((String) str);  // search the keys
            Boolean found = false;
            if (matcher.find()) {
                DocUtils.writeDictArea(count + " " + (String) words.get(str) + "\n", false);
                count++;
            }
        }
        DocUtils.writeDictArea(count - 1 + " entries in dictionary" + "\n", true);

        v = new ArrayList<String>(stems.keySet());
        Collections.sort(v);
        DocUtils.writeDictArea("Searching stems..." + "\n", true);
        count = 1;
        for (String str : v) {
            matcher = pattern.matcher((String) str);  // search the keys
            Boolean found = false;
            if (matcher.find()) {
                DocUtils.writeDictArea(count + " " + str + " " + (String) stems.get(str) + "\n", false);
                count++;
            }
        }
        DocUtils.writeDictArea(count - 1 + " stems" + "\n", true);
    }




}
