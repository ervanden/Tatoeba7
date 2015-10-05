package tatoeba;

import utils.GenericTextFrame;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;

import langeditor.LanguageTextPane;
import langeditor.LanguageEditor;
import utils.AreaFont;

//github test 1/10/2015
// If the source or target language is one single language, a language-specific text pane is used.
// In all other cases it is a normal JTextPane.
// The class 'LanguageTextPane remembers which kind of text pane is active and has wrapper methods so that
// the method of the language-specific text pane can be called.
class LanguageTextPanes {

    static LanguageTextPane source = null;
    static LanguageTextPane target = null;

    private static void setParameters(JTextPane area) {
        area.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // 2 pixels around text in JTextPane    
        area.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));
    }

    public static JTextPane getSourceTextPane(String language) {
        if (!language.equals("")) {
            source = new LanguageTextPane(language);
            setParameters(source);
            return (JTextPane) source;
        } else {
            JTextPane area = new JTextPane();
            setParameters(area);
            return area;
        }
    }

    public static JTextPane getTargetTextPane(String language) {
        if (!language.equals("")) {
            target = new LanguageTextPane(language);
            setParameters(target);
            return (JTextPane) target;
        } else {
            JTextPane area = new JTextPane();
            setParameters(area);
            return area;
        }
    }

    public static void setAutoCorrect(boolean b) {
        if (source != null) {
            source.setAutoCorrect(b);
        }
        if (target != null) {
            target.setAutoCorrect(b);
        }
    }
}

class TatoebaFrame extends JFrame implements ActionListener {

    private JFrame thisFrame = (JFrame) this;

    public JTextPane sourceArea;
    public JTextPane targetArea;
    public JTextPane infoArea;

    JScrollPane scrollingSourceArea;
    JScrollPane scrollingTargetArea;
    JScrollPane scrollingInfoArea;

    JSplitPane sentencesSplitPane;   // contains source and target panes
    JSplitPane topSplitPane;        // contains sentencesSplitPane and info are
    JPanel content = new JPanel();

    JButton buttonPlus = new JButton("+");
    JButton buttonMinus = new JButton("-");
    JButton buttonNext = new JButton("Next");
    JButton buttonPrevious = new JButton("Previous");
    JButton buttonTranslate = new JButton("Translate");
    JButton buttonCreate = new JButton("Create");
    JButton buttonEdit = new JButton("Edit");
    JButton buttonCommit = new JButton("Save edits");
    JButton buttonCancel = new JButton("Cancel");

    JLabel spacer1 = new JLabel(" ");
    JLabel spacer2 = new JLabel(" ");

    ClusterStack clusterFifo = new ClusterStack();

    boolean sourceDisplayed = false;
    boolean targetDisplayed = false;

    GenericTextFrame unsavedClustersFrame = null;
    GenericTextFrame ngramFrame = null;

    boolean editing = false;
    Cluster editingCluster = null;   // cluster that is currently being edited

    class WindowUtils extends WindowAdapter {

        public void windowClosing(WindowEvent e) {
            if (Graph.unsavedClusters() > 0) {
                JOptionPane.showMessageDialog(thisFrame, "There are unsaved clusters! Close window via Exit menu");
            } else {
                System.exit(0);
            }
        }
    }

