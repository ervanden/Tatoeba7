package tatoeba;

import java.util.ArrayList;
import java.util.Collections;
import utils.*;

public class WorkingSet {

     ArrayList<Cluster> workingSet;
     int nextInWorkingSet;
     TatoebaFrame tatoebaFrame;

    public WorkingSet(TatoebaFrame t){
      workingSet = new ArrayList<Cluster>();
      nextInWorkingSet = -1;
      tatoebaFrame=t;
    }
    
    public  int size() {
        return workingSet.size();
    }
 
    public  void build() { 
        workingSet.clear();
        if (tatoebaFrame.graph.selectedClusterCount < 1) {
            tatoebaFrame.selectionFrame.setVisible(true);
        }
        if (tatoebaFrame.graph.selectedClusterCount < 1) {
            MsgTextPane.write("No clusters selected.");
        }

        for (Cluster c : tatoebaFrame.graph.clusters.values()) {
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
