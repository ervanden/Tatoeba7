package tatoeba;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import languages.Language;
import languages.LanguageContext;
import static languagetrainer.LanguageTrainer.userLanguages;
import utils.MsgTextPane;

class ImagePanel extends JPanel {

    BufferedImage imageObject;

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imageObject != null) {
            g.drawImage(imageObject, 0, 0, this);
        }
    }

    public boolean setImage(String theme, String image) {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        String fileName = defaultFolder + "\\Tatoeba\\Images\\" + theme + "\\" + image + ".jpg";
        try {
            imageObject = ImageIO.read(new File(fileName));
            int width = imageObject.getWidth();
            int height = imageObject.getHeight();
            setPreferredSize(new Dimension(width, height));
            repaint();
            return true;
        } catch (IOException e) {
            System.out.println("io exception : " + fileName);
            return false;
        }
    }

}

public class PictureTrainer extends JFrame implements ActionListener, ItemListener {

    PictureTrainer thisPictureTrainer = this;
    JFrame thisFrame = (JFrame) this;
    JPanel content = new JPanel();
    JPanel topPanel;
    JPanel namePanel;
    JLabel name;
    ImagePanel imagePanel;
    String theme;
    ArrayList<String> pictures;
    Random randomGenerator = new Random();
    JCheckBox cBox;
    boolean circular = false;
    int currentPictureIndex = 0;
    String currentPicture;

    public PictureTrainer(String pictureTheme) {
        theme = pictureTheme;
        pictures = getPictureNames(theme);

        JButton transButton = new JButton("translate");
        transButton.setActionCommand("translate");
        transButton.addActionListener(thisPictureTrainer);
        JButton nextButton = new JButton("next");
        nextButton.setActionCommand("next");
        nextButton.addActionListener(thisPictureTrainer);
        cBox = new JCheckBox("circular");
        cBox.addItemListener(thisPictureTrainer);
        cBox.setEnabled(true);
        name = new JLabel("");
        Font font = new Font("Courier", Font.BOLD, 23);
        name.setFont(font);

        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
        topPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        topPanel.add(nextButton);
        topPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        topPanel.add(transButton);
        topPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        topPanel.add(cBox);
        topPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        topPanel.add(Box.createHorizontalGlue());

        namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.LINE_AXIS));
        namePanel.add(Box.createRigidArea(new Dimension(10, 10)));
        namePanel.add(name);
        namePanel.add(Box.createRigidArea(new Dimension(10, 10)));
        namePanel.add(Box.createHorizontalGlue());

        imagePanel = new ImagePanel();
        nextPicture();

        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.add(topPanel);
        content.add(namePanel);
        content.add(imagePanel);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setContentPane(content);
        pack();
    }

    public static ArrayList<String> getPictureThemes() {
        ArrayList<String> themeNames;
        themeNames = new ArrayList<>();
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        String folderName = defaultFolder + "\\Tatoeba\\Images\\";
        File folder = new File(folderName);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isDirectory()) {
                themeNames.add(file.getName());
            }
        }
        return themeNames;
    }

    public static ArrayList<String> getPictureNames(String theme) {
        ArrayList<String> pictureNames;
        pictureNames = new ArrayList<>();
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        String folderName = defaultFolder + "\\Tatoeba\\Images\\" + theme + "\\";
        File folder = new File(folderName);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                //               System.out.println("File " + file.getName());
                if (file.getName().matches(".*[.]jpg")) {
                    //                   System.out.println("   Picture " + file.getName());
                    String pictureName = file.getName().replaceAll("[.]jpg$", "");
                    //                   System.out.println("   PictureName " + pictureName);
                    pictureNames.add(pictureName);
                }
            } else if (file.isDirectory()) {
                System.out.println("Directory " + file.getName());
            }
        }
        return pictureNames;
    }

    private void nextPicture() {
        if (circular) {
            currentPictureIndex++;
            if (currentPictureIndex >= pictures.size()) {
                currentPictureIndex = 0;
            }
        } else {
            currentPictureIndex = randomGenerator.nextInt(pictures.size());
        }
        currentPicture = pictures.get(currentPictureIndex);
        imagePanel.setImage(theme, currentPicture);

        String sourceLanguage = languagetrainer.LanguageTrainer.sourceLanguage;
        String translation;
        translation = translateCurrentPicture(languagetrainer.LanguageTrainer.sourceLanguage);
        name.setText(translation);
    }

    private String translateCurrentPicture(String lang) {
        // translate the name of the picture if it is not english
        String translation;
        if (lang.equals("eng")) {
            translation = currentPicture;
        } else {
            Language language = LanguageContext.get(lang);
            translation = language.translate(theme, currentPicture);
            if (translation == null) {
                translation = "no translation";
            }
        }
        return translation;
    }

    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        if (action.equals("next")) {
            nextPicture();
            // remove and create new image otherwise pack() seems to have no effect
            // need pack to resize JFrame to image
            content.remove(imagePanel);
            content.add(imagePanel);
            pack();
        }
        if (action.equals("translate")) {
            String translation;
            translation = translateCurrentPicture(languagetrainer.LanguageTrainer.targetLanguage);
            name.setText(translation);
            namePanel.revalidate();
            content.revalidate();
            this.pack();
            content.repaint();
        }
    }

    public void itemStateChanged(ItemEvent e) {

        Object source = e.getItemSelectable();

        if (source == cBox) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                circular = true;
                currentPictureIndex = pictures.size();
            }
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                circular = false;
            }
        }
    }

    public static void displayWordMaps() {
        
        // load all maps for all languages so that messages appear all together
        
        for (String theme : getPictureThemes()) {
            for (String lang : userLanguages) {
                Language language = LanguageContext.get(lang);
                language.translate(theme, "");
            }
        }

        for (String theme : getPictureThemes()) {
            for (String picture : getPictureNames(theme)) {
                MsgTextPane.write("---------------------------");
                MsgTextPane.write(theme + " " + picture);
                MsgTextPane.write("---------------------------");
                for (String lang : userLanguages) {
                    if (!lang.equals("eng")){
                    Language language = LanguageContext.get(lang);
                    MsgTextPane.write(lang + " : " + language.translate(theme, picture));
                    }
                }
            }
        }
    }

}
