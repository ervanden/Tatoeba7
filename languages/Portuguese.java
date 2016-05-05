package languages;

import dictionaries.PortugueseDictionary;

public class Portuguese extends GenericLanguage implements Language {

    PortugueseDictionary d = null;

    public Portuguese() {
        languageName = "Portuguese";
    }

    public PortugueseDictionary dictionary() {
        if (d == null) {
            d = new PortugueseDictionary(this);
            d.readDictionaryFromFile(dictionaryFileName());
        }
        return d;
    }

    public String diacriticsGroups() {
        return "aáâãà cç eéê ií oóôõ uú AÁÂÃÀ CÇ EÉÊ IÍ OÓÔÕ UÚ";
    }

    public String number(int n) {
        return (String.format("numero %s", n));
    }

    public String color(String color) {
        String tcolor = "?"; // translated color
        if (color.equals("white")) {
            tcolor = "branco";
        }
        if (color.equals("black")) {
            tcolor = "preto";
        }
        if (color.equals("brown")) {
            tcolor = "marrom";
        }
        if (color.equals("red")) {
            tcolor = "vermelho";
        }
        if (color.equals("salmon")) {
            tcolor = "salmão";
        }
        if (color.equals("orange")) {
            tcolor = "laranja";
        }
        if (color.equals("gold")) {
            tcolor = "dourado";
        }
        if (color.equals("yellow")) {
            tcolor = "amarelo";
        }
        if (color.equals("olive")) {
            tcolor = "azeitona";
        }
        if (color.equals("green")) {
            tcolor = "verde";
        }
        if (color.equals("light blue")) {
            tcolor = "azul claro";
        }
        if (color.equals("turquoise")) {
            tcolor = "de cor verde-azulada";
        }
        if (color.equals("dark blue")) {
            tcolor = "azul escuro";
        }
        if (color.equals("blue")) {
            tcolor = "azul";
        }
        if (color.equals("purple")) {
            tcolor = "purpura";
        }
        if (color.equals("violet")) {
            tcolor = "violeta";
        }
        if (color.equals("magenta / fuchsia")) {
            tcolor = "fúcsia";
        }
        if (color.equals("beige")) {
            tcolor = "bege";
        }
        if (color.equals("pink")) {
            tcolor = "cor de rosa";
        }
        if (color.equals("ivory")) {
            tcolor = "marfim";
        }
        if (color.equals("black")) {
            tcolor = "preto";
        }
        if (color.equals("grey")) {
            tcolor = "cinza / cinzento";
        }
        if (color.equals("silver")) {
            tcolor = "prateado";
        }
        return tcolor;
    }

}
