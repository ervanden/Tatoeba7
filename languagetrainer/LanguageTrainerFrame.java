package languagetrainer;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import langeditor.LanguageEditorFrame;
import tatoeba.NumberTrainer;
import tatoeba.SelectionFrame;
import tatoeba.TatoebaFrame;

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

        content.setLayout(new FlowLayout());

        JPanel numberPanel = new JPanel();
        numberPanel.setLayout(new BoxLayout(numberPanel, BoxLayout.LINE_AXIS));

        for (String tool : tools) {
            content.add(Box.createRigidArea(new Dimension(0, 10)));
            content.add(buttons.get(tool));
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(content);
        pack();
    }

    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
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
    }

}
