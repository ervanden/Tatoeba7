package languages;

import dictionaries.DutchDictionary;

public class Dutch extends GenericLanguage implements Language {

    DutchDictionary d = null;

    public Dutch() {
        languageName = "Dutch";
    }

    public DutchDictionary dictionary() {
        if (d == null) {
            d = new DutchDictionary(this);
            d.readDictionaryFromFile(dictionaryFileName());
        }
        return d;
    }

    public String letters() {
        return "";
    }

    public String removeDiacritics(String word) {
        return word;
    }

    public String color(String color) {
        String tcolor = "?"; // translated color
        if (color.equals("white")) {
            tcolor = "wit";
        }
        if (color.equals("brown")) {
            tcolor = "bruin";
        }
        if (color.equals("red")) {
            tcolor = "rood";
        }
        if (color.equals("salmon")) {
            tcolor = "zalmkleurig";
        }
        if (color.equals("orange")) {
            tcolor = "oranje";
        }
        if (color.equals("gold")) {
            tcolor = "goudkleurig";
        }
        if (color.equals("yellow")) {
            tcolor = "geel";
        }
        if (color.equals("olive")) {
            tcolor = "olijfgroen";
        }
        if (color.equals("green")) {
            tcolor = "goren";
        }
        if (color.equals("light blue")) {
            tcolor = "lichtblauw";
        }
        if (color.equals("turquoise")) {
            tcolor = "turquoise";
        }
        if (color.equals("dark blue")) {
            tcolor = "donkerblauw";
        }
        if (color.equals("blue")) {
            tcolor = "blauw";
        }
        if (color.equals("purple")) {
            tcolor = "purper";
        }
        if (color.equals("violet")) {
            tcolor = "lila";
        }
        if (color.equals("magenta / fuchsia")) {
            tcolor = "fucsia";
        }
        if (color.equals("beige")) {
            tcolor = "beige";
        }
        if (color.equals("pink")) {
            tcolor = "roze";
        }
        if (color.equals("ivory")) {
            tcolor = "ivoorkleurig";
        }
        if (color.equals("black")) {
            tcolor = "zwart";
        }
        if (color.equals("grey")) {
            tcolor = "grijs";
        }
        if (color.equals("silver")) {
            tcolor = "zilverkleurig";
        }
        return tcolor;
    }

}
