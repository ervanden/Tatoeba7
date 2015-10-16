package langeditor;

import languages.LanguageContext;
import dictionaries.WordUtils;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import utils.AreaFont;
import utils.MsgTextPane;
import languages.Language;

public class LanguageTextPane extends JTextPane {

    LanguageTextPane thisTextPane;
    Language language;

    boolean autoCorrect = true;
    boolean finalInsert = false; // prevents correction/insertion infinite loop
    boolean manualCorrect = true; // set during correction of selected text
    // store position of last selected text for "correct selected text" function
    public int selectedPosition = 0;
    public int selectedLength = 0;

    public Language getLanguage(){
        return language;
    }
    
    public void setAutoCorrect(boolean b) {
        autoCorrect = b;
    }

    public void setFinalInsert(boolean b) {
        finalInsert = b;
    }

    public void setManualCorrect(boolean b) {
        manualCorrect = b;
    }

    public LanguageTextPane(String lang) {       
        language=LanguageContext.get(lang);      
        this.getStyledDocument().addDocumentListener(editAreaListener);
        this.addCaretListener(editAreaCaretListener);
        thisTextPane = this;
    }
    
   public void displayParameters() {
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // 2 pixels around text in JTextPane    
        setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));
    }

    class SubstitutionTask implements Runnable {

        private StyledDocument doc;
        private int position, length;

        SubstitutionTask(int position, int length) {
            this.doc = thisTextPane.getStyledDocument();
            this.position = position;
            this.length = length;
        }

        public void run() {
            String selection;

            int wordposition = position;
            int wordlength = length;

            try {
                selection = doc.getText(position, length);
//        System.out.println("inverting "+selection);
                if (selection.matches("[0-9]+")){
                   selection = language.number(Integer.valueOf(selection)); 
                } else {
                selection = language.invertDiacritics(selection);
                }
//        System.out.println("inverted= "+selection);
                finalInsert = true;
                doc.remove(position, length);
                doc.insertString(position, selection, doc.getStyle("default"));
                finalInsert = false;

            //  put the words in the dictionary
                // First extend the selection to complete words
                wordposition = WordUtils.startOfWord(doc, position);
                wordlength = WordUtils.endOfWord(doc, position + length) - wordposition;

                String selectedString = doc.getText(wordposition, wordlength);
//            MsgTextPane.write("selection <" + selectedString + ">");

                // remove all punctuation from the selected string
                for (int i = 0; i < selectedString.length(); i++) {
                    if (!Character.isAlphabetic(selectedString.codePointAt(i))) {
                        selectedString = selectedString.replace(selectedString.charAt(i), ' ');
                    }
                }
                // split in words  and put the words in the dictionary
                String[] words = selectedString.split(" ");
                for (String word : words) {
//                MsgTextPane.write("selected word <" + word + ">");
                    if (word.length() != 0) {
                        language.dictionary().addWord(word.replaceAll("I", "ı").replaceAll("İ", "i").toLowerCase());
                    }
                }
            } catch (BadLocationException ex) {
                MsgTextPane.write("BadLocationException " + wordposition);
                ex.printStackTrace();
                System.exit(1);
            };

        }
    }

    class RunDictionaryTask implements Runnable {

        private Document doc;
        private int position, length;

        RunDictionaryTask(int position, int length) {
            this.doc = thisTextPane.getStyledDocument();
            this.position = position;
            this.length = length;
        }

        public void run() {
            language.dictionary().runDictionary(thisTextPane, position, length);
        }
    }

    class setAttributesTask implements Runnable {

        private int position, length;
        private SimpleAttributeSet sas;

        setAttributesTask(int position, int length) {
            this.position = position;
            this.length = length;
        }

        public void run() {
            sas = new SimpleAttributeSet();
            StyleConstants.setFontSize(sas, AreaFont.getSize());
            thisTextPane.getStyledDocument().setCharacterAttributes(position, length, sas, false);

        }
    }

    DocumentListener editAreaListener = new DocumentListener() {

        public void insertUpdate(DocumentEvent e) {
            int position = e.getOffset();
            int length = e.getLength();

            if (autoCorrect && !finalInsert) {
                SwingUtilities.invokeLater(new RunDictionaryTask(position, length));
            }

            SwingUtilities.invokeLater(new setAttributesTask(position, length));
        }

        public void removeUpdate(DocumentEvent e) {
        }

        public void changedUpdate(DocumentEvent e) {
        }

    };

    CaretListener editAreaCaretListener = new CaretListener() {

        public void caretUpdate(CaretEvent e) {
            int position = e.getMark();
            int length = e.getDot() - e.getMark();
            if (length < 0) {
                position = position + length;
                length = -length;
            }

            selectedPosition = position;
            selectedLength = length;

            if (manualCorrect) {
                if (length > 0) {
                    if (length < 6) {
                        SwingUtilities.invokeLater(
                                new SubstitutionTask(position, length));
                    }
                }
            }
        }
    };

}
