package tatoeba;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import languages.Language;
import languages.LanguageContext;
import utils.GenericTextPanel;

public class NumberTrainer extends JFrame implements ActionListener {

    NumberTrainer thisNumberTrainer = this;
    JFrame thisFrame = (JFrame) this;

    GenericTextPanel textPanel;

    JPanel content = new JPanel();
    Random randomGenerator = new Random();
    int randomMin = 0;
    int randomMax = 999;

    int currentNumber = 0;

    public NumberTrainer() {

        textPanel = new GenericTextPanel();
        textPanel.getTextPane().setEditable(false);

        JButton randomButton = new JButton("random");
        randomButton.setActionCommand("random");
        randomButton.addActionListener(thisNumberTrainer);
        JButton incrButton = new JButton("+1");
        incrButton.setActionCommand("increment");
        incrButton.addActionListener(thisNumberTrainer);
        JButton transButton = new JButton("translate");
        transButton.setActionCommand("translate");
        transButton.addActionListener(thisNumberTrainer);

        JTextField numberField = new JTextField("enter nr");
        numberField.setPreferredSize(new Dimension(20, 10));
        numberField.addActionListener(thisNumberTrainer);
        numberField.setActionCommand("enter nr");

        JTextField randomMinField = new JTextField(5);
        randomMinField.setMaximumSize(new Dimension(50, 30));
        randomMinField.addActionListener(thisNumberTrainer);
        randomMinField.setActionCommand("randomMin");
        randomMinField.setText(String.format("%d", randomMin));

        JTextField randomMaxField = new JTextField(5);
        randomMaxField.setMaximumSize(new Dimension(50, 30));
        randomMaxField.addActionListener(thisNumberTrainer);
        randomMaxField.setActionCommand("randomMax");
        randomMaxField.setText(String.format("%d", randomMax));

        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

        content.add(textPanel);

        JPanel numberPanel = new JPanel();
        numberPanel.setLayout(new BoxLayout(numberPanel, BoxLayout.LINE_AXIS));
        numberPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        numberPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        numberPanel.add(numberField);
        numberPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        numberPanel.add(incrButton);
        numberPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        numberPanel.add(randomButton);
        numberPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        numberPanel.add(new JLabel("between : "));
        numberPanel.add(randomMinField);
        numberPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        numberPanel.add(new JLabel(" and : "));
        numberPanel.add(randomMaxField);
        numberPanel.add(Box.createRigidArea(new Dimension(100, 0)));
        transButton.setMaximumSize(new Dimension(10000, 20));
        transButton.setAlignmentX(0.5f);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(transButton);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(numberPanel);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setContentPane(content);
        pack();
    }

    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        if (action.equals("enter nr")) {
            String sn = ((JTextField) ae.getSource()).getText();
            currentNumber = Integer.valueOf(sn);
            textPanel.write(sn);
        }
        if (action.equals("random")) {
            currentNumber = randomMin + randomGenerator.nextInt(randomMax - randomMin + 1);
            textPanel.write(String.format("%d", currentNumber));
        }
        if (action.equals("randomMin")) {
            String sn = ((JTextField) ae.getSource()).getText();
            randomMin = Integer.valueOf(sn);
            textPanel.write("random between " + randomMin + " and " + randomMax);
        }
        if (action.equals("randomMax")) {
            String sn = ((JTextField) ae.getSource()).getText();
            randomMax = Integer.valueOf(sn);
            textPanel.write("random between " + randomMin + " and " + randomMax);
        }
        if (action.equals("increment")) {
            currentNumber++;
            textPanel.write(String.format("%d", currentNumber));
        }
        if (action.equals("translate")) {
            String lang = languagetrainer.LanguageTrainer.targetLanguage;
            Language language = LanguageContext.get(lang);
            textPanel.write(language.number(currentNumber));
        }

    }

}