    private void erasePane(JTextPane pane) {
        Document doc = pane.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException ble) {
            System.out.println("ble");
        }

    }

    public void writePane(JTextPane pane, String msg) {
        Document doc = pane.getDocument();
        try {
            doc.insertString(doc.getLength(), msg + "\n", null);
        } catch (BadLocationException blex) {
        }
        pane.setCaretPosition(doc.getLength());
    }

    public void writeInfo(String msg) {
        writePane(infoArea, msg);
    }

    public void enableStandard() {

        enableMenuItem("Save clusters and exit", false);
        enableMenuItem("Exit without saving clusters", false);
        enableMenuItem("Read Tatoeba Database", true);
        enableMenuItem("Read clusters", true);
        enableMenuItem("Cluster Overview", true);
        enableMenuItem("Select clusters", true);
        enableMenuItem("Save all clusters", true);
        enableMenuItem("Save selected clusters", true);
        enableMenuItem("Save special clusters", true);
        buttonNext.setEnabled(true);
        buttonTranslate.setEnabled(false);
        buttonPrevious.setEnabled(false);
        buttonCreate.setEnabled(true);
        buttonEdit.setEnabled(false);
        buttonCommit.setEnabled(false);
        buttonCancel.setEnabled(false);
        sourceDisplayed = false;
        targetDisplayed = false;
        editing = false;
        editingCluster = null;

        erasePane(sourceArea);
        erasePane(targetArea);
    }

    // reading clusters is run as separate thread because otherwise all output to screen
    // appears only after the action is finished
    private class readClustersThread implements Runnable {

        String fileName;

        public readClustersThread(String f) {
            fileName = f;
        }

        public void run() {
            ClustersInOut.readClusters(fileName);
            Graph.LanguageMatrix.generate();
            SelectionFrame.populateAreas();
            SelectionFrame.setVisible(true);
            enableStandard();
        }
    }

    private class readSentencesThread implements Runnable {

        String dirName;

        public readSentencesThread(String f) {
            dirName = f;
        }

        public void run() {
            ClustersInOut.readSentences(dirName);
            ClustersInOut.readLanguages();
            Graph.LanguageMatrix.generate();
            SelectionFrame.populateAreas();
            SelectionFrame.setVisible(true);
            enableStandard();

            /*                       
             for(String lan : SelectionFrame.usedLanguages){
             System.out.println("used language code |"+lan+"|   |"+ LanguageNames.shortToLong(lan)+"|"); 
             }
             */
        }
    }

    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        executeAction(action);
    }

    private void executeAction(String action) {

        StyledDocument doc;
        JFileChooser fileChooser;
        int retval;

        // menu items
        if (action.equals("Exit without saving clusters")) {
            System.exit(0);
        }

        if (action.equals("Save clusters and exit")) {
            ClustersInOut.saveClusters("all");
            System.exit(0);
        }

        if (action.equals("Save all clusters")) {
            ClustersInOut.saveClusters("all");
        }

        if (action.equals("Save special clusters")) {
            ClustersInOut.saveClustersToDo("");
        }

        if (action.equals("Save selected clusters")) {
            ClustersInOut.saveClusters("selected");
        }

        if (action.equals("Read Tatoeba Database")) {
            String dirName;
            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            retval = fileChooser.showOpenDialog(Tatoeba.tatoebaFrame);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                dirName = f.getAbsolutePath();

                enableMenuItem("Read clusters", false); // prevent starting two reading threads
                enableMenuItem("Read Tatoeba Database", false);
                Thread readSentencesThread = new Thread(new readSentencesThread(dirName));
                readSentencesThread.start();

            }
        }

        if (action.equals("Read clusters")) {
            String fileName;
            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle("Select a cluster database file");
            retval = fileChooser.showOpenDialog(Tatoeba.tatoebaFrame);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                fileName = f.getAbsolutePath();

                enableMenuItem("Read clusters", false); // prevent starting two reading threads
                enableMenuItem("Read Tatoeba Database", false);
                Thread readClustersThread = new Thread(new readClustersThread(fileName));
                readClustersThread.start();
            }
        }

        if (action.equals("Cluster Overview")) {
            ClusterCountFrame cc = Tatoeba.clusterCountFrame;
            if (!cc.isInitialized()) {
                cc.execute();
            } else {
                cc.setVisible(true);
            }
        }

        if (action.equals("Select clusters")) {
            SelectionFrame.setVisible(true);
            clusterFifo.reset();
            spacer1.setText("");
            erasePane(sourceArea);
            erasePane(targetArea);
            erasePane(infoArea);

            sourceDisplayed = false;
            targetDisplayed = false;
            editing = false;
            editingCluster = null;
            buttonPrevious.setEnabled(false);
            buttonEdit.setEnabled(false);

        }

        if (action.equals("Display unsaved clusters")) {
            if (unsavedClustersFrame == null) {
                unsavedClustersFrame = new GenericTextFrame();
            }
            unsavedClustersFrame.setVisible(true);
            Graph.displayClusters(unsavedClustersFrame, "unsaved");
        }

        if (action.equals("Horizontal")) {
            displayGUI(true);
            enableMenuItem("Vertical", true);
            enableMenuItem("Horizontal", false);
        }

        if (action.equals("Vertical")) {
            displayGUI(false);
            enableMenuItem("Vertical", false);
            enableMenuItem("Horizontal", true);
        }

        if (action.equals("Turkish")) {
            LanguageEditor.initialize("tur");
            LanguageEditor.setVisible(true);
        }

        if (action.equals("Polish")) {
            LanguageEditor.initialize("pol");
            LanguageEditor.setVisible(true);
        }

        // buttons
        if (action.equals("buttonPlus")) {
            AreaFont.multiply((float) 1.2);
            SimpleAttributeSet sas = new SimpleAttributeSet();
            StyleConstants.setFontSize(sas, AreaFont.getSize());

            doc = sourceArea.getStyledDocument();
            doc.setCharacterAttributes(0, doc.getLength(), sas, false);
            doc = targetArea.getStyledDocument();
            doc.setCharacterAttributes(0, doc.getLength(), sas, false);
            doc = infoArea.getStyledDocument();
            doc.setCharacterAttributes(0, doc.getLength(), sas, false);
            sourceArea.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));
            targetArea.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));
            infoArea.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));

        }

        if (action.equals("buttonMinus")) {
            AreaFont.multiply((float) 0.8);
            SimpleAttributeSet sas = new SimpleAttributeSet();
            StyleConstants.setFontSize(sas, AreaFont.getSize());
            doc = sourceArea.getStyledDocument();
            doc.setCharacterAttributes(0, doc.getLength(), sas, false);
            doc = targetArea.getStyledDocument();
            doc.setCharacterAttributes(0, doc.getLength(), sas, false);
            doc = infoArea.getStyledDocument();
            doc.setCharacterAttributes(0, doc.getLength(), sas, false);
            sourceArea.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));
            targetArea.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));
            infoArea.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));

        }

        if (action.equals("buttonPrevious")) {

            erasePane(sourceArea);
            erasePane(targetArea);
            erasePane(infoArea);
            if (!clusterFifo.isEmpty()) {
                clusterFifo.pop();
                sourceDisplayed = false;
                targetDisplayed = false;
            }
            if (clusterFifo.isEmpty()) {
                writePane(infoArea, "");
                writePane(infoArea, "no previous cluster");
            }

        }

        if (action.equals("buttonNext") || action.equals("buttonPrevious")) {

            LanguageTextPanes.setAutoCorrect(false);

            Cluster activeCluster = null;

            if (WorkingSet.size() == 0) {
                WorkingSet.build();
            }

            if (action.equals("buttonNext")) {

                // if we are below the top of the stack, move one up and use that cluster
                // if we are at the top of the stack, take a new cluster and put it on the stack
                if (clusterFifo.rise()) {
                    activeCluster = clusterFifo.peekFirst();
                    spacer1.setText("-");
                } else {
                    activeCluster = WorkingSet.pickCluster();
                    clusterFifo.push(activeCluster);
                    spacer1.setText(WorkingSet.pickedClusterToString());
                }

            } else {
                activeCluster = clusterFifo.peekFirst();
                spacer1.setText("-");
            }

            if (activeCluster != null) {

                erasePane(sourceArea);
                erasePane(targetArea);
                erasePane(infoArea);

                writePane(sourceArea, "");
                for (Sentence s : activeCluster.sentences) {
                    if (SelectionFrame.sourceLanguages.contains(s.language)) {
                        writePane(sourceArea, " " + s.language + ">  " + s.sentence);
                    }
                }

                for (String tag : activeCluster.tags) {
                    writePane(infoArea, "tag : " + tag);
                }
                sourceDisplayed = true;
                targetDisplayed = false;
                buttonEdit.setEnabled(true);
                buttonTranslate.setEnabled(true);
            }
            buttonPrevious.setEnabled(clusterFifo.size() > 1);
        }

        if (action.equals("buttonTranslate")) {

            LanguageTextPanes.setAutoCorrect(false);

            Cluster activeCluster = null;

            if (WorkingSet.size() == 0) {
                WorkingSet.build();
            }

            if (sourceDisplayed) {

                activeCluster = clusterFifo.peekFirst();
                if (activeCluster != null) {

                    writePane(targetArea, "");
                    for (Sentence s : activeCluster.sentences) {
                        if (SelectionFrame.targetLanguages.contains(s.language)) {
                            writePane(targetArea, " " + s.language + ">  " + s.sentence);
                        }
                    }
                    sourceDisplayed = true;
                    targetDisplayed = true;
                    buttonEdit.setEnabled(true);
                }

            } else {
                System.out.println("nothing to translate");
            }
            buttonPrevious.setEnabled(clusterFifo.size() > 1);
        }

        if (action.equals("buttonCommit")) {

            LanguageTextPanes.setAutoCorrect(false); // because user stops editing

            if (!editing) {
                System.out.println("Commit while not editing! Ignored");
            } else {

                Cluster c = editingCluster;
                if (c == null) { // user created a new cluster
                    c = new Cluster();
                    c.nr = Graph.maximumClusterNumber() + 1;
                    Graph.clusters.put(c.nr, c);

                    // a newly created cluster is now on the screen. Adjust state accordingly
                    clusterFifo.push(c);
                    sourceDisplayed = true;
                    targetDisplayed = true;

                    System.out.println("==================Added cluster " + c.nr);
                }

                c.sentences.clear();
                c.readSentencesFromDocument(Tatoeba.tatoebaFrame.sourceArea.getStyledDocument());
                c.readSentencesFromDocument(Tatoeba.tatoebaFrame.targetArea.getStyledDocument());
                c.tags.clear();
                c.readTagsFromDocument(Tatoeba.tatoebaFrame.infoArea.getStyledDocument());
                c.unsaved = true;

                editing = false;
                buttonCommit.setEnabled(false);
                buttonCancel.setEnabled(false);
            }

            sourceArea.setBackground(Color.WHITE);
            targetArea.setBackground(Color.WHITE);
            infoArea.setBackground(Color.WHITE);
            sourceArea.setEditable(false);
            targetArea.setEditable(false);
            infoArea.setEditable(false);

            enableMenuItem("Save clusters and exit", true);
            enableMenuItem("Exit without saving clusters", true);
            enableMenuItem("Read Tatoeba Database", true);
            enableMenuItem("Read clusters", true);
            enableMenuItem("Cluster Overview", true);
            enableMenuItem("Select clusters", true);
            enableMenuItem("Display unsaved clusters", true);
            enableMenuItem("Save all clusters", true);
            enableMenuItem("Save selected clusters", true);

            buttonNext.setEnabled(true);
            buttonCreate.setEnabled(true);
            buttonPrevious.setEnabled(clusterFifo.size() > 1);
            buttonEdit.setEnabled(true);

            spacer2.setText(" " + Graph.unsavedClusters());

        }

        if (action.equals("buttonCancel")) {

            LanguageTextPanes.setAutoCorrect(false); // because user stops editing

            if (!editing) {
                System.out.println("Cancel while not editing! Ignored");
            } else {

                if (editingCluster == null) { // user created a new cluster
                    erasePane(sourceArea);
                    erasePane(targetArea);
                    erasePane(infoArea);
                } else {
                    // The user was editing an existing cluster: redisplay the original sentences
                    // The cluster is on the  stack so we can call ButtonNext and buttonTranslate
                    sourceDisplayed = false;
                    targetDisplayed = false;
                    // when sourceDisplayed and targetDisplayed are false, buttonNext will pick a next cluster
                    // or go one up in the stack. If we pop(), the latter will happen and we have our cluster redisplayed
                    clusterFifo.pop();
                    executeAction("buttonNext");
                    executeAction("buttonTranslate");
                }
            }

            editing = false;
            editingCluster = null;

            sourceArea.setBackground(Color.WHITE);
            targetArea.setBackground(Color.WHITE);
            infoArea.setBackground(Color.WHITE);
            sourceArea.setEditable(false);
            targetArea.setEditable(false);
            infoArea.setEditable(false);

            buttonNext.setEnabled(true);
            buttonTranslate.setEnabled(false);
            buttonPrevious.setEnabled(clusterFifo.size() > 1);
            buttonCreate.setEnabled(true);
            buttonEdit.setEnabled(true);
            buttonCommit.setEnabled(false);
            buttonCancel.setEnabled(false);

            //           spacer2.setText(" " + Graph.unsavedClusters());
        }

        if (action.equals("buttonCreate")) {

            editing = true;
            editingCluster = null;
            buttonCommit.setEnabled(true);
            buttonCancel.setEnabled(true);

            erasePane(sourceArea);
            erasePane(targetArea);
            erasePane(infoArea);

            LanguageTextPanes.setAutoCorrect(false);
            writePane(sourceArea, "");
            for (String s : SelectionFrame.sourceLanguages) {
                writePane(sourceArea, " " + s + ">  ");
            }
            writePane(targetArea, "");
            for (String s : SelectionFrame.targetLanguages) {
                writePane(targetArea, " " + s + ">  ");
            }
            buttonCreate.setEnabled(false);
            buttonNext.setEnabled(false);
            buttonTranslate.setEnabled(false);
            buttonPrevious.setEnabled(false);
            buttonEdit.setEnabled(false);
            LanguageTextPanes.setAutoCorrect(true);
            sourceDisplayed = false;
            targetDisplayed = false;
            sourceArea.setEditable(true);
            targetArea.setEditable(true);
            infoArea.setEditable(true);
            sourceArea.setBackground(Color.LIGHT_GRAY);
            targetArea.setBackground(Color.LIGHT_GRAY);
            infoArea.setBackground(Color.LIGHT_GRAY);

        }

        if (action.equals("buttonEdit")) {

            if (sourceDisplayed) {  // if not, there is no cluster to edit
                editing = true;
                editingCluster = clusterFifo.peekFirst();

                System.out.println("Editing c" + editingCluster.nr);
                buttonCommit.setEnabled(true);
                buttonCancel.setEnabled(true);

                if (!targetDisplayed) {
                    executeAction("buttonTranslate"); // make sure all sentences are on the screens
                }
                buttonEdit.setEnabled(false);
                buttonTranslate.setEnabled(false);
                buttonNext.setEnabled(false);
                buttonPrevious.setEnabled(false);
                buttonCreate.setEnabled(false);
                LanguageTextPanes.setAutoCorrect(true);
                sourceArea.setEditable(true);
                targetArea.setEditable(true);
                infoArea.setEditable(true);
                sourceArea.setBackground(Color.LIGHT_GRAY);
                targetArea.setBackground(Color.LIGHT_GRAY);
                infoArea.setBackground(Color.LIGHT_GRAY);
            }
        }

    }

    HashMap<String, JMenuItem> menuItems = new HashMap<>();

    private void AddMenuItem(JMenu menu, String name) {
        JMenuItem menuItem;
        menuItem = new JMenuItem(name);
        menuItem.addActionListener(this);
        menuItem.setActionCommand(name);
        menu.add(menuItem);
        menuItems.put(name, menuItem);
    }

    public void enableMenuItem(String actionName, boolean enabled) {
        JMenuItem menuItem;
        menuItem = menuItems.get(actionName);
        menuItem.setEnabled(enabled);
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

    private void displayGUI(boolean horizontal) {

        Dimension minimumDimension = new Dimension(200, 50);
        Dimension preferredDimension = new Dimension(780, 200);

        sourceArea.setMinimumSize(minimumDimension);
        sourceArea.setPreferredSize(preferredDimension);

        targetArea.setMinimumSize(minimumDimension);
        targetArea.setPreferredSize(preferredDimension);

        infoArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // 2 pixels around text   
        infoArea.setFont(new Font("monospaced", Font.PLAIN, AreaFont.getSize()));

        scrollingSourceArea.setMinimumSize(minimumDimension);
        scrollingSourceArea.setPreferredSize(preferredDimension);

        scrollingTargetArea.setMinimumSize(minimumDimension);
        scrollingTargetArea.setPreferredSize(preferredDimension);

        scrollingInfoArea.setMinimumSize(new Dimension(780, 100));
        scrollingInfoArea.setMaximumSize(new Dimension(780, 100));
        scrollingInfoArea.setPreferredSize(new Dimension(780, 100));

        int topFields = 0;
        int bottomFields;

        GridBagConstraints c;

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = topFields++;
        c.gridy = 0;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(buttonNext, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = topFields++;
        c.gridy = 0;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(buttonTranslate, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = topFields++;
        c.gridy = 0;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(buttonPrevious, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = topFields++;
        c.gridy = 0;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(spacer1, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = topFields++;
        c.gridy = 0;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(buttonCreate, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = topFields++;
        c.gridy = 0;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(buttonEdit, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = topFields++;
        c.gridy = 0;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(buttonCommit, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = topFields++;
        c.gridy = 0;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(buttonCancel, c);

        bottomFields = topFields;

        // RIGHT : TARGET
        c = newGridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = bottomFields++;
        c.gridy = 0;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(spacer2, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = bottomFields++;
        c.gridy = 0;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(buttonPlus, c);

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = bottomFields++;
        c.gridy = 0;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(buttonMinus, c);

        if (horizontal) {
            sentencesSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        } else {
            sentencesSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        }

        c = newGridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.5;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = bottomFields;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        content.add(topSplitPane, c);

        JMenuBar menuBar;
        JMenu menuExit;
        JMenu menuClusters;

        menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        menuExit = new JMenu("Exit");
        menuBar.add(menuExit);
        AddMenuItem(menuExit, "Save clusters and exit");
        AddMenuItem(menuExit, "Exit without saving clusters");

        menuClusters = new JMenu("Clusters");
        menuBar.add(menuClusters);
        AddMenuItem(menuClusters, "Read Tatoeba Database");
        AddMenuItem(menuClusters, "Read clusters");
        AddMenuItem(menuClusters, "Cluster Overview");
        AddMenuItem(menuClusters, "Select clusters");
        AddMenuItem(menuClusters, "Display unsaved clusters");
        AddMenuItem(menuClusters, "Save all clusters");
        AddMenuItem(menuClusters, "Save selected clusters");
        AddMenuItem(menuClusters, "Save special clusters");

        menuClusters = new JMenu("View");
        menuBar.add(menuClusters);
        AddMenuItem(menuClusters, "Horizontal");
        AddMenuItem(menuClusters, "Vertical");

        menuClusters = new JMenu("Editors");
        menuBar.add(menuClusters);
        AddMenuItem(menuClusters, "Turkish");
        AddMenuItem(menuClusters, "Polish");

        pack();
    }

    public void changeSourceArea(String sourceLanguage) {
        sourceArea = LanguageTextPanes.getSourceTextPane(sourceLanguage);
        scrollingSourceArea.getViewport().setView(sourceArea);
    }

    public void changeTargetArea(String targetLanguage) {
        targetArea = LanguageTextPanes.getTargetTextPane(targetLanguage);
        scrollingTargetArea.getViewport().setView(targetArea);
    }

    public TatoebaFrame() {

        sourceArea = LanguageTextPanes.getSourceTextPane("");
        targetArea = LanguageTextPanes.getTargetTextPane("");

        infoArea = new JTextPane();

        scrollingSourceArea = new JScrollPane(sourceArea);
        scrollingTargetArea = new JScrollPane(targetArea);
        scrollingInfoArea = new JScrollPane(infoArea);

        sentencesSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollingSourceArea, scrollingTargetArea);
        topSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sentencesSplitPane, scrollingInfoArea);

        sentencesSplitPane.setDividerSize(sentencesSplitPane.getDividerSize() / 4);
        topSplitPane.setDividerSize(topSplitPane.getDividerSize() / 4);
        sentencesSplitPane.setResizeWeight(0.5);

        sourceArea.setEditable(false);
        targetArea.setEditable(false);
        infoArea.setEditable(false);

        content.setLayout(new GridBagLayout());

        buttonPlus.addActionListener(this);
        buttonMinus.addActionListener(this);
        buttonNext.addActionListener(this);
        buttonTranslate.addActionListener(this);
        buttonPrevious.addActionListener(this);
        buttonCommit.addActionListener(this);
        buttonCancel.addActionListener(this);
        buttonEdit.addActionListener(this);
        buttonCreate.addActionListener(this);

        buttonPlus.setActionCommand("buttonPlus");
        buttonMinus.setActionCommand("buttonMinus");
        buttonNext.setActionCommand("buttonNext");
        buttonTranslate.setActionCommand("buttonTranslate");
        buttonPrevious.setActionCommand("buttonPrevious");
        buttonCommit.setActionCommand("buttonCommit");
        buttonCancel.setActionCommand("buttonCancel");
        buttonEdit.setActionCommand("buttonEdit");
        buttonCreate.setActionCommand("buttonCreate");

        displayGUI(false);
        enableMenuItem("Vertical", false);
        enableMenuItem("Horizontal", true);

        enableMenuItem("Save clusters and exit", false);
        enableMenuItem("Exit without saving clusters", false);
        enableMenuItem("Read Tatoeba Database", true);
        enableMenuItem("Read clusters", true);
        enableMenuItem("Cluster Overview", false);
        enableMenuItem("Select clusters", false);
        enableMenuItem("Display unsaved clusters", false);
        enableMenuItem("Save all clusters", false);
        enableMenuItem("Save selected clusters", false);
        enableMenuItem("Save special clusters", false);

        buttonNext.setEnabled(false);
        buttonTranslate.setEnabled(false);
        buttonPrevious.setEnabled(false);
        buttonCreate.setEnabled(false);
        buttonEdit.setEnabled(false);
        buttonCommit.setEnabled(false);
        buttonCancel.setEnabled(false);

        setContentPane(content);
        setTitle("Tatoeba language trainer");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowUtils());

    }
}

public class Tatoeba {

    static TatoebaFrame tatoebaFrame;
    static ClusterCountFrame clusterCountFrame = new ClusterCountFrame();
    static String currentSourceLanguage = "";
    static String currentTargetLanguage = "";

    public static void changeTatoebaFrame(String sourceLanguage, String targetLanguage) {
        // replace the language areas by a language-sensitive editor if the language is unique
        if (!sourceLanguage.equals(currentSourceLanguage)) {
            currentSourceLanguage = sourceLanguage;
            tatoebaFrame.changeSourceArea(sourceLanguage);
        }
        if (!targetLanguage.equals(currentTargetLanguage)) {
            currentTargetLanguage = targetLanguage;
            tatoebaFrame.changeTargetArea(targetLanguage);
        }
    }

    public static void main(String[] args) {

        tatoebaFrame = new TatoebaFrame();
        SelectionFrame.execute();

//        testStack = new TestStack();
    }
}
