
package utils;

// Common text frame for system messages

public class MsgTextPane {

    static GenericTextFrame msgFrame = new GenericTextFrame();

    public static void write(String msg) {
        msgFrame.write(msg);
    }
    
    public static void setVisible(boolean b){
        msgFrame.setVisible(b);
    }
}
