package tatoeba;

import java.util.HashSet;
import java.util.LinkedList;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import utils.*;

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
                    //                   MsgTextPane.write("line " + lineNr + " lang=" + lineArray[0] + " sentence |" + lineArray[1] + "|");
                    Sentence s = new Sentence();
                    s.language = lineArray[0];
                    s.language = s.language.replaceAll("^ *", "");
                    s.language = s.language.replaceAll(" *$", "");
                    String sentence = lineArray[1];
                    s.set(sentence);

                    // sanity check
                    if (!selectionFrame.sourceLanguages.contains(s.language)
                            && !selectionFrame.targetLanguages.contains(s.language)) {
                        MsgTextPane.write("unrecognized language <" + s.language + ">");
                    } else {
                        sentences.add(s);
                        MsgTextPane.write("added sentence to cluster " + nr + " nr of sentences=" + sentences.size());
                    }
                } else { // format must be :  lang> sentence
                    MsgTextPane.write("line " + lineNr + " DISCARDED");
                }
            } catch (BadLocationException ble) {
            }
        }

    }

    public void readCommentsFromDocument(StyledDocument document, SelectionFrame selectionFrame) {
        // read comments from comment area. 
        javax.swing.text.Element root = document.getDefaultRootElement();
        int count = root.getElementCount();
        for (int i = 0; i < count; i++) {
            javax.swing.text.Element lineElement = (javax.swing.text.Element) root.getElement(i);
            int start = lineElement.getStartOffset();
            int end = lineElement.getEndOffset();
            try {
                String line = document.getText(start, end - start - 1);
                Sentence s = new Sentence();
                s.language = selectionFrame.targetLanguage();
                s.set(line);
                s.comment = true;
                sentences.add(s);
                MsgTextPane.write("added [" + s.language + "] to cluster " + nr + " :" + s.sentence);
            } catch (BadLocationException ble) {
            }
        }

    }

    public synchronized void add(Sentence v) {
        sentences.add(v);
    }

}
