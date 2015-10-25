package languages;

import dictionaries.FrenchDictionary;
import javax.swing.JFileChooser;

public class French extends GenericLanguage implements Language {

    FrenchDictionary d = null;

    public String dictionaryFileName() {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        return defaultFolder + "\\Tatoeba\\FrenchDictionary.txt";
    }

    public FrenchDictionary dictionary() {
        if (d == null) {
            d = new FrenchDictionary(this);
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
        String 		tcolor="?"; // translated color
if (color.equals("white")) 		tcolor="blanc";
if (color.equals("brown")) 		tcolor="marron";
if (color.equals("red")) 		tcolor="rouge";
if (color.equals("salmon")) 		tcolor="saumon";
if (color.equals("orange")) 		tcolor="orange";
if (color.equals("gold")) 		tcolor="doré";
if (color.equals("yellow")) 		tcolor="jaune";
if (color.equals("olive")) 		tcolor="vert olive";
if (color.equals("green")) 		tcolor="vert";
if (color.equals("light blue")) 	tcolor="blue clair";
if (color.equals("turquoise")) 		tcolor="turquoise";
if (color.equals("dark blue")) 		tcolor="blue foncé";
if (color.equals("blue")) 		tcolor="blue";
if (color.equals("purple")) 		tcolor="violet";
if (color.equals("violet")) 		tcolor="lila";
if (color.equals("magenta / fuchsia")) 	tcolor="fushia";
if (color.equals("beige")) 		tcolor="beige";
if (color.equals("pink")) 		tcolor="rose";
if (color.equals("ivory")) 		tcolor="ivoire/blanc cassé";
if (color.equals("black")) 		tcolor="noir";
if (color.equals("grey")) 		tcolor="gris";
if (color.equals("silver")) 		tcolor="argenté";
return tcolor;
    }

}

