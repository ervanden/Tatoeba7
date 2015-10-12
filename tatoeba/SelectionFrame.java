package tatoeba;

import utils.GenericTextFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class SelectionFrame {

    private static JFrame frame = new JFrame();
    static GenericTextFrame searchResultsFrame = null;

    public static final JTextPane allLanguagesArea = new JTextPane();
    public static final JTextPane sourceLanguagesArea = new JTextPane();
    public static final JTextPane targetLanguagesArea = new JTextPane();
    public static final JTextPane allTagsArea = new JTextPane();
    public static final JTextPane selectedTagsArea = new JTextPane();

    public static JScrollPane allLanguagesScroll = new JScrollPane(allLanguagesArea);
    public static JScrollPane sourceLanguagesScroll = new JScrollPane(sourceLanguagesArea);
    public static JScrollPane targetLanguagesScroll = new JScrollPane(targetLanguagesArea);
    public static JScrollPane allTagsScroll = new JScrollPane(allTagsArea);
    public static JScrollPane selectedTagsScroll = new JScrollPane(selectedTagsArea);

    public static HashSet<String> usedLanguages = new HashSet<>();
    public static HashSet<String> allLanguages = new HashSet<>();
    public static HashSet<String> allTags = new HashSet<>();
    public static HashSet<String> sourceLanguages = new HashSet<>();
    public static HashSet<String> targetLanguages = new HashSet<>();
    public static HashSet<String> selectedTags = new HashSet<>();

    public static HashSet<String> allLanguagesSelected = new HashSet<>();
    public static HashSet<String> allTagsSelected = new HashSet<>();
    public static HashSet<String> sourceLanguagesSelected = new HashSet<>();
    public static HashSet<String> targetLanguagesSelected = new HashSet<>();
    public static HashSet<String> selectedTagsSelected = new HashSet<>();

    public static String sourcePattern = "";
    public static String targetPattern = "";

//    public static int currentFontSize = 14;
    static Font areaFont = new Font("monospaced", Font.PLAIN, 14);

    static JPanel content = new JPanel();

    static JButton buttonSelect = new JButton("Select");
    static JButton buttonDisplay = new JButton("Select and Display");

    static JButton buttonRight1 = new JButton(">");
    static JButton buttonRight2 = new JButton(">");
    static JButton buttonRight3 = new JButton(">");
    static JButton buttonLeft1 = new JButton("<");
    static JButton buttonLeft2 = new JButton("<");
    static JButton buttonLeft3 = new JButton("<");

    static int sliderScale = 100;
    static JSlider sliderMin = new JSlider(0, sliderScale, 0);
    static JSlider sliderMax = new JSlider(0, sliderScale, sliderScale);

    static JLabel statusLabel = new JLabel("...");
    static JLabel minComplexityLabel = new JLabel("Minimum complexity");
    static JLabel maxComplexityLabel = new JLabel("Maximum complexity");
    static JLabel sourcePatternLabel = new JLabel("Source contains : ");
    static JLabel targetPatternLabel = new JLabel("Target contains : ");
    static JTextField sourcePatternField = new JTextField("");
    static JTextField targetPatternField = new JTextField("");

    static boolean enableCaretListener = false;

    static final SimpleAttributeSet sas_selected = new SimpleAttributeSet();

    static {
        StyleConstants.setBold(sas_selected, true);
        StyleConstants.setForeground(sas_selected, Color.BLUE);
        sliderMin.setMajorTickSpacing(1);
        sliderMax.setMajorTickSpacing(1);
    }

    static class WindowCloser extends WindowAdapter {

        public void windowClosing(WindowEvent e) {
            SelectionFrame.setVisible(false);
            if (searchResultsFrame != null) {
                searchResultsFrame.setVisible(false);
            }
        }
    }

    public static void setVisible(boolean visible) {
        if (visible) {
            Graph.selectClustersByParameters(sourceLanguages, targetLanguages, selectedTags, sourcePattern, targetPattern);
            Graph.selectClustersByComplexity((float) sliderMin.getValue() / (float) sliderScale,
                    (float) sliderMax.getValue() / (float) sliderScale, true);
            statusMessage(true);
        }
        frame.setVisible(visible);
        enableCaretListener = true;  // now we are sure that boxes are populated
    }

    private static void eraseDocument(StyledDocument doc) {
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException ble) {
            System.out.println("ble");
        }
    }

    static class selectLinesTask implements Runnable {

        private int line1, line2;
        private JTextPane area;

        selectLinesTask(JTextPane area, int line1, int line2) {
            this.line1 = line1;
            this.line2 = line2;
            this.area = area;
        }

        public void run() {
            selectLines(area, line1, line2);
        }
    }

    private static void selectLines(JTextPane area, int line1, int line2) {
        StyledDocument document = area.getStyledDocument();
        javax.swing.text.Element root = document.getDefaultRootElement();
        for (int i = line1; i <= line2; i++) {
            javax.swing.text.Element lineElement = (javax.swing.text.Element) root.getElement(i);
            int start = lineElement.getStartOffset();
            int end = lineElement.getEndOffset();
            document.setCharacterAttributes(start, end - start, sas_selected, false);
            try {
                String languageName = document.getText(start, end - start - 1);
//                System.out.println("Line " + i + " Selected |" + languageName + "|");
                if (!languageName.equals("")) {
                    languageName = LanguageNames.longToShort(languageName);
                    if (area.equals(allLanguagesArea)) {
                        System.out.println("selected " + languageName);
                        allLanguagesSelected.add(languageName);
                    }
                    if (area.equals(sourceLanguagesArea)) {
                        sourceLanguagesSelected.add(languageName);
                    }
                    if (area.equals(targetLanguagesArea)) {
                        targetLanguagesSelected.add(languageName);
                    }
                    if (area.equals(allTagsArea)) {
                        allTagsSelected.add(languageName);
                    }
                    if (area.equals(selectedTagsArea)) {
                        selectedTagsSelected.add(languageName);
                    }
                }

            } catch (BadLocationException ble) {
            }
        }
    }

    static class areaCaretListener implements CaretListener {

        JTextPane area;

        public areaCaretListener(JTextPane activeArea) {
            area = activeArea;
        }

        private int lineFinder(StyledDocument document, int position) {
            javax.swing.text.Element root = document.getDefaultRootElement();
            int line = 0;
            int count = root.getElementCount();
            for (int i = 0; i < count; i++) {
                javax.swing.text.Element lineElement = (javax.swing.text.Element) root.getElement(i);
                if ((lineElement.getStartOffset() <= position) && (position <= lineElement.getEndOffset())) {
                    line = i;
                }
            }
            return line;
        }

        public void caretUpdate(CaretEvent e) {
            StyledDocument document = area.getStyledDocument();
            if (enableCaretListener) {
                int line1 = lineFinder(document, e.getMark());
                int line2 = lineFinder(document, e.getDot());
                SwingUtilities.invokeLater(new selectLinesTask(area, line1, line2));
            }
        }

    }

    static areaCaretListener allLanguagesCaretListener = new areaCaretListener(allLanguagesArea);
    static areaCaretListener sourceLanguagesCaretListener = new areaCaretListener(sourceLanguagesArea);
    static areaCaretListener targetLanguagesCaretListener = new areaCaretListener(targetLanguagesArea);
    static areaCaretListener allTagsCaretListener = new areaCaretListener(allTagsArea);
    static areaCaretListener selectedTagsCaretListener = new areaCaretListener(selectedTagsArea);

    private static void populateArea(JTextPane pane, HashSet<String> names) {
        ArrayList<String> longNames = new ArrayList<String>();
        StyledDocument doc;

        for (String s : names) {
            longNames.add(LanguageNames.shortToLong(s));
        }
        Collections.sort(longNames);
        doc = pane.getStyledDocument();
        eraseDocument(doc);
        for (String language : longNames) {
            try {
                doc.insertString(doc.getLength(), language + "\n", null);
            } catch (BadLocationException blex) {
            }
        };

    }

    public static void populateAreas() {

        enableCaretListener = false; // prevent that caretlistener fires and selects everything

        populateArea(allLanguagesArea, allLanguages);
        populateArea(sourceLanguagesArea, sourceLanguages);
        populateArea(targetLanguagesArea, targetLanguages);
        populateArea(allTagsArea, allTags);
        populateArea(selectedTagsArea, selectedTags);

        allLanguagesSelected.clear();
        sourceLanguagesSelected.clear();
        targetLanguagesSelected.clear();
        allTagsSelected.clear();
        selectedTagsSelected.clear();

        enableCaretListener = true;

    }

    private static void resetGridBagConstraints(GridBagConstraints c) {
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.ipadx = 0;
        c.ipady = 0;
    }

    private static void displayGUI() {

        int position = 0;
        int y0 = 1;

        GridBagConstraints c = new GridBagConstraints();
        Insets insets = new Insets(0, 5, 0, 0);

        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        c.gridx = position++;
        c.gridy = y0 + 0;
        c.gridheight = 4;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(allLanguagesScroll, c);

        // < > buttons
        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weighty = 1;
        c.gridx = position;
        c.gridy = y0 + 0;
        c.gridheight = 1;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(buttonRight1, c);

        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weighty = 1;
        c.gridx = position;
        c.gridy = y0 + 1;
        c.gridheight = 1;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(buttonLeft1, c);

        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weighty = 1;
        c.gridx = position;
        c.gridy = y0 + 2;
        c.gridheight = 1;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(buttonRight2, c);

        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weighty = 1;
        c.gridx = position++;    // to next column
        c.gridy = y0 + 3;
        c.gridheight = 1;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(buttonLeft2, c);

        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = position;
        c.gridy = y0 + 0;
        c.gridheight = 2;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(sourceLanguagesScroll, c);

        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = position++;
        c.gridy = y0 + 2;
        c.gridheight = 2;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(targetLanguagesScroll, c);

        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        c.gridx = position++;
        c.gridy = y0 + 0;
        c.gridheight = 2;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(allTagsScroll, c);

        // < > buttons
        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weighty = 1;
        c.gridx = position;
        c.gridy = y0;//y0 + 1;
        c.gridheight = 1;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(buttonRight3, c);

        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weighty = 1;
        c.gridx = position++;
        c.gridy = y0 + 1;
        c.gridheight = 1;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(buttonLeft3, c);

        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        c.gridx = position++;
        c.gridy = y0 + 0;
        c.gridheight = 2;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(selectedTagsScroll, c);

        // search pattern
        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0.5;
        c.gridx = 3;
        c.gridy = y0 + 2;
        c.gridheight = 1;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(sourcePatternLabel, c);

        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 4;
        c.gridy = y0 + 2;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(sourcePatternField, c);

        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0.5;
        c.gridx = 3;
        c.gridy = y0 + 3;
        c.gridheight = 1;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(targetPatternLabel, c);

        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 4;
        c.gridy = y0 + 3;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.insets = new Insets(0, 5, 0, 0);  // top left bottom right
        content.add(targetPatternField, c);

        // top : status label and apply button
        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.insets = new Insets(20, 5, 20, 5);  // top left bottom right
        content.add(statusLabel, c);

        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        c.gridx = 4;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.insets = new Insets(20, 5, 20, 5);  // top left bottom right
        content.add(buttonSelect, c);

        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        c.gridx = 5;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.insets = new Insets(20, 5, 20, 5);  // top left bottom right
        content.add(buttonDisplay, c);

        // min complexity slider
        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = y0 + 4;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.insets = new Insets(10, 0, 10, 0);  // top left bottom right
        content.add(minComplexityLabel, c);

        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = 2;
        c.gridy = y0 + 4;
        c.gridheight = 1;
        c.gridwidth = 4;
        c.insets = new Insets(10, 0, 10, 0);  // top left bottom right
        content.add(sliderMin, c);

        // min complexity slider
        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = y0 + 5;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.insets = new Insets(10, 0, 10, 0);  // top left bottom right
        content.add(maxComplexityLabel, c);

        resetGridBagConstraints(c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = 2;
        c.gridy = y0 + 5;
        c.gridheight = 1;
        c.gridwidth = 4;
        c.insets = new Insets(10, 0, 10, 0);  // top left bottom right
        content.add(sliderMax, c);

        frame.pack();
    }

    private static void setAreaParameters(JTextPane area, String title) {
        area.setName(title);
        area.setMinimumSize(new Dimension(100, 300));
        area.setPreferredSize(new Dimension(100, 300));
        area.setMaximumSize(new Dimension(100, 300));
        //       area.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // 2 pixels around text in JTextPane
        area.setBorder(BorderFactory.createTitledBorder(title));
        area.setFont(areaFont);
    }

    private static void setScrollParameters(JScrollPane area) {
        area.setMinimumSize(new Dimension(200, 300));
        area.setPreferredSize(new Dimension(200, 300));
        //       area.setMaximumSize(new Dimension(100, 300));
        //       area.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // 2 pixels around text in JTextPane    
        //       area.setFont(areaFont);
    }

    private static void setButtonParameters(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
    }

    private static void statusMessage(boolean printCount) {
        if (!printCount) {
            statusLabel.setText(Graph.clusters.size() + " clusters available, ...");

        } else {
            Graph.selectClustersByComplexity((float) sliderMin.getValue() / (float) sliderScale,
                    (float) sliderMax.getValue() / (float) sliderScale, true);
            statusLabel.setText(Graph.clusters.size() + " clusters available, " + Graph.selectedClusterCount + " matches");
            minComplexityLabel.setText("Minimum Length = " + sliderMin.getValue());
            maxComplexityLabel.setText("Maximum Length = " + sliderMax.getValue());
        }
    }

    public static void execute() {

        setAreaParameters(allLanguagesArea, "Languages");
        setAreaParameters(sourceLanguagesArea, "Source");
        setAreaParameters(targetLanguagesArea, "Target");
        setAreaParameters(allTagsArea, "Tags");
        setAreaParameters(selectedTagsArea, "Selected Tags");

        setScrollParameters(allLanguagesScroll);
        setScrollParameters(sourceLanguagesScroll);
        setScrollParameters(targetLanguagesScroll);
        setScrollParameters(allTagsScroll);
        setScrollParameters(selectedTagsScroll);

        setButtonParameters(buttonRight1);
        setButtonParameters(buttonRight2);
        setButtonParameters(buttonRight3);
        setButtonParameters(buttonLeft1);
        setButtonParameters(buttonLeft2);
        setButtonParameters(buttonLeft3);

        buttonSelect.setBackground(Color.green);
        buttonDisplay.setBackground(Color.green);

        allLanguagesArea.setEditable(false);
        sourceLanguagesArea.setEditable(false);
        targetLanguagesArea.setEditable(false);
        allTagsArea.setEditable(false);
        selectedTagsArea.setEditable(false);

        content.setLayout(new GridBagLayout());

        sourcePatternField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sourcePattern = sourcePatternField.getText();
                Graph.selectClustersByParameters(sourceLanguages, targetLanguages, selectedTags, sourcePattern, targetPattern);
                statusMessage(true);
            }
        ;
        });
        
        targetPatternField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                targetPattern = targetPatternField.getText();
                Graph.selectClustersByParameters(sourceLanguages, targetLanguages, selectedTags, sourcePattern, targetPattern);
                statusMessage(true);
            }
        ;
        });
                 
        buttonRight1.addActionListener((new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableCaretListener = false;
                sourceLanguages.addAll(allLanguagesSelected);
                populateArea(allLanguagesArea, allLanguages);
                populateArea(sourceLanguagesArea, sourceLanguages);
                allLanguagesSelected.clear();
                sourceLanguagesSelected.clear();
                statusMessage(false);
                Graph.selectClustersByParameters(sourceLanguages, targetLanguages, selectedTags, sourcePattern, targetPattern);
                statusMessage(true);
                enableCaretListener = true;
            }
        }));

        buttonLeft1.addActionListener((new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableCaretListener = false;
                sourceLanguages.removeAll(sourceLanguagesSelected);
                populateArea(allLanguagesArea, allLanguages);
                populateArea(sourceLanguagesArea, sourceLanguages);
                allLanguagesSelected.clear();
                sourceLanguagesSelected.clear();
                statusMessage(false);
                Graph.selectClustersByParameters(sourceLanguages, targetLanguages, selectedTags, sourcePattern, targetPattern);
                statusMessage(true);
                enableCaretListener = true;
            }
        }));

        buttonRight2.addActionListener((new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableCaretListener = false;
                targetLanguages.addAll(allLanguagesSelected);
                populateArea(allLanguagesArea, allLanguages);
                populateArea(targetLanguagesArea, targetLanguages);
                allLanguagesSelected.clear();
                targetLanguagesSelected.clear();
                statusMessage(false);
                Graph.selectClustersByParameters(sourceLanguages, targetLanguages, selectedTags, sourcePattern, targetPattern);
                statusMessage(true);
                enableCaretListener = true;
            }
        }));

        buttonLeft2.addActionListener((new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableCaretListener = false;
                targetLanguages.removeAll(targetLanguagesSelected);
                populateArea(allLanguagesArea, allLanguages);
                populateArea(targetLanguagesArea, targetLanguages);
                allLanguagesSelected.clear();
                targetLanguagesSelected.clear();
                statusMessage(false);
                Graph.selectClustersByParameters(sourceLanguages, targetLanguages, selectedTags, sourcePattern, targetPattern);
                statusMessage(true);
                enableCaretListener = true;
            }
        }));

        buttonRight3.addActionListener((new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableCaretListener = false;
                selectedTags.addAll(allTagsSelected);
                populateArea(allTagsArea, allTags);
                populateArea(selectedTagsArea, selectedTags);
                allTagsSelected.clear();
                selectedTagsSelected.clear();
                statusMessage(false);
                Graph.selectClustersByParameters(sourceLanguages, targetLanguages, selectedTags, sourcePattern, targetPattern);
                statusMessage(true);
                enableCaretListener = true;
            }
        }));

        buttonLeft3.addActionListener((new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableCaretListener = false;
                selectedTags.removeAll(selectedTagsSelected);
                populateArea(allTagsArea, allTags);
                populateArea(selectedTagsArea, selectedTags);
                allTagsSelected.clear();
                selectedTagsSelected.clear();
                statusMessage(false);
                Graph.selectClustersByParameters(sourceLanguages, targetLanguages, selectedTags, sourcePattern, targetPattern);
                statusMessage(true);
                enableCaretListener = true;
            }
        }));

        buttonDisplay.addActionListener((new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (searchResultsFrame == null) {
                    searchResultsFrame = new GenericTextFrame();
                }
                searchResultsFrame.setVisible(true);
                Graph.selectClustersByComplexity((float) sliderMin.getValue() / (float) sliderScale,
                        (float) sliderMax.getValue() / (float) sliderScale, false);
                Graph.selectClusters();
                Graph.displayClusters(searchResultsFrame, "selected");
                WorkingSet.build();
            }
        }));

        buttonSelect.addActionListener((new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Graph.selectClustersByComplexity((float) sliderMin.getValue() / (float) sliderScale,
                        (float) sliderMax.getValue() / (float) sliderScale, false);
                Graph.selectClusters();
                WorkingSet.build();

                // if the source or target are one single language, make the corresponding window language sensitive
                
                if (targetLanguages.size() == 1) {
                    for (String language : targetLanguages) {
                        Tatoeba.tatoebaFrame.newTargetArea(language);
                    }
                }
                if (sourceLanguages.size() == 1) {
                    for (String language : sourceLanguages) {
                        Tatoeba.tatoebaFrame.newSourceArea(language);
                    }
                }

                JOptionPane.showMessageDialog(frame, Graph.selectedClusterCount + " clusters selected");
                if (Graph.selectedClusterCount > 0) {
                    setVisible(false);
                }
            }
        }));

        sliderMin.addChangeListener((new ChangeListener() {

            public void stateChanged(ChangeEvent changeEvent) {
                if (sliderMin.getValue() > sliderMax.getValue()) {
                    sliderMin.setValue(sliderMax.getValue());
                }
                Graph.selectClustersByComplexity((float) sliderMin.getValue() / (float) sliderScale,
                        (float) sliderMax.getValue() / (float) sliderScale, true); // true = count only
                statusMessage(true);
            }
        }));

        sliderMax.addChangeListener((new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                if (sliderMin.getValue() > sliderMax.getValue()) {
                    sliderMax.setValue(sliderMin.getValue());
                }
                Graph.selectClustersByComplexity((float) sliderMin.getValue() / (float) sliderScale,
                        (float) sliderMax.getValue() / (float) sliderScale, true); // true = count only
                statusMessage(true);
            }
        }));

        allLanguagesArea.addCaretListener(allLanguagesCaretListener);
        sourceLanguagesArea.addCaretListener(sourceLanguagesCaretListener);
        targetLanguagesArea.addCaretListener(targetLanguagesCaretListener);
        allTagsArea.addCaretListener(allTagsCaretListener);
        selectedTagsArea.addCaretListener(selectedTagsCaretListener);

        statusMessage(true);
        displayGUI();

        frame.setContentPane(content);
        frame.setTitle("languages and tags");
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setVisible(false);
        frame.addWindowListener(new WindowCloser());

    }
}
