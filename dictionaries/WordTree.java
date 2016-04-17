package dictionaries;

import java.util.ArrayList;

class WordLetters {
    public static final String allLetters = "abcdefghijklmnopqrstuvwxyzşçğıöüąćęłńóśźż";
    public static final int nrLetters = allLetters.length();
    
//better but not allowed since letters() not static:    
//  static final String letters = "abcdefghijklmnopqrstuvwxyz"+Turkish.letters()+Polish.letters();
}


class WordNode {

    WordNode[] next;
    int totalweight;
    int children;
    boolean isTerminal;

    public WordNode() {
        next = new WordNode[WordLetters.nrLetters];
        for (int i = 0; i < WordLetters.nrLetters; i++) {
            next[i] = null;
        };
        totalweight = 0;
        isTerminal=false; // last letter of  a word. children may be > 0

    }
}

public class WordTree {

    WordNode root;

    public WordTree() {
        root = new WordNode();
    }

    public void addWord(String word, int weight) {
        appendWord(word, weight, root);
    }

    private void appendWord(String word, int weight, WordNode n) {
        WordNode nextNode = null;
        n.totalweight = n.totalweight + weight;
        if (word.length() > 0) {
            char firstChar = word.charAt(0);
            //           System.out.println("appending : first char = " + firstChar + " next " + word.substring(1, word.length()));
            int firstIndex = charToInt(firstChar);
            if (n.next[firstIndex] == null) {
                n.next[firstIndex] = new WordNode();
                n.children++;
            }
            nextNode = n.next[firstIndex];
            appendWord(word.substring(1, word.length()), weight, nextNode);
        } else {
           n.isTerminal=true;
        }
    }

    private int countWords(WordNode n) {
        int total = 0;
        for (int i = 0; i < WordLetters.nrLetters; i++) {
            if (n.next[i] != null) {
                total = total + countWords(n.next[i]);
            };
        }
        return total;
    }

    public void printWordTree() {
        System.out.println("root(" + root.children + "," + root.totalweight + ")");
        printTree(root, 0);
    }

    private void printTree(WordNode n, int depth) {

        for (int i = 0; i < WordLetters.nrLetters; i++) {
            if (n.next[i] != null) {
                for (int k = 0; k <= depth + 1; k++) {
                    System.out.print(' ');
                }
                System.out.println(intToChar(i) + "(" + n.next[i].children + "," + n.next[i].totalweight + ")");
                printTree(n.next[i], depth + 2);
            }
        }
    }
    
    char[] prefix;
    int scanStemsCount;
    ArrayList<String> stemList = new ArrayList<String>();;
    
    public ArrayList<String> scanStems() {
        stemList.clear();
        prefix = new char[500];  // longest possible word in the tree
        scanStemsCount=0;
        scanStemsNode(root, 0);
        System.out.println(scanStemsCount+" stems found");
        return stemList;
        
    }

    private void scanStemsNode(WordNode n, int depth) {

        for (int i = 0; i < WordLetters.nrLetters; i++) {
            if (n.next[i] != null) {
                prefix[depth]=intToChar(i);
                String prefixString = new String(prefix,0,depth+1);
//                System.out.println(intToChar(i) + "["+prefixString+"]"+"(" + n.next[i].children + "," + n.next[i].totalweight + ")");
                if ((n.next[i].children>1)||(n.next[i].isTerminal && n.next[i].children>0)) {
                    scanStemsCount++;
                    stemList.add(prefixString);
//                    System.out.println("["+prefixString+"] stem for " + n.next[i].children + " words");
                }
                scanStemsNode(n.next[i], depth+1);
            }
        }
    }

    private int charToInt(char c) {
       String letters = WordLetters.allLetters;     
 //      System.out.println("charToInt "+c);
 //      System.out.println(" >> "+letters.indexOf((int) c));
        return letters.indexOf((int) c);
    }


    private char intToChar(int i) {
       String letters = WordLetters.allLetters;     
//       System.out.println("intToChar "+i);
//       System.out.println(" >> "+letters.charAt(i));
        return letters.charAt(i);
    }

}
