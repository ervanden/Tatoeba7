package langeditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.util.*;

import utils.MsgTextPane;
import utils.AreaFont;

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
        LanguageEditor.languageEditorFrame.docDict.setCharacterAttributes(position, length, sas, false);

    }
}

class ManualSelectTask implements Runnable {

    private int position, length;

    ManualSelectTask(int position, int length) {
        this.position = position;
        this.length = length;
    }

    public void run() {
        DocUtils.manualSelectDictArea(position, length);
    }
}

class LanguageEditorFrame extends JFrame implements ActionListener, ItemListener {

    private JFrame thisFrame = (JFrame) this;
    private boolean expertMode = false;

    private LanguageTextPane editArea;
    public static JTextPane dictArea = null;  // public static for scrollEnd() function
    private JTextPane msgArea;

    private JFileChooser fileChooser = new JFileChooser();
    private StyledDocument docEdit;          // edit area
    public static StyledDocument docDict = null;   // dictionary area, accessed from DocUtils

    JScrollPane scrollingEditArea;
    JScrollPane scrollingDictArea;
    JScrollPane scrollingMsgArea;

    JPanel content = new JPanel();

    JButton buttonPlus = new JButton("+");
    JButton buttonMinus = new JButton("-");
    JButton buttonAddWord = new JButton("+");
    JButton buttonAddStem = new JButton("[+]");
    JButton buttonRemoveWord = new JButton("-");
    JButton buttonRemoveStem = new JButton("[-]");
    JRadioButton radioButtonAuto = new JRadioButton("auto", null, true);
    JTextField textFieldPattern = new JTextField("pattern");
    JTextField textFieldDictFileName = new JTextField("");
    Dimension textFieldDictFileNameSize;

    class WindowUtils extends WindowAdapter {

        public void windowClosing(WindowEvent e) {
            if (expertMode) {
                JOptionPane.showMessageDialog(thisFrame, "Close window via Exit menu");
            } else {
                thisFrame.setVisible(false);
            }
        }
    }

    DocumentListener dictAreaListener = new DocumentListener() {

        public void insertUpdate(DocumentEvent e) {
            int position = e.getOffset();
            int length = e.getLength();
            SwingUtilities.invokeLater(new setAttributesTask2(position, length));
        }

        public void removeUpdate(DocumentEvent e) {
//        MsgTextPane.write("doc remove offset=" + e.getOffset() + " len=" + e.getLength());
        }

        public void changedUpdate(DocumentEvent e) {
//        MsgTextPane.write("doc change offset=" + e.getOffset() + " len=" + e.getLength());
        }

    };

