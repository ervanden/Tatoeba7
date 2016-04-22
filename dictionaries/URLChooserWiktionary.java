package dictionaries;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.StyledDocument;
import languages.Language;
import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;

import org.jsoup.examples.ListLinks;
import org.jsoup.nodes.Document;

import utils.MsgTextPane;
import utils.FileOpener;

class UrlNode {
// encapsulates an url and the level in the reference tree
// level is needed to be able to impose a limit on the depth of the traversal.
// The level of the URL is stored by means of UrlNode in the queue todoUrls 
// By overriding equals() the contains() method only looks at the URL, not at the level
// to check if an URL is already on the queue

    String urlString;
    int level;

    public UrlNode(String s, int i) {
        urlString = s;
        level = i;
    }

    public String printString() {
        return String.format("[%d] %s", level, urlString);
    }

    @Override
    public boolean equals(Object o) {
        UrlNode u = (UrlNode) o;
        return (urlString.equals(u.urlString));
    }

    @Override
    public int hashCode() {
        return urlString.hashCode() * level;
    }
}

public class URLChooserWiktionary extends JFrame implements ActionListener {

    public boolean stop = false;

    private JFrame thisFrame = (JFrame) this;
    private JTextPane editArea;
    private JTextPane msgArea;

    FileOpener f;

    Language language;

    JScrollPane scrollingEditArea;
    JScrollPane scrollingMsgArea;

    JPanel content = new JPanel();
    JButton buttonScan = new JButton("URL to file");
    JButton buttonStopScan = new JButton("Stop");
    JButton buttonProcess = new JButton("File to Dictionary");
    JButton buttonStopProcess = new JButton("Stop");
    JButton buttonLinks = new JButton("show links");
    JButton buttonWords = new JButton("show words");
    JButton buttonLimit = new JButton("download limit");

    ArrayDeque<UrlNode> todoUrls = new ArrayDeque<>();
    SortedSet<String> doneUrls = new TreeSet<String>();
    int downloadedBytes = 0;
    int downloadedBytesLimit;
    int downloadedWords = 0;
    int downloadedWordsLimit;

    boolean showLinks;
    boolean showWords;

    SortedSet<String> urlWords = new TreeSet<String>();
    Hashtable<String, Integer> dictCandidatesFrequency = new Hashtable<String, Integer>();
    Hashtable<String, String> xDictWords = new Hashtable<String, String>();
    Hashtable<String, Integer> xDictFrequency = new Hashtable<String, Integer>();

    public URLChooserWiktionary(Language l) {
        language = l;
    }

    public void writeMsg(String msg) {

        javax.swing.text.Document doc = msgArea.getDocument();

        try {
            doc.insertString(doc.getLength(), msg + "\n", null);
        } catch (BadLocationException blex) {
            writeMsg("bad location in URLChooser.writeMsg");
        }
        msgArea.setCaretPosition(doc.getLength());
    }

    class ExtractFromURLList implements Runnable {

        public void run() {
            stop = false;

            f.openOutputFile(1000000);

            downloadedWords = 0;
            String urlString;
            int rootUrls = 0;

            StyledDocument document = (StyledDocument) editArea.getDocument();

            if (document.getLength() == 0) { // if editArea is empty, read urls from a file 

                f.openInputFile();

                while (!stop && (urlString = f.readLine()) != null) {
                    rootUrls++;
                    writeMsg(rootUrls + " URL FROM FILE : <" + urlString + ">");
                    try {   // start with a new root URL

                        URL rootURL = new URL(urlString);  // test if valid url string

                        todoUrls.clear();
                        doneUrls.clear();
                        todoUrls.addLast(new UrlNode(urlString, 0));
                        while (!stop && extractFromURLQueue(rootURL)) {
                        };

                    } catch (MalformedURLException mfu) {
                        writeMsg("Not a URL : <" + urlString + ">");
                    }
                }
                f.closeInputFile();

            } else {  // read urls from editArea

                javax.swing.text.Element root = document.getDefaultRootElement();
                int count = root.getElementCount();
                for (int i = 0; i < count; i++) {
                    javax.swing.text.Element lineElement = (javax.swing.text.Element) root.getElement(i);
                    try {
                        urlString = document.getText(lineElement.getStartOffset(),
                                lineElement.getEndOffset() - lineElement.getStartOffset() - 1);

                        if (!stop && (urlString != null) && (!urlString.equals(""))) {

                            writeMsg("URL : <" + urlString + ">");
                            try {   // start with a new root URL

                                URL rootURL = new URL(urlString);  // test if valid url string

                                todoUrls.clear();
                                doneUrls.clear();
                                todoUrls.addLast(new UrlNode(urlString, 0));
                                while (!stop && extractFromURLQueue(rootURL)) {
                                };

                            } catch (MalformedURLException mfu) {
                                writeMsg("Not a URL : <" + urlString + ">");
                            }
                        }
                    } catch (BadLocationException ble) {
                        writeMsg("Bad location in url window ");
                    }
                }
            }

            writeMsg("end");
            f.closeOutputFile();
        }
    }

