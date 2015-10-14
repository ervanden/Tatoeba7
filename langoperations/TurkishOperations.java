package langoperations;

import java.util.HashMap;
import javax.swing.JFileChooser;
import langeditor.Dictionary;

public class TurkishOperations implements LanguageOperations {

    Dictionary d = null;

    public String dictionaryFileName() {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        return defaultFolder + "\\Tatoeba\\TurkishDictionary.txt";
    }

    public Dictionary dictionary() {
         if (d == null) {
            d = new Dictionary();
            d.readDictionaryFromFile(dictionaryFileName());
        }       
        return d;
    }
    
    public void disposeDictionary(){
        d=null;
    }
    

    public String invertDiacritics(String word) {
        char c;
        String newword = "";
        for (int i = 0; i <= word.length() - 1; i++) {
            c = word.charAt(i);
            if (c == 'ş') {
                c = 's';
            } else if (c == 'Ş') {
                c = 'S';
            } else if (c == 'ç') {
                c = 'c';
            } else if (c == 'Ç') {
                c = 'C';
            } else if (c == 'ğ') {
                c = 'g';
            } else if (c == 'ı') {
                c = 'i';
            } else if (c == 'İ') {
                c = 'I';
            } else if (c == 'ö') {
                c = 'o';
            } else if (c == 'Ö') {
                c = 'O';
            } else if (c == 'ü') {
                c = 'u';
            } else if (c == 'Ü') {
                c = 'U';
            } else if (c == 's') {
                c = 'ş';
            } else if (c == 'S') {
                c = 'Ş';
            } else if (c == 'c') {
                c = 'ç';
            } else if (c == 'C') {
                c = 'Ç';
            } else if (c == 'g') {
                c = 'ğ';
            } else if (c == 'i') {
                c = 'ı';
            } else if (c == 'I') {
                c = 'İ';
            } else if (c == 'o') {
                c = 'ö';
            } else if (c == 'O') {
                c = 'Ö';
            } else if (c == 'u') {
                c = 'ü';
            } else if (c == 'U') {
                c = 'Ü';
            }
            newword = newword + c;

        }
        return newword;
    }

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

    public String number(int n) {
        HashMap<Integer, String> nrs = new HashMap<>();

        nrs.put(0, "sifir");
        nrs.put(1, "bir");
        nrs.put(2, "iki");
        nrs.put(3, "uc");
        nrs.put(4, "dort");
        nrs.put(5, "bes");
        nrs.put(6, "alti");
        nrs.put(7, "yedi");
        nrs.put(8, "sekiz");
        nrs.put(9, "dokuz");
        nrs.put(10, "on");
        nrs.put(20, "yirmi");
        nrs.put(30, "otuz");
        nrs.put(40, "kirk");
        nrs.put(50, "elli");
        nrs.put(60, "altmis");
        nrs.put(70, "yetmis");
        nrs.put(80, "seksen");
        nrs.put(90, "doksan");
        nrs.put(100, "yuz");
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

    

 //   private class Dictionary extends langeditor.GenericDictionary {
 //   }
    

}
