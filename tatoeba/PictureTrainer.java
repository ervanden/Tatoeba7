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
    int currentPicture = 0;

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
            currentPicture++;
            if (currentPicture >= pictures.size()) {
                currentPicture = 0;
            }
        } else {
            currentPicture = randomGenerator.nextInt(pictures.size());
        }
        name.setText(pictures.get(currentPicture));
        imagePanel.setImage(theme, pictures.get(currentPicture));
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
            String lang = languagetrainer.LanguageTrainer.targetLanguage;
            Language language = LanguageContext.get(lang);
            String translatedWord = language.translate(theme, name.getText());
            if (translatedWord == null) {
                translatedWord = "no translation";
            }
            name.setText(translatedWord);

        }
    }

    public void itemStateChanged(ItemEvent e) {

        Object source = e.getItemSelectable();

        if (source == cBox) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                circular = true;
                currentPicture=pictures.size();
            }
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                circular = false;
            }
        }
    }

}
