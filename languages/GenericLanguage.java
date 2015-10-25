package languages;

import dictionaries.GenericDictionary;
import javax.swing.JFileChooser;

public class GenericLanguage {

    GenericDictionary d = null;

    public String dictionaryFileName() {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        return defaultFolder + "\\Tatoeba\\GenericDictionary.txt";
    }

    public GenericDictionary dictionary() {
        if (d == null) {
            d = new GenericDictionary(LanguageContext.get("generic"));
            d.readDictionaryFromFile(dictionaryFileName());
        }
        return d;
    }

    public void disposeDictionary() {
        d = null;
    }

    public char toUpperCase(char c) {
        return Character.toUpperCase(c);  // works correctly for most languages
    }
    
    public String toLowerCase(String s){
        return s.toLowerCase();  // works correctly for most languages
    }
    
    public String letters(){
        return "";
    }
    
    
    public String invertDiacritics(String word) {
        return word;
    }

    public String removeDiacritics(String word) {
        return word;
    }

    public String number(int n) {
        return "number translation in this language is not implemented";
    }
    
    public String color(String color){
        String 	tcolor="?"; // translated color
if (color.equals("white")) 		tcolor="?";
if (color.equals("black")) 		tcolor="?";
if (color.equals("brown")) 		tcolor="?";
if (color.equals("red")) 		tcolor="?";
if (color.equals("salmon")) 		tcolor="?";
if (color.equals("orange")) 		tcolor="?";
if (color.equals("gold")) 		tcolor="?";
if (color.equals("yellow")) 		tcolor="?";
if (color.equals("olive")) 		tcolor="?";
if (color.equals("green")) 		tcolor="?";
if (color.equals("light blue")) 	tcolor="?";
if (color.equals("turquoise")) 		tcolor="?";
if (color.equals("dark blue")) 		tcolor="?";
if (color.equals("blue")) 		tcolor="?";
if (color.equals("purple")) 		tcolor="?";
if (color.equals("violet")) 		tcolor="?";
if (color.equals("magenta / fuchsia")) 	tcolor="?";
if (color.equals("beige")) 		tcolor="?";
if (color.equals("pink")) 		tcolor="?";
if (color.equals("ivory")) 		tcolor="?";
if (color.equals("black")) 		tcolor="?";
if (color.equals("grey")) 		tcolor="?";
if (color.equals("silver")) 		tcolor="?";
return 	tcolor;
    }

}
