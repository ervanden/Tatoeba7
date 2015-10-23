package utils;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class GenericTextPanel extends JPanel implements ActionListener {
    GenericTextPanel thisGenericTextPanel;
    JTextPane textPane;
    JScrollPane textScrollPane;

    public GenericTextPanel() {
        thisGenericTextPanel=this;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textPane = new JTextPane();
        textScrollPane = new JScrollPane(textPane);

        Dimension minimumDimension = new Dimension(780, 50);
        Dimension preferredDimension = new Dimension(780, 800);
        textPane.setMinimumSize(minimumDimension);
        textPane.setPreferredSize(preferredDimension);
        textScrollPane.setMinimumSize(minimumDimension);
        textScrollPane.setPreferredSize(preferredDimension);

        JPanel buttonPanel = new JPanel();
        JButton plusButton = new JButton("+");
        JButton minusButton = new JButton("-");
        plusButton.setActionCommand("+");
        plusButton.addActionListener(thisGenericTextPanel);
        minusButton.setActionCommand("-");
        minusButton.addActionListener(thisGenericTextPanel);
        buttonPanel.add(plusButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        buttonPanel.add(minusButton);

        add(textScrollPane);
        add(buttonPanel);
    }

    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        if (action.equals("+")) {
            AreaFont.multiply((float) 1.2);
            AreaFont.setFont(textPane);
        }
        if (action.equals("-")) {
            AreaFont.multiply((float) 0.8);
            AreaFont.setFont(textPane);
        }
    }

    public JTextPane getTextPane() {
        return textPane;
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
