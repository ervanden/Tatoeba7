package utils;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GenericTextFrame extends JFrame {

    private JFrame thisFrame = (JFrame) this;
    GenericTextPanel textPanel = new GenericTextPanel(800,600);

    public GenericTextFrame() {
        JPanel content = new JPanel();
        BorderLayout borderLayout = new BorderLayout();
        content.setLayout(borderLayout);
        content.add(textPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setContentPane(content);
        pack();
    }

    public void erase() {
        textPanel.erase();
    }

    public void write(String msg) {
       textPanel.write(msg);
    }
}
