package langeditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.util.*;

import utils.AreaFont;
import utils.MsgTextPane;

public class DictionaryFrame extends JFrame implements ActionListener {

//    private DictionaryFrame thisLanguageEditorFrame = this;
    private JFrame thisFrame = (JFrame) this;
    public JTextPane dictArea = null;  // public static for scrollEnd() function
    public StyledDocument docDict = null;   // dictionary area, accessed from DocUtils

    JScrollPane scrollingDictArea;

    JPanel content = new JPanel();

    JButton buttonPlus = new JButton("+");
    JButton buttonMinus = new JButton("-");
    JButton buttonAddWord = new JButton("+");
    JButton buttonAddStem = new JButton("[+]");
    JButton buttonRemoveWord = new JButton("-");
    JButton buttonRemoveStem = new JButton("[-]");
    JTextField textFieldPattern = new JTextField(20);
    JTextField textFieldDictFileName = new JTextField("");
    Dimension textFieldDictFileNameSize;

    class setAttributesTask2 implements Runnable {

        private int position, length;
        private SimpleAttributeSet sas;

        setAttributesTask2(int position, int length) {
            this.position = position;
            this.length = length;
        }

        public void run() {
            sas = new SimpleAttributeSet();
            StyleConstants.setFontSize(sas, AreaFont.getSize());
            docDict.setCharacterAttributes(position, length, sas, false);
        }
    }

    class ManualSelectTask implements Runnable {

        private int position, length;

        ManualSelectTask(int position, int length) {
            this.position = position;
            this.length = length;
        }

        public void run() {
            manualSelectDictArea(position, length);
        }
    }

    class WindowUtils extends WindowAdapter {

        public void windowClosing(WindowEvent e) {
            thisFrame.setVisible(false);
        }
    }

    DocumentListener dictAreaListener = new DocumentListener() {

        public void insertUpdate(DocumentEvent e) {
//            LanguageContext.set(thisLanguageEditorFrame,editorLanguage,"dictAreaListener insertUpdate");
            int position = e.getOffset();
            int length = e.getLength();
            SwingUtilities.invokeLater(new setAttributesTask2(position, length));
        }

        public void removeUpdate(DocumentEvent e) {
        }

        public void changedUpdate(DocumentEvent e) {
        }

    };

    CaretListener dictAreaCaretListener = new CaretListener() {

        public void caretUpdate(CaretEvent e) {
//            LanguageContext.set(thisLanguageEditorFrame,editorLanguage,"dictAreaCaretListener caretUpdate");
            int position = e.getMark();
            int length = e.getDot() - e.getMark();
            if (length != 0) { // called when dict area is written to > erases selection
                if (length < 0) {
                    position = position + length;
                    length = -length;
                }
                SwingUtilities.invokeLater(new ManualSelectTask(position, length));
            }

            //     MsgTextPane.write("caret in dict area pos="+position+"length="+length+" "+dictArea.getCharacterAttributes().toString());
        }

    };

