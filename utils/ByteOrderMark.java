
package utils;

public class ByteOrderMark {

    public static String remove(String l) {  // remove the magic character put by Notepad
        if ((l.length() > 0) && (l.codePointAt(0) == 65279)) {
            MsgTextPane.write("BOM removed from input file");
            l = l.substring(1);
        }
        return l;
    }
}
