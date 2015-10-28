package tatoeba;

import languages.LanguageNames;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashSet;
import javax.swing.JFileChooser;
import utils.MsgTextPane;

class ClustersInOut {

    static String clustersFileName = "?";
//    static String dictionaryPattern = "";

    public static void readSentences(String dirName) {

        BufferedReader inputStream = null;
        String fileName;
        int count = 0;
        int validLinks = 0;

        fileName = dirName + "/sentences.csv";

        try {

            File initialFile = new File(fileName);
            InputStream is = new FileInputStream(initialFile);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            inputStream = new BufferedReader(isr);

            MsgTextPane.write("reading sentences...");
            String l;
            count = 0;
            while ((count < Integer.MAX_VALUE) && ((l = inputStream.readLine()) != null)) {
                String[] ls = l.split("\u0009");
                Sentence s = new Sentence();
                s.nr = Integer.parseInt(ls[0]);
                s.sentence = ls[2];
                s.language = ls[1];

                if (s.language.matches("[a-z]+")) {

                    Graph.addSentence(s);

                    // when reading the tatoeba files, there are no source, target or language
                    // keywords like in a cluster database, so 'allLanguages' is populated here
                    SelectionFrame.usedLanguages.add(s.language);
                    count++;
                }
            }
        } catch (FileNotFoundException fnf) {
            MsgTextPane.write("file not found : " + fileName);
        } catch (IOException io) {
            MsgTextPane.write("io exception : " + fileName);
        }

        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException io) {
        }

        MsgTextPane.write(count + " sentences read from " + fileName);
        MsgTextPane.write("reading links...");

