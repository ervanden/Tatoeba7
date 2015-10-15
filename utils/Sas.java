
package utils;

import java.awt.Color;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Sas {
    static public SimpleAttributeSet underline = new SimpleAttributeSet();
    static public SimpleAttributeSet noUnderline = new SimpleAttributeSet();
    static public SimpleAttributeSet bold = new SimpleAttributeSet();
    static public SimpleAttributeSet noBold = new SimpleAttributeSet();
    static public SimpleAttributeSet red = new SimpleAttributeSet();

    static {
        StyleConstants.setUnderline(underline, true);
        StyleConstants.setUnderline(noUnderline, false);
        StyleConstants.setBold(bold, true);
        StyleConstants.setBold(noBold, false);
        StyleConstants.setForeground(red, Color.RED);
    }   
}
