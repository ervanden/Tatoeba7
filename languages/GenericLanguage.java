package languages;

import dictionaries.DictionaryFrame;
import dictionaries.DictionaryFrameDevNull;
import dictionaries.DictionaryFrameInterface;
import dictionaries.GenericDictionary;
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
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFileChooser;
import languagetrainer.LanguageServer;
import tatoeba.PictureTrainer;
import utils.*;

public class GenericLanguage {

    /* all other languages extend GenericLanguage
     So this class contains default functions in case the language does not redefine them
     or generic functions that are useful in all languages (e.g. invertDiacriticsGeneric()
     */
    GenericDictionary d = null;
    public String languageName = "generic";
    HashMap<String, HashMap<String, String>> wordMaps = new HashMap<>();

    public String dictionaryFileName() {
        if (LanguageServer.server_mode) { // supposed to be running on Linux PI
            return "/home/pi/Tatoeba/data/" + languageName + "Dictionary.txt";
        } else {  // supposed to be running on Windows laptop
            String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
            return defaultFolder + "\\Tatoeba\\" + languageName + "Dictionary.txt";
        }

    }

    public GenericDictionary dictionary() {
        if (d == null) {
            d = new GenericDictionary(LanguageContext.get("generic"));
            d.readDictionaryFromFile(dictionaryFileName());
        }
        return d;
    }

    public void disposeDictionary() {
        d = null;
    }

    public char toUpperCase(char c) {
        return Character.toUpperCase(c);  // works correctly for most languages
    }

    public String toLowerCase(String s) {
        return s.toLowerCase();  // works correctly for most languages
    }

    public String letters() {
        return "";
    }

    public String diacriticsGroups() {
        return "";
    }
    /*
     public String invertDiacritics(String word) {
     return word;
     }

     public String invertDiacriticsGeneric(String word, String letterGroups) {


     ArrayList<Character> letterGroupsArray = new ArrayList<>();
     for (Character c : letterGroups.toCharArray()) {
     letterGroupsArray.add(c);
     }

     char c;
     String newword = "";
     for (int i = 0; i <= word.length() - 1; i++) {
     c = word.charAt(i);
     if (letterGroupsArray.contains(c)) {
     if (c != ' ') {
     int j = letterGroupsArray.indexOf(c);
     if (j >= 0) {
     c = letterGroupsArray.get(j + 1);
     } // if c is not in the array it is not replaced
     }
     }
     newword = newword + c;
     }
     return newword;
     }
     */

    public String removeDiacritics(String word) {
        word = word.replaceAll("[âäàąáâã]", "a");
        word = word.replaceAll("[éèêëę]", "e");
        word = word.replaceAll("[îïıí]", "i");
        word = word.replaceAll("[ôöóõ]", "o");
        word = word.replaceAll("[ûüú]", "u");
        word = word.replaceAll("[şś]", "s");
        word = word.replaceAll("[çć]", "c");
        word = word.replaceAll("ğ", "g");
        word = word.replaceAll("ł", "l");
        word = word.replaceAll("ń", "n");
        word = word.replaceAll("[źż]", "z");

        return word;
    }

    public String number(int n) {
        return "number translation in this language is not implemented";
    }

    public String color(String color) {
        String tcolor = "?"; // translated color
        if (color.equals("white")) {
            tcolor = "?";
        }
        if (color.equals("black")) {
            tcolor = "?";
        }
        if (color.equals("brown")) {
            tcolor = "?";
        }
        if (color.equals("red")) {
            tcolor = "?";
        }
        if (color.equals("salmon")) {
            tcolor = "?";
        }
        if (color.equals("orange")) {
            tcolor = "?";
        }
        if (color.equals("gold")) {
            tcolor = "?";
        }
        if (color.equals("yellow")) {
            tcolor = "?";
        }
        if (color.equals("olive")) {
            tcolor = "?";
        }
        if (color.equals("green")) {
            tcolor = "?";
        }
        if (color.equals("light blue")) {
            tcolor = "?";
        }
        if (color.equals("turquoise")) {
            tcolor = "?";
        }
        if (color.equals("dark blue")) {
            tcolor = "?";
        }
        if (color.equals("blue")) {
            tcolor = "?";
        }
        if (color.equals("purple")) {
            tcolor = "?";
        }
        if (color.equals("violet")) {
            tcolor = "?";
        }
        if (color.equals("magenta / fuchsia")) {
            tcolor = "?";
        }
        if (color.equals("beige")) {
            tcolor = "?";
        }
        if (color.equals("pink")) {
            tcolor = "?";
        }
        if (color.equals("ivory")) {
            tcolor = "?";
        }
        if (color.equals("black")) {
            tcolor = "?";
        }
        if (color.equals("grey")) {
            tcolor = "?";
        }
        if (color.equals("silver")) {
            tcolor = "?";
        }
        return tcolor;
    }

    public String translate(String theme, String word) {

        HashMap<String, String> themeWordMap = wordMaps.get(theme);
        if (themeWordMap == null) {
            themeWordMap = new HashMap<>();
            wordMaps.put(theme, themeWordMap);

            String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
            String fileName = defaultFolder + "\\Tatoeba\\" + languageName + theme + ".txt";
            BufferedReader inputStream = null;
            int count = 0;
            try {
                InputStream is = new FileInputStream(new File(fileName));
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                inputStream = new BufferedReader(isr);

                MsgTextPane.write("reading " + fileName);
                String l;
                count = 0;
                while ((l = inputStream.readLine()) != null) {
                    if (count == 0) {
                        l = ByteOrderMark.remove(l);
                    }
                    String[] ls = l.split("\u0009");
                    if (ls.length == 2) {
                        String key = ls[0];
                        String value = ls[1];
                        themeWordMap.put(key, value);
                        count++;
                    }
                }
            } catch (FileNotFoundException fnf) {
                MsgTextPane.write("file not found : " + fileName);
            } catch (IOException io) {
                MsgTextPane.write("io exception : " + fileName);
            }

            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException io) {
            }

            MsgTextPane.write(count + " entries read from " + fileName);

        }

        return themeWordMap.get(word);
    }

    public void updateWordMaps() {
        for (String theme : PictureTrainer.getPictureThemes()) {
            addWordsToMap(theme, PictureTrainer.getPictureNames(theme));
            writeWordMap(theme);
        }
    }

    public void rereadWordMaps() {
        for (String theme : PictureTrainer.getPictureThemes()) {
            wordMaps.remove(theme);
            translate(theme, "");

        }
    }

    private void addWordsToMap(String theme, ArrayList<String> words) {
        HashMap<String, String> themeWordMap;
        translate(theme, ""); // force word map to be read
        themeWordMap = wordMaps.get(theme);
        for (String word : words) {
            if (!themeWordMap.containsKey(word)) {
                themeWordMap.put(word, null);
            }
        }
    }

    private void writeWordMap(String theme) {
        HashMap<String, String> themeWordMap;
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        String fileName = defaultFolder + "\\Tatoeba\\" + languageName + theme + ".txt";
        MsgTextPane.write("Writing " + fileName);
        themeWordMap = wordMaps.get(theme);

        try {
            OutputStream is = new FileOutputStream(new File(fileName));
            OutputStreamWriter isr = new OutputStreamWriter(is, "UTF-8");
            BufferedWriter outputStream = new BufferedWriter(isr);

            for (Map.Entry entry : themeWordMap.entrySet()) {
                outputStream.write(entry.getKey() + "\t" + entry.getValue());
                outputStream.newLine();
            }
            outputStream.close();

        } catch (IOException io) {
            MsgTextPane.write(" io exception during save" + fileName);
        }
    }
}
