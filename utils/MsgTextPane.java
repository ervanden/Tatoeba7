
package utils;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import tatoeba.GenericTextFrame;

// Common text frame for system messages

public class MsgTextPane {

    static GenericTextFrame msgFrame = new GenericTextFrame();
    
    public static JTextPane getMsgTextPane(){
        msgFrame.setVisible(true);
        return msgFrame.getTextPane();
    }

    public static void write(String msg) {
        Document doc;
        doc = msgFrame.getTextPane().getDocument();
        try {
            doc.insertString(doc.getLength(), msg + "\n", null);
        } catch (BadLocationException blex) {
            System.out.println("BadLocationException in MsgTextPane.write()");
        }

        msgFrame.getTextPane().setCaretPosition(doc.getLength());

    }
}
