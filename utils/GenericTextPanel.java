package utils;

import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class GenericTextPanel extends JPanel {

    JTextPane textPane;
    JScrollPane textScrollPane;

    public GenericTextPanel() {
        
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
        JButton buttonPlus = new JButton("+");
        JButton buttonMinus = new JButton("-");
        buttonPanel.add(buttonPlus);
                 buttonPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        buttonPanel.add(buttonMinus);


        add(textScrollPane);
                add(buttonPanel);
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