    public boolean extractFromURLQueue(URL rootURL) {
        String urlString;
        UrlNode urlNode;
        ArrayList<String> urlList = new ArrayList<>();
        urlList.clear();
        boolean subURLs = true;
        int maxSubLevel = 0;

        urlNode = todoUrls.peekFirst();
        if (urlNode == null) {
            return false;
        } else {
            urlString = urlNode.urlString;
            todoUrls.removeFirst();
            doneUrls.add(urlString);

            extractFromURL(urlString);

            if (showLinks) {
                writeMsg("PROCESSED: " + urlNode.printString());
            }
            /*
             String word = urlString.replace("https://pl.wiktionary.org/wiki/", "");           
             if (!word.contains(":")) {
             writeMsg("<"+word+">");
             fileWrite(word);
             };
             */
            if (subURLs) {
//                                    writeMsg("SUBURLs");
                urlList = ListLinks.run(urlString);
                int urlcount = 0;
                for (String suburlString : urlList) {
                    //                   writeMsg("SUBURL " + suburlString);
                    urlcount++;
                    try {
                        URL subURL = new URL(suburlString);
                        if (rootURL.getHost().equals(subURL.getHost())) {

                            // remove trailing #
                            suburlString = suburlString.replaceAll("#.*$", "");

                            boolean skip = false;
                            if (urlNode.level >= maxSubLevel) {
                                skip = true;
                            }
                            if (suburlString.contains("Specjalna:")) {
                                skip = true;
                            }
                            //                           if (suburlString.contains("pl.wiktionary.org/w/")) skip=true;
                            if (!skip) {
                                UrlNode suburlNode = new UrlNode(suburlString, urlNode.level + 1);
                                if (suburlString.contains("Kategoria:polski_(indeks)&pagefrom=")
                                        && suburlString.contains("subcatfrom=")) {
                                    writeMsg("RESET: " + suburlString);
                                    suburlNode.level = 0;
                                }
                                if (!doneUrls.contains(suburlString) && !todoUrls.contains(suburlNode)) {
                                    todoUrls.addLast(suburlNode);
                                    if (showLinks) {
                                        writeMsg("ADDED: " + suburlNode.printString() + " todo= " + todoUrls.size() + " done= " + doneUrls.size());
                                    }
                                }
                            }
                        }
                    } catch (MalformedURLException mfu) {
                        //                                 no msg for invalid sub url's
                    }
                }
            }
            return true;
        }
    }

    public void extractFromURL(String url) {

        f.writeln("<" + url + ">");

        if (doneUrls.size() % 10 == 1) {
            writeMsg("totalWords   words  downloaded   urls:done  urls:todo");
        }

        try {

            Document doc = Jsoup.connect(url).get();
            HtmlToPlainText formatter = new HtmlToPlainText();
            String plainText = formatter.getPlainText(doc);
            //           System.out.println(plainText);
            downloadedBytes = downloadedBytes + plainText.length();
            if (downloadedBytes > downloadedBytesLimit) {
                stop = true;
            }
            urlWords.clear();
            extractFromString(plainText); // puts new words in newwords
        } catch (IOException ioe) {
            writeMsg("extractFromURL: IOException");
        };

        downloadedWords = downloadedWords + urlWords.size();
        for (String word : urlWords) {
            if (showWords) {
                writeMsg("+ " + word);
            }
            f.writeln(word);
        }

        float printvalue;
        String units;
        if (downloadedBytes < 1000000) {
            printvalue = (float) downloadedBytes / 1000;
            units = "KB";
        } else {
            printvalue = (float) downloadedBytes / 1000000;
            units = "MB";
        }

        writeMsg(String.format("%10d %5d %7.1f %2s done=%d todo=%d", downloadedWords, urlWords.size(), printvalue, units, doneUrls.size(), todoUrls.size()));

    }

