package langeditor;

import javax.swing.text.*;
import java.util.*;
import java.awt.Color;
import javax.swing.JTextPane;
import utils.MsgTextPane;
import static langeditor.LanguageEditorFrame.docDict;

public class DocUtils {

    static SimpleAttributeSet sas_underline = new SimpleAttributeSet();
    static SimpleAttributeSet sas_noUnderline = new SimpleAttributeSet();
    static SimpleAttributeSet sas_bold = new SimpleAttributeSet();
    static SimpleAttributeSet sas_noBold = new SimpleAttributeSet();
    static SimpleAttributeSet sas_red = new SimpleAttributeSet();

    static {
        StyleConstants.setUnderline(sas_underline, true);
        StyleConstants.setUnderline(sas_noUnderline, false);
        StyleConstants.setBold(sas_bold, true);
        StyleConstants.setBold(sas_noBold, false);
        StyleConstants.setForeground(sas_red, Color.RED);
    }

    static int selectedPos = 0;     // first char of first selected word
    static int selectedLength = 0;   // gives last char of last selected word
    static ArrayList<String> selectedWords = new ArrayList<String>();

    public static void writeSelectDictArea(String str) {

        StyledDocument docDict = LanguageEditorFrame.docDict;

        if (docDict == null) {
            return;
        }

        docDict.setCharacterAttributes(selectedPos, selectedLength, sas_noUnderline, false);

        selectedPos = docDict.getLength();
        selectedLength = str.length();
        selectedWords.clear();
        selectedWords.add(str);
        try {
            docDict.insertString(docDict.getLength(), str, sas_underline);
        } catch (BadLocationException blex) {
            MsgTextPane.write(" bad location in writeSelectDictArea");
        }
        docDict.setCharacterAttributes(selectedPos, selectedLength, sas_underline, false);
    }

    public static void manualSelectDictArea(int position, int length) {

        StyledDocument docDict = LanguageEditorFrame.docDict;

        if (docDict == null) {
            return;
        }

        docDict.setCharacterAttributes(selectedPos, selectedLength, sas_noUnderline, false);

        selectedPos = position;
        selectedLength = length;
        selectedWords.clear();
        int startWordPosition = 0;
        int endWordPosition = 0;
        String word;
        int endPosition = position + length;

        boolean nextWord = true;
        while (nextWord) {
            try {
                startWordPosition = DocUtils.nextAlphabetic(docDict, position);
                if (startWordPosition >= endPosition) {
                    nextWord = false;
                } else {
                    endWordPosition = DocUtils.nextNonAlphabetic(docDict, startWordPosition);
                    if (endWordPosition >= endPosition) {
                        endWordPosition = endPosition;
                        nextWord = false;
                    }

                    word = docDict.getText(startWordPosition, endWordPosition - startWordPosition);
                    docDict.setCharacterAttributes(startWordPosition, endWordPosition - startWordPosition, sas_underline, false);

                    selectedWords.add(word);
                }

            } catch (BadLocationException blex) {
            }
            position = endWordPosition;
        }
    }

    private static void writeArea(StyledDocument doc, String s, boolean bold) {
        try {
            if (bold) {
                doc.insertString(doc.getLength(), s, sas_bold);
            } else {
                doc.insertString(doc.getLength(), s, sas_noBold);
            }
        } catch (BadLocationException blex) {
        }
    }

    public static void writeDictArea(String s, boolean bold) {

        StyledDocument docDict = LanguageEditorFrame.docDict;

        if (LanguageEditorFrame.docDict == null) {
            return;
        }

        writeArea(LanguageEditorFrame.docDict, s, bold);
    }

    public static void scrollEnd() {
        JTextPane dictArea = LanguageEditorFrame.dictArea;
        if (dictArea==null) return;
        dictArea.setCaretPosition(docDict.getLength());
    };

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
