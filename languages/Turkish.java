package languages;

import dictionaries.TurkishDictionary;
import java.util.HashMap;

public class Turkish extends GenericLanguage implements Language {

    TurkishDictionary d = null;

    public Turkish() {
        languageName = "Turkish";
    }

    public TurkishDictionary dictionary() {
        if (d == null) {
            d = new TurkishDictionary(this);
            d.readDictionaryFromFile(dictionaryFileName());
        }
        return d;
    }

    public char toUpperCase(char c) {
        if (c == 'i') {   // toUpperCase does not do I correctly
            return 'İ';
        } else {
            return Character.toUpperCase(c);
        }
    }

    public String toLowerCase(String s) {
        return s.replaceAll("[İI]", "i").toLowerCase();
    }

    public String letters() {
        return "şçğıöü";
    }

    public String invertDiacritics(String word) {

        return invertDiacriticsGeneric(word, "sşs cçc gğg iıi oöo uüu CÇC GĞG İIİ OÖO SŞS UÜU");

    }
    
    /*

    public String removeDiacritics(String word) {
        word = word.replaceAll("ş", "s");
        word = word.replaceAll("Ş", "S");
        word = word.replaceAll("ç", "c");
        word = word.replaceAll("Ç", "C");
        word = word.replaceAll("ğ", "g");
        word = word.replaceAll("ı", "i");
        word = word.replaceAll("I", "İ");
        word = word.replaceAll("ö", "o");
        word = word.replaceAll("Ö", "O");
        word = word.replaceAll("ü", "u");
        word = word.replaceAll("Ü", "U");
        return word;
    }
    */

    public String number(int n) {
        HashMap<Integer, String> nrs = new HashMap<>();

        nrs.put(0, "sıfır");
        nrs.put(1, "bir");
        nrs.put(2, "iki");
        nrs.put(3, "üç");
        nrs.put(4, "dört");
        nrs.put(5, "beş");
        nrs.put(6, "altı");
        nrs.put(7, "yedi");
        nrs.put(8, "sekiz");
        nrs.put(9, "dokuz");
        nrs.put(10, "on");
        nrs.put(20, "yirmi");
        nrs.put(30, "otuz");
        nrs.put(40, "kırk");
        nrs.put(50, "elli");
        nrs.put(60, "altmış");
        nrs.put(70, "yetmiş");
        nrs.put(80, "seksen");
        nrs.put(90, "doksan");
        nrs.put(100, "yüz");
        nrs.put(1000, "bin");

        int nlow3 = n % 1000;
        int n1000 = (n - nlow3) / 1000;
        int nlow2 = nlow3 % 100;
        int n100 = (nlow3 - nlow2) / 100;
        int nlow1 = nlow2 % 10;
        int n10 = (nlow2 - nlow1) / 10;
        int n1 = nlow1;

        //       System.out.println(" n=" + (n1000 * 1000 + n100 * 100 + n10 * 10 + n1));
        String s = "";

        //thousands
        if (n1000 > 0) {
            if (n1000 == 1) {
                s = "bin";
            } else {
                s = number(n1000) + " bin";
            }
        }

        if (n100 > 0) {
            s = s + " " + nrs.get(n100) + " " + nrs.get(100);
        }
        if (n10 > 0) {
            s = s + " " + nrs.get(n10 * 10);
        }
        if (n1 > 0) {
            s = s + " " + nrs.get(n1);
        }

        return s;
    }

    public String color(String color) {
        String tcolor = "?"; // translated color
        if (color.equals("white")) {
            tcolor = "beyaz";
        }
        if (color.equals("black")) {
            tcolor = "siyah";
        }
        if (color.equals("brown")) {
            tcolor = "kahverengi";
        }
        if (color.equals("red")) {
            tcolor = "kırmızı";
        }
        if (color.equals("salmon")) {
            tcolor = "?";
        }
        if (color.equals("orange")) {
            tcolor = "turuncu";
        }
        if (color.equals("yellow")) {
            tcolor = "sarı";
        }
        if (color.equals("olive")) {
            tcolor = "zeytin rengi";
        }
        if (color.equals("green")) {
            tcolor = "yeşil";
        }
        if (color.equals("ivory")) {
            tcolor = "?";
        }
        if (color.equals("light blue")) {
            tcolor = "açık mavi";
        }
        if (color.equals("turquoise")) {
            tcolor = "turkuvaz";
        }
        if (color.equals("dark blue")) {
            tcolor = "koyu mavi";
        }
        if (color.equals("blue")) {
            tcolor = "mavi";
        }
        if (color.equals("purple")) {
            tcolor = "mor";
        }
        if (color.equals("violet")) {
            tcolor = "menekşe rengi";
        }
        if (color.equals("magenta / fuchsia")) {
            tcolor = "galibarda";
        }
        if (color.equals("beige")) {
            tcolor = "bej";
        }
        if (color.equals("pink")) {
            tcolor = "pembe";
        }
        if (color.equals("black")) {
            tcolor = "siyah";
        }
        if (color.equals("grey")) {
            tcolor = "gri";
        }
        if (color.equals("silver")) {
            tcolor = "gümüş renkli";
        }
        return tcolor;
    }

}
