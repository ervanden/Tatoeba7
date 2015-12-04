package tatoeba;

import languages.LanguageNames;
import utils.GenericTextFrame;
import java.awt.*;
import java.awt.event.*;
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
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import javax.imageio.ImageIO;

import langeditor.LanguageTextPane;
import langeditor.LanguageEditorFrame;

import utils.*;

// If the source or target language is one single language, a language-specific text pane is used.
public class TatoebaFrame extends JFrame implements ActionListener {

    public SelectionFrame selectionFrame;
    public WorkingSet workingSet;
    public Graph graph;
    //   public TagsFrame tagsFrame;

    public LanguageTextPane sourceArea;
    public LanguageTextPane targetArea;
    public JTextPane commentArea;

    JScrollPane scrollingSourceArea;
    JScrollPane scrollingTargetArea;
    JScrollPane scrollingCommentArea;
    boolean commentAreaMinimized = false;

    JPanel tagsPanel = null;

    JPanel content = new JPanel();

    JButton buttonPlus = new JButton("+");
    JButton buttonMinus = new JButton("-");
    JButton buttonNext = new JButton("Next");
    JButton buttonPrevious = new JButton("Previous");
    JButton buttonTags = new JButton("Tags");
    JButton buttonTranslate = new JButton("Translate");
    JButton buttonCreate = new JButton("Create");
    JButton buttonEdit = new JButton("Edit");
    JButton buttonCommit = new JButton("Save edits");
    JButton buttonCancel = new JButton("Cancel");

    Color neutralButtonColor = buttonCancel.getBackground();

    JButton buttonFavourite;
    JButton buttonNotFavourite;

    // components in tags panel
    HashMap<String, JButton> tagButtons = new HashMap<>();
    JButton buttonAddTag = new JButton("+tag");
    JTextField newTagField = new JTextField();

    // text between the top panel buttons
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
            if (graph.unsavedClusters() > 0) {
                JOptionPane.showMessageDialog(null, "There are unsaved clusters! Close window via Exit menu");
            } else {
                setVisible(false);
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

    public void setAutoCorrect(boolean b) {
        if (sourceArea != null) {
            sourceArea.setAutoCorrect(b);
        }
        if (targetArea != null) {
            targetArea.setAutoCorrect(b);
        }
    }

    public void newSourceArea(String language) {
        sourceArea = createTextPane(language);
        scrollingSourceArea.getViewport().setView(sourceArea);
    }

    public void newTargetArea(String language) {
        targetArea = createTextPane(language);
        scrollingTargetArea.getViewport().setView(targetArea);
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
        buttonNext.setEnabled(true);
        buttonTranslate.setEnabled(false);
        buttonPrevious.setEnabled(false);
        buttonTags.setEnabled(false);
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
            readClusters(fileName);
            graph.generateLanguageMatrix();
            selectionFrame.populateAreas();
            selectionFrame.setVisible(true);
            enableStandard();
        }
    }

    private class readSentencesThread implements Runnable {

        String dirName;

        public readSentencesThread(String f) {
            dirName = f;
        }

