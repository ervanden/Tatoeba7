package utils;

import languagetrainer.LanguageTrainer;

// Common text frame for system messages
public class MsgTextPane {

    static GenericTextPanel panel;

    public static void write(String msg) {
        panel = LanguageTrainer.messageTextPanel;
        if (panel == null) {
            System.out.println(msg);
        } else {
            panel.write(msg);
        }
    }

    public static void setVisible(boolean b) {

    }
}
