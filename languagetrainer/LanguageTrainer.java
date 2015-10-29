package languagetrainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import static languages.LanguageNames.longToShort;
import utils.*;

public class LanguageTrainer {

    static LanguageTrainerFrame languageTrainerFrame;
    public static GenericTextPanel messageTextPanel;
    public static ArrayList<Entry> userParameters = new ArrayList<>();
    public static ArrayList<String> userLanguages = new ArrayList<>();
    public static String targetLanguage = "eng";
    public static String sourceLanguage = "eng";

    private static void readParameters() {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        String fileName = defaultFolder + "\\Tatoeba\\LanguageTrainer.txt";
        MsgTextPane.write("Reading " + fileName);
        File fileIn = new File(fileName);
        try {
            InputStream is = new FileInputStream(fileIn);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader in = new BufferedReader(isr);
            String l;
            String[] ls;
            int lineNr = 0;
            while ((l = in.readLine()) != null) {
                lineNr++;
                ls = l.split("\\=");
                if (ls.length < 2) {
                    MsgTextPane.write("invalid line [" + lineNr + "] : " + l);
                } else {
                    String key = ls[0];
                    key = key.replaceAll("^ *", "");
                    key = key.replaceAll(" *$", "");
                    String value = ls[1];
                    value = value.replaceAll("^ *", "");
                    value = value.replaceAll(" *$", "");

                    userParameters.add(new Entry(key, value));
                }
            };
            in.close();

        } catch (IOException i) {
            MsgTextPane.write("Error when reading this file");
        }

        for (Entry e : userParameters) {
            MsgTextPane.write(e.key + "=" + e.value);
        }
    }

    public static void main(String[] args) {
        messageTextPanel = new GenericTextPanel(780,500);
        readParameters();
        languages.LanguageNames.readLanguages();

        for (Entry e : userParameters) {
            if (e.key.equals("language")) {
                userLanguages.add(longToShort(e.value));
            } else if (e.key.equals("target")) {
                targetLanguage = e.value;
            } else if (e.key.equals("source")) {
                sourceLanguage = e.value;
            } else {
                MsgTextPane.write("invalid parameter : " + e.key);
            }
        }

        languageTrainerFrame = new LanguageTrainerFrame();
        languageTrainerFrame.setVisible(true);

    }
}
