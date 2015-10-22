package languagetrainer;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import langeditor.LanguageEditorFrame;
import languages.LanguageNames;
import tatoeba.NumberTrainer;
import tatoeba.SelectionFrame;
import tatoeba.TatoebaFrame;
import utils.*;

public class LanguageTrainerFrame extends JFrame implements ActionListener {

    JFrame thisFrame = this;
    LanguageTrainerFrame thisLanguageTrainer = this;
    JPanel content = new JPanel();
    String[] tools = {"sentences", "numbers", "editors"};
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
        JComboBox languageBox = new JComboBox(languageArray);
        languageBox.addActionListener(this);
        languageBox.setActionCommand("languageBox");
        toolsPanel.add(languageBox);

        JPanel numberPanel = new JPanel();
        numberPanel.setLayout(new BoxLayout(numberPanel, BoxLayout.LINE_AXIS));

        for (String tool : tools) {
            toolsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            toolsPanel.add(buttons.get(tool));
        }
        
      content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));  
        content.add(toolsPanel);
        GenericTextPanel textPanel = new GenericTextPanel();
        LanguageTrainer.messageTextPanel=textPanel;
        content.add(textPanel);

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
        }
        if (action.equals("editors")) {
            LanguageEditorFrame f = new LanguageEditorFrame("pol");
            f.setVisible(true);
        }
        if (action.equals("sentences")) {
            TatoebaFrame t = new TatoebaFrame();
            SelectionFrame.create();
            SelectionFrame.setTatoebaFrame(t);
        }
        if (action.equals("languageBox")) {
            JComboBox box = (JComboBox) ae.getSource();
            String longName = (String) box.getSelectedItem();
            LanguageTrainer.targetLanguage = LanguageNames.longToShort(longName);
            MsgTextPane.write("target language is "+LanguageTrainer.targetLanguage);
        }
    }

}
