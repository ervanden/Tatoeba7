package tatoeba;

    public class LanguageName {

        String shortName;
        String longName;
        int frequency;

 
      public LanguageName(String shortName, String longName) {
            this.longName = longName;
            this.shortName = shortName;
            frequency=0;
        }
    }
