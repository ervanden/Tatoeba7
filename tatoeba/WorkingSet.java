package tatoeba;

import java.util.ArrayList;
import java.util.Collections;
import utils.*;

public class WorkingSet {

    static ArrayList<Cluster> workingSet = new ArrayList<Cluster>();
    static int nextInWorkingSet = -1;

    public static int size() {
        return workingSet.size();
    }

    /*
    public static void clear() {
        workingSet.clear();
    }
*/
    
    public static void build() { 
        workingSet.clear();
        if (Graph.selectedClusterCount < 1) {
            SelectionFrame.setVisible(true);
        }
        if (Graph.selectedClusterCount < 1) {
            MsgTextPane.write("No clusters selected.");
        }

        for (Cluster c : Graph.clusters.values()) {
            if (c.selected) workingSet.add(c);
        }
        
        Collections.shuffle(workingSet);
        nextInWorkingSet = -1;
    }


    public static Cluster pickCluster() {

        // circular
        
        if (workingSet.isEmpty()) {
            MsgTextPane.write("Working Set is empty");
            return null;
        }

        nextInWorkingSet++;
        if (nextInWorkingSet >= WorkingSet.size()) {
            nextInWorkingSet = 0;
        }
        return workingSet.get(nextInWorkingSet);

    }
    
    public static String pickedClusterToString(){
        return (nextInWorkingSet+1) + "/" + size();
    }

}
