
package utils;

public class AreaFont {
    static int fontSize = 14;
    
    public static int getSize(){
        return fontSize;
    }
    public static void setSize(int size){
        fontSize=size;
    }
    public static void  multiply(float factor){
     fontSize=(int) ((float) fontSize * factor);
     if (fontSize>50) fontSize=50;
     if (fontSize<10) fontSize=10;
    }
}
