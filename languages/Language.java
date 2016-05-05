package languages;

import dictionaries.GenericDictionary;

public interface Language {

    public char toUpperCase(char c);
        // implemented using Character.toUpperCase in GenericLanguage but should be overwritten when 
    // this method does not work correctly , as for Turkish

    public String toLowerCase(String s);
        // same as for toUpperCase()

    public  String letters();
        // returns a string with all the language-specific letters.
    
    public String diacriticsGroups();
        // returns a string with characters and their corresponding diacritics, separated by blanks

//    public String invertDiacritics(String word);
         // swap plain latin letters to letters with accents and vice versa

    public String removeDiacritics(String word);
        // replace letters with accents with plain latin letters

    public String dictionaryFileName();
        // the file containing the dictionary for this language

    public GenericDictionary dictionary();
        // the in-memory dictionary for this language
    // this dictionary is read from its file when dictionary() is called for the first time
    // The dictionary that is returned for a specific language extends GenericDictionary, with some functions overwritten
    // Common functions like readDictionaryFromFile are implemented in GenericDictionary

    public void disposeDictionary();
        // to be called on 'exit without saving dictionary'
    // If dictionary() is called again, it is a completley new object and the dictionary file is reread

    public String number(int n);
        // Convert an integer number to its translation in this language

    public String color(String color);
        // Convert the english name of a color to the name in this language

    public String translate(String theme, String word);
        // translates the picture names from english to this language

    public void updateWordMaps();
        // causes all word maps for all languages to be augmented with the missing picture names
    
    public void rereadWordMaps();
       // reset Word Maps for this lang
}
