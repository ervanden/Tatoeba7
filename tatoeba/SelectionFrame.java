package tatoeba;

import languages.LanguageNames;
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
import java.util.Iterator;
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
import utils.*;

public class SelectionFrame implements ActionListener {

    boolean isCreated = false;
    private JFrame frame = new JFrame();
    GenericTextFrame searchResultsFrame = null;
    TatoebaFrame tatoebaFrame = null;

    public final JTextPane allLanguagesArea = new JTextPane();
    public final JTextPane sourceLanguagesArea = new JTextPane();
    public final JTextPane targetLanguagesArea = new JTextPane();
    public final JTextPane allTagsArea = new JTextPane();
    public final JTextPane selectedTagsArea = new JTextPane();

    public JScrollPane allLanguagesScroll = new JScrollPane(allLanguagesArea);
    public JScrollPane sourceLanguagesScroll = new JScrollPane(sourceLanguagesArea);
    public JScrollPane targetLanguagesScroll = new JScrollPane(targetLanguagesArea);
    public JScrollPane allTagsScroll = new JScrollPane(allTagsArea);
    public JScrollPane selectedTagsScroll = new JScrollPane(selectedTagsArea);

    public HashSet<String> usedLanguages = new HashSet<>();
    public HashSet<String> allTags = new HashSet<>();
    public HashSet<String> sourceLanguages = new HashSet<>();
    public HashSet<String> targetLanguages = new HashSet<>();
    public HashSet<String> selectedTags = new HashSet<>();

    public HashSet<String> allLanguagesSelected = new HashSet<>();
    public HashSet<String> allTagsSelected = new HashSet<>();
    public HashSet<String> sourceLanguagesSelected = new HashSet<>();
    public HashSet<String> targetLanguagesSelected = new HashSet<>();
    public HashSet<String> selectedTagsSelected = new HashSet<>();

    public String sourcePattern = "";
    public String targetPattern = "";

//    public int currentFontSize = 14;
    Font areaFont = new Font("monospaced", Font.PLAIN, 14);

    JPanel content = new JPanel();

    JButton buttonSelect = new JButton("Select");
    JButton buttonDisplay = new JButton("Select and Display");

    JButton buttonRight1 = new JButton(">");
    JButton buttonRight2 = new JButton(">");
    JButton buttonRight3 = new JButton(">");
    JButton buttonLeft1 = new JButton("<");
    JButton buttonLeft2 = new JButton("<");
    JButton buttonLeft3 = new JButton("<");

    int sliderScale = 100;
    JSlider sliderMin = new JSlider(0, sliderScale, 0);
    JSlider sliderMax = new JSlider(0, sliderScale, sliderScale);
    boolean sliderValuesValid = false;

    JLabel statusLabel = new JLabel("...");
    JLabel minComplexityLabel = new JLabel("Minimum complexity");
    JLabel maxComplexityLabel = new JLabel("Maximum complexity");
    JLabel sourcePatternLabel = new JLabel("Source contains : ");
    JLabel targetPatternLabel = new JLabel("Target contains : ");
    JTextField sourcePatternField = new JTextField("");
    JTextField targetPatternField = new JTextField("");

    boolean enableCaretListener = false;

    final SimpleAttributeSet sas_selected = new SimpleAttributeSet();

    {
        StyleConstants.setBold(sas_selected, true);
        StyleConstants.setForeground(sas_selected, Color.BLUE);
        System.out.println("init block");
        sliderMin.setPaintTicks(true);
        sliderMin.setMinorTickSpacing(1);
                sliderMin.setMajorTickSpacing(5);
                sliderMin.setSnapToTicks(false);
                sliderMax.setPaintTicks(true);
        sliderMax.setMinorTickSpacing(1);
                sliderMax.setMajorTickSpacing(5);
                sliderMax.setSnapToTicks(false);
    }

    class WindowCloser extends WindowAdapter {

        public void windowClosing(WindowEvent e) {
            setVisible(false);
            if (searchResultsFrame != null) {
                searchResultsFrame.setVisible(false);
            }
        }
    }

