package tatoeba;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import languages.Language;
import languages.LanguageContext;

class NoFocusButton extends JButton {

    public NoFocusButton(String s) {
        super(s);
    }

    public boolean isRequestFocusEnabled() {
        return false;
    }

    public boolean isFocusTraversable() {
        return false;
    }
}

public class ColorTrainer extends JFrame implements ActionListener, ChangeListener {

    ColorTrainer thisColorTrainer = this;
    JFrame thisFrame = (JFrame) this;
    ArrayList<NamedColor> allColors = new ArrayList<>();

    JPanel content = new JPanel();
    Random randomGenerator = new Random();
    NoFocusButton colorButton;
    JColorChooser tcc = new JColorChooser();
    JLabel name;
    int currentIndex;
    String newColorName;
    String newColorTranslatedName;
    boolean nameIsDisplayed = true;

    class NamedColor {

        String name;
        int r;
        int g;
        int b;

        public NamedColor(String colorName, int red, int green, int blue) {
            name = colorName;
            r = red;
            b = blue;
            g = green;

        }
    }

    public ColorTrainer() {

        allColors.add(new NamedColor("white", 255, 255, 255));
        allColors.add(new NamedColor("black", 0, 0, 0));
        allColors.add(new NamedColor("brown", 153, 76, 0));
        allColors.add(new NamedColor("red", 255, 0, 0));
        allColors.add(new NamedColor("salmon", 250, 128, 114));
        allColors.add(new NamedColor("orange", 255, 165, 0));
        allColors.add(new NamedColor("yellow", 255, 255, 0));
        allColors.add(new NamedColor("olive", 128, 128, 0));
        allColors.add(new NamedColor("green", 0, 128, 0));
        allColors.add(new NamedColor("light blue", 173, 216, 230));
        allColors.add(new NamedColor("turquoise", 64, 224, 208));
        allColors.add(new NamedColor("dark blue", 0, 0, 139));
        allColors.add(new NamedColor("blue", 0, 0, 255));
        allColors.add(new NamedColor("purple", 128, 0, 128));
        allColors.add(new NamedColor("violet", 238, 130, 238));
        allColors.add(new NamedColor("magenta / fuchsia", 255, 0, 255));
        allColors.add(new NamedColor("beige", 245, 245, 220));
        allColors.add(new NamedColor("pink", 255, 192, 203));
        allColors.add(new NamedColor("ivory", 255, 255, 240));
        allColors.add(new NamedColor("black", 0, 0, 0));
        allColors.add(new NamedColor("grey", 128, 128, 128));
        allColors.add(new NamedColor("silver", 192, 192, 192));
        allColors.add(new NamedColor("white", 255, 255, 255));

//       allColors.add(new NamedColor("maroon", 128, 0, 0));
//       allColors.add(new NamedColor("dark red", 139, 0, 0));
//       allColors.add(new NamedColor("firebrick", 178, 34, 34));
//       allColors.add(new NamedColor("crimson", 220, 20, 60));
//       allColors.add(new NamedColor("tomato", 255, 99, 71));
//       allColors.add(new NamedColor("coral", 255, 127, 80));
//       allColors.add(new NamedColor("indian red", 205, 92, 92));
//       allColors.add(new NamedColor("light coral", 240, 128, 128));
//       allColors.add(new NamedColor("dark salmon", 233, 150, 122));
//       allColors.add(new NamedColor("light salmon", 255, 160, 122));
//       allColors.add(new NamedColor("orange red", 255, 69, 0));
//       allColors.add(new NamedColor("dark orange", 255, 140, 0));
//       allColors.add(new NamedColor("dark golden rod", 184, 134, 11));
//       allColors.add(new NamedColor("golden rod", 218, 165, 32));
//       allColors.add(new NamedColor("pale golden rod", 238, 232, 170));
//       allColors.add(new NamedColor("dark khaki", 189, 183, 107));
//       allColors.add(new NamedColor("sea green", 46, 139, 87));
//       allColors.add(new NamedColor("cyan", 0, 255, 255));        
//        allColors.add(new NamedColor("khaki", 240, 230, 140));
//        allColors.add(new NamedColor("yellow green", 154, 205, 50));
//        allColors.add(new NamedColor("dark olive green", 85, 107, 47));
//        allColors.add(new NamedColor("olive drab", 107, 142, 35));
//        allColors.add(new NamedColor("lawn green", 124, 252, 0));
//        allColors.add(new NamedColor("chart reuse", 127, 255, 0));
//        allColors.add(new NamedColor("green yellow", 173, 255, 47));
//        allColors.add(new NamedColor("dark green", 0, 100, 0));
//        allColors.add(new NamedColor("forest green", 34, 139, 34));
//        allColors.add(new NamedColor("lime", 0, 255, 0));
//        allColors.add(new NamedColor("lime green", 50, 205, 50));
//        allColors.add(new NamedColor("light green", 144, 238, 144));
//        allColors.add(new NamedColor("pale green", 152, 251, 152));
//        allColors.add(new NamedColor("dark sea green", 143, 188, 143));
//        allColors.add(new NamedColor("medium spring green", 0, 250, 154));
//        allColors.add(new NamedColor("spring green", 0, 255, 127));
//        allColors.add(new NamedColor("medium aqua marine", 102, 205, 170));
//        allColors.add(new NamedColor("medium sea green", 60, 179, 113));
//        allColors.add(new NamedColor("light sea green", 32, 178, 170));
//        allColors.add(new NamedColor("dark slate gray", 47, 79, 79));
//        allColors.add(new NamedColor("teal", 0, 128, 128));
//        allColors.add(new NamedColor("dark cyan", 0, 139, 139));
//        allColors.add(new NamedColor("aqua", 0, 255, 255));
//        allColors.add(new NamedColor("light cyan", 224, 255, 255));
//        allColors.add(new NamedColor("dark turquoise", 0, 206, 209));
//        allColors.add(new NamedColor("medium turquoise", 72, 209, 204));
//        allColors.add(new NamedColor("pale turquoise", 175, 238, 238));
//        allColors.add(new NamedColor("aqua marine", 127, 255, 212));
//        allColors.add(new NamedColor("powder blue", 176, 224, 230));
//        allColors.add(new NamedColor("cadet blue", 95, 158, 160));
//        allColors.add(new NamedColor("steel blue", 70, 130, 180));
//        allColors.add(new NamedColor("corn flower blue", 100, 149, 237));
//        allColors.add(new NamedColor("deep sky blue", 0, 191, 255));
//        allColors.add(new NamedColor("dodger blue", 30, 144, 255));
//        allColors.add(new NamedColor("sky blue", 135, 206, 235));
//        allColors.add(new NamedColor("light sky blue", 135, 206, 250));
//        allColors.add(new NamedColor("midnight blue", 25, 25, 112));
//        allColors.add(new NamedColor("navy", 0, 0, 128));
//        allColors.add(new NamedColor("medium blue", 0, 0, 205));
//        allColors.add(new NamedColor("royal blue", 65, 105, 225));
//        allColors.add(new NamedColor("blue violet", 138, 43, 226));
//        allColors.add(new NamedColor("indigo", 75, 0, 130));
//        allColors.add(new NamedColor("dark slate blue", 72, 61, 139));
//        allColors.add(new NamedColor("slate blue", 106, 90, 205));
//        allColors.add(new NamedColor("medium slate blue", 123, 104, 238));
//        allColors.add(new NamedColor("medium purple", 147, 112, 219));
//        allColors.add(new NamedColor("dark magenta", 139, 0, 139));
//        allColors.add(new NamedColor("dark violet", 148, 0, 211));
//        allColors.add(new NamedColor("dark orchid", 153, 50, 204));
//        allColors.add(new NamedColor("medium orchid", 186, 85, 211));
//        allColors.add(new NamedColor("thistle", 216, 191, 216));
//        allColors.add(new NamedColor("plum", 221, 160, 221));
//        allColors.add(new NamedColor("orchid", 218, 112, 214));
//        allColors.add(new NamedColor("medium violet red", 199, 21, 133));
//        allColors.add(new NamedColor("pale violet red", 219, 112, 147));
//        allColors.add(new NamedColor("deep pink", 255, 20, 147));
//        allColors.add(new NamedColor("hot pink", 255, 105, 180));
//        allColors.add(new NamedColor("light pink", 255, 182, 193));
//        allColors.add(new NamedColor("antique white", 250, 235, 215));
//        allColors.add(new NamedColor("bisque", 255, 228, 196));
//        allColors.add(new NamedColor("blanched almond", 255, 235, 205));
//        allColors.add(new NamedColor("wheat", 245, 222, 179));
//        allColors.add(new NamedColor("corn silk", 255, 248, 220));
//        allColors.add(new NamedColor("lemon chiffon", 255, 250, 205));
//        allColors.add(new NamedColor("light golden rod yellow", 250, 250, 210));
//        allColors.add(new NamedColor("light yellow", 255, 255, 224));
//        allColors.add(new NamedColor("saddle brown", 139, 69, 19));
//        allColors.add(new NamedColor("sienna", 160, 82, 45));
//        allColors.add(new NamedColor("chocolate", 210, 105, 30));
//        allColors.add(new NamedColor("peru", 205, 133, 63));
//        allColors.add(new NamedColor("sandy brown", 244, 164, 96));
//        allColors.add(new NamedColor("burly wood", 222, 184, 135));
//        allColors.add(new NamedColor("tan", 210, 180, 140));
//        allColors.add(new NamedColor("rosy brown", 188, 143, 143));
//        allColors.add(new NamedColor("moccasin", 255, 228, 181));
//        allColors.add(new NamedColor("navajo white", 255, 222, 173));
//        allColors.add(new NamedColor("peach puff", 255, 218, 185));
//        allColors.add(new NamedColor("misty rose", 255, 228, 225));
//        allColors.add(new NamedColor("lavender blush", 255, 240, 245));
//        allColors.add(new NamedColor("linen", 250, 240, 230));
//        allColors.add(new NamedColor("old lace", 253, 245, 230));
//        allColors.add(new NamedColor("papaya whip", 255, 239, 213));
//        allColors.add(new NamedColor("sea shell", 255, 245, 238));
//        allColors.add(new NamedColor("mint cream", 245, 255, 250));
//        allColors.add(new NamedColor("slate gray", 112, 128, 144));
//        allColors.add(new NamedColor("light slate gray", 119, 136, 153));
//        allColors.add(new NamedColor("light steel blue", 176, 196, 222));
//        allColors.add(new NamedColor("lavender", 230, 230, 250));
//        allColors.add(new NamedColor("floral white", 255, 250, 240));
//        allColors.add(new NamedColor("alice blue", 240, 248, 255));
//        allColors.add(new NamedColor("ghost white", 248, 248, 255));
//        allColors.add(new NamedColor("honeydew", 240, 255, 240));
//        allColors.add(new NamedColor("azure", 240, 255, 255));
//        allColors.add(new NamedColor("snow", 255, 250, 250));
//        allColors.add(new NamedColor("dim gray / dim grey", 105, 105, 105));
//        allColors.add(new NamedColor("dark gray / dark grey", 169, 169, 169));
//        allColors.add(new NamedColor("light gray / light grey", 211, 211, 211));
//        allColors.add(new NamedColor("gainsboro", 220, 220, 220));
//        allColors.add(new NamedColor("white smoke", 245, 245, 245));
        colorButton = new NoFocusButton("");
        colorButton.setActionCommand("next");
        colorButton.addActionListener(thisColorTrainer);
        name = new JLabel("click on the button");
        Font font = new Font("Courier", Font.BOLD, 23);
        name.setFont(font);
        colorButton.setFont(font);

        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

        colorButton.setMaximumSize(new Dimension(10000, 10000));
        colorButton.setPreferredSize(new Dimension(300, 300));
        colorButton.setAlignmentX(0.5f);
//      content.add(tcc);
        content.add(colorButton);
        content.add(name);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setContentPane(content);
        pack();
    }

