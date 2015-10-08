package utils;

import java.awt.Font;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class AreaFont {

    static int fontSize = 14;

    public static int getSize() {
        return fontSize;
    }

    public static void setSize(int size) {
        fontSize = size;
    }

    public static void multiply(float factor) {
        fontSize = (int) ((float) fontSize * factor);
        if (fontSize > 50) {
            fontSize = 50;
        }
        if (fontSize < 10) {
            fontSize = 10;
        }
    }

    public static void setFont(JTextPane pane) {
        SimpleAttributeSet sas = new SimpleAttributeSet();
        StyleConstants.setFontSize(sas, fontSize);

        StyledDocument doc = pane.getStyledDocument();
        doc.setCharacterAttributes(0, doc.getLength(), sas, false);
        pane.setFont(new Font("monospaced", Font.PLAIN, fontSize));
    }
}
