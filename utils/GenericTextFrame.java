package utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class GenericTextFrame extends JFrame {

    private JFrame thisFrame = (JFrame) this;
    public JTextPane textPane;
    JScrollPane textScrollPane;
    JPanel content = new JPanel();

    public GenericTextFrame() {
        textPane = new JTextPane();
        textScrollPane = new JScrollPane(textPane);

        Dimension minimumDimension = new Dimension(780, 300);
        Dimension preferredDimension = new Dimension(780, 700);
        textPane.setMinimumSize(minimumDimension);
        textPane.setPreferredSize(preferredDimension);
        textScrollPane.setMinimumSize(minimumDimension);
        textScrollPane.setPreferredSize(preferredDimension);

        BorderLayout borderLayout = new BorderLayout();
        content.setLayout(borderLayout);

        content.add(textScrollPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setContentPane(content);
        pack();
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
    
    public JTextPane getTextPane(){
        return textPane;
    }
    
}
