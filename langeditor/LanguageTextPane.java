package langeditor;

import languages.LanguageContext;
import dictionaries.WordUtils;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URI;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import utils.AreaFont;
import utils.MsgTextPane;
import languages.Language;

public class LanguageTextPane extends JTextPane {

    LanguageTextPane thisTextPane;
    String languageCode;
    Language language;
    DefaultHighlighter.DefaultHighlightPainter highlightPainter = null;
    DefaultHighlighter.DefaultHighlightPainter highlightPainterBabla
            = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
    DefaultHighlighter.DefaultHighlightPainter highlightPainterWiktionary
            = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
    public String lookupSource = "";
    public boolean autoCorrect = true;

    // store position of caret and selected text (not used)
    public int selectedPosition = 0;
    public int selectedLength = 0;

    public Language getLanguage() {
        return language;
    }

    public void setAutoCorrect(boolean b) {
        autoCorrect = b;
    }

    public void removeHighlights() {
        thisTextPane.getHighlighter().removeAllHighlights();
    }

    public LanguageTextPane(String lang) {
        languageCode = lang;
        language = LanguageContext.get(lang);
        this.getStyledDocument().addDocumentListener(areaListener);
        this.addCaretListener(areaCaretListener);
        this.addKeyListener(areaKeyListener);
        thisTextPane = this;
    }

    public void displayParameters() {
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // 2 pixels around text in JTextPane    
        setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));
    }

    public void lookupWord(int position, String site) {
        SwingUtilities.invokeLater(new LookupTask(position, site));
    }

    class LookupTask implements Runnable {

        private StyledDocument doc;
        private int position;
        private String site;

        LookupTask(int position, String site) {
            this.doc = thisTextPane.getStyledDocument();
            this.position = position;
            this.site = site;
        }

        public void run() {

            int wordposition = 0;
            int wordlength = 0;

            try {
                wordposition = WordUtils.startOfWord(doc, position);
                wordlength = WordUtils.endOfWord(doc, position) - wordposition;

                thisTextPane.getHighlighter().removeAllHighlights();
                thisTextPane.getHighlighter().addHighlight(wordposition, wordposition + wordlength,
                        highlightPainter);

                String selectedString = doc.getText(wordposition, wordlength);
                if (selectedString.length() > 0) {
                    String url = "none";
                    if (languageCode.equals("fra")) {
                        if (site.equals("Babla")) {
                            url = "http://en.bab.la/dictionary/french-english/" + selectedString;
                        }
                        if (site.equals("Wiktionary")) {
                            url = "https://fr.wiktionary.org/wiki/" + selectedString + "#fr";
                        }
                    } else if (languageCode.equals("pol")) {
                        if (site.equals("Babla")) {
                            url = "http://en.bab.la/dictionary/polish-english/" + selectedString;
                        }
                        if (site.equals("Wiktionary")) {
                            url = "https://pl.wiktionary.org/wiki/" + selectedString + "#pl";
                        }
                    } else if (languageCode.equals("tur")) {
                        if (site.equals("Babla")) {
                            url = "http://en.bab.la/dictionary/turkish-english/" + selectedString;
                        }
                        if (site.equals("Wiktionary")) {
                            url = "https://tr.wiktionary.org/wiki/" + selectedString + "#tr";
                        }
                    }
                    MsgTextPane.write("Opening site  <" + url + ">");
                    try {
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (Exception ioe) {
                        MsgTextPane.write("Can not open browser ");
                    }
                } else {
                    MsgTextPane.write("No word at cursor position " + wordposition + ". No lookup");

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
            language.dictionary().correctText(thisTextPane, position, length);
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

    DocumentListener areaListener = new DocumentListener() {

        public void insertUpdate(DocumentEvent e) {
            int position = e.getOffset();
            int length = e.getLength();
            if (autoCorrect) {
                SwingUtilities.invokeLater(new RunDictionaryTask(position, length));
            }

            SwingUtilities.invokeLater(new setAttributesTask(position, length));
        }

        public void removeUpdate(DocumentEvent e) {
        }

        public void changedUpdate(DocumentEvent e) {
        }

    };

    CaretListener areaCaretListener = new CaretListener() {

        public void caretUpdate(CaretEvent e) { // store caret position and selected text
            int position = e.getMark();
            int length = e.getDot() - e.getMark();
            if (length < 0) {
                position = position + length;
                length = -length;
            }
            selectedPosition = position;
            selectedLength = length;
            if (lookupSource.equals("Babla")) {
                highlightPainter = highlightPainterBabla;
                lookupWord(selectedPosition, "Babla");
            }
            if (lookupSource.equals("Wiktionary")) {
                highlightPainter = highlightPainterWiktionary;
                lookupWord(selectedPosition, "Wiktionary");
            }
        }
    };

    KeyListener areaKeyListener = new KeyListener() {

        public String invertChar(char c) {

            ArrayList<Character> letterGroupsArray = new ArrayList<>();

            String letterGroups = language.diacriticsGroups();
            String[] groups = letterGroups.split(" +");

            // create an array of characters so that every character is followed by the replacing character on INSERT
            //           System.out.println("---- groups");
            for (String group : groups) {
                //               System.out.println("group " + group);
                for (Character cc : group.toCharArray()) {
                    letterGroupsArray.add(cc);
                };
                letterGroupsArray.add(group.charAt(0));
            }
            //          System.out.println("----");

            if (letterGroupsArray.contains(c)) {
                if (c != ' ') {
                    int j = letterGroupsArray.indexOf(c);
                    if (j >= 0) {
                        c = letterGroupsArray.get(j + 1);
                    } // if c is not in the array it is not replaced
                }
            };
            return "" + c;

        }

        public void keyPressed(KeyEvent e) {
//System.out.println("Pressed <" + e.getKeyChar() + "> <" + e.getKeyCode() + "> " + " at position " + selectedPosition);
        }

        public void keyReleased(KeyEvent e) {
//System.out.println("Released <" + e.getKeyChar() + "> <" + e.getKeyCode() + "> " + " at position " + selectedPosition);
            if (e.getKeyCode() == KeyEvent.VK_INSERT) {

                try {
                    StyledDocument doc = thisTextPane.getStyledDocument();
                    String selection = doc.getText(selectedPosition - 1, 1);
                    String newSelection = invertChar(selection.charAt(0));

                    if (!newSelection.equals(selection)) {
                        doc.remove(selectedPosition - 1, 1);
                        autoCorrect = false;
                        doc.insertString(selectedPosition, newSelection, doc.getStyle("default"));
                        autoCorrect = true;
                        thisTextPane.setCaretPosition(selectedPosition); // because insertion advances caret

                        // put the words in the dictionary
                        // this code handles text with multiple words
                        // First extend the selection to complete words
                        int wordposition = WordUtils.startOfWord(doc, selectedPosition);
                        int wordlength = WordUtils.endOfWord(doc, selectedPosition) - wordposition;

                        String selectedString = doc.getText(wordposition, wordlength);

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
                    }

                } catch (BadLocationException ex) {
                    // happens when pressed INSERT at beginning of text
                };

            };
        }

        ;
            public void keyTyped(KeyEvent e) {
        }
    ;
};

}
