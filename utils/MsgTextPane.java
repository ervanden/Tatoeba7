
package utils;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

// one single text pane for system messages
// the caller can get it and put in a JFrame or other window

public class MsgTextPane {
    static JTextPane msgArea = new JTextPane();
    
    public static JTextPane getMsgTextPane(){
        return msgArea;
    }

    public static void write(String msg) {
        Document doc;
        doc = msgArea.getDocument();
        try {
            doc.insertString(doc.getLength(), msg + "\n", null);
        } catch (BadLocationException blex) {
            System.out.println("BadLocationException in MsgTextPane.write()");
        }

        msgArea.setCaretPosition(doc.getLength());

    }
}
