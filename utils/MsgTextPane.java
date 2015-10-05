
package utils;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;


// Common text frame for system messages

public class MsgTextPane {

    static GenericTextFrame msgFrame = new GenericTextFrame();
    
    public static JTextPane getMsgTextPane(){
        return msgFrame.getTextPane();
    }

    public static void write(String msg) {
        msgFrame.write(msg);
    }
    
    public static void setVisible(boolean b){
        msgFrame.setVisible(b);
    }
}