    public void stateChanged(ChangeEvent e) {
        Color newColor = tcc.getColor();
        colorButton.setBackground(newColor);
    }

    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        if (action.equals("next")) {
            if (nameIsDisplayed) {  // pick a new color and paint the button in this color
                int index, r, g, b;
                do {
                    index = randomGenerator.nextInt(allColors.size());
                    System.out.println("current color = " + currentIndex + " new = " + index);
                    r = allColors.get(index).r;
                    g = allColors.get(index).g;
                    b = allColors.get(index).b;
                    colorButton.setBackground(new Color(r, g, b));
                    newColorName = allColors.get(index).name;
                    String lang = languagetrainer.LanguageTrainer.targetLanguage;
                    Language language = LanguageContext.get(lang);
                    newColorTranslatedName = language.color(newColorName);
                } while (index == currentIndex);  // skip if the same as the previous color
                System.out.println("selected new color = " + index + " [" + (r + g + b) + "]");
                currentIndex = index;
                colorButton.setText(newColorName);
                if ((r + g + b) < 256) {
                    colorButton.setForeground(Color.white);

                } else {
                    colorButton.setForeground(Color.black);
                }
                name.setText("");
                nameIsDisplayed = false;
            } else {
                // translate the name again in case the target language has changed
                String lang = languagetrainer.LanguageTrainer.targetLanguage;
                Language language = LanguageContext.get(lang);
                newColorTranslatedName = language.color(newColorName);

                name.setText(newColorTranslatedName);
                nameIsDisplayed = true;
            }

        }
    }

}
