
package languages;

import dictionary.GenericDictionary;



public interface Language {

        public String invertDiacritics(String word);
        public String removeDiacritics(String word);
        public String dictionaryFileName();
        public GenericDictionary dictionary();
        public void disposeDictionary();
        public String number(int n);

}  

