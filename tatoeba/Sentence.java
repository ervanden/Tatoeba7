package tatoeba;


import tatoeba.Cluster;


public class Sentence {

    int nr;
    String language;
    String sentence;
    Sentence modified;  // modified version of another sentence in this cluster
    Cluster cluster;

    public Sentence() {
        cluster = null;
    }

    public String printString() {
        return nr + "|" + language + "|" + sentence;
    }
}
