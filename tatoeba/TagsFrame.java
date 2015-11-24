package tatoeba;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TagsFrame extends JFrame implements ActionListener {

    TagsFrame thisTagsFrame = this;
    JFrame thisFrame = (JFrame) this;
    JPanel content = new JPanel();
    JPanel topPanel;

    JButton addButton;
    JButton removeButton;
    JTextField newTagField;
    Cluster cluster;
    SelectionFrame selectionFrame;

    public TagsFrame(Cluster c, SelectionFrame s) {

        cluster = c;
        selectionFrame = s;

        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));

        Iterator iterator = selectionFrame.allTags.iterator();
        while (iterator.hasNext()) {
            String tag = (String) iterator.next();
            System.out.println("Value: " + tag + " ");
            topPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            JButton button = new JButton(tag);
            button.setActionCommand("+" + tag);
            button.addActionListener(thisTagsFrame);
            topPanel.add(button);
        }
        addButton = new JButton("+");
        addButton.setActionCommand("add");
        addButton.addActionListener(thisTagsFrame);
        newTagField = new JTextField("enter new tag");
        topPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        topPanel.add(addButton);
        topPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        topPanel.add(newTagField);

        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.add(topPanel);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setContentPane(content);
        pack();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        if (action.equals("add")) {
            String newTag = newTagField.getText();
            System.out.println("add " + newTag);
            cluster.tags.add(newTag);
            selectionFrame.allTags.add(newTag);
        }
 //       System.out.println("split " + action.substring(0, 1) + "-" + action.substring(1));
        if (action.substring(0, 1).equals("+")) {
            String newTag = action.substring(1);
            System.out.println("add " + newTag);
            cluster.tags.add(newTag);
        }

    }

}
