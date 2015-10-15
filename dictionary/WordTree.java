package dictionary;

import java.util.ArrayList;

class WordNode {

    WordNode[] next;
    int totalweight;
    int children;
    boolean isTerminal;

    public WordNode() {
        next = new WordNode[32];
        for (int i = 0; i < 32; i++) {
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
        for (int i = 0; i < 32; i++) {
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

        for (int i = 0; i < 32; i++) {
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

        for (int i = 0; i < 32; i++) {
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
        int i = 0;
        if (c == 'a') {
            return i;
        }
        i++;
        if (c == 'b') {
            return i;
        }
        i++;
        if (c == 'c') {
            return i;
        }
        i++;
        if (c == 'd') {
            return i;
        }
        i++;
        if (c == 'e') {
            return i;
        }
        i++;
        if (c == 'f') {
            return i;
        }
        i++;
        if (c == 'g') {
            return i;
        }
        i++;
        if (c == 'h') {
            return i;
        }
        i++;
        if (c == 'i') {
            return i;
        }
        i++;
        if (c == 'j') {
            return i;
        }
        i++;
        if (c == 'k') {
            return i;
        }
        i++;
        if (c == 'l') {
            return i;
        }
        i++;
        if (c == 'm') {
            return i;
        }
        i++;
        if (c == 'n') {
            return i;
        }
        i++;
        if (c == 'o') {
            return i;
        }
        i++;
        if (c == 'p') {
            return i;
        }
        i++;
        if (c == 'q') {
            return i;
        }
        i++;
        if (c == 'r') {
            return i;
        }
        i++;
        if (c == 's') {
            return i;
        }
        i++;
        if (c == 't') {
            return i;
        }
        i++;
        if (c == 'u') {
            return i;
        }
        i++;
        if (c == 'v') {
            return i;
        }
        i++;
        if (c == 'w') {
            return i;
        }
        i++;
        if (c == 'x') {
            return i;
        }
        i++;
        if (c == 'y') {
            return i;
        }
        i++;
        if (c == 'z') {
            return i;
        }
        i++;
        if (c == 'ş') {
            return i;
        }
        i++;
        if (c == 'ç') {
            return i;
        }
        i++;
        if (c == 'ğ') {
            return i;
        }
        i++;
        if (c == 'ı') {
            return i;
        }
        i++;
        if (c == 'ö') {
            return i;
        }
        i++;
        if (c == 'ü') {
            return i;
        }
        i++;
        System.out.println("Invalid character in dictionary word");
        return -1;
    }

    private char intToChar(int i) {
        if (i == 0) {
            return 'a';
        }
        if (i == 1) {
            return 'b';
        }
        if (i == 2) {
            return 'c';
        }
        if (i == 3) {
            return 'd';
        }
        if (i == 4) {
            return 'e';
        }
        if (i == 5) {
            return 'f';
        }
        if (i == 6) {
            return 'g';
        }
        if (i == 7) {
            return 'h';
        }
        if (i == 8) {
            return 'i';
        }
        if (i == 9) {
            return 'j';
        }
        if (i == 10) {
            return 'k';
        }
        if (i == 11) {
            return 'l';
        }
        if (i == 12) {
            return 'm';
        }
        if (i == 13) {
            return 'n';
        }
        if (i == 14) {
            return 'o';
        }
        if (i == 15) {
            return 'p';
        }
        if (i == 16) {
            return 'q';
        }
        if (i == 17) {
            return 'r';
        }
        if (i == 18) {
            return 's';
        }
        if (i == 19) {
            return 't';
        }
        if (i == 20) {
            return 'u';
        }
        if (i == 21) {
            return 'v';
        }
        if (i == 22) {
            return 'w';
        }
        if (i == 23) {
            return 'x';
        }
        if (i == 24) {
            return 'y';
        }
        if (i == 25) {
            return 'z';
        }
        if (i == 26) {
            return 'ş';
        }
        if (i == 27) {
            return 'ç';
        }
        if (i == 28) {
            return 'ğ';
        }
        if (i == 29) {
            return 'ı';
        }
        if (i == 30) {
            return 'ö';
        }
        if (i == 31) {
            return 'ü';
        }
        return '#';
    }

}
