package langeditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import utils.MsgTextPane;

public class ConfirmDialog implements ActionListener, PropertyChangeListener {

    JFrame owningFrame;
    JDialog dialog;
    public boolean confirm;

    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        MsgTextPane.write(" property changed: " + prop);
    }

    public void actionPerformed(ActionEvent ae) {
        String lastButtonClicked = ae.getActionCommand();
        if (ae.getActionCommand().equals("Yes")) {
            confirm = true;
        } else if (ae.getActionCommand().equals("No")) {
            confirm = false;
        }
        dialog.dispose();
    }

    public void popUp(JFrame f, String message, String yesButton, String noButton) {
        owningFrame = f;

        JLabel label = new JLabel(message);

        Object[] array = {label};

        JButton btnEnter = new JButton(yesButton);
        JButton btnReset = new JButton(noButton);
        btnEnter.addActionListener(this);
        btnEnter.setActionCommand("Yes");
        btnReset.addActionListener(this);
        btnReset.setActionCommand("No");

        Object[] options = {btnEnter, btnReset};

        //Create the JOptionPane.
        JOptionPane optionPane = new JOptionPane(array,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                null,
                options,
                null);

        dialog = new JDialog(owningFrame, "Grid Parameters", true);
        dialog.setContentPane(optionPane);
        dialog.pack();
        dialog.setVisible(true);
    }
}
