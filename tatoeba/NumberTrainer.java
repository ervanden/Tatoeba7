package tatoeba;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import langeditor.LanguageContext;
import utils.AreaFont;

public class NumberTrainer extends JFrame implements ActionListener {

    NumberTrainer thisNumberTrainer = this;
    JFrame thisFrame = (JFrame) this;
    JTextPane textPane;
    JScrollPane textScrollPane;
    JPanel content = new JPanel();
    Random randomGenerator = new Random();
    int currentNumber = -1;

    public NumberTrainer(String language) {

        LanguageContext.set(null, language, "NumberTrainer constructor");

        textPane = new JTextPane();
        textScrollPane = new JScrollPane(textPane);

        Dimension preferredDimension = new Dimension(1200, 300);
        textPane.setPreferredSize(preferredDimension);
        textScrollPane.setPreferredSize(preferredDimension);

        JButton nextButton = new JButton("next");
        nextButton.setActionCommand("next");
        nextButton.addActionListener(thisNumberTrainer);

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

        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

        content.add(textScrollPane);

        JPanel numberPanel = new JPanel();
        numberPanel.setLayout(new BoxLayout(numberPanel, BoxLayout.LINE_AXIS));
        numberPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        numberPanel.add(Box.createRigidArea(new Dimension(80, 0)));
        numberPanel.add(nextButton);
        numberPanel.add(numberField);
        numberPanel.add(Box.createRigidArea(new Dimension(80, 0)));
        numberPanel.add(plusButton);
        numberPanel.add(minusButton);
        content.add(numberPanel);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setContentPane(content);
        pack();
    }

    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        if (action.equals("next")) {
            executeAction(action);
        }
        if (action.equals("+")) {
            AreaFont.multiply((float) 1.2);
            SimpleAttributeSet sas = new SimpleAttributeSet();
            StyleConstants.setFontSize(sas, AreaFont.getSize());

            StyledDocument doc = textPane.getStyledDocument();
            doc.setCharacterAttributes(0, doc.getLength(), sas, false);
            textPane.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));
        }
        if (action.equals("-")) {
            AreaFont.multiply((float) 0.8);
            SimpleAttributeSet sas = new SimpleAttributeSet();
            StyleConstants.setFontSize(sas, AreaFont.getSize());

            StyledDocument doc = textPane.getStyledDocument();
            doc.setCharacterAttributes(0, doc.getLength(), sas, false);
            textPane.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));
        }
    }

    private void executeAction(String action) {
        if (currentNumber < 1) {
            currentNumber = randomGenerator.nextInt(999999);
            write(String.format("%d", currentNumber));
        } else {
            write(LanguageContext.get().number(currentNumber));
            currentNumber = -1;
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

    public JTextPane getTextPane() {
        return textPane;
    }

}
