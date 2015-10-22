package languages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFileChooser;
import tatoeba.Cluster;
import tatoeba.Sentence;
import utils.MsgTextPane;


public class LanguageNames {
    
    static HashMap<String, String> languagesByShortName = new HashMap<>();
    static HashMap<String, String> languagesByLongName = new HashMap<>();

    public static ArrayList<String> languageShortNames() {
        return new ArrayList<String>(languagesByShortName.keySet());
    }

    public static ArrayList<String> languageLongNames() {
        return new ArrayList<String>(languagesByLongName.keySet());
    }

    public static void addLanguage(String shortName, String longName) {
 //       System.out.println("addLanguage "+shortName+" "+longName);
        languagesByShortName.put(shortName, longName);
        languagesByLongName.put(longName, shortName);
    }

    public static String longToShort(String longName) {
        // if it can not be translated, assume that longName is a short name returned by shortToLong()
        String shortName = languagesByLongName.get(longName);
//                      MsgTextPane.write("longToShort "+longName+" > "+shortName);
        if (shortName == null) {
            return longName;
        } else {
            return shortName;
        }
    }

    public static String shortToLong(String shortName) {
        // if shortName can not be translated, return shortName 
        String longName = languagesByShortName.get(shortName);
//              MsgTextPane.write("shortToLong "+shortName+" > "+longName);
        if (longName == null) {
            return shortName;
        } else {
            return longName;
        }
    }

    public static boolean readLanguages() {

        BufferedReader inputStream = null;
        String fileName;
        int count = 0;
        Cluster c = null;
        Sentence s = null;

        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        fileName = defaultFolder + "\\Tatoeba\\TatoebaLanguages.txt";

        try {

            File initialFile = new File(fileName);
            InputStream is = new FileInputStream(initialFile);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            inputStream = new BufferedReader(isr);

            count = 0;

            MsgTextPane.write("reading languages...");
            String l;
            int linecount = 0;
            while ((l = inputStream.readLine()) != null) {
                linecount++;
                String[] ls = l.split("[|;\u0009]");
                if (ls.length == 2) {
                    String shortName = ls[0];
                    String longName = ls[1];

                    if (!shortName.equals("") && !longName.equals("")) {
                        count++;
                        LanguageNames.addLanguage(shortName, longName);
//                        MsgTextPane.write("add language " + shortName + "|" + longName);
                    } else {
                        MsgTextPane.write("invalid line " + linecount + " |" + l + "|");
                    }
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

        MsgTextPane.write(count + " languages read from " + fileName);

        return true;
    }

}
