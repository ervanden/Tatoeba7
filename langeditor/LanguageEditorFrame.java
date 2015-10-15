package langeditor;

import dictionary.GenericDictionary;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import utils.MsgTextPane;
import utils.AreaFont;

public class LanguageEditorFrame extends JFrame implements ActionListener, ItemListener {

    private LanguageEditorFrame thisLanguageEditorFrame = this;
    private JFrame thisFrame = (JFrame) this;
 //   public String editorLanguage;
    private GenericDictionary dictionary;
    private LanguageTextPane editArea;
    public JTextPane dictArea = null;  // public static for scrollEnd() function

    private JFileChooser fileChooser = new JFileChooser();
//    private StyledDocument docEdit;          // edit area
//    public StyledDocument docDict = null;   // dictionary area, accessed from DocUtils

    JScrollPane scrollingEditArea;
//    JScrollPane scrollingDictArea;

    JPanel content = new JPanel();

    JButton buttonPlus = new JButton("+");
    JButton buttonMinus = new JButton("-");
    JButton buttonAddWord = new JButton("+");
    JButton buttonAddStem = new JButton("[+]");
    JButton buttonRemoveWord = new JButton("-");
    JButton buttonRemoveStem = new JButton("[-]");
    JRadioButton radioButtonAuto = new JRadioButton("auto", null, true);
    JTextField textFieldDictFileName = new JTextField("");
    Dimension textFieldDictFileNameSize;


    class WindowUtils extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
 if (dictionary.isModified())
     dictionary.dictionaryWindowVisible(true); 
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

        if (action.equals("Correct selected text")) {
            editArea.setManualCorrect(false);
            LanguageContext.get().dictionary().setMarkCorrection(true);
            LanguageContext.get().dictionary().runDictionary(editArea,
                    editArea.selectedPosition, editArea.selectedLength);
            LanguageContext.get().dictionary().setMarkCorrection(false);
            editArea.setManualCorrect(true);
        }

        if (action.equals("Show Dictionary Window")) {
            LanguageContext.get().dictionary().dictionaryWindowVisible(true);
        }

        if (action.equals("Show System Messages")) {
            MsgTextPane.setVisible(true);
        }
        
        if (action.equals("buttonPlus")) {
            AreaFont.multiply((float) 1.2);
            AreaFont.setFont(editArea);
        }

        if (action.equals("buttonMinus")) {
            AreaFont.multiply((float) 0.8);
            AreaFont.setFont(editArea);
        }
 /*       
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
*/
        
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
        this.setJMenuBar(menuBar);

        JMenu menuView = new JMenu("View");
        menuBar.add(menuView);
        AddMenuItem(menuView, "Show Dictionary Window", "Show Dictionary Window");
        AddMenuItem(menuView, "Show System Messages", "Show System Messages");
        pack();

    }

    public LanguageEditorFrame(String language) {
        
        LanguageContext.set(language, "LanguageEditorFrame constructor");
        dictionary=LanguageContext.get().dictionary();
        
        editArea = new LanguageTextPane(language);
        editArea.setAutoCorrect(true);
        editArea.setFinalInsert(false);
        editArea.setManualCorrect(true);

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

        buttonAddWord.addActionListener(this);
        buttonAddStem.addActionListener(this);
        buttonRemoveWord.addActionListener(this);
        buttonRemoveStem.addActionListener(this);
        buttonAddWord.setActionCommand("buttonAddWord");
        buttonAddStem.setActionCommand("buttonAddStem");
        buttonRemoveWord.setActionCommand("buttonRemoveWord");
        buttonRemoveStem.setActionCommand("buttonRemoveStem");

        radioButtonAuto.addItemListener(this);

        textFieldDictFileName.setText(LanguageContext.get().dictionaryFileName());
        textFieldDictFileName.setEditable(false);
        textFieldDictFileNameSize = textFieldDictFileName.getPreferredSize();

        displayGUI();

        setContentPane(content);
 //       setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setTitle("TextEditor ("+language+")");
        pack();
        setLocationRelativeTo(null);
        setVisible(false);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowUtils());

    }
}
