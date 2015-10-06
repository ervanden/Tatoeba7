package langeditor;

import javax.swing.text.*;
import java.util.*;
import java.awt.Color;
import javax.swing.JTextPane;
import utils.MsgTextPane;

public class DictUtils {

    static LanguageEditorFrame frame=null;
    
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
        StyledDocument docDict = null;
//        StyledDocument docDict = LanguageEditorFrame.docDict;
        if (LanguageContext.getFrame() == null) {
            MsgTextPane.write("getFrame()==null can not find docDict");
        } else {
            docDict = LanguageContext.getFrame().docDict;
        }

        if (docDict == null) {
            System.out.println(str);
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

        StyledDocument docDict = null;
//        StyledDocument docDict = LanguageEditorFrame.docDict;
        if (LanguageContext.getFrame() == null) {
            MsgTextPane.write("getFrame()==null :  no docDict");
        } else {
            docDict = LanguageContext.getFrame().docDict;
        }

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
                startWordPosition = WordUtils.nextAlphabetic(docDict, position);
                if (startWordPosition >= endPosition) {
                    nextWord = false;
                } else {
                    endWordPosition = WordUtils.nextNonAlphabetic(docDict, startWordPosition);
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


    public static void writeDictArea(String s, boolean bold) {

        StyledDocument docDict = null;
//        StyledDocument docDict = LanguageEditorFrame.docDict;
        if (LanguageContext.getFrame() == null) {
            MsgTextPane.write("getFrame()==null : no docDict");
        } else {
            docDict = LanguageContext.getFrame().docDict;
        }

        if (docDict == null) {
            System.out.println(s);
            return;
        }
        
        try {
            if (bold) {
                docDict.insertString(docDict.getLength(), s, sas_bold);
            } else {
                docDict.insertString(docDict.getLength(), s, sas_noBold);
            }
        } catch (BadLocationException blex) {
        }
    }

    public static void scrollEnd() {
        JTextPane dictArea = null;

        if (LanguageContext.getFrame() == null) {
            MsgTextPane.write("getFrame()==null : no docDict");
        } else {
            dictArea = LanguageContext.getFrame().dictArea;
        }
        if (dictArea == null) {
            return;
        }
        dictArea.setCaretPosition(LanguageContext.getFrame().docDict.getLength());
    }

}
