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

    public boolean setImage(String imageFile) {
        String defaultFolder = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        String fileName = defaultFolder + "\\Tatoeba\\Images\\" + imageFile + ".jpg";
        try {
            image = ImageIO.read(new File(fileName));
            repaint();
            return true;
        } catch (IOException e) {
            System.out.println("io exception : " + fileName);
            return false;
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
        pictures = new ArrayList<>();
        pictures.add("alligator");
        pictures.add("bird");
        pictures.add("cat");
        /*       
         pictures.add("cow");
         pictures.add("dog");
         pictures.add("elephant");
         pictures.add("giraffe");
         pictures.add("horse");
         pictures.add("monkey");
         pictures.add("owl");
         pictures.add("parrot");
         pictures.add("peacock");
         pictures.add("pig");
         pictures.add("polar-bear");
         pictures.add("rabbit");
         pictures.add("shark");
         pictures.add("sheep");
         pictures.add("snail");
         pictures.add("snake");
         pictures.add("spider");
         pictures.add("squirrel");
         pictures.add("tiger");
         pictures.add("wolf");
         pictures.add("zebra");
         */
        JButton transButton = new JButton("translate");
        transButton.setActionCommand("translate");
        transButton.addActionListener(thisPictureTrainer);
        JButton nextButton = new JButton("next");
        nextButton.setActionCommand("next");
        nextButton.addActionListener(thisPictureTrainer);
        imagePanel = new ImagePanel();
        imagePanel.setImage(pictures.get(0));
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.add(imagePanel);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(transButton);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(nextButton);
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setContentPane(content);
        pack();
    }

    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        if (action.equals("next")) {
            int nr = randomGenerator.nextInt(pictures.size());
            imagePanel.setImage(pictures.get(nr));
        }
        if (action.equals("translate")) {
            String lang = languagetrainer.LanguageTrainer.targetLanguage;
            Language language = LanguageContext.get(lang);
            //           textPanel.write(language.number(currentPicture));
        }
    }

}
