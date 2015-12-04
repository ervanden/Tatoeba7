package tatoeba;


public class Sentence {

    int nr;
    String language;
    String sentence;
    boolean comment;    // comment on the sentence(s) of this cluster in this language
    Sentence modified;  // modified version of another sentence in this cluster
    Cluster cluster;

    public Sentence() {
        cluster = null;
    }

    public String printString() {
        return nr + "|" + language + "|" + sentence;
    }
}
