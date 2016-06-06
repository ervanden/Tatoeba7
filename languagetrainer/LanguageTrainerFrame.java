package languagetrainer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import langeditor.LanguageEditorFrame;
import languages.Language;
import languages.LanguageContext;
import languages.LanguageNames;
import static languagetrainer.LanguageTrainer.userLanguages;
import tatoeba.*;
import utils.*;

public class LanguageTrainerFrame extends JFrame implements ActionListener {

    JFrame thisFrame = this;
    LanguageTrainerFrame thisLanguageTrainer = this;
    JPanel content = new JPanel();
    ArrayList<String> tools = new ArrayList<>();
    HashMap<String, JButton> buttons = new HashMap<>();

    public LanguageTrainerFrame() {

        tools.add("Writepad");
        tools.add("Sentences");
        tools.add("Numbers");
        tools.add("Colors");

        for (String theme : PictureTrainer.getPictureThemes()) {
            tools.add(theme);
        }

        int buttonHeight = 0;
        int buttonWidth = 0;
        for (String tool : tools) {
            JButton button = new JButton(tool);
            buttons.put(tool, button);
            button.setActionCommand(tool);
            button.addActionListener(thisLanguageTrainer);
            if (button.getPreferredSize().getWidth() > buttonWidth) {
                buttonWidth = (int) button.getPreferredSize().getWidth();
            }
            if (button.getPreferredSize().getHeight() > buttonHeight) {
                buttonHeight = (int) button.getPreferredSize().getHeight();
            }
        }

        for (JButton button : buttons.values()) {
            // BoxLayout seems to take into account only MaximumSize
            button.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        }

        System.out.println(buttonWidth + " " + buttonHeight);
        JPanel languagePanel = new JPanel();

        String[] languageArray = new String[LanguageTrainer.userLanguages.size()];
        for (int i = 0; i < languageArray.length; i++) {
            languageArray[i] = LanguageNames.shortToLong(LanguageTrainer.userLanguages.get(i));
        }

        JLabel sourceLabel = new JLabel("source");
        @SuppressWarnings("unchecked")
        JComboBox sourceLanguageBox = new JComboBox(languageArray);
        sourceLanguageBox.addActionListener(this);
        sourceLanguageBox.setActionCommand("sourceLanguageBox");
        sourceLanguageBox.setSelectedItem(LanguageNames.shortToLong(LanguageTrainer.sourceLanguage));

        JLabel targetLabel = new JLabel("target");
        @SuppressWarnings("unchecked")
        JComboBox targetLanguageBox = new JComboBox(languageArray);
        targetLanguageBox.addActionListener(this);
        targetLanguageBox.setActionCommand("targetLanguageBox");
        targetLanguageBox.setSelectedItem(LanguageNames.shortToLong(LanguageTrainer.targetLanguage));

        languagePanel.add(sourceLabel);
        languagePanel.add(sourceLanguageBox);
        languagePanel.add(targetLabel);
        languagePanel.add(targetLanguageBox);

        JPanel toolsPanel = new JPanel();
        toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.PAGE_AXIS));

        for (String tool : tools) {
            JButton button = buttons.get(tool);
            toolsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            toolsPanel.add(button);
            //           System.out.println(" added to panel " + button.getText() + " " + button.getMinimumSize());
        }

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
        mainPanel.add(toolsPanel);
        mainPanel.add(LanguageTrainer.messageTextPanel);

        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.add(languagePanel);
        content.add(mainPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(content);

        JMenuBar menuBar;
        JMenu menuActions;

        menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        menuActions = new JMenu("Actions");
        menuBar.add(menuActions);
        AddMenuItem(menuActions, "Re-read Word Maps from files", "");
        AddMenuItem(menuActions, "Update Word Map Files", "");
        AddMenuItem(menuActions, "Display Word Maps", "");
        pack();
    }

    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();

        if (action.equals("Numbers")) {
            NumberTrainer n = new NumberTrainer();
            n.setVisible(true);
        } else if (action.equals("Colors")) {
            ColorTrainer n = new ColorTrainer();
            n.setVisible(true);
        } else if (action.equals("Writepad")) {
            LanguageEditorFrame f = new LanguageEditorFrame(LanguageTrainer.targetLanguage);
            f.setVisible(true);
        } else if (action.equals("Sentences")) {
            TatoebaFrame t = new TatoebaFrame();
        } else if (action.equals("targetLanguageBox")) {
            JComboBox box = (JComboBox) ae.getSource();
            String longName = (String) box.getSelectedItem();
            LanguageTrainer.targetLanguage = LanguageNames.longToShort(longName);
            MsgTextPane.write("target language is " + LanguageTrainer.targetLanguage);
        } else if (action.equals("sourceLanguageBox")) {
            JComboBox box = (JComboBox) ae.getSource();
            String longName = (String) box.getSelectedItem();
            LanguageTrainer.sourceLanguage = LanguageNames.longToShort(longName);
            MsgTextPane.write("source language is " + LanguageTrainer.sourceLanguage);
        } else if (action.equals("Re-read Word Maps from files")) {
            for (String lang : userLanguages) {
                Language language = LanguageContext.get(lang);
                language.rereadWordMaps();
            }
        } else if (action.equals("Update Word Map Files")) {
            for (String lang : userLanguages) {
                Language language = LanguageContext.get(lang);
                language.updateWordMaps();
            }
        } else if (action.equals("Display Word Maps")) {
            PictureTrainer.displayWordMaps();
        } else {
            PictureTrainer p = new PictureTrainer(action);
            p.setVisible(true);
        }
    }

    HashMap<String, JMenuItem> menuItems = new HashMap<>();

    private void AddMenuItem(JMenu menu, String name, String subName) {
        JMenuItem menuItem;
        if (subName.equals("")) {
            menuItem = new JMenuItem(name);
            menuItem.setActionCommand(name);
        } else { // name is a language, subname indicates an action for this language (Editor, Numbers,..)
            menuItem = new JMenuItem(LanguageNames.shortToLong(name));
            menuItem.setActionCommand(name + "|" + subName);

        };
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItems.put(name, menuItem);
    }

    public void enableMenuItem(String actionName, boolean enabled) {
        JMenuItem menuItem;
        menuItem = menuItems.get(actionName);
        menuItem.setEnabled(enabled);
    }

}