    public void actionPerformed(ActionEvent ae) {

        String action = ae.getActionCommand();

//        LanguageContext.set(thisLanguageEditorFrame,editorLanguage,"actionPerformed "+action);
        if (action.equals("Save dictionary and exit")) {
            if (LanguageContext.get().dictionary().saveDictionary(LanguageContext.get().dictionary().dictionaryFileName)) {
//                thisFrame.setVisible(false);
                thisFrame.dispose();
            }
        }

        if (action.equals("Save dictionary")) {
            LanguageContext.get().dictionary().saveDictionary(LanguageContext.get().dictionary().dictionaryFileName);
        }

        if (action.equals("Exit without saving dictionary")) {
            //                          thisFrame.setVisible(false);
            thisFrame.dispose();
        }

        if (action.equals("Create a backup dictionary")) {
            Date dNow = new Date();
            LanguageContext.get().dictionary().saveDictionary(LanguageContext.get().dictionary().dictionaryFileName
                    + String.format(" %1$te%1$tb%1$ty %1$tHh%1$tM", dNow));
        }

        if (action.equals("Optimize word dictionary")) {
            LanguageContext.get().dictionary().optimizeWords();
        }

        if (action.equals("Optimize stem dictionary")) {
            LanguageContext.get().dictionary().optimizeStems();
        }

        if (action.equals("buttonAddWord")) {
            for (String word : selectedWords) {
                LanguageContext.get().dictionary().addWord(word);
            }
        }

        if (action.equals("buttonAddStem")) {
            for (String word : selectedWords) {
                LanguageContext.get().dictionary().addStem(word);
            }
        }

        if (action.equals("buttonRemoveWord")) {
            for (String word : selectedWords) {
                LanguageContext.get().dictionary().removeWord(word);
            }
        }

        if (action.equals("buttonRemoveStem")) {
            for (String word : selectedWords) {
                LanguageContext.get().dictionary().removeStem(word);
            }
        }

        if (action.equals("buttonPlus")) {
            AreaFont.multiply((float) 1.2);
            SimpleAttributeSet sas = new SimpleAttributeSet();
            StyleConstants.setFontSize(sas, AreaFont.getSize());
            docDict.setCharacterAttributes(0, docDict.getLength(), sas, false);
            dictArea.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));

        }

        if (action.equals("buttonMinus")) {
            AreaFont.multiply((float) 0.8);
            SimpleAttributeSet sas = new SimpleAttributeSet();
            StyleConstants.setFontSize(sas, AreaFont.getSize());
            docDict.setCharacterAttributes(0, docDict.getLength(), sas, false);
            dictArea.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));
        }

    }

    private void AddMenuItem(JMenu menu, String name, String actionName) {
        JMenuItem menuItem;
        menuItem = new JMenuItem(name);
        menuItem.addActionListener(this);
        menuItem.setActionCommand(actionName);
        menu.add(menuItem);
    }

    private void displayGUI() {

        scrollingDictArea.setPreferredSize(new Dimension(450, 300));

        JPanel p1 = new JPanel();
        p1.add(buttonPlus);
        p1.add(buttonMinus);
        p1.add(textFieldDictFileName);

        JPanel p2 = new JPanel();
        p2.add(textFieldPattern);
        p2.add(buttonAddWord);
        p2.add(buttonRemoveWord);
        p2.add(buttonAddStem);
        p2.add(buttonRemoveStem);

        content.add(p1);
        content.add(p2);
        content.add(scrollingDictArea);

        JMenuBar menuBar;
        JMenu menuExit;
        JMenu menuDictionary;

        menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        menuExit = new JMenu("Exit");
        menuBar.add(menuExit);
        AddMenuItem(menuExit, "Save dictionary and exit", "Save dictionary and exit");
        AddMenuItem(menuExit, "Exit without saving dictionary", "Exit without saving dictionary");
        menuDictionary = new JMenu("Dictionary");
        menuBar.add(menuDictionary);
        AddMenuItem(menuDictionary, "Save", "Save dictionary");
        AddMenuItem(menuDictionary, "Backup", "Create a backup dictionary");
        AddMenuItem(menuDictionary, "Optimize", "Optimize word dictionary");
        AddMenuItem(menuDictionary, "Recreate stems", "Optimize stem dictionary");
        AddMenuItem(menuDictionary, "Words from Web", "words from web");

    }

    public DictionaryFrame() {

        dictArea = new JTextPane();
        dictArea.setMinimumSize(new Dimension(300, 300));
        dictArea.setPreferredSize(new Dimension(300, 300));
        dictArea.setMaximumSize(new Dimension(300, 300));
        dictArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // 2 pixels around text in JTextPane    
        dictArea.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));
        scrollingDictArea = new JScrollPane(dictArea);

        //       content.setLayout(new GridBagLayout());
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        buttonPlus.addActionListener(this);
        buttonPlus.setActionCommand("buttonPlus");
        buttonMinus.addActionListener(this);
        buttonMinus.setActionCommand("buttonMinus");

        buttonAddWord.addActionListener(this);
        buttonAddStem.addActionListener(this);
        buttonRemoveWord.addActionListener(this);
        buttonRemoveStem.addActionListener(this);
        buttonAddWord.setActionCommand("buttonAddWord");
        buttonAddStem.setActionCommand("buttonAddStem");
        buttonRemoveWord.setActionCommand("buttonRemoveWord");
        buttonRemoveStem.setActionCommand("buttonRemoveStem");

        textFieldDictFileName.setText(LanguageContext.get().dictionaryFileName());
        textFieldDictFileName.setEditable(false);

        textFieldPattern.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //                       LanguageContext.set(thisLanguageEditorFrame,editorLanguage,"textfieldpattern ");
                LanguageContext.get().dictionary().dictionaryPattern = textFieldPattern.getText();
                LanguageContext.get().dictionary().printAll();
                scrollEnd();
                dictArea.setCaretPosition(docDict.getLength());
            }
        ;
        }
      ); 
      
        textFieldPattern.setText("");

        displayGUI();
        setContentPane(content);
        setTitle("Dictionary Frame");
        pack();
        setLocationRelativeTo(null);
        setVisible(false);

        dictArea.addCaretListener(dictAreaCaretListener);
        docDict = dictArea.getStyledDocument();
        docDict.addDocumentListener(dictAreaListener);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowUtils());

    }
    

    
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
    
    

    public void writeSelectDictArea(String str) {
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

    public void manualSelectDictArea(int position, int length) {

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


    public void writeDictArea(String s, boolean bold) {
        
        try {
            if (bold) {
                docDict.insertString(docDict.getLength(), s, sas_bold);
            } else {
                docDict.insertString(docDict.getLength(), s, sas_noBold);
            }
        } catch (BadLocationException blex) {
        }
    }

    public void scrollEnd() {
        dictArea.setCaretPosition(docDict.getLength());
    }

}
   
   
