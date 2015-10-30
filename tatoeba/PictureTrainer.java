package tatoeba;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import languages.Language;
import languages.LanguageContext;

class ImagePanel extends JPanel {

    BufferedImage image;

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }
    }

    public Dimension setImage(String imageFile) {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        String fileName = defaultFolder + "\\Tatoeba\\Images\\" + imageFile + ".jpg";
        try {
            image = ImageIO.read(new File(fileName));
            int width = image.getWidth();
            int height = image.getHeight();
            System.out.println("image w=" + width + "  h=" + height);
            setPreferredSize(new Dimension(width, height));
            setMinimumSize(new Dimension(width, height));
            repaint();
            return new Dimension(width, height);
        } catch (IOException e) {
            System.out.println("io exception : " + fileName);
            return null;
        }
    }

}

public class PictureTrainer extends JFrame implements ActionListener {

    PictureTrainer thisPictureTrainer = this;
    JFrame thisFrame = (JFrame) this;
    JPanel content = new JPanel();
    ImagePanel imagePanel;
    ArrayList<String> pictures;
    Random randomGenerator = new Random();

    public PictureTrainer() {

        pictures = getPictureNames();

        JButton transButton = new JButton("translate");
        transButton.setActionCommand("translate");
        transButton.addActionListener(thisPictureTrainer);
        JButton nextButton = new JButton("next");
        nextButton.setActionCommand("next");
        nextButton.addActionListener(thisPictureTrainer);
        imagePanel = new ImagePanel();
        imagePanel.setImage(pictures.get(0));
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(transButton);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(nextButton);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(imagePanel);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setContentPane(content);
        pack();
    }

    private ArrayList<String> getPictureNames() {
        ArrayList<String> pictureNames;
        pictureNames = new ArrayList<>();
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        String folderName = defaultFolder + "\\Tatoeba\\Images\\";
        File folder = new File(folderName);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println("File " + file.getName());
                if (file.getName().matches(".*[.]jpg")) {
                    System.out.println("   Picture " + file.getName());
                    String pictureName = file.getName().replaceAll("[.]jpg$", "");
                    System.out.println("   PictureName " + pictureName);
                    pictureNames.add(pictureName);
                }
            } else if (file.isDirectory()) {
                System.out.println("Directory " + file.getName());
            }
        }
        return pictureNames;
    }

    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        if (action.equals("next")) {
            Dimension imageDimension;
            int nr = randomGenerator.nextInt(pictures.size());
            imageDimension = imagePanel.setImage(pictures.get(nr));
            if (imageDimension != null) {
                content.remove(imagePanel);
  //              System.out.println("setbounds " + imageDimension.getWidth() + " " + (int) imageDimension.getHeight());
  //              setBounds(0, 0, (int) imageDimension.getWidth(), (int) imageDimension.getHeight());
                content.remove(imagePanel);
                imagePanel = new ImagePanel();
                imagePanel.setImage(pictures.get(nr));
                content.add(imagePanel);
                pack();
            }
        }
        if (action.equals("translate")) {
            String lang = languagetrainer.LanguageTrainer.targetLanguage;
            Language language = LanguageContext.get(lang);
            //           textPanel.write(language.number(currentPicture));
        }
    }

}