        public void run() {
            readSentences(dirName);
            graph.generateLanguageMatrix();
            selectionFrame.populateAreas();
            selectionFrame.setVisible(true);
            enableStandard();
        }
    }

    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        executeAction(action);
    }

    private void executeAction(String action) {

        JFileChooser fileChooser;
        int retval;

        // actions from tags panel
        if (action.equals("buttonNotFavourite") || action.equals("buttonFavourite")) {
            Cluster cluster = clusterFifo.peekFirst();
            if (cluster == null) {
                MsgTextPane.write("no cluster on top of the stack");
            } else {
                if (cluster.tags.contains("favourite")) {
                    MsgTextPane.write("removing tag " + "favourite" + " from cluster " + cluster.nr);
                    cluster.tags.remove("favourite");
                } else {
                    MsgTextPane.write("adding new tag " + "favourite" + " to cluster " + cluster.nr);
                    cluster.tags.add("favourite");
                }
                cluster.unsaved = true;
                selectionFrame.allTags.add("favourite");
                updateTagsPanel(cluster);
            }
        }

        if (action.equals("buttonAddTag")) {
            String newTag = newTagField.getText();
            Cluster cluster = clusterFifo.peekFirst();
            if (cluster == null) {
                MsgTextPane.write("no cluster on top of the stack");
            } else {
                MsgTextPane.write("adding new tag " + newTag + " to cluster " + cluster.nr);
                cluster.tags.add(newTag);
                cluster.unsaved = true;
                selectionFrame.allTags.add(newTag);
                updateTagsPanel(cluster);
            }
        }

        if (action.substring(0, 4).equals("tag|")) {
            String tag = action.substring(4);
            Cluster cluster = clusterFifo.peekFirst();
            if (cluster == null) {
                MsgTextPane.write("no cluster on top of the stack");
            } else {
                JButton button = tagButtons.get(tag);  // button must exist since it was clicked
                if (!cluster.tags.contains(tag)) {
                    MsgTextPane.write("adding tag " + tag + " to cluster " + cluster.nr);
                    cluster.tags.add(tag);
                    cluster.unsaved = true;
                    button.setBackground(Color.GREEN);
                } else {
                    MsgTextPane.write("removing tag " + tag + " from cluster " + cluster.nr);
                    cluster.tags.remove(tag);
                    cluster.unsaved = true;
                    button.setBackground(neutralButtonColor);

                }
            }
        }

        // menu items
        if (action.equals("Exit without saving clusters")) {
            setVisible(false);
        }

        if (action.equals("Save clusters and exit")) {
            saveClusters("all");
            setVisible(false);
        }

        if (action.equals("Save all clusters")) {
            saveClusters("all");
        }

        if (action.equals("Save selected clusters")) {
            saveClusters("selected");
        }

        if (action.equals("Read Tatoeba Database")) {
            String dirName;
            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            retval = fileChooser.showOpenDialog(null);
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
            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle("Select a cluster database file");
            retval = fileChooser.showOpenDialog(null);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                clustersFileName = f.getAbsolutePath();

                enableMenuItem("Read clusters", false); // prevent starting two reading threads
                enableMenuItem("Read Tatoeba Database", false);
                Thread readClustersThread = new Thread(new readClustersThread(clustersFileName));
                readClustersThread.start();
            }
        }

        if (action.equals("Cluster Overview")) {
            new ClusterCountFrame(this);
        }

        if (action.equals("Select clusters")) {
            selectionFrame.setVisible(true);
            clusterFifo.reset();
            spacer1.setText("");
            erasePane(sourceArea);
            erasePane(targetArea);
            erasePane(commentArea);

            sourceDisplayed = false;
            targetDisplayed = false;
            editing = false;
            editingCluster = null;
            buttonPrevious.setEnabled(false);
            buttonEdit.setEnabled(false);
            buttonTags.setEnabled(false);

        }

        if (action.equals("Display unsaved clusters")) {
            if (unsavedClustersFrame == null) {
                unsavedClustersFrame = new GenericTextFrame();
            }
            unsavedClustersFrame.setVisible(true);
            graph.displayClusters(unsavedClustersFrame, "unsaved", selectionFrame);
        }

        if (action.equals("Horizontal")) {

        }

        if (action.equals("Vertical")) {

        }

        if (action.matches(".*[|].*")) {
            String[] ls = action.split("\\|");
            System.out.println(action);
            System.out.println(ls[0]);
            System.out.println(ls[1]);
            if (ls[1].equals("Editor")) {
                LanguageEditorFrame f = new LanguageEditorFrame(ls[0]);
                f.setVisible(true);
            }
            if (ls[1].equals("Numbers")) {
                NumberTrainer n = new NumberTrainer();
                n.setVisible(true);
            }
        }

        // buttons
        if (action.equals("buttonTags")) {

            System.out.println("buttonTags");
            if (commentAreaMinimized) {
                Dimension minimumDimension = new Dimension(780, 0);
                Dimension preferredDimension = new Dimension(780, 200);
                commentArea.setMinimumSize(minimumDimension);
                commentArea.setPreferredSize(preferredDimension);
                scrollingCommentArea.setMinimumSize(minimumDimension);
                scrollingCommentArea.setPreferredSize(preferredDimension);
            } else {
                Dimension minimumDimension = new Dimension(780, 0);
 //               Dimension preferredDimension = new Dimension(780, 10);
                commentArea.setMinimumSize(minimumDimension);
                commentArea.setPreferredSize(minimumDimension);
                scrollingCommentArea.setMinimumSize(minimumDimension);
                scrollingCommentArea.setPreferredSize(minimumDimension);
            }
            commentAreaMinimized = !commentAreaMinimized;

            commentArea.revalidate();
            scrollingCommentArea.revalidate();
            content.revalidate();
            pack();
            content.repaint();
        }

        if (action.equals("buttonPlus")) {
            AreaFont.multiply((float) 1.2);
            AreaFont.setFont(sourceArea);
            AreaFont.setFont(targetArea);
            AreaFont.setFont(commentArea);
        }

        if (action.equals("buttonMinus")) {
            AreaFont.multiply((float) 0.8);
            AreaFont.setFont(sourceArea);
            AreaFont.setFont(targetArea);
            AreaFont.setFont(commentArea);
        }

        if (action.equals("buttonPrevious")) {

            erasePane(sourceArea);
            erasePane(targetArea);
            erasePane(commentArea);
            if (!clusterFifo.isEmpty()) {
                clusterFifo.pop();
                sourceDisplayed = false;
                targetDisplayed = false;
            }
            if (clusterFifo.isEmpty()) {
                MsgTextPane.write("no previous cluster");
            }

        }

        if (action.equals("buttonNext") || action.equals("buttonPrevious")) {

            setAutoCorrect(false);

            Cluster activeCluster = null;

            if (workingSet.size() == 0) {
                workingSet.build();
            }

            if (action.equals("buttonNext")) {

                // if we are below the top of the stack, move one up and use that cluster
                // if we are at the top of the stack, take a new cluster and put it on the stack
                if (clusterFifo.rise()) {
                    activeCluster = clusterFifo.peekFirst();
                    spacer1.setText("-");
                } else {
                    activeCluster = workingSet.pickCluster();
                    clusterFifo.push(activeCluster);
                    spacer1.setText(workingSet.pickedClusterToString());
                }

            } else {
                activeCluster = clusterFifo.peekFirst();
                spacer1.setText("-");
            }

            if (activeCluster != null) {

                erasePane(sourceArea);
                erasePane(targetArea);
                erasePane(commentArea);

                updateTagsPanel(activeCluster);

                writePane(sourceArea, "");
                for (Sentence s : activeCluster.sentences) {
                    if (selectionFrame.sourceLanguages.contains(s.language)) {
                        writePane(sourceArea, " " + s.language + ">  " + s.sentence);
                    }
                }

                sourceDisplayed = true;
                targetDisplayed = false;
                buttonEdit.setEnabled(true);
                buttonTags.setEnabled(true);
                buttonTranslate.setEnabled(true);
            }
            buttonPrevious.setEnabled(clusterFifo.size() > 1);
        }

        if (action.equals("buttonTranslate")) {

            setAutoCorrect(false);

            Cluster activeCluster = null;

            if (workingSet.size() == 0) {
                workingSet.build();
            }

            if (sourceDisplayed) {

                activeCluster = clusterFifo.peekFirst();
                if (activeCluster != null) {

                    writePane(targetArea, "");
                    for (Sentence s : activeCluster.sentences) {
                        if (selectionFrame.targetLanguages.contains(s.language)) {
                            writePane(targetArea, " " + s.language + ">  " + s.sentence);
                        }
                    }
                    sourceDisplayed = true;
                    targetDisplayed = true;
                    buttonEdit.setEnabled(true);
                    buttonTags.setEnabled(true);
                }

            } else {
                System.out.println("nothing to translate");
            }
            buttonPrevious.setEnabled(clusterFifo.size() > 1);
        }

        if (action.equals("buttonCommit")) {

            setAutoCorrect(false); // because user stops editing

            if (!editing) {
                System.out.println("Commit while not editing! Ignored");
            } else {

                Cluster c = editingCluster;
                if (c == null) { // user created a new cluster
                    c = new Cluster();
                    c.nr = graph.maximumClusterNumber() + 1;
                    graph.clusters.put(c.nr, c);

                    // a newly created cluster is now on the screen. Adjust state accordingly
                    clusterFifo.push(c);
                    sourceDisplayed = true;
                    targetDisplayed = true;

                    System.out.println("==================Added cluster " + c.nr);
                }

                c.sentences.clear();
                c.readSentencesFromDocument(sourceArea.getStyledDocument(), selectionFrame);
                c.readSentencesFromDocument(targetArea.getStyledDocument(), selectionFrame);
                c.readSentencesFromDocument(commentArea.getStyledDocument(), selectionFrame);

                c.unsaved = true;

                editing = false;
                buttonCommit.setEnabled(false);
                buttonCancel.setEnabled(false);
            }

            sourceArea.setBackground(Color.WHITE);
            targetArea.setBackground(Color.WHITE);
            commentArea.setBackground(Color.WHITE);
            sourceArea.setEditable(false);
            targetArea.setEditable(false);
            commentArea.setEditable(false);

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
            buttonTags.setEnabled(true);
            buttonCreate.setEnabled(true);
            buttonPrevious.setEnabled(clusterFifo.size() > 1);
            buttonEdit.setEnabled(true);

            spacer2.setText(" " + graph.unsavedClusters());

        }

        if (action.equals("buttonCancel")) {

            setAutoCorrect(false); // because user stops editing

            if (!editing) {
                System.out.println("Cancel while not editing! Ignored");
            } else {

                if (editingCluster == null) { // user created a new cluster
                    erasePane(sourceArea);
                    erasePane(targetArea);
                    erasePane(commentArea);
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
            commentArea.setBackground(Color.WHITE);
            sourceArea.setEditable(false);
            targetArea.setEditable(false);
            commentArea.setEditable(false);

            buttonNext.setEnabled(true);
            buttonTranslate.setEnabled(false);
            buttonPrevious.setEnabled(clusterFifo.size() > 1);
            buttonTags.setEnabled(true);
            buttonCreate.setEnabled(true);
            buttonEdit.setEnabled(true);
            buttonCommit.setEnabled(false);
            buttonCancel.setEnabled(false);
        }

        if (action.equals("buttonCreate")) {

            editing = true;
            editingCluster = null;
            buttonCommit.setEnabled(true);
            buttonCancel.setEnabled(true);

            erasePane(sourceArea);
            erasePane(targetArea);
            erasePane(commentArea);
            sourceArea.getLanguage().dictionary().dictionaryWindowVisible(true);
            targetArea.getLanguage().dictionary().dictionaryWindowVisible(true);

            setAutoCorrect(false);
            writePane(sourceArea, "");
            for (String s : selectionFrame.sourceLanguages) {
                writePane(sourceArea, " " + s + ">  ");
            }
            writePane(targetArea, "");
            for (String s : selectionFrame.targetLanguages) {
                writePane(targetArea, " " + s + ">  ");
            }
            buttonCreate.setEnabled(false);
            buttonNext.setEnabled(false);
            buttonTranslate.setEnabled(false);
            buttonPrevious.setEnabled(false);
            buttonEdit.setEnabled(false);
            buttonTags.setEnabled(false);
            setAutoCorrect(true);
            sourceDisplayed = false;
            targetDisplayed = false;
            sourceArea.setEditable(true);
            targetArea.setEditable(true);
            commentArea.setEditable(true);
            sourceArea.setBackground(Color.LIGHT_GRAY);
            targetArea.setBackground(Color.LIGHT_GRAY);
            commentArea.setBackground(Color.LIGHT_GRAY);
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
                buttonTags.setEnabled(false);
                buttonTranslate.setEnabled(false);
                buttonNext.setEnabled(false);
                buttonPrevious.setEnabled(false);
                buttonCreate.setEnabled(false);
                setAutoCorrect(true);
                sourceArea.setEditable(true);
                targetArea.setEditable(true);
                commentArea.setEditable(true);
                sourceArea.setBackground(Color.LIGHT_GRAY);
                targetArea.setBackground(Color.LIGHT_GRAY);
                commentArea.setBackground(Color.LIGHT_GRAY);
            }
        }

    }

    HashMap<String, JMenuItem> menuItems = new HashMap<>();

    private void AddMenuItem(JMenu menu, String name, String subName) {
        JMenuItem menuItem;
        if (subName.equals("")) {
            menuItem = new JMenuItem(name);
            menuItem.setActionCommand(name);
        } else { // name is a language, subname indicates an action for this language (Editor, Numbers,..)
            menuItem = new JMenuItem(LanguageNames.shortToLong(name));
            menuItem.setActionCommand(name + "|" + subName);

        };
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItems.put(name, menuItem);
    }

    public void enableMenuItem(String actionName, boolean enabled) {
        JMenuItem menuItem;
        menuItem = menuItems.get(actionName);
        menuItem.setEnabled(enabled);
    }

    private void updateTagsPanel(Cluster c) {
        //       MsgTextPane.write("updateTagsPanel");
        for (Component component : tagsPanel.getComponents()) {
            tagsPanel.remove(component);
            //           MsgTextPane.write("updateTagsPanel: removing component " + component.getClass().getSimpleName());
        }

        if (c.tags.contains("favourite")) {
            tagsPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            tagsPanel.add(buttonFavourite);
        } else {
            tagsPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            tagsPanel.add(buttonNotFavourite);
        }

        Iterator iterator = selectionFrame.allTags.iterator();
        while (iterator.hasNext()) {
            String tag = (String) iterator.next();
            if (!tag.equals("favourite")) {
                //           MsgTextPane.write("updateTagsPanel: adding tag " + tag);
                tagsPanel.add(Box.createRigidArea(new Dimension(10, 10)));
                JButton button = tagButtons.get(tag);
                if (button == null) {
//                MsgTextPane.write("updateTagsPanel: new button created");
                    button = new JButton(tag);
                    button.setActionCommand("tag|" + tag);
                    button.addActionListener(this);
                    tagButtons.put(tag, button);
                }
                if (c.tags.contains(tag)) {
                    button.setBackground(Color.GREEN);
                } else {
                    button.setBackground(neutralButtonColor);
                }

                tagsPanel.add(button);
            }
        }

        newTagField.setText("enter new tag");
        tagsPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        tagsPanel.add(buttonAddTag);
        tagsPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        tagsPanel.add(newTagField);

        tagsPanel.revalidate();
        content.revalidate();
        pack();
        content.repaint();
    }

    private void displayGUInew() {

        Dimension minimumDimension = new Dimension(200, 50);
        Dimension preferredDimension = new Dimension(780, 200);

        sourceArea.setMinimumSize(minimumDimension);
        sourceArea.setPreferredSize(preferredDimension);
        targetArea.setMinimumSize(minimumDimension);
        targetArea.setPreferredSize(preferredDimension);
        commentArea.setMinimumSize(minimumDimension);
        commentArea.setPreferredSize(preferredDimension);

        scrollingSourceArea.setMinimumSize(minimumDimension);
        scrollingSourceArea.setPreferredSize(preferredDimension);
        scrollingTargetArea.setMinimumSize(minimumDimension);
        scrollingTargetArea.setPreferredSize(preferredDimension);
        scrollingCommentArea.setMinimumSize(minimumDimension);
        scrollingCommentArea.setPreferredSize(preferredDimension);

        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
        topPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        topPanel.add(buttonNext);
        topPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        topPanel.add(buttonTranslate);
        topPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        topPanel.add(buttonPrevious);
        topPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        topPanel.add(spacer1);
        topPanel.add(Box.createRigidArea(new Dimension(30, 0)));
        topPanel.add(buttonTags);
        topPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        topPanel.add(buttonCreate);
        topPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        topPanel.add(buttonEdit);
        topPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        topPanel.add(buttonCommit);
        topPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        topPanel.add(buttonCancel);
        topPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        topPanel.add(spacer2);
        topPanel.add(Box.createRigidArea(new Dimension(30, 0)));
        topPanel.add(buttonPlus);
        topPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        topPanel.add(buttonMinus);
        topPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        content.add(topPanel);
        content.add(scrollingSourceArea);
        content.add(scrollingTargetArea);
        content.add(scrollingCommentArea);
        content.add(tagsPanel);

        JMenuBar menuBar;
        JMenu menuExit;
        JMenu menuClusters;

        menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        menuExit = new JMenu("Exit");
        menuBar.add(menuExit);
        AddMenuItem(menuExit, "Save clusters and exit", "");
        AddMenuItem(menuExit, "Exit without saving clusters", "");

        menuClusters = new JMenu("Clusters");
        menuBar.add(menuClusters);
        AddMenuItem(menuClusters, "Read Tatoeba Database", "");
        AddMenuItem(menuClusters, "Read clusters", "");
        AddMenuItem(menuClusters, "Cluster Overview", "");
        AddMenuItem(menuClusters, "Select clusters", "");
        AddMenuItem(menuClusters, "Display unsaved clusters", "");
        AddMenuItem(menuClusters, "Save all clusters", "");
        AddMenuItem(menuClusters, "Save selected clusters", "");
        AddMenuItem(menuClusters, "Save special clusters", "");

        menuClusters = new JMenu("View");
        menuBar.add(menuClusters);
        AddMenuItem(menuClusters, "Horizontal", "");
        AddMenuItem(menuClusters, "Vertical", "");

        pack();
    }

    public LanguageTextPane createTextPane(String language) {
        LanguageTextPane pane = new LanguageTextPane(language);
        pane.displayParameters();
        return pane;
    }

    public TatoebaFrame() {

        sourceArea = createTextPane("generic");
        targetArea = createTextPane("generic");
        commentArea = new JTextPane();

        scrollingSourceArea = new JScrollPane(sourceArea);
        scrollingTargetArea = new JScrollPane(targetArea);
        scrollingCommentArea = new JScrollPane(commentArea);

        sourceArea.setEditable(false);
        targetArea.setEditable(false);
        commentArea.setEditable(false);

        tagsPanel = new JPanel();
        tagsPanel.setLayout(new BoxLayout(tagsPanel, BoxLayout.LINE_AXIS));

        content.setLayout(new GridBagLayout());

        // create favourite and notFavourite buttons
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        String fileName = "";
        try {

            Image image;
            ImageIcon icon;

            fileName = defaultFolder + "\\Tatoeba\\Images\\favourite.jpg";
            image = ImageIO.read(new File(fileName));
            image = image.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(image);
            buttonFavourite = new JButton(icon);
            buttonFavourite.setOpaque(false);
            buttonFavourite.setBorderPainted(false);

            fileName = defaultFolder + "\\Tatoeba\\Images\\notFavourite.jpg";
            image = ImageIO.read(new File(fileName));
            image = image.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(image);
            buttonNotFavourite = new JButton(icon);
            buttonNotFavourite.setOpaque(false);
            buttonNotFavourite.setBorderPainted(false);
        } catch (IOException e) {
            System.out.println("io exception : " + fileName);
        }

        buttonPlus.addActionListener(this);
        buttonMinus.addActionListener(this);
        buttonNext.addActionListener(this);
        buttonTranslate.addActionListener(this);
        buttonPrevious.addActionListener(this);
        buttonCommit.addActionListener(this);
        buttonCancel.addActionListener(this);
        buttonEdit.addActionListener(this);
        buttonTags.addActionListener(this);
        buttonCreate.addActionListener(this);
        buttonAddTag.addActionListener(this);
        buttonFavourite.addActionListener(this);
        buttonNotFavourite.addActionListener(this);

        buttonPlus.setActionCommand("buttonPlus");
        buttonMinus.setActionCommand("buttonMinus");
        buttonNext.setActionCommand("buttonNext");
        buttonTranslate.setActionCommand("buttonTranslate");
        buttonPrevious.setActionCommand("buttonPrevious");
        buttonCommit.setActionCommand("buttonCommit");
        buttonCancel.setActionCommand("buttonCancel");
        buttonEdit.setActionCommand("buttonEdit");
        buttonTags.setActionCommand("buttonTags");
        buttonCreate.setActionCommand("buttonCreate");
        buttonAddTag.setActionCommand("buttonAddTag");
        buttonNotFavourite.setActionCommand("buttonNotFavourite");
        buttonFavourite.setActionCommand("buttonFavourite");

        displayGUInew();

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
        buttonTags.setEnabled(false);
        buttonCommit.setEnabled(false);
        buttonCancel.setEnabled(false);

        setContentPane(content);
        setTitle("Tatoeba language trainer");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowUtils());

        graph = new Graph();
        workingSet = new WorkingSet(this);
        selectionFrame = new SelectionFrame(this);
    }

    String clustersFileName = "?";

    public void readSentences(String dirName) {

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

                    graph.addSentence(s);

                    // when reading the tatoeba files, there are no source, target or language
                    // keywords like in a cluster database, so 'allLanguages' is populated here
                    selectionFrame.usedLanguages.add(s.language);
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
                if (graph.addLink(nr1, nr2)) {
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

    public boolean readClusters(String fileName) {

        BufferedReader inputStream = null;
        int clusterCount = 0;
        Cluster c = null;
        Sentence s = null;

        try {

            File initialFile = new File(fileName);
            InputStream is = new FileInputStream(initialFile);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            inputStream = new BufferedReader(isr);

            clusterCount = graph.clusters.size();

            clustersFileName = fileName;
            MsgTextPane.write("reading clusters from " + fileName + "...");

            String l;
            String[] ls;
            int lineCount = 0;
            while ((l = inputStream.readLine()) != null) {

                if (lineCount == 0) {
                    l = ByteOrderMark.remove(l);
                }

                lineCount++;
                ls = l.split("\u0009");
                if (ls[0].matches(" *cluster *")) {
                    clusterCount++;
                    c = new Cluster();
                    c.nr = clusterCount;
                    graph.clusters.put(c.nr, c);
                    // the remaining strings are cluster tags
                    String tag;
                    for (int i = 1; i <= ls.length - 1; i++) {
                        tag = ls[i];
                        tag = tag.replaceAll(" *", "");
                        c.tags.add(tag);
                        selectionFrame.allTags.add(tag);
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

                        selectionFrame.usedLanguages.add(s.language);

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

        return true;
    }

    public void saveClusters(String mode) {

        String fileName;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File(clustersFileName));
        int retval = fileChooser.showSaveDialog(null);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            fileName = f.getAbsolutePath();

            try {
                File initialFile = new File(fileName);
                OutputStream is = new FileOutputStream(initialFile);
                OutputStreamWriter isr = new OutputStreamWriter(is, "UTF-8");
                BufferedWriter outputStream = new BufferedWriter(isr);
                /*
                 HashSet<String> usedLanguages;
                 if (mode.equals("all")) {
                 usedLanguages = new HashSet<String>(selectionFrame.usedLanguages);
                 } else {
                 usedLanguages = new HashSet<String>(selectionFrame.sourceLanguages);
                 usedLanguages.addAll(selectionFrame.targetLanguages);
                 }
                 */
                for (Cluster c : graph.clusters.values()) {
                    if (mode.equals("all") || c.selected) {
                        outputStream.write("cluster");
                        for (String tag : c.tags) {
                            outputStream.write("\u0009");
                            outputStream.write(tag);
                        }
                        outputStream.newLine();
                        for (Sentence s : c.sentences) {
//                            if (usedLanguages.contains(s.language)) {
                            outputStream.write(s.language);
                            outputStream.write("\u0009");
                            outputStream.write(s.sentence);
                            outputStream.newLine();
//                            }
                        }
                        c.unsaved = false;
                    }
                }

                outputStream.close();

            } catch (IOException io) {
                MsgTextPane.write(" io exception during save clusters");
            }
        }
    }
}