    public void extractFromString(String str) {

        try {
            // put string in a Document so that DocUtils can be used
            javax.swing.text.Document doc = new PlainDocument();
            doc.insertString(0, str, null);

            int position, length;

            int startWordPosition = 0;
            int endWordPosition = 0;
            String wordorig, wordlc;

            position = 0;
            length = doc.getLength();
            while (position < length) {

                startWordPosition = WordUtils.nextAlphabetic(doc, position);
                endWordPosition = WordUtils.nextNonAlphabetic(doc, startWordPosition);
                wordorig = doc.getText(startWordPosition, endWordPosition - startWordPosition);

//                if (wordorig.matches("^[a-zA-ZşŞçÇğıİöÖüÜ]*$")) {
                if (wordorig.matches("^[a-z" + language.letters() + "]*$")) { // no capitals to avoid proper names, misspelled headers, ...
                    //                   wordlc = wordorig.replaceAll("I", "ı").toLowerCase();
                    wordlc = language.toLowerCase(wordorig);
                    if (wordlc.length() > 2) {
                        urlWords.add(wordlc);
                    }
                }

                position = endWordPosition;
            }
        } catch (BadLocationException blc) {
            MsgTextPane.write("Bad Location in extractFromString ");
            blc.printStackTrace();
            System.exit(1);
        }
    }

    int urls = 0;
    int variants = 0;
    int words = 0;

    class processFromFile implements Runnable {

        public void run() {

            stop = false;

            Integer freq = 0;
            String word, keyword, dictword;

            f.openInputFile();// opens inputStream

            while (!stop && (word = f.readLine()) != null) {

                if (word.charAt(0) == '<') {
                    word = word.substring(1, word.length() - 1);
                    urls++;
                } else {
                    // sanitize if from unknown source
                    // words containing capital letters or punctuation are ignored
                    if (!word.replaceAll("[^a-z" + language.letters() + "]", " ").contains(" ")) {

                        words++;

                        if (words % 10000 == 1) {
                            writeMsg(String.format("%5s %8s %8s %8s %10s %9s",
                                    "urls",
                                    "words",
                                    "correct",
                                    "accuracy",
                                    "newWords",
                                    "newKeys"));
                        }
                        if (words % 1000 == 0) {
                            writeMsg(String.format("%5d %8d %8d %8.2f %10d %9d",
                                    urls,
                                    words,
                                    words - variants,
                                    (float) (words - variants) / (float) words,
                                    dictCandidatesFrequency.size(),
                                    xDictWords.size())
                            );
                        }

                        // calculate accuracy against current dictionary
                        language.dictionary().setMatchInfo(false);
                        keyword = language.removeDiacritics(word);
                        dictword = language.dictionary().correctWord(keyword);
                        if (!word.equals(dictword)) {
                            variants++;
                        }
                        language.dictionary().setMatchInfo(true);

                        freq = dictCandidatesFrequency.get(word);
                        if (freq == null) {
                            freq = 0;
                        }
                        dictCandidatesFrequency.put(word, freq + 1);
                        String key = language.removeDiacritics(word);
                        xDictWords.put(key, word);
                        xDictFrequency.put(key, 0);
                    }
                }
            }

            f.closeInputFile();

            //  xDictWords and xDictFrequency contain the keys of all spelling variants, with frequency 0
            for (String w : dictCandidatesFrequency.keySet()) {
                String key = language.removeDiacritics(w);
                if (dictCandidatesFrequency.get(w) > xDictFrequency.get(key)) {
                    xDictWords.put(key, w);
                    xDictFrequency.put(key, dictCandidatesFrequency.get(w));
                }
            }

            // Now xDictWords contains the spelling variants with the highest frequency
            writeMsg("Resetting Dictionary");
            language.dictionary().reset();
            writeMsg("Adding " + xDictWords.size() + " dictionary entries");
            /*
             for (String key : xDictWords.keySet()) {
             language.dictionary().words.put(key, xDictWords.get(key));
             }
             */
            language.dictionary().addWordsBulk(xDictWords.values());

            writeMsg("Done");

        }
    }

