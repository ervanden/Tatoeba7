package dictionaries;

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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import langeditor.ConfirmDialog;
import langeditor.LanguageTextPane;
import languages.Language;
import utils.ByteOrderMark;
import utils.MsgTextPane;
import utils.Sas;

public class GenericDictionary {

    public HashMap<String, String> words = new HashMap<>();
    public HashMap<String, String> stems = new HashMap<>();

    public HashSet<String> addedwords = new HashSet<>();
    public HashSet<String> addedstems = new HashSet<>();
    public HashSet<String> removedwords = new HashSet<>();
    public HashSet<String> removedstems = new HashSet<>();

    Language language;

    DictionaryFrame dictFrame = null;
    Boolean markCorrection = false;
    Boolean matchInfo = true;

    public GenericDictionary(Language l) {
        language = l;
        dictFrame = new DictionaryFrame(language);
        dictFrame.setVisible(false);
    }

    public boolean isModified() {
        return !(addedwords.isEmpty()
                && addedstems.isEmpty()
                && removedwords.isEmpty()
                && removedstems.isEmpty());
    }

    public void dictionaryWindowVisible(boolean b) {
        dictFrame.setVisible(b);
    }

    public void setMatchInfo(boolean b) {
        matchInfo = b;
    }

    public void setMarkCorrection(boolean b) {
        markCorrection = b;
    }

    private boolean validCharacters(String word) {
        // to be called after removeDiacritics(). If letters other than a-z are present, return false.
        // Such words should not be in the dictionary  to avoid unknown letters that cause problems in WordTree

        String residu = word.replaceAll("[a-z]", "");
        if (!residu.equals("")) {
            MsgTextPane.write("unknown character in <" + word + ">  : <" + residu + ">");
            return false;
        } else {
            return true;
        }
    }

    public void addWord(String word) {
        String key = language.removeDiacritics(word);
        if (validCharacters(key)) {
            words.put(key, word);
            if (removedwords.contains(word)) {
                removedwords.remove(word);
            } else {
                addedwords.add(word);
            }
            dictFrame.writeDictArea("Word added: ", false);
            dictFrame.writeSelectDictArea(word);
            dictFrame.writeDictArea("\n", false);
            dictFrame.scrollEnd();
            dictFrame.isModified(isModified());
        }
    }

    public void addStem(String word) {
        String key = language.removeDiacritics(word);
        if (validCharacters(key)) {
            stems.put(key, word);
            if (removedstems.contains(word)) {
                removedstems.remove(word);
            } else {
                addedstems.add(word);
            }
            dictFrame.writeDictArea("Stem added: ", false);
            dictFrame.writeSelectDictArea(word);
            dictFrame.writeDictArea("\n", false);
            dictFrame.scrollEnd();
            dictFrame.isModified(isModified());
        }
    }

    public void removeWord(String word) {
        words.remove(language.removeDiacritics(word));
        if (addedwords.contains(word)) {
            addedwords.remove(word);
        } else {
            removedwords.add(word);
        }
        dictFrame.writeDictArea("Word removed: ", false);
        dictFrame.writeDictArea(word, false);
        dictFrame.writeDictArea("\n", false);
        dictFrame.scrollEnd();
        dictFrame.isModified(isModified());
    }

    public void removeStem(String word) {
        stems.remove(language.removeDiacritics(word));
        if (addedstems.contains(word)) {
            addedstems.remove(word);
        } else {
            removedstems.add(word);
        }
        dictFrame.writeDictArea("Stem removed: ", false);
        dictFrame.writeSelectDictArea(word);
        dictFrame.writeDictArea("\n", false);
        dictFrame.scrollEnd();
        dictFrame.isModified(isModified());
    }

    public void reset() {
        words.clear();
    }

    public void addWordsBulk(Collection<String> c) {
        for (String word : c) {
            String key = language.removeDiacritics(word);
            if (validCharacters(key)) {
                words.put(key, word);
            }
        }
    }

