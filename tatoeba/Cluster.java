package tatoeba;


import java.util.HashSet;
import java.util.LinkedList;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class Cluster {

    int nr;
    LinkedList<Sentence> sentences = new LinkedList<>();
    int complexity;
    HashSet<String> tags = new HashSet<>();
    boolean unsaved;
    boolean selected; // set to  true if the two others are true
    boolean selectedByComplexity;
    boolean selectedByParameters;

    public Cluster() {
        unsaved = false;
        complexity = 0;
        selectedByParameters = true;
        selectedByComplexity = true;
    }

    public void readSentencesFromDocument(StyledDocument document, SelectionFrame selectionFrame) {
        // read sentences from edited source and target text panes
        javax.swing.text.Element root = document.getDefaultRootElement();
        int lineNr;
        int count = root.getElementCount();
        for (int i = 0; i < count; i++) {
            lineNr = i + 1;
            javax.swing.text.Element lineElement = (javax.swing.text.Element) root.getElement(i);
            int start = lineElement.getStartOffset();
            int end = lineElement.getEndOffset();
            try {
                String line = document.getText(start, end - start - 1);
                String[] lineArray = line.split(">");
                if ((lineArray.length == 2) && !(lineArray[1].equals(""))) {
                    System.out.println("line " + lineNr + " lang=" + lineArray[0] + " sentence |" + lineArray[1] + "|");
                    Sentence s = new Sentence();
                    s.language = lineArray[0];
                    s.language = s.language.replaceAll("^ *", "");
                    s.language = s.language.replaceAll(" *$", "");
                    s.sentence = lineArray[1];
                    s.sentence = s.sentence.replaceAll("^ *", "");
                    s.sentence = s.sentence.replaceAll(" *$", "");

                    // sanity check
                    
                    if (!selectionFrame.sourceLanguages.contains(s.language)
                            && !selectionFrame.targetLanguages.contains(s.language)) {
                        System.out.println("unrecognized language <" + s.language + ">");
                    } else {
                        sentences.add(s);
                        System.out.println("added sentence to cluster " + nr + " nr of sentences=" + sentences.size());
                    }
                } else {
                    if (line.matches("^ *[a-zA-Z0-9]+ *$")) {
                        line = line.replaceAll(" ", "");
                        tags.add(line);
                        System.out.println("added tag " + line + " to cluster " + nr + " nr of tags=" + tags.size());
                    } else { // format must be :  lang> sentence
                        System.out.println("line " + lineNr + " DISCARDED");
                    }
                }
            } catch (BadLocationException ble) {
            }
        }

    }
    
    
        public void readTagsFromDocument(StyledDocument document) {
        // read tags from edited info text panes
        javax.swing.text.Element root = document.getDefaultRootElement();
        int lineNr;
        String s1,s2;
        int count = root.getElementCount();
        for (int i = 0; i < count; i++) {
            lineNr = i + 1;
            javax.swing.text.Element lineElement = (javax.swing.text.Element) root.getElement(i);
            int start = lineElement.getStartOffset();
            int end = lineElement.getEndOffset();
            try {
                String line = document.getText(start, end - start - 1);
                String[] lineArray = line.split(":");
                if (lineArray.length == 2) {

                    s1 = lineArray[0];
                    s1=s1.replaceAll("^ *", "");
                    s1=s1.replaceAll(" *$", "");
                    s2 = lineArray[1];
                    s2=s2.replaceAll("^ *", "");
                    s2=s2.replaceAll(" *$", "");

                   if (s1.equals("tag")) tags.add(s2);

                }
            } catch (BadLocationException ble) {
            }
        }

    }

    public synchronized void add(Sentence v) {
        sentences.add(v);
    }

}
