package languagetrainer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import langeditor.LanguageEditorFrame;
import languages.LanguageNames;
import tatoeba.*;
import tatoeba.SelectionFrame;
import tatoeba.TatoebaFrame;
import utils.*;

public class LanguageTrainerFrame extends JFrame implements ActionListener {

    JFrame thisFrame = this;
    LanguageTrainerFrame thisLanguageTrainer = this;
    JPanel content = new JPanel();
    String[] tools = {"sentences", "numbers", "colors", "editors"};
    HashMap<String, JButton> buttons = new HashMap<>();

    public LanguageTrainerFrame() {

        for (String tool : tools) {
            JButton button = new JButton(tool);
            buttons.put(tool, button);
            button.setActionCommand(tool);
            button.addActionListener(thisLanguageTrainer);
        }

        JPanel toolsPanel = new JPanel();

        String[] languageArray = new String[LanguageTrainer.userLanguages.size()];
        for (int i = 0; i < languageArray.length; i++) {
            languageArray[i] = LanguageNames.shortToLong(LanguageTrainer.userLanguages.get(i));
        }

        JLabel sourceLabel = new JLabel("source");
        JComboBox sourceLanguageBox = new JComboBox(languageArray);
        sourceLanguageBox.addActionListener(this);
        sourceLanguageBox.setActionCommand("sourceLanguageBox");
        sourceLanguageBox.setSelectedItem(LanguageNames.shortToLong(LanguageTrainer.sourceLanguage));

        JLabel targetLabel = new JLabel("target");
        JComboBox targetLanguageBox = new JComboBox(languageArray);
        targetLanguageBox.addActionListener(this);
        targetLanguageBox.setActionCommand("targetLanguageBox");
        targetLanguageBox.setSelectedItem(LanguageNames.shortToLong(LanguageTrainer.targetLanguage));

        toolsPanel.add(sourceLabel);
        toolsPanel.add(sourceLanguageBox);
        toolsPanel.add(targetLabel);
        toolsPanel.add(targetLanguageBox);

        JPanel numberPanel = new JPanel();
        numberPanel.setLayout(new BoxLayout(numberPanel, BoxLayout.LINE_AXIS));

        for (String tool : tools) {
            toolsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            toolsPanel.add(buttons.get(tool));
        }

        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.add(toolsPanel);
        content.add(LanguageTrainer.messageTextPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(content);
        pack();
    }

    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        System.out.println(action);
        if (action.equals("numbers")) {
            NumberTrainer n = new NumberTrainer();
            n.setVisible(true);
        } else if (action.equals("colors")) {
            ColorTrainer n = new ColorTrainer();
            n.setVisible(true);
        } else if (action.equals("editors")) {
            LanguageEditorFrame f = new LanguageEditorFrame("pol");
            f.setVisible(true);
        } else if (action.equals("sentences")) {
            TatoebaFrame t = new TatoebaFrame();
            SelectionFrame.create();
            SelectionFrame.setTatoebaFrame(t);
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
        }
    }

}
