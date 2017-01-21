package tatoeba;

public class Sentence {

    int nr;
    String language;
    String sentence;
    int complexity;
    boolean comment;    // comment on the sentence(s) of this cluster in this language
    Sentence modified;  // modified version of another sentence in this cluster
    Cluster cluster;

    public Sentence() {
        cluster = null;
        comment = false;
    }

    public void set(String s) {
        //remove trailing and leading blanks unless it is a comment
        if (!comment) {
            s = s.replaceAll("^ *", "");
            s = s.replaceAll(" *$", "");
        }
        sentence = s;
 //       complexity = s.length();
        complexity=wordCount(s);
    }

    public String printString() {
        return nr + "|" + language + "|" + sentence;
    }

    public static boolean isLetter(char c) {
        return (Character.isAlphabetic(c) || (c == '\''));
    }

    private int wordCount(String s) {
        int lastpos = s.length()-1;
        int wc = 0;
        int pos = 0;
 
        while (pos <= lastpos) {
            // skip non-letters
            while ((pos <= lastpos) && !(isLetter(s.charAt(pos)))){
                pos++;
            }
            // skip letters
            boolean skippedWord=false;
            while ((pos <= lastpos) && (isLetter(s.charAt(pos)))){
                skippedWord=true;
                pos++;
            }
            // skip non-letters
            while ((pos <= lastpos) && !(isLetter(s.charAt(pos)))){
                pos++;
            }
            if (skippedWord) wc++;
        }
//System.out.println("wordCount on |"+s+"| = " +wc);
        return wc;
    }
}
