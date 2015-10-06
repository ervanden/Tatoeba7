
package langoperations;

import langeditor.Dictionary;



public interface LanguageOperations {

        public String invertDiacritics(String word);
                public String removeDiacritics(String word);
        public String dictionaryFileName();
        public Dictionary dictionary();
        public void initialize();

}  