    public void actionPerformed(ActionEvent ae) {

        String action = ae.getActionCommand();
//        writeMsg(action);

        if (action.equals("scan")) {

            downloadedBytes = 0;
            downloadedBytesLimit = Integer.MAX_VALUE;

            Thread extractThread = new Thread(new ExtractFromURLList());
            extractThread.start();
        }

        if (action.equals("stop")) {
            stop = true;
        }

        if (action.equals("process")) {
            showWords = false;
            showLinks = false;
            Thread processThread = new Thread(new processFromFile());
            processThread.start();

        }

        if (action.equals("download limit")) {

            String maxBytes = (String) JOptionPane.showInputDialog(
                    null,
                    "Download limit (MB)",
                    "stop scan when download limit is reached",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "1000");

            if (maxBytes != null) {
                if (maxBytes.length() > 0) {
                    downloadedBytesLimit = Integer.parseInt(maxBytes) * 1000000;
                }
            }
            writeMsg("download limit = " + downloadedBytesLimit);

        }

        if (action.equals("show links")) {
            showLinks = !showLinks;
            if (showLinks) {
                buttonLinks.setBackground(Color.green);
            } else {
                buttonLinks.setBackground(Color.red);
            }
        }

        if (action.equals("show words")) {
            showWords = !showWords;
            if (showWords) {
                buttonWords.setBackground(Color.green);
            } else {
                buttonWords.setBackground(Color.red);
            }
        }

    }

    private GridBagConstraints newGridBagConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.ipadx = 0;
        c.ipady = 0;
//                c.insets=null;
        return c;
    }

    private void displayGUI(boolean on) {

        scrollingEditArea.setMinimumSize(new Dimension(800, 100));
        scrollingEditArea.setMaximumSize(new Dimension(800, 100));
        scrollingEditArea.setPreferredSize(new Dimension(800, 100));

        scrollingMsgArea.setMinimumSize(new Dimension(800, 500));
        scrollingMsgArea.setMaximumSize(new Dimension(800, 500));
        scrollingMsgArea.setPreferredSize(new Dimension(800, 500));

        GridBagConstraints c;

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 5, 0, 5);  // top left bottom right
        content.add(buttonScan, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 5);  // top left bottom right
        content.add(buttonLinks, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;
        c.gridx = 2;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 5);  // top left bottom right
        content.add(buttonWords, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;
        c.gridx = 3;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 5);  // top left bottom right
        content.add(buttonLimit, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;
        c.gridx = 4;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 5);  // top left bottom right
        content.add(buttonStopScan, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = 5;
        c.gridy = 0;
        c.insets = new Insets(0, 100, 0, 5);  // top left bottom right
        content.add(buttonProcess, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;
        c.gridx = 6;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 5);  // top left bottom right
        content.add(buttonStopProcess, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.1;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 7;            // align with number of  buttons
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        content.add(scrollingEditArea, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.9;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 7;            // align with number of  buttons
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        content.add(scrollingMsgArea, c);

        pack();

    }

    public void execute() {

        editArea = new JTextPane();
        editArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // 2 pixels around text in JTextPane    
        editArea.setFont(new Font("monospaced", Font.PLAIN, 14));
        scrollingEditArea = new JScrollPane(editArea);

        msgArea = new JTextPane();
        msgArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // 2 pixels around text in JTextPane    
        msgArea.setFont(new Font("monospaced", Font.PLAIN, 14));
        scrollingMsgArea = new JScrollPane(msgArea);

        content.setLayout(new GridBagLayout());

        buttonScan.addActionListener(this);
        buttonScan.setActionCommand("scan");

        buttonScan.setBackground(Color.yellow);

        buttonStopScan.addActionListener(this);
        buttonStopScan.setActionCommand("stop");
        buttonProcess.addActionListener(this);
        buttonProcess.setActionCommand("process");
        buttonStopProcess.addActionListener(this);
        buttonStopProcess.setActionCommand("stop");
        buttonLinks.addActionListener(this);
        buttonLinks.setActionCommand("show links");
        buttonWords.addActionListener(this);
        buttonWords.setActionCommand("show words");
        buttonLimit.addActionListener(this);
        buttonLimit.setActionCommand("download limit");

        displayGUI(false);

        f = new FileOpener();

        setContentPane(content);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Enter URL's (one per line)");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        dictCandidatesFrequency.clear();
        xDictWords.clear();
        xDictFrequency.clear();

    }
}
