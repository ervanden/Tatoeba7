package languages;

import dictionaries.PortugueseDictionary;
import javax.swing.JFileChooser;

public class Portuguese extends GenericLanguage implements Language {

    PortugueseDictionary d = null;

    public String dictionaryFileName() {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        return defaultFolder + "\\Tatoeba\\PortugueseDictionary.txt";
    }

    public PortugueseDictionary dictionary() {
         if (d == null) {
            d = new PortugueseDictionary(this);
            d.readDictionaryFromFile(dictionaryFileName());
        }       
        return d;
    }

    public String invertDiacritics(String word) {
                char c;
        String newword = "";
        for (int i = 0; i <= word.length() - 1; i++) {
            c = word.charAt(i);
if (c=='á') {c='a';} else
if (c=='â') {c='á';} else
if (c=='ã') {c='â';} else
if (c=='à') {c='ã';} else
if (c=='a') {c='à';} else
if (c=='ç') {c='c';} else
if (c=='c') {c='ç';} else
if (c=='é') {c='e';} else
if (c=='e') {c='ê';} else
if (c=='ê') {c='é';} else
if (c=='í') {c='i';} else
if (c=='i') {c='í';} else
if (c=='ó') {c='o';} else
if (c=='o') {c='ô';} else
if (c=='ô') {c='õ';} else
if (c=='õ') {c='o';} else
if (c=='ú') {c='u';} else
if (c=='u') {c='ú';}
            newword = newword + c;

        }
        return newword;
        }

    public String removeDiacritics(String word) {
word = word.replaceAll("á","a");
word = word.replaceAll("â","a");
word = word.replaceAll("ã","a");
word = word.replaceAll("à","a");
word = word.replaceAll("ç","c");
word = word.replaceAll("é","e");
word = word.replaceAll("ê","e");
word = word.replaceAll("í","i");
word = word.replaceAll("ó","o");
word = word.replaceAll("ô","o");
word = word.replaceAll("õ","o");
word = word.replaceAll("ú","u");
        return word;
    }

    public String number(int n) {
        return (String.format("numero %s", n));
    }

        public String color(String color){
        String 	tcolor="?"; // translated color
if (color.equals("white")) 		tcolor="branco";
if (color.equals("black")) 		tcolor="preto";
if (color.equals("brown")) 		tcolor="marrom";
if (color.equals("red")) 		tcolor="vermelho";
if (color.equals("salmon")) 		tcolor="salmão";
if (color.equals("orange")) 		tcolor="laranja";
if (color.equals("gold")) 		tcolor="dourado";
if (color.equals("yellow")) 		tcolor="amarelo";
if (color.equals("olive")) 		tcolor="azeitona";
if (color.equals("green")) 		tcolor="verde";
if (color.equals("light blue")) 	tcolor="azul claro";
if (color.equals("turquoise")) 		tcolor="de cor verde-azulada";
if (color.equals("dark blue")) 		tcolor="azul escuro";
if (color.equals("blue")) 		tcolor="azul";
if (color.equals("purple")) 		tcolor="purpura";
if (color.equals("violet")) 		tcolor="violeta";
if (color.equals("magenta / fuchsia")) 	tcolor="fúcsia";
if (color.equals("beige")) 		tcolor="bege";
if (color.equals("pink")) 		tcolor="cor de rosa";
if (color.equals("ivory")) 		tcolor="marfim";
if (color.equals("black")) 		tcolor="preto";
if (color.equals("grey")) 		tcolor="cinza / cinzento";
if (color.equals("silver")) 		tcolor="prateado";
return 	tcolor;
    }

}
