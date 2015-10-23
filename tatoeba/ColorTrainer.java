/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

public class ColorTrainer extends JFrame implements ActionListener, ChangeListener {

    ColorTrainer thisColorTrainer = this;
    JFrame thisFrame = (JFrame) this;
            ArrayList<NamedColor> allColors = new ArrayList<>();

    JPanel content = new JPanel();
    Random randomGenerator = new Random();
    JButton colorButton;
    JColorChooser tcc = new JColorChooser();

    class NamedColor {

        String name;
        int r;
        int g;
        int b;

        public NamedColor(String colorName, int red, int blue, int green) {
            name = colorName;
            r = red;
            b = blue;
            g = green;

        }
    }

    public ColorTrainer() {


        allColors.add(new NamedColor("black",255,255,255));
        allColors.add(new NamedColor("white",0,0,0));
         allColors.add(new NamedColor("red",255,0,0));        
         allColors.add(new NamedColor("green",0,255,0));        
         allColors.add(new NamedColor("blue",0,0,255));       
        
        
        /*
         black
         blue
         cyan
         darkGray
         gray
         green
         lightGray
         magenta
         orange
         pink
         red
         white
         yellow
         */

        colorButton = new JButton("");
        colorButton.setActionCommand("next");
        colorButton.addActionListener(thisColorTrainer);
        JLabel name = new JLabel("click on the button");
        Font font = new Font("Courier", Font.BOLD, 23);
        name.setFont(font);

        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

        colorButton.setMaximumSize(new Dimension(10000, 10000));
        colorButton.setPreferredSize(new Dimension(300, 300));
        colorButton.setAlignmentX(0.5f);
        content.add(tcc);
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
        System.out.println(action);
        if (action.equals("next")) {
            int i = randomGenerator.nextInt(allColors.size()-1);
            int r = allColors.get(i).r;
            int g = allColors.get(i).g;            
            int b = allColors.get(i).b;
            colorButton.setBackground(new Color(r,g,b));
        }
    }

}