    public String targetLanguage() {
        // targetLanguages is a set for uniformity with the other sets
        // There can only be one target language
        // This function returns this language
        if (targetLanguages.size() != 1) {
            MsgTextPane.write("ERROR size of target language set is " + targetLanguages.size() + ", must be 1 ");
            return null;
        } else {
            Iterator iter = targetLanguages.iterator();
            return (String) iter.next();
        }
    }

    public void setVisible(boolean visible) {
        if (visible) {
            selectClustersByParameters();
            tatoebaFrame.graph.selectClustersByComplexity((float) sliderMin.getValue() / (float) sliderScale,
                    (float) sliderMax.getValue() / (float) sliderScale, true);
            statusMessage(true);
        }

        // tags may have changed, so re-populate tags area
        populateArea(allTagsArea, new ArrayList<String>(allTags));

        frame.setVisible(visible);
        enableCaretListener = true;  // now we are sure that boxes are populated
    }

    private void eraseDocument(StyledDocument doc) {
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException ble) {
            System.out.println("ble");
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonSelect) {
            tatoebaFrame.graph.selectClustersByComplexity((float) sliderMin.getValue() / (float) sliderScale,
                    (float) sliderMax.getValue() / (float) sliderScale, false);
            tatoebaFrame.graph.selectClusters();
            tatoebaFrame.workingSet.build();

            // if the source or target are one single language, make the corresponding window language sensitive
            if (targetLanguages.size() == 1) {
                for (String language : targetLanguages) {
                    tatoebaFrame.newTargetArea(language);
                }
            }
            if (sourceLanguages.size() == 1) {
                for (String language : sourceLanguages) {
                    tatoebaFrame.newSourceArea(language);
                }
            }

            JOptionPane.showMessageDialog(frame, tatoebaFrame.graph.selectedClusterCount + " clusters selected");
//                if (tatoebaFrame.graph.selectedClusterCount > 0) {
            setVisible(false);
//                }
        } else if (e.getSource() == buttonDisplay) {
            if (searchResultsFrame == null) {
                searchResultsFrame = new GenericTextFrame();
            }
            searchResultsFrame.setVisible(true);
            tatoebaFrame.graph.selectClustersByComplexity((float) sliderMin.getValue() / (float) sliderScale,
                    (float) sliderMax.getValue() / (float) sliderScale, false);
            tatoebaFrame.graph.selectClusters();
            tatoebaFrame.graph.displayClusters(searchResultsFrame, "selected", this);
            tatoebaFrame.workingSet.build();
        }
    }

    class selectLinesTask implements Runnable {

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

    private void selectLines(JTextPane area, int line1, int line2) {
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

    class areaCaretListener implements CaretListener {

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

    areaCaretListener allLanguagesCaretListener = new areaCaretListener(allLanguagesArea);
    areaCaretListener sourceLanguagesCaretListener = new areaCaretListener(sourceLanguagesArea);
    areaCaretListener targetLanguagesCaretListener = new areaCaretListener(targetLanguagesArea);
    areaCaretListener allTagsCaretListener = new areaCaretListener(allTagsArea);
    areaCaretListener selectedTagsCaretListener = new areaCaretListener(selectedTagsArea);

    ArrayList<String> listShortToLong(HashSet<String> shortNames) {
        ArrayList<String> longNames = new ArrayList<String>();
        for (String s : shortNames) {
            longNames.add(LanguageNames.shortToLong(s));
        }
        return longNames;
    }

    private void populateArea(JTextPane pane, ArrayList<String> longNames) {
        StyledDocument doc;
        Collections.sort(longNames);
        doc = pane.getStyledDocument();
        eraseDocument(doc);
        for (String language : longNames) {
            try {
                doc.insertString(doc.getLength(), language + "\n", null);
            } catch (BadLocationException blex) {
            }
        }
    }

    public void populateAreas() {

        enableCaretListener = false; // prevent that caretlistener fires and selects everything

        populateArea(allLanguagesArea, listShortToLong(usedLanguages));
        populateArea(sourceLanguagesArea, listShortToLong(sourceLanguages));
        populateArea(targetLanguagesArea, listShortToLong(targetLanguages));
        populateArea(allTagsArea, new ArrayList<String>(allTags));
        populateArea(selectedTagsArea, new ArrayList<String>(selectedTags));

        allLanguagesSelected.clear();
        sourceLanguagesSelected.clear();
        targetLanguagesSelected.clear();
        allTagsSelected.clear();
        selectedTagsSelected.clear();

        enableCaretListener = true;

    }

    private void resetGridBagConstraints(GridBagConstraints c) {
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

    private void displayGUI() {

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

    private void setAreaParameters(JTextPane area, String title) {
        area.setName(title);
        area.setMinimumSize(new Dimension(100, 300));
        area.setPreferredSize(new Dimension(100, 300));
        area.setMaximumSize(new Dimension(100, 300));
        //       area.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // 2 pixels around text in JTextPane
        area.setBorder(BorderFactory.createTitledBorder(title));
        area.setFont(areaFont);
    }

    private void setScrollParameters(JScrollPane area) {
        area.setMinimumSize(new Dimension(200, 300));
        area.setPreferredSize(new Dimension(200, 300));
        //       area.setMaximumSize(new Dimension(100, 300));
        //       area.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // 2 pixels around text in JTextPane    
        //       area.setFont(areaFont);
    }

    private void setButtonParameters(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
    }

    private void statusMessage(boolean printCount) {
        if (!printCount) {
            statusLabel.setText(tatoebaFrame.graph.clusters.size() + " clusters available, ...");

        } else {
            tatoebaFrame.graph.selectClustersByComplexity((float) sliderMin.getValue() / (float) sliderScale,
                    (float) sliderMax.getValue() / (float) sliderScale, true);
            statusLabel.setText(tatoebaFrame.graph.clusters.size() + " clusters available, " + tatoebaFrame.graph.selectedClusterCount + " matches");
            minComplexityLabel.setText("Minimum Length = " + sliderMin.getValue());
            maxComplexityLabel.setText("Maximum Length = " + sliderMax.getValue());
        }
    }

    private void selectClustersByParameters() {
        // call this method each time a parameter in the selectionFrame is changed
        // It calls the corresponding graph method. This selects the clusters according to the new parameters, and also recalculates the frequency table of the cluster lengths.
        // The sliders min and max values are then also adapted
        tatoebaFrame.graph.selectClustersByParameters(sourceLanguages, targetLanguages, selectedTags, sourcePattern, targetPattern);
        sliderScale = tatoebaFrame.graph.maxComplexity;
        System.out.println("new slider scale = " + sliderScale);
        sliderValuesValid = false; // the methods  below trigger the change listener. Avoid this until all values are set.
        sliderMin.setMinimum(0);
        sliderMax.setMinimum(0);
        sliderMin.setMaximum(sliderScale);
        sliderMax.setMaximum(sliderScale);
        sliderMin.setValue(0);
        sliderMax.setValue(sliderScale);
        sliderValuesValid = true;

    }

    public SelectionFrame(TatoebaFrame t) {
        tatoebaFrame = t;
        sourceLanguages.add(languagetrainer.LanguageTrainer.sourceLanguage);
        targetLanguages.add(languagetrainer.LanguageTrainer.targetLanguage);

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
                selectClustersByParameters();
                statusMessage(true);
            }
        ;
        });
        
        targetPatternField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                targetPattern = targetPatternField.getText();
                selectClustersByParameters();
                statusMessage(true);
            }
        ;
        });
                 
        buttonRight1.addActionListener((new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableCaretListener = false;
                sourceLanguages.addAll(allLanguagesSelected);
                populateArea(allLanguagesArea, listShortToLong(usedLanguages));
                populateArea(sourceLanguagesArea, listShortToLong(sourceLanguages));
                allLanguagesSelected.clear();
                sourceLanguagesSelected.clear();
                statusMessage(false);
                selectClustersByParameters();
                statusMessage(true);
                enableCaretListener = true;
            }
        }));

        buttonLeft1.addActionListener((new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableCaretListener = false;
                sourceLanguages.removeAll(sourceLanguagesSelected);
                populateArea(allLanguagesArea, listShortToLong(usedLanguages));
                populateArea(sourceLanguagesArea, listShortToLong(sourceLanguages));
                allLanguagesSelected.clear();
                sourceLanguagesSelected.clear();
                statusMessage(false);
                selectClustersByParameters();
                statusMessage(true);
                enableCaretListener = true;
            }
        }));

        buttonRight2.addActionListener((new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableCaretListener = false;
                // there can only be one target language. Arbitrarily take the first selected language as target language
                if (allLanguagesSelected.size() > 0) {
                    targetLanguages.clear();
                    Iterator iter = allLanguagesSelected.iterator();
                    targetLanguages.add((String) iter.next());
                    populateArea(allLanguagesArea, listShortToLong(usedLanguages));
                    populateArea(targetLanguagesArea, listShortToLong(targetLanguages));
                    allLanguagesSelected.clear();
                    targetLanguagesSelected.clear();
                    statusMessage(false);
                    selectClustersByParameters();
                    statusMessage(true);
                    enableCaretListener = true;
                }
            }

        }));

        buttonLeft2.addActionListener((new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                enableCaretListener = false;
                targetLanguages.removeAll(targetLanguagesSelected);
                populateArea(allLanguagesArea, listShortToLong(usedLanguages));
                populateArea(targetLanguagesArea, listShortToLong(targetLanguages));
                allLanguagesSelected.clear();
                targetLanguagesSelected.clear();
                statusMessage(false);
                selectClustersByParameters();
                statusMessage(true);
                enableCaretListener = true;
            }
        }));

        buttonRight3.addActionListener((new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableCaretListener = false;
                selectedTags.addAll(allTagsSelected);
                populateArea(allTagsArea, new ArrayList<String>(allTags));
                populateArea(selectedTagsArea, new ArrayList<String>(selectedTags));
                allTagsSelected.clear();
                selectedTagsSelected.clear();
                statusMessage(false);
                selectClustersByParameters();
                statusMessage(true);
                enableCaretListener = true;
            }
        }));

        buttonLeft3.addActionListener((new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableCaretListener = false;
                selectedTags.removeAll(selectedTagsSelected);
                populateArea(allTagsArea, new ArrayList<String>(allTags));
                populateArea(selectedTagsArea, new ArrayList<String>(selectedTags));
                allTagsSelected.clear();
                selectedTagsSelected.clear();
                statusMessage(false);
                selectClustersByParameters();
                statusMessage(true);
                enableCaretListener = true;
            }
        }));

        buttonDisplay.addActionListener(this);
        buttonSelect.addActionListener(this);

        sliderMin.addChangeListener((new ChangeListener() {

            public void stateChanged(ChangeEvent changeEvent) {
                if (sliderValuesValid) {
                    if (sliderMin.getValue() > sliderMax.getValue()) {
                        sliderMin.setValue(sliderMax.getValue());
                    }
                    tatoebaFrame.graph.selectClustersByComplexity((float) sliderMin.getValue() / (float) sliderScale,
                            (float) sliderMax.getValue() / (float) sliderScale, true); // true = count only
                    statusMessage(true);
                }
            }
        }));

        sliderMax.addChangeListener((new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                if (sliderValuesValid) {
                    if (sliderMin.getValue() > sliderMax.getValue()) {
                        sliderMax.setValue(sliderMin.getValue());
                    }
                    tatoebaFrame.graph.selectClustersByComplexity((float) sliderMin.getValue() / (float) sliderScale,
                            (float) sliderMax.getValue() / (float) sliderScale, true); // true = count only
                    statusMessage(true);
                }
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