        fileName = dirName + "/links.csv";
        count = 0;
        try {

            File initialFile = new File(fileName);
            InputStream is = new FileInputStream(initialFile);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            inputStream = new BufferedReader(isr);

            String l;
            while ((l = inputStream.readLine()) != null) {
                String[] ls = l.split("\u0009");
                int nr1 = Integer.parseInt(ls[0]);
                int nr2 = Integer.parseInt(ls[1]);
                if (Graph.addLink(nr1, nr2)) {
                    validLinks++;
                }
                count++;
            }
        } catch (FileNotFoundException fnf) {
            MsgTextPane.write("file not found : " + fileName);
        } catch (IOException io) {
            MsgTextPane.write("io exception : " + fileName);
        }

        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException io) {
        }

        MsgTextPane.write(count + " links read from " + fileName);
    }

    public static boolean readClusters(String fileName) {

        BufferedReader inputStream = null;
        int clusterCount = 0;
        int languageCount = 0;
        Cluster c = null;
        Sentence s = null;

        try {

            File initialFile = new File(fileName);
            InputStream is = new FileInputStream(initialFile);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            inputStream = new BufferedReader(isr);

            clusterCount = Graph.clusters.size();

            clustersFileName = fileName;
            MsgTextPane.write("reading clusters from " + fileName + "...");

            String l;
            String[] ls;
            int lineCount = 0;
            while ((l = inputStream.readLine()) != null) {
                lineCount++;
                ls = l.split("\u0009");
                if (l.matches("^.*cluster.*$")) {
                    clusterCount++;
                    c = new Cluster();
                    c.nr = clusterCount;
                    Graph.clusters.put(c.nr, c);
                    // the remaining strings are cluster tags
                    String tag;
                    for (int i = 1; i <= ls.length - 1; i++) {
                        tag = ls[i];
                        tag = tag.replaceAll(" *", "");
                        c.tags.add(tag);
                        SelectionFrame.allTags.add(tag);
                    }

                } else {
                    int lslength = 0;
                    for (String z : ls) {
                        lslength++;
                    }

                    if ((lslength == 2) && (ls[0].length() == 3) && (ls[1].length() > 3)) {
                        s = new Sentence();
                        s.language = ls[0];
                        s.sentence = ls[1];
                        s.sentence = s.sentence.replaceAll("^ *", "");
                        s.sentence = s.sentence.replaceAll(" *$", "");
                        c.sentences.add(s);

                        SelectionFrame.usedLanguages.add(s.language);

                    } else {
                        System.out.println("invalid line " + lineCount + " |" + l + "|");
                    }
                }
            }
        } catch (FileNotFoundException fnf) {
            MsgTextPane.write("file not found : " + fileName);
        } catch (IOException io) {
            MsgTextPane.write("io exception : " + fileName);
        }

        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException io) {
        }

        MsgTextPane.write(clusterCount + " clusters read from " + fileName);
        MsgTextPane.write(languageCount + " languages read from " + fileName);

        return true;
    }

    public static void saveClusters(String mode) {

        Date now = new Date();
        String fileName = clustersFileName;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File(fileName));
        int retval = fileChooser.showSaveDialog(null);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            fileName = f.getAbsolutePath();

            try {
                File initialFile = new File(fileName);
                OutputStream is = new FileOutputStream(initialFile);
                OutputStreamWriter isr = new OutputStreamWriter(is, "UTF-8");
                BufferedWriter outputStream = new BufferedWriter(isr);

                HashSet<String> usedLanguages;
                if (mode.equals("all")) {
                    usedLanguages = new HashSet<String>(SelectionFrame.usedLanguages);
                } else {
                    usedLanguages = new HashSet<String>(SelectionFrame.sourceLanguages);
                    usedLanguages.addAll(SelectionFrame.targetLanguages);
                }

                for (Cluster c : Graph.clusters.values()) {
                    if (mode.equals("all") || c.selected) {
                        outputStream.write("cluster");
                        for (String tag : c.tags) {
                            outputStream.write("\u0009");
                            outputStream.write(tag);
                        }
                        outputStream.newLine();
                        for (Sentence s : c.sentences) {
                            if (usedLanguages.contains(s.language)) {
                                outputStream.write(s.language);
                                outputStream.write("\u0009");
                                outputStream.write(s.sentence);
                                outputStream.newLine();
                            }
                        }
                        c.unsaved = false;
                    }
                }

                for (String l : usedLanguages) {
                    outputStream.write("language");
                    outputStream.write("\u0009");
                    outputStream.write(l);
                    outputStream.write("\u0009");
                    outputStream.write(LanguageNames.shortToLong(l));
                    outputStream.newLine();
                }

                for (String l : SelectionFrame.sourceLanguages) {
                    outputStream.write("source");
                    outputStream.write("\u0009");
                    outputStream.write(l);
                    outputStream.newLine();
                }

                for (String l : SelectionFrame.targetLanguages) {
                    outputStream.write("target");
                    outputStream.write("\u0009");
                    outputStream.write(l);
                    outputStream.newLine();
                }

                outputStream.close();

            } catch (IOException io) {
                MsgTextPane.write(" io exception during save clusters");
            }
        }
    }

    public static void saveClustersToDo(String mode) {

        String fileName = clustersFileName;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File(fileName));
        int retval = fileChooser.showSaveDialog(null);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            fileName = f.getAbsolutePath();

            try {
                File initialFile = new File(fileName);
                OutputStream is = new FileOutputStream(initialFile);
                OutputStreamWriter isr = new OutputStreamWriter(is, "UTF-8");
                BufferedWriter outputStream = new BufferedWriter(isr);

                for (Cluster c : Graph.clusters.values()) {
                    boolean english = false;
                    boolean turkish = false;
                    for (Sentence s : c.sentences) {
                        if (s.language.equals("tur")) {
                            turkish = true;
                        };
                        if (s.language.equals("eng")) {
                            english = true;
                        };

                    }
                    if (english && !turkish) {
                        for (Sentence s : c.sentences) {
                            if (s.language.equals("eng")) {
                                outputStream.write(s.nr + "");
                                outputStream.write("\u0009");
                                outputStream.write(s.sentence);
                                outputStream.newLine();
                            };

                        }
                    }

                }

                outputStream.close();

            } catch (IOException io) {
                MsgTextPane.write(" io exception during save clusters");
            }
        }
    }

}
