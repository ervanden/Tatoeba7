package languages;

import dictionaries.ItalianDictionary;
import dictionaries.GenericDictionary;
import javax.swing.JFileChooser;

public class Italian extends GenericLanguage implements Language {

    ItalianDictionary d = null;

    public String dictionaryFileName() {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        return defaultFolder + "\\Tatoeba\\ItalianDictionary.txt";
    }

    public GenericDictionary dictionary() {
        if (d == null) {
            d = new ItalianDictionary(this);
            d.readDictionaryFromFile(dictionaryFileName());
        }
        return d;
    }

    public String letters() {
        return "";    
    }

    public String invertDiacritics(String word) {
        return word;
    }

    public String removeDiacritics(String word) {
        return word;
    }
    
        public String color(String color){
        String 		tcolor=""; // translated color
if (color.equals("white")) 		tcolor="bianco";
if (color.equals("brown")) 		tcolor="marrone";
if (color.equals("red")) 		tcolor="rosso";
if (color.equals("salmon")) 		tcolor="salmone";
if (color.equals("orange")) 		tcolor="arancione";
if (color.equals("gold")) 		tcolor="oro";
if (color.equals("yellow")) 		tcolor="giallo";
if (color.equals("olive")) 		tcolor="verde oliva";
if (color.equals("green")) 		tcolor="verde";
if (color.equals("light blue")) 	tcolor="blu chiaro";
if (color.equals("turquoise")) 		tcolor="turchese";
if (color.equals("dark blue")) 		tcolor="blu scuro";
if (color.equals("blue")) 		tcolor="blu";
if (color.equals("purple")) 		tcolor="viola";
if (color.equals("violet")) 		tcolor="violetto";
if (color.equals("magenta / fuchsia")) 	tcolor="fuchsia";
if (color.equals("beige")) 		tcolor="beige";
if (color.equals("pink")) 		tcolor="rosa";
if (color.equals("ivory")) 		tcolor="avorio";
if (color.equals("black")) 		tcolor="nero";
if (color.equals("grey")) 		tcolor="grigio";
if (color.equals("silver")) 		tcolor="argento";
return tcolor;
    }

}