    CaretListener dictAreaCaretListener = new CaretListener() {

        public void caretUpdate(CaretEvent e) {

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

    public void itemStateChanged(ItemEvent e) {

        Object source = e.getItemSelectable();

        if (source == radioButtonAuto) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                editArea.setAutoCorrect(true);
            };
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                editArea.setAutoCorrect(false);
            };
        };
    }

    public void actionPerformed(ActionEvent ae) {

        String action = ae.getActionCommand();

        if (action.equals("Correct selected text")) {
            editArea.setManualCorrect(false);
            Dictionary.markCorrection = true;
            Dictionary.runDictionary((StyledDocument) editArea.getStyledDocument(),
                    editArea.selectedPosition, editArea.selectedLength);
            Dictionary.markCorrection = false;
            editArea.setManualCorrect(true);
        }

        if (action.equals("Save dictionary and exit")) {
            if (Dictionary.saveDictionary(Dictionary.dictionaryFileName)) {
                System.exit(0);
            }
        }

        if (action.equals("Save dictionary")) {
            Dictionary.saveDictionary(Dictionary.dictionaryFileName);
        }

        if (action.equals("Exit without saving dictionary")) {
            System.exit(0);
        }

        /*
         if (action.equals("Change dictionary location")) {
         String dirName = "";
         JFileChooser fileChooser = new JFileChooser();
         fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
         int retval = fileChooser.showOpenDialog(this);
         if (retval == JFileChooser.APPROVE_OPTION) {
         // check if it is a directory !!!            
         File f = fileChooser.getSelectedFile();
         dirName = f.getAbsolutePath();
         Dictionary.dictionaryFileName = dirName + "\\TurkEditor.dictionary";
         textFieldDictFileName.setText(Dictionary.dictionaryFileName);
         }

         try {  // write dictionary location file
         File f = new File(Dictionary.dictionaryLocationFileName());
         OutputStream is = new FileOutputStream(f);
         OutputStreamWriter isr = new OutputStreamWriter(is, "UTF-8");
         BufferedWriter outputStream = new BufferedWriter(isr);

         outputStream.write(Dictionary.dictionaryFileName);
         outputStream.newLine();
         outputStream.close();

         } catch (IOException io) {
         MsgTextPane.write(" io exception while writing dictionary location file");
         }

         }
         */
        if (action.equals("Read another dictionary")) {

            String fileName = "";
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int retval = fileChooser.showOpenDialog(this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                fileName = f.getAbsolutePath();

                boolean confirm;
                ConfirmDialog cd = new ConfirmDialog();
                cd.popUp(LanguageEditor.languageEditorFrame,
                        "In-memory dictionary will be replaced with contents of " + fileName, "Continue", "Cancel");
                confirm = cd.confirm;

                if (confirm) {
                    Dictionary.words.clear();
                    Dictionary.stems.clear();
                    Dictionary.readDictionaryFromFile(fileName);
                }
            }
        }

        if (action.equals("Create a backup dictionary")) {
            Date dNow = new Date();
            Dictionary.saveDictionary(Dictionary.dictionaryFileName
                    + String.format(" %1$te%1$tb%1$ty %1$tHh%1$tM", dNow));
        }

        if (action.equals(
                "Optimize word dictionary")) {
            Dictionary.optimizeWords();
        }

        if (action.equals(
                "Optimize stem dictionary")) {
            Dictionary.optimizeStems();
        }

        if (action.equals("words from web")) {
            URLChooser urlChooser = new URLChooser();
            urlChooser.execute();

        }

        if (action.equals(
                "buttonAddWord")) {
            for (String word : DocUtils.selectedWords) {
                Dictionary.addWord(word);
            }
        }

        if (action.equals("buttonAddStem")) {
            for (String word : DocUtils.selectedWords) {
                Dictionary.addStem(word);
            }
        }

        if (action.equals("buttonRemoveWord")) {
            for (String word : DocUtils.selectedWords) {
                Dictionary.removeWord(word);
            }
        }

        if (action.equals("buttonRemoveStem")) {
            for (String word : DocUtils.selectedWords) {
                Dictionary.removeStem(word);
            }
        }

        if (action.equals("buttonPlus")) {
            AreaFont.multiply((float) 1.2);
            SimpleAttributeSet sas = new SimpleAttributeSet();
            StyleConstants.setFontSize(sas, AreaFont.getSize());
            docEdit.setCharacterAttributes(0, docEdit.getLength(), sas, false);
            docDict.setCharacterAttributes(0, docDict.getLength(), sas, false);
            editArea.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));
            dictArea.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));

        }

        if (action.equals("buttonMinus")) {
            AreaFont.multiply((float) 0.8);
            SimpleAttributeSet sas = new SimpleAttributeSet();
            StyleConstants.setFontSize(sas, AreaFont.getSize());
            docEdit.setCharacterAttributes(0, docEdit.getLength(), sas, false);
            docDict.setCharacterAttributes(0, docDict.getLength(), sas, false);
            editArea.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));
            dictArea.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));
        }

        if (action.equals("System messages on")) {
            expertMode = true;
            displayGUI(true);
        }

        if (action.equals("System messages off")) {
            expertMode = false;
            displayGUI(false);
        }

    }

    private void AddMenuItem(JMenu menu, String name, String actionName) {
        JMenuItem menuItem;
        menuItem = new JMenuItem(name);
        menuItem.addActionListener(this);
        menuItem.setActionCommand(actionName);
        menu.add(menuItem);
    }

    private GridBagConstraints newGridBagConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.ipadx = 0;
        c.ipady = 0;
