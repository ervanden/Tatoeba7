package tatoeba;

    public class Language {

        String shortName;
        String longName;
        int frequency;

 
      public Language(String shortName, String longName) {
            this.longName = longName;
            this.shortName = shortName;
            frequency=0;
        }
    }
