package tatoeba;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import languages.Language;
import languages.LanguageContext;
import languages.LanguageNames;
import utils.AreaFont;

public class NumberTrainer extends JFrame implements ActionListener {

    NumberTrainer thisNumberTrainer = this;
    JFrame thisFrame = (JFrame) this;
    JTextPane textPane;
    JScrollPane textScrollPane;
    JPanel content = new JPanel();
    Random randomGenerator = new Random();
    int randomMin = 0;
    int randomMax = 999;

    int currentNumber = 0;

    public NumberTrainer() {
        textPane = new JTextPane();
        textScrollPane = new JScrollPane(textPane);
        textPane.setEditable(false);

        Dimension preferredDimension = new Dimension(1200, 300);
        textPane.setPreferredSize(preferredDimension);
        textScrollPane.setPreferredSize(preferredDimension);

        JButton randomButton = new JButton("random");
        randomButton.setActionCommand("random");
        randomButton.addActionListener(thisNumberTrainer);
        JButton incrButton = new JButton("+1");
        incrButton.setActionCommand("increment");
        incrButton.addActionListener(thisNumberTrainer);
        JButton transButton = new JButton("translate");
        transButton.setActionCommand("translate");
        transButton.addActionListener(thisNumberTrainer);

        JButton plusButton = new JButton("+");
        plusButton.setActionCommand("+");
        plusButton.addActionListener(thisNumberTrainer);
        JButton minusButton = new JButton("-");
        minusButton.setActionCommand("-");
        minusButton.addActionListener(thisNumberTrainer);

        JTextField numberField = new JTextField("enter nr");
        numberField.setPreferredSize(new Dimension(20, 10));
        numberField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sn = ((JTextField) e.getSource()).getText();
                currentNumber = Integer.valueOf(sn);
                write(sn);
            }
        ;
        }
            );
        
                JTextField randomMinField = new JTextField(5);
        randomMinField.setMaximumSize(new Dimension(50, 30));
        randomMinField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sn = ((JTextField) e.getSource()).getText();
                randomMin = Integer.valueOf(sn);
                write("random between " + randomMin + " and " + randomMax);
            }
        ;
        }
            );
        
               JTextField randomMaxField = new JTextField(5);
        randomMaxField.setMaximumSize(new Dimension(50, 30));
        randomMaxField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sn = ((JTextField) e.getSource()).getText();
                randomMax = Integer.valueOf(sn);
                write("random between " + randomMin + " and " + randomMax);
            }
        ;
        }
            );

        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

        content.add(textScrollPane);

        JPanel numberPanel = new JPanel();
        numberPanel.setLayout(new BoxLayout(numberPanel, BoxLayout.LINE_AXIS));
        numberPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        numberPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        numberPanel.add(numberField);
//        numberPanel.add(Box.createRigidArea(new Dimension(10, 0)));
//        numberPanel.add(transButton);
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
        numberPanel.add(plusButton);
        numberPanel.add(minusButton);
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
        if (action.equals("random")) {
            currentNumber = randomMin + randomGenerator.nextInt(randomMax - randomMin + 1);
            write(String.format("%d", currentNumber));
        }
        if (action.equals("increment")) {
            currentNumber++;
            write(String.format("%d", currentNumber));
        }
        if (action.equals("translate")) {
            String lang = languagetrainer.LanguageTrainer.targetLanguage;
            Language language = LanguageContext.get(lang);
            write(language.number(currentNumber));
        }
        if (action.equals("+")) {
            AreaFont.multiply((float) 1.2);
            AreaFont.setFont(textPane);
        }
        if (action.equals("-")) {
            AreaFont.multiply((float) 0.8);
            AreaFont.setFont(textPane);
        }
    }

    public void erase() {
        Document doc = textPane.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException ble) {
            System.out.println("ble");
        }

    }

    public void write(String msg) {
        Document doc = textPane.getDocument();
        try {
            doc.insertString(doc.getLength(), msg + "\n", null);
        } catch (BadLocationException blex) {
        }
        textPane.setCaretPosition(doc.getLength());
    }

}
