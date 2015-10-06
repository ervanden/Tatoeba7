package langoperations;

import javax.swing.JFileChooser;
import langeditor.Dictionary;

public class PolishOperations implements LanguageOperations {

    Dictionary d=null;

    public String dictionaryFileName() {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        return defaultFolder + "\\Tatoeba\\PolishDictionary.txt";
    }

    public Dictionary dictionary() {
        return d;
    }

    public void initialize() {
        if (d==null) {
        d = new Dictionary();
        d.readDictionaryFromFile(dictionaryFileName());
        }
    }

    public String invertDiacritics(String word) {
        char c;
        String newword = "";
        for (int i = 0; i <= word.length() - 1; i++) {
            c = word.charAt(i);
            if (c == 'ą') {
                c = 'a';
            } else if (c == 'a') {
                c = 'ą';
            } else if (c == 'Ą') {
                c = 'A';
            } else if (c == 'A') {
                c = 'Ą';
            } else if (c == 'ć') {
                c = 'c';
            } else if (c == 'c') {
                c = 'ć';
            } else if (c == 'Ć') {
                c = 'C';
            } else if (c == 'C') {
                c = 'Ć';
            } else if (c == 'ę') {
                c = 'e';
            } else if (c == 'e') {
                c = 'ę';
            } else if (c == 'Ę') {
                c = 'E';
            } else if (c == 'E') {
                c = 'Ę';
            } else if (c == 'ł') {
                c = 'l';
            } else if (c == 'l') {
                c = 'ł';
            } else if (c == 'Ł') {
                c = 'L';
            } else if (c == 'L') {
                c = 'Ł';
            } else if (c == 'ń') {
                c = 'n';
            } else if (c == 'n') {
                c = 'ń';
            } else if (c == 'Ń') {
                c = 'N';
            } else if (c == 'N') {
                c = 'Ń';
            } else if (c == 'ó') {
                c = 'o';
            } else if (c == 'o') {
                c = 'ó';
            } else if (c == 'Ó') {
                c = 'O';
            } else if (c == 'O') {
                c = 'Ó';
            } else if (c == 'ś') {
                c = 's';
            } else if (c == 's') {
                c = 'ś';
            } else if (c == 'Ś') {
                c = 'S';
            } else if (c == 'S') {
                c = 'Ś';
            } else if (c == 'ź') {
                c = 'ż';
            } else if (c == 'Ź') {
                c = 'Z';
            } else if (c == 'ż') {
                c = 'z';
            } else if (c == 'Ż') {
                c = 'Z';
            } else if (c == 'z') {
                c = 'ź';
            } else if (c == 'Z') {
                c = 'Ź';
            }
            newword = newword + c;

        }
        return newword;
    }

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

}