    public String readDictionaryFromFile(String fileName) {

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
            int linecount = 0;
            while ((l = inputStream.readLine()) != null) {
                if (linecount == 0) {
                    l = ByteOrderMark.remove(l);
                }
                linecount++;
                if (l.charAt(0) == '[') {
                    l = l.substring(1, l.length() - 1);
                    String key = language.removeDiacritics(l);
                    if (validCharacters(key)) {
                        stems.put(key, l);
                        scount++;
                    }
                } else {
                    String key = language.removeDiacritics(l);
                    if (validCharacters(key)) {
                        words.put(key, l);
                        wcount++;
                    }
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

    public boolean saveDictionary(String fileName) {

        boolean confirm;
        ConfirmDialog cd = new ConfirmDialog();
        cd.popUp(null,
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

                addedwords.clear();
                addedstems.clear();
                removedwords.clear();
                removedstems.clear();
                dictFrame.isModified(false);

                return true;

            } catch (IOException io) {
                MsgTextPane.write(" io exception during save dictionary");
                return false;
            }
        } else {
            return false;
        }
    }

    public String correctWord(String word) {
        // word is expected to be lowercase and diacritics removed
        if (words.containsKey(word)) {
            return correctWordByDictionary(word);
        } else {
            return correctWordByRules(word);
        }
    }

    public String correctWordByDictionary(String word) {
        if (words.containsKey(word)) {
            String correctedWord = words.get(word);
            if (matchInfo) {
                dictFrame.writeDictArea(word + " >> ", false);
                dictFrame.writeSelectDictArea(correctedWord);
                dictFrame.writeDictArea("\n", false);
                dictFrame.scrollEnd();
            }
            return correctedWord;
        } else {
            return word;
        }
    }

    public String correctWordByRules(String word) {
        return word;
    }

    public void correctText(LanguageTextPane textPane, int position, int length) {
        StyledDocument doc = textPane.getStyledDocument();

        int startWordPosition = 0;
        int endWordPosition = 0;
        String wordorig, word, wordlc, wordnew, wordnewlc;
        int percentageDone = 0;

        int nrWords = 0;
        int nrCorrected = 0;
        if (markCorrection) {
            nrWords = 0;
            nrCorrected = 0;
            MsgTextPane.write(String.format("%d%% corrected", percentageDone));
        }

        position = WordUtils.startOfWord(doc, position);
        length = WordUtils.endOfWord(doc, position + length) - position;
        int endPosition = position + length;
        while (position < endPosition) {
            try {
                if (markCorrection) {
                    nrWords++;
                }

                startWordPosition = WordUtils.nextAlphabetic(doc, position);
                endWordPosition = WordUtils.nextNonAlphabetic(doc, startWordPosition);
                wordorig = doc.getText(startWordPosition, endWordPosition - startWordPosition);
                word = language.removeDiacritics(wordorig);
                wordlc = language.toLowerCase(word);
                wordnewlc = correctWord(wordlc);  // dictionary lookup for words and stems              
                // make characters uppercase if they were originally
                wordnew = "";
                for (int i = 0; i < word.length(); i++) {
                    char nextChar = wordnewlc.charAt(i);
                    if (Character.isUpperCase(word.charAt(i))) {
                        wordnew = wordnew + language.toUpperCase(nextChar); // DocUtils does I correctly
                    } else {
                        wordnew = wordnew + nextChar;
                    }
                }

                if (!wordnew.equals(wordorig)) {
                    textPane.setFinalInsert(true);
                    doc.remove(startWordPosition, endWordPosition - startWordPosition);
                    doc.insertString(startWordPosition, wordnew, null);
                    if (markCorrection) {
                        doc.setCharacterAttributes(startWordPosition, wordnew.length(), Sas.red, false);
                        nrCorrected++;
                    }
                    textPane.setFinalInsert(false);
                }
            } catch (BadLocationException ex) {
                MsgTextPane.write("Bad Location in runDictionary ");
                ex.printStackTrace();
                System.exit(1);
            };
            position = endWordPosition;

            if (markCorrection) {
                int p = Math.round(((float) position / (float) length) * 100);
                if (((p / 10) * 10) > (percentageDone)) {
                    percentageDone = ((p / 10) * 10);
                    MsgTextPane.write(String.format("%d%% corrected", percentageDone));
                }
            }

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

    public void optimizeWords() {
        String correctedWord;
        java.util.List<String> v = new ArrayList<String>(words.keySet());
        MsgTextPane.write("Applying stem correction to Dictionary...");
        matchInfo = false;
        Collections.sort(v);
        int success = 0;
        int failed = 0;
        for (String str : v) {
            // str is lowercase and deturkified because it is in keySet
            correctedWord = correctWordByRules(str);
            if (!correctedWord.equals(words.get(str))) {
                failed++;
            } else {
                if (success < 100) {
                    dictFrame.writeDictArea("Redundant word removed : " + correctedWord + "\n", false);
                } else if (success == 100) {
                    dictFrame.writeDictArea("...", false);
                }
                words.remove(str);
                success++;
            }
        }
        MsgTextPane.write("Purge on dictionary : " + success + " words removed\n");
        matchInfo = true;
    }

    public void optimizeStems() {

        WordTree w = new WordTree(language);
        for (String key : words.keySet()) {
            String word = words.get(key);
            w.addWord(word, 1);
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

    public void printAll(String dictionaryPattern) {

        Pattern pattern;
        Matcher matcher;
        java.util.List<String> v;
        int count;

        if (dictionaryPattern.equals("")) {
            dictionaryPattern = "^.*$";
        }
        pattern = Pattern.compile(dictionaryPattern);
        v = new ArrayList<>(words.keySet());
        Collections.sort(v);
        dictFrame.writeDictArea("Searching Dictionary..." + "\n", true);
        count = 1;
        for (String str : v) {
            matcher = pattern.matcher((String) str);  // search the keys
            Boolean found = false;
            if (matcher.find()) {
                dictFrame.writeDictArea(count + " " + (String) words.get(str) + "\n", false);
                count++;
            }
        }
        dictFrame.writeDictArea(count - 1 + " entries in dictionary" + "\n", true);

        v = new ArrayList<>(stems.keySet());
        Collections.sort(v);
        dictFrame.writeDictArea("Searching stems..." + "\n", true);
        count = 1;
        for (String str : v) {
            matcher = pattern.matcher((String) str);  // search the keys
            Boolean found = false;
            if (matcher.find()) {
                dictFrame.writeDictArea(count + " " + str + " " + (String) stems.get(str) + "\n", false);
                count++;
            }
        }
        dictFrame.writeDictArea(count - 1 + " stems" + "\n", true);
    }

}
