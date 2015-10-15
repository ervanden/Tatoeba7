package dictionary;

import javax.swing.text.*;
import utils.MsgTextPane;

public class WordUtils {

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

    public static boolean isLetter(char c) {
        return (Character.isAlphabetic(c) || (c == '\''));
    }

    public static char toUpperCase(char c) {
        if (c == 'i') {   // toUpperCase does not do I correctly
            return 'İ';
        } else {
            return Character.toUpperCase(c);
        }
    }

    public static int nextAlphabetic(Document doc, int position) {
        // ' is considered alphabetic so that Ali'nin is a single word and vowel correction is applied to 'nin also
        try {
            while (position < doc.getLength()
                    && !(isLetter(doc.getText(position, 1).charAt(0)))) {
                position++;
            };
        } catch (BadLocationException ex) {
            MsgTextPane.write("BadLocationException in nextAlphabetic " + position);
            ex.printStackTrace();
            System.exit(1);
        };
        return position;

    }

    public static int nextNonAlphabetic(Document doc, int position) {
        try {
            while (position < doc.getLength()
                    && (isLetter(doc.getText(position, 1).charAt(0)))) {
                position++;
            };
        } catch (BadLocationException ex) {
            MsgTextPane.write("BadLocationException in nextAlphabetic " + position);
            ex.printStackTrace();
            System.exit(1);
        };

        return position;

    }

    public static int startOfWord(Document doc, int position) {
        try {

            boolean found = false;
            while (!found) {
                if (position == 0) {
                    found = true;
                } else {
                    if (isLetter(doc.getText(position - 1, 1).charAt(0))) {
                        position--;
                    } else {
                        found = true;
                    };

                }
            }
        } catch (BadLocationException ex) {
            MsgTextPane.write("BadLocationException in startOfWord " + position);
            ex.printStackTrace();
            System.exit(1);
        };
        return position;

    }

    public static int endOfWord(Document doc, int position) {
        try {
            while (position < doc.getLength()
                    && isLetter(doc.getText(position, 1).charAt(0))) {
                position++;
            };
        } catch (BadLocationException ex) {
            MsgTextPane.write("BadLocationException in endOfWord " + position);
            ex.printStackTrace();
            System.exit(1);
        };

        return position;
    }
}

