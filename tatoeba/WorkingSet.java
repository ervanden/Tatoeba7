package tatoeba;

import java.util.ArrayList;
import java.util.Collections;
import utils.*;

public class WorkingSet {

     ArrayList<Cluster> workingSet;
     int nextInWorkingSet;
    SelectionFrame selectionFrame;

    public WorkingSet(SelectionFrame s){
      workingSet = new ArrayList<Cluster>();
      nextInWorkingSet = -1;
      selectionFrame=s;
    }
    
    public  int size() {
        return workingSet.size();
    }
 
    public  void build() { 
        workingSet.clear();
        if (Graph.selectedClusterCount < 1) {
            selectionFrame.setVisible(true);
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


    public  Cluster pickCluster() {

        // circular
        
        if (workingSet.isEmpty()) {
            MsgTextPane.write("Working Set is empty");
            return null;
        }

        nextInWorkingSet++;
        if (nextInWorkingSet >= size()) {
            nextInWorkingSet = 0;
        }
        return workingSet.get(nextInWorkingSet);

    }
    
    public  String pickedClusterToString(){
        return (nextInWorkingSet+1) + "/" + size();
    }

}
