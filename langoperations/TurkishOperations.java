package langoperations;

import javax.swing.JFileChooser;
import langeditor.Dictionary;

public class TurkishOperations implements LanguageOperations {

    Dictionary d = null;

    public String dictionaryFileName() {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        return defaultFolder + "\\Tatoeba\\TurkishDictionary.txt";
    }

    public Dictionary dictionary() {
        return d;
    }

    public void initialize() {
        if (d == null) {
            d = new Dictionary();
            d.readDictionaryFromFile(dictionaryFileName());
        }
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
        return (String.format("%s", n));
    }

}
