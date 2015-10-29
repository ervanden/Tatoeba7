package tatoeba;

import languages.LanguageNames;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JFrame;

class lunchCanvas extends Canvas {

    ArrayList<String> shortLanguageNames;
    ArrayList<String> longLanguageNames;

    int xorig = 0;
    int yorig = 0;
    
    public lunchCanvas(SelectionFrame selectionFrame){
        System.out.println("used languages : "+selectionFrame.usedLanguages.size());
          shortLanguageNames = new ArrayList<>(selectionFrame.usedLanguages);
        longLanguageNames = new ArrayList<>();

        for (String s : shortLanguageNames) {
            longLanguageNames.add(LanguageNames.shortToLong(s));
        }
                System.out.println("long languages : "+longLanguageNames.size());
        Collections.sort(longLanguageNames);  
    }

    public void setVisible(boolean b) { 
        super.setVisible(b);
    }

    public void setOrigin(int x, int y) {
        xorig = xorig + x;
        yorig = yorig + y;
        if (xorig > 0) {
            xorig = 0;
        }
        if (yorig > 0) {
            yorig = 0;
        }
    }

    public void blitPaint() {
        int h = this.getSize().height;
        int w = this.getSize().width;

        BufferStrategy strategy = this.getBufferStrategy();
        Graphics g = strategy.getDrawGraphics();
        g.clearRect(0, 0, w, h);

        int sw;
        int sh;

        Font font = g.getFont();
        int fontSize = font.getSize();
        Font namesFont = new Font("TimesRoman", Font.PLAIN, 2 * fontSize);
        Font valuesFont = new Font("TimesRoman", Font.PLAIN, fontSize);

        // determine dimensions of longest string
        FontMetrics fm = g.getFontMetrics(namesFont);
        sh = fm.getHeight();

        sw = 0;
        for (String s : longLanguageNames) {
            int width = fm.stringWidth(s);
            if (width > sw) {
                sw = width;
            }
        }

        // draw vertical names (increasing y)
        g.setFont(namesFont);
        int x, y;
        int verticalFirst = 1;
        int horizontalFirst = 1;

        x = 0;
        y = yorig + sw;
        verticalFirst = 1;
        for (String s : longLanguageNames) {
            y = y + sh;
            if (y >= (sw + sh)) {
                g.drawString(s, x, y);
            } else {
                verticalFirst++;
            }
        }

        Graphics2D g2 = (Graphics2D) g;
        AffineTransform orig = g2.getTransform();
        g2.rotate(-Math.PI / 2, 0, 0);

        x = -sw;
        y = xorig + sw;
        horizontalFirst = 1;
        for (String s : longLanguageNames) {
            y = y + sh;
            if (y >= (sw + sh)) {
                g.drawString(s, x, y);
            } else {
                horizontalFirst++;
            }
        }
        g2.setTransform(orig);

        //       System.out.println("vertical starts at vertical: "+languages.get(verticalFirst)+" horizontal: "+languages.get(horizontalFirst));
        g.setFont(valuesFont);
        for (int i = 1; i <= longLanguageNames.size(); i++) {
            for (int j = 1; j <= longLanguageNames.size(); j++) {
                x = xorig + sw + i * sh - sh / 2;
                y = yorig + sw + j * sh;
                String lang1 = longLanguageNames.get(i - 1);
                String lang2 = longLanguageNames.get(j - 1);
                lang1 = LanguageNames.longToShort(lang1);
                lang2 = LanguageNames.longToShort(lang2);

                int value = Graph.LanguageMatrix.value(lang1, lang2);
                if ((i >= horizontalFirst) && (j >= verticalFirst)) {
                    g.drawString(value + "", x, y);
                }
            }
        }

        g.dispose();
        strategy.show();

    }  // paint 
}

class ClusterCountFrame extends ComponentAdapter implements MouseMotionListener, MouseListener, WindowListener {

    static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    static GraphicsDevice gd = ge.getDefaultScreenDevice();
    static GraphicsConfiguration gc = gd.getDefaultConfiguration();

    static JFrame lunchFrame = new JFrame();
    lunchCanvas lunchPlane=null;

    boolean isInitialized = false;

    int mousePrevX = 0;
    int mousePrevY = 0;

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setVisible(boolean b) {
        lunchFrame.setVisible(b);
    }

    public void componentResized(ComponentEvent e) {
        lunchPlane.blitPaint();
    }

    public void componentShown(ComponentEvent e) {
        lunchPlane.blitPaint();
    }

    public void componentMoved(ComponentEvent e) {
        lunchPlane.blitPaint();
    }

    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        //       System.out.println("Mouse moved "+x+" "+y);

    }

    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        int deltaX = x - mousePrevX;
        int deltaY = y - mousePrevY;
        mousePrevX = x;
        mousePrevY = y;
//        System.out.println("Mouse dragged " + deltaX + " " + deltaY);
        lunchPlane.setOrigin(deltaX, deltaY);
        lunchPlane.blitPaint();
    }

    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
//        System.out.println("Mouse pressed " + x + " " + y);
        mousePrevX = x;
        mousePrevY = y;

    }

    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        //       System.out.println("Mouse released " + x + " " + y);
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) {

    }

    public void windowClosing(WindowEvent e) {
        setVisible(false);
    }

    public void windowClosed(WindowEvent e) {
//        DocUtils.writeMsg("WindowListener method called: windowClosed.");
    }

    public void windowOpened(WindowEvent e) {
//        DocUtils.writeMsg("WindowListener method called: windowOpened.");
        lunchPlane.blitPaint();
    }

    public void windowIconified(WindowEvent e) {
//        DocUtils.writeMsg("WindowListener method called: windowIconified.");
    }

    public void windowDeiconified(WindowEvent e) {
//        DocUtils.writeMsg("WindowListener method called: windowDeiconified.");
        lunchPlane.blitPaint();
    }

    public void windowActivated(WindowEvent e) {
//        DocUtils.writeMsg("WindowListener method called: windowActivated.");
    }

    public void windowDeactivated(WindowEvent e) {
//        DocUtils.writeMsg("WindowListener method called: windowDeactivated.");
    }

    private void initialize() {   // Create and set up the windows.

        Rectangle bounds = gc.getBounds(); // device coordinates of the screen (0,0) = upper left (w,h) = lo right
        
//lunchPlane = new lunchCanvas();

        lunchFrame.setLocation(bounds.width / 2, bounds.height / 3);
        lunchFrame.setSize(bounds.width / 2 - 22, bounds.width / 2);

        lunchPlane.setBackground(Color.white);
        lunchPlane.addMouseListener(this);
        lunchPlane.addMouseMotionListener(this);

        lunchFrame.add(lunchPlane);
        lunchFrame.setVisible(true);
        lunchPlane.createBufferStrategy(2);
        lunchPlane.setIgnoreRepaint(true);
        lunchFrame.pack();
        lunchFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        lunchFrame.addWindowListener(this);
        lunchFrame.addComponentListener(this);

        isInitialized = true;
    }

    public void display() {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initialize();
            }
        });

    }

}
