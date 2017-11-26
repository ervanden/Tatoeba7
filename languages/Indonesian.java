package languages;


import dictionaries.OtherDictionary;

public class Indonesian extends GenericLanguage implements Language {

    OtherDictionary d = null;

        public Indonesian(){
        languageName="Indonesian";
    }
/*        
    @Override
    public String dictionaryFileName() {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        return defaultFolder + "\\Tatoeba\\OtherDictionary.txt";
    }
*/
    @Override
    public OtherDictionary dictionary() {
         if (d == null) {
            d = new OtherDictionary(this);
            d.readDictionaryFromFile(dictionaryFileName());
        }       
        return d;
    }
    
       public String color(String color) {
        String tcolor = "?"; // translated color
        if (color.equals("white")) {
            tcolor = "putih";
        }
        if (color.equals("brown")) {
            tcolor = "coklat";
        }
        if (color.equals("red")) {
            tcolor = "merah";
        }
        if (color.equals("orange")) {
            tcolor = "jeruk";
        }
        if (color.equals("gold")) {
            tcolor = "emas";
        }
        if (color.equals("yellow")) {
            tcolor = "kuning";
        }
        if (color.equals("green")) {
            tcolor = "hijau";
        }
        if (color.equals("blue")) {
            tcolor = "biru";
        }
        if (color.equals("purple")) {
            tcolor = "ungu";
        }
        if (color.equals("pink")) {
            tcolor = "merah muda";
        }
        if (color.equals("black")) {
            tcolor = "hitam";
        }
        if (color.equals("grey")) {
            tcolor = "abu-abu";
        }
        if (color.equals("silver")) {
            tcolor = "perak";
        }
        return tcolor;
    }


}
