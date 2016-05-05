package languages;

import dictionaries.PolishDictionary;
import java.util.HashMap;

public class Polish extends GenericLanguage implements Language {

    PolishDictionary d = null;

    public Polish() {
        languageName = "Polish";
    }

    public PolishDictionary dictionary() {
        if (d == null) {
            d = new PolishDictionary(this);
            d.readDictionaryFromFile(dictionaryFileName());
        }
        return d;
    }

    public String letters() {
        return "ąćęłńóśżź";
    }
    
    public String diacriticsGroups(){
        return "aą cć eę lł nń oó sś zżź AĄ CĆ EĘ LŁ OÓ SŚ ZŻŹ";
    }
/*
    public String invertDiacritics(String word) {

        return invertDiacriticsGeneric(word, "aąa cćc eęe lłl nńn oóo sśs zżźz AĄA CĆC EĘE LŁL OÓO SŚS ZŻŹZ");

    }
*/
 
/*
    public String removeDiacritics(String word) {
        word = word.replaceAll("ą", "a");
        word = word.replaceAll("Ą", "A");
        word = word.replaceAll("ć", "c");
        word = word.replaceAll("Ć", "C");
        word = word.replaceAll("ę", "e");
        word = word.replaceAll("Ę", "E");
        word = word.replaceAll("ł", "l");
        word = word.replaceAll("Ł", "L");
        word = word.replaceAll("ń", "n");
        word = word.replaceAll("Ń", "N");
        word = word.replaceAll("ó", "o");
        word = word.replaceAll("Ó", "O");
        word = word.replaceAll("ś", "s");
        word = word.replaceAll("Ś", "S");
        word = word.replaceAll("ź", "z");
        word = word.replaceAll("Ź", "Z");
        word = word.replaceAll("ż", "z");
        word = word.replaceAll("Ż", "Z");
        return word;
    }
*/

    public String number(int n) {
        HashMap<Integer, String> nrs = new HashMap<>();

        nrs.put(0, "zero");
        nrs.put(1, "jeden");
        nrs.put(2, "dwa");
        nrs.put(3, "trzy");
        nrs.put(4, "cztery");
        nrs.put(5, "pięć");
        nrs.put(6, "sześć");
        nrs.put(7, "siedem");
        nrs.put(8, "osiem");
        nrs.put(9, "dziewięć");
        nrs.put(10, "dziesięć");
        nrs.put(11, "jedenaście");
        nrs.put(12, "dwanaście");
        nrs.put(13, "trzynaście");
        nrs.put(14, "czternaście");
        nrs.put(15, "piętnaście");
        nrs.put(16, "szesnaście");
        nrs.put(17, "siedemnaście");
        nrs.put(18, "osiemnaście");
        nrs.put(19, "dziewiętnaście");
        nrs.put(20, "dwadzieścia");
        nrs.put(30, "trzydzieści");
        nrs.put(40, "czterdzieści");
        nrs.put(50, "pięćdzesiąt");
        nrs.put(60, "sześćdzesiąt");
        nrs.put(70, "siedemdzesiąt");
        nrs.put(80, "osiemdzesiąt");
        nrs.put(90, "dziewięćdzesiąt");
        nrs.put(100, "sto");
        nrs.put(200, "dwieście");
        nrs.put(300, "trzysta");
        nrs.put(400, "czterysta");
        nrs.put(500, "pięćset");
        nrs.put(600, "sześćset");
        nrs.put(700, "siedemset");
        nrs.put(800, "osiemset");
        nrs.put(900, "dziewięćset");
        nrs.put(1000, "tysiąc");
        nrs.put(2000, "dwa tysiące");
        nrs.put(3000, "trzy tysiące");
        nrs.put(5000, "pięć tysięcy");

        int nlow3 = n % 1000;
        int n1000 = (n - nlow3) / 1000;
        int nlow2 = nlow3 % 100;
        int n100 = (nlow3 - nlow2) / 100;
        int nlow1 = nlow2 % 10;
        int n10 = (nlow2 - nlow1) / 10;
        int n1 = nlow1;

        //       System.out.println(" n=" + (n1000 * 1000 + n100 * 100 + n10 * 10 + n1));
        String s = "";

        if (n == 0) {
            return nrs.get(0);
        }

        //thousands
        if (n1000 > 0) {
            if (n1000 == 1) {
                s = "tysiąc";
            } else if (n1000 % 10 == 2) {
                s = number(n1000) + " tysiące";
            } else if (n1000 % 10 == 3) {
                s = number(n1000) + " tysiące";
            } else if (n1000 % 10 == 4) {
                s = number(n1000) + " tysiące";
            } else {
                s = number(n1000) + " tysięcy";
            }
        }

        // hundreds
        if (n100 > 0) {
            s = s + " " + nrs.get(n100 * 100);
        }

        // 0-99
        if (n10 == 1) {  // 10 - 19
            s = s + " " + nrs.get(10 + n1);
        } else {  // 20-99
            if (n10 > 0) { // 20-90
                s = s + " " + nrs.get(n10 * 10);
            }
            if (n1 > 0) {  // 1-9
                s = s + " " + nrs.get(n1);
            }
        }
        return s;
    }

    public String color(String color) {
        String tcolor = "?"; // translated color
        if (color.equals("white")) {
            tcolor = "biały";
        }
        if (color.equals("brown")) {
            tcolor = "brązowy";
        }
        if (color.equals("red")) {
            tcolor = "czerwony";
        }
        if (color.equals("salmon")) {
            tcolor = "łososiowy";
        }
        if (color.equals("orange")) {
            tcolor = "pomarańczowy";
        }
        if (color.equals("gold")) {
            tcolor = "złoty";
        }
        if (color.equals("yellow")) {
            tcolor = "żółty";
        }
        if (color.equals("olive")) {
            tcolor = "oliwkowy";
        }
        if (color.equals("green")) {
            tcolor = "zielony";
        }
        if (color.equals("light blue")) {
            tcolor = "jasny niebieski";
        }
        if (color.equals("turquoise")) {
            tcolor = "turkusowy";
        }
        if (color.equals("dark blue")) {
            tcolor = "ciemny niebieski";
        }
        if (color.equals("blue")) {
            tcolor = "niebieski";
        }
        if (color.equals("purple")) {
            tcolor = "purpurowy";
        }
        if (color.equals("violet")) {
            tcolor = "fioletowy";
        }
        if (color.equals("magenta / fuchsia")) {
            tcolor = "fuksja";
        }
        if (color.equals("beige")) {
            tcolor = " beżowy";
        }
        if (color.equals("pink")) {
            tcolor = "różowy";
        }
        if (color.equals("ivory")) {
            tcolor = "koloru kości słoniowej";
        }
        if (color.equals("black")) {
            tcolor = "czarny";
        }
        if (color.equals("grey")) {
            tcolor = "szary";
        }
        if (color.equals("silver")) {
            tcolor = "srebrny";
        }
        return tcolor;
    }

}
