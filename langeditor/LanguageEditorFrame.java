package langeditor;

import languages.LanguageContext;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import dictionaries.GenericDictionary;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import languages.Language;
import utils.AreaFont;
import utils.FileOpener;
import utils.MsgTextPane;

public class LanguageEditorFrame extends JFrame implements ActionListener, ItemListener {

    private LanguageEditorFrame thisLanguageEditorFrame = this;
    private JFrame thisFrame = (JFrame) this;
    private Language language;
    private GenericDictionary dictionary;
    private LanguageTextPane editArea;

    private JFileChooser fileChooser = new JFileChooser();
    JScrollPane scrollingEditArea;

    JPanel content = new JPanel();

    JButton buttonPlus = new JButton("+");
    JButton buttonMinus = new JButton("-");
    JButton buttonLookupBabla = new JButton("Bab.la");
    JButton buttonLookupWiktionary = new JButton("Wiktionary");

    JRadioButton radioButtonAuto = new JRadioButton("auto", null, true);
    JTextField textFieldDictFileName = new JTextField("");
    Dimension textFieldDictFileNameSize;

    class WindowUtils extends WindowAdapter {

        public void windowClosing(WindowEvent e) {
            if (dictionary.isModified()) {
                dictionary.dictionaryWindowVisible(true);
            }
        }
    }

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

        if (action.equals("Correct Text")) {
            language.dictionary().setMarkCorrection(true);
            language.dictionary().setMatchInfo(false);
            language.dictionary().correctText(editArea, 0, editArea.getDocument().getLength());
            language.dictionary().setMarkCorrection(false);
            language.dictionary().setMatchInfo(true);
        }

        if (action.equals("Save to file")) {
            StyledDocument document = (StyledDocument) editArea.getDocument();
            FileOpener f = new FileOpener();
            f.openOutputFile(null);

            javax.swing.text.Element root = document.getDefaultRootElement();
            int count = root.getElementCount();
            for (int i = 0; i < count; i++) {
                javax.swing.text.Element lineElement = (javax.swing.text.Element) root.getElement(i);
                try {
                    String line = document.getText(lineElement.getStartOffset(),
                            lineElement.getEndOffset() - lineElement.getStartOffset() - 1);
                    f.writeln(line);
                } catch (Exception e) {
                };
            }
            f.closeOutputFile();

        }

        if (action.equals("Read from file")) {
            StyledDocument document = (StyledDocument) editArea.getDocument();
            MsgTextPane.write("setting auto button OFF");
            radioButtonAuto.setSelected(false);
            FileOpener f = new FileOpener();
            f.openInputFile();
            String line;
            while ((line = f.readLine()) != null) {
                try {
                    document.insertString(document.getLength(), line + "\n", null);
                } catch (BadLocationException blex) {
                }
            }
        }

        if (action.equals("Show Dictionary Window")) {
            language.dictionary().dictionaryWindowVisible(true);
        }

        if (action.equals("buttonPlus")) {
            AreaFont.multiply((float) 1.2);
            AreaFont.setFont(editArea);
        }

        if (action.equals("buttonMinus")) {
            AreaFont.multiply((float) 0.8);
            AreaFont.setFont(editArea);
        }

        if (action.equals("buttonLookupBabla")) {
            editArea.lookupWord(editArea.selectedPosition, "Babla");
        }

        if (action.equals("buttonLookupWiktionary")) {
            editArea.lookupWord(editArea.selectedPosition, "Wiktionary");
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

    private void displayGUI() {

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
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;
        c.gridx = 2;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 5);  // top left bottom right
        content.add(buttonLookupBabla, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;
        c.gridx = 3;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 5);  // top left bottom right
        content.add(buttonLookupWiktionary, c);
        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = 4;
        c.gridy = 0;
        content.add(radioButtonAuto, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 5;            // align with number of editArea buttons
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        content.add(scrollingEditArea, c);

        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JMenu menuView = new JMenu("View");
        menuBar.add(menuView);
        AddMenuItem(menuView, "Show Dictionary Window", "Show Dictionary Window");
        JMenu menuText = new JMenu("Text");
        menuBar.add(menuText);
        AddMenuItem(menuText, "Correct Text", "Correct Text");
        menuBar.add(menuText);
        AddMenuItem(menuText, "Read from file", "Read from file");
        menuBar.add(menuText);
        AddMenuItem(menuText, "Save to file", "Save to file");
        pack();

    }

    public LanguageEditorFrame(String lang) {

        language = LanguageContext.get(lang);
        dictionary = language.dictionary();

        editArea = new LanguageTextPane(lang);
        radioButtonAuto.setSelected(true);

        editArea.setMinimumSize(new Dimension(800, 300));
        editArea.setPreferredSize(new Dimension(800, 300));
        editArea.setMaximumSize(new Dimension(800, 300));
        editArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // 2 pixels around text in JTextPane    
        editArea.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));
        scrollingEditArea = new JScrollPane(editArea);

        content.setLayout(new GridBagLayout());

        buttonPlus.addActionListener(this);
        buttonPlus.setActionCommand("buttonPlus");
        buttonMinus.addActionListener(this);
        buttonMinus.setActionCommand("buttonMinus");

        buttonLookupBabla.addActionListener(this);
        buttonLookupBabla.setActionCommand("buttonLookupBabla");
        buttonLookupWiktionary.addActionListener(this);
        buttonLookupWiktionary.setActionCommand("buttonLookupWiktionary");

        radioButtonAuto.addItemListener(this);

        textFieldDictFileName.setText(language.dictionaryFileName());
        textFieldDictFileName.setEditable(false);
        textFieldDictFileNameSize = textFieldDictFileName.getPreferredSize();

        displayGUI();

        setContentPane(content);
        setTitle("TextEditor (" + lang + ")");
        pack();
        setLocationRelativeTo(null);
        setVisible(false);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowUtils());

    }
}