//                c.insets=null;
        return c;
    }

    private void displayGUI(boolean on) {
        for (Component c : content.getComponents()) {
            content.remove(c);
        }
        if (on) {
            displayGUI1();
        } else {
            displayGUI2();
        }
    }

    private void displayGUI1() {
        textFieldDictFileName.setMinimumSize(textFieldDictFileNameSize);
        textFieldDictFileName.setPreferredSize(textFieldDictFileNameSize);
        scrollingDictArea.setMinimumSize(new Dimension(300, 300));
        scrollingDictArea.setMaximumSize(new Dimension(300, 300));
        scrollingDictArea.setPreferredSize(new Dimension(300, 300));
        scrollingEditArea.setMinimumSize(new Dimension(800, 300));
        scrollingEditArea.setMaximumSize(new Dimension(800, 300));
        scrollingEditArea.setPreferredSize(new Dimension(800, 300));
        scrollingMsgArea.setMinimumSize(new Dimension(800, 100));
        scrollingMsgArea.setMaximumSize(new Dimension(800, 100));
        scrollingMsgArea.setPreferredSize(new Dimension(800, 100));

        GridBagConstraints c;
        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(buttonPlus, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 5);  // top left bottom right
        content.add(buttonMinus, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.EAST;
        c.weightx = 1;
        c.gridx = 2;
        c.gridy = 0;
        content.add(textFieldDictFileName, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = 3;
        c.gridy = 0;
        content.add(radioButtonAuto, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        c.gridx = 4;     // first dictionary related item
        c.gridy = 0;
        content.add(textFieldPattern, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = 5;     // first dictionary related item
        c.gridy = 0;
        content.add(buttonAddWord, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = 6;     // first dictionary related item
        c.gridy = 0;
        content.add(buttonRemoveWord, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = 7;
        c.gridy = 0;
        content.add(buttonAddStem, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = 8;
        c.gridy = 0;
        content.add(buttonRemoveStem, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 4;            // align with number of editArea buttons
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        content.add(scrollingEditArea, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.weightx = 0.5;
        c.gridx = 4;   // align with first dictionary related component in toolbar
        c.gridy = 1;
        c.gridwidth = 5;   // align with number of dictArea buttons
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        content.add(scrollingDictArea, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 9;   // full width
        content.add(scrollingMsgArea, c);

        JMenuBar menuBar;
        JMenu menuExit;
        JMenu menuText;
        JMenu menuDictionary;
        JMenu menuExpert;

        menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        menuExit = new JMenu("Exit");
        menuBar.add(menuExit);
        AddMenuItem(menuExit, "Save dictionary and exit", "Save dictionary and exit");
        AddMenuItem(menuExit, "Exit without saving dictionary", "Exit without saving dictionary");
        menuText = new JMenu("Text");
        menuBar.add(menuText);
        AddMenuItem(menuText, "Correct selected text", "Correct selected text");
        menuDictionary = new JMenu("Dictionary");
        menuBar.add(menuDictionary);
        AddMenuItem(menuDictionary, "Save", "Save dictionary");
        AddMenuItem(menuDictionary, "Backup", "Create a backup dictionary");
        AddMenuItem(menuDictionary, "Optimize", "Optimize word dictionary");
        AddMenuItem(menuDictionary, "Recreate stems", "Optimize stem dictionary");
//        AddMenuItem(menuDictionary, "Change Folder", "Change dictionary location");
        AddMenuItem(menuDictionary, "Read", "Read another dictionary");
        AddMenuItem(menuDictionary, "Words from Web", "words from web");
        menuExpert = new JMenu("Expert Mode");
        menuBar.add(menuExpert);
        AddMenuItem(menuExpert, "Off", "System messages off");
        pack();
    }

    private void displayGUI2() {

        scrollingEditArea.setMinimumSize(new Dimension(1100, 400));
        scrollingEditArea.setMaximumSize(new Dimension(1100, 400));
        scrollingEditArea.setPreferredSize(new Dimension(1100, 400));

        GridBagConstraints c;
        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(buttonPlus, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 5);  // top left bottom right
        content.add(buttonMinus, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = 2;
        c.gridy = 0;
        content.add(radioButtonAuto, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;            // align with number of editArea buttons
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        content.add(scrollingEditArea, c);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuExit = new JMenu("Exit");
        JMenu menuDictionary = new JMenu("Dictionary");
        JMenu menuExpert = new JMenu("Expert");

        menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        menuExit = new JMenu("Exit");
        menuBar.add(menuExit);
        AddMenuItem(menuExit, "Exit", "Exit without saving dictionary");
        menuExpert = new JMenu("Expert Mode");
        menuBar.add(menuExpert);
        AddMenuItem(menuExpert, "On", "System messages on");
        pack();

    }

    public void display() {
        dictArea = new JTextPane();
        dictArea.setMinimumSize(new Dimension(300, 300));
        dictArea.setPreferredSize(new Dimension(300, 300));
        dictArea.setMaximumSize(new Dimension(300, 300));
        dictArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // 2 pixels around text in JTextPane    
        dictArea.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));
        scrollingDictArea = new JScrollPane(dictArea);

        editArea = new LanguageTextPane();
        editArea.setAutoCorrect(true);
        editArea.setFinalInsert(false);
        editArea.setManualCorrect(true);

        editArea.setMinimumSize(new Dimension(800, 300));
        editArea.setPreferredSize(new Dimension(800, 300));
        editArea.setMaximumSize(new Dimension(800, 300));
        editArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // 2 pixels around text in JTextPane    
        editArea.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));
        scrollingEditArea = new JScrollPane(editArea);

        msgArea = MsgTextPane.getMsgTextPane();
        msgArea.setMinimumSize(new Dimension(800, 100));
        msgArea.setPreferredSize(new Dimension(800, 100));
        msgArea.setMaximumSize(new Dimension(800, 100));
        msgArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // 2 pixels around text   
        msgArea.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));
        scrollingMsgArea = new JScrollPane(msgArea);

        content.setLayout(new GridBagLayout());

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

        radioButtonAuto.addItemListener(this);

        textFieldDictFileName.setText(Dictionary.dictionaryFileName);
        textFieldDictFileName.setEditable(false);
        textFieldDictFileNameSize = textFieldDictFileName.getPreferredSize();

        textFieldPattern.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Dictionary.dictionaryPattern = textFieldPattern.getText();
                Dictionary.printAll();
                DocUtils.scrollEnd();
                dictArea.setCaretPosition(docDict.getLength());
            }
        ;
        }
      ); 
      
        textFieldPattern.setText("");

        expertMode = false;
        displayGUI(false);

        setContentPane(content);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setTitle("TextEditor");
        pack();
        setLocationRelativeTo(null);
        setVisible(false);

        dictArea.addCaretListener(dictAreaCaretListener);

        docEdit = editArea.getStyledDocument();
        docDict = dictArea.getStyledDocument();
        docDict.addDocumentListener(dictAreaListener);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowUtils());

    }
}

public class LanguageEditor {

    static LanguageEditorFrame languageEditorFrame = null;
    static LanguageOperations ops = null;

    public static void initialize(String language) {
        if (language.equals("Turkish")) {
            ops = new TurkishOperations();
        }
        if (language.equals("Polish")) {
            ops = new PolishOperations();
        }

        if (languageEditorFrame == null) {
            languageEditorFrame = new LanguageEditorFrame();
        }

        languageEditorFrame.display();
    }

    public static void setVisible(boolean b) {
        languageEditorFrame.setVisible(b);
    }
}
