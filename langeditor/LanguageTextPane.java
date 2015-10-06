package langeditor;

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

public class LanguageTextPane extends JTextPane {

    LanguageTextPane thisTextPane;
    LanguageEditorFrame parent=null;  // set when this is used as editArea in a LanguageEditorFrame
    String textPaneLanguage;

    boolean autoCorrect = true;
    static boolean finalInsert = false; // prevents correction/insertion infinite loop
    boolean manualCorrect = true; // set during correction of selected text
    // store position of last selected text for "correct selected text" function
    public int selectedPosition = 0;
    public int selectedLength = 0;

    public void setAutoCorrect(boolean b) {
        autoCorrect = b;
    }

    public void setFinalInsert(boolean b) {
        finalInsert = b;
    }

    public void setManualCorrect(boolean b) {
        manualCorrect = b;
    }

    public LanguageTextPane(String language) {
        textPaneLanguage = language;
        this.getStyledDocument().addDocumentListener(editAreaListener);
        this.addCaretListener(editAreaCaretListener);
        thisTextPane = this;
        LanguageContext.set(parent,language,"LanguageTextPane constructor");
        /*        
         LanguageContext.get().dictionary().dictionaryFileName=LanguageContext.get().dictionaryFilename();
         if (!dictionaryIsRead){
         LanguageContext.get().dictionary().readDictionaryFromFile(LanguageContext.get().dictionary().dictionaryFileName);
         dictionaryIsRead=true;        
         }
         */
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
                selection = LanguageContext.get().invertDiacritics(selection);
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
                        LanguageContext.get().dictionary().addWord(word.replaceAll("I", "ı").replaceAll("İ", "i").toLowerCase());
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
            LanguageContext.get().dictionary().runDictionary((StyledDocument) doc, position, length);
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
            LanguageContext.set(parent,textPaneLanguage,"editAreaListener insertUpdate");
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
            LanguageContext.set(parent,textPaneLanguage,"editAreaCaretListener caretUpdate");
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
