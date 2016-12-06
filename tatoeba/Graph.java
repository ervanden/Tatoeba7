package tatoeba;

import utils.GenericTextFrame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ClusterHashMap extends HashMap<Integer, Cluster> {

    /* ClusterHashMap stores a number that is equal to (or higher then, if clusters are removed) the largest
     assigned cluster number. This is needed to create a unique cluster number when the user creates a new cluster
     */
    int maxKey = Integer.MIN_VALUE;

    public Cluster put(Integer key, Cluster c) {
        if (key > maxKey) {
            maxKey = key;
        }
        return super.put(key, c);
    }

    public Cluster remove(Integer key) {
        return super.remove(key);
    }

    public int maximumClusterNumber() {
        return maxKey;
    }
}

public class Graph {

    HashMap<Integer, Sentence> sentences = new HashMap<Integer, Sentence>();
    ClusterHashMap clusters = new ClusterHashMap();
    LanguageMatrix languageMatrix;

    public int clusterCount = 0;
    public int selectedClusterCount = 0;

    public int minComplexity;
    public int maxComplexity;

    ArrayList<Integer> complexityFreq = new ArrayList<>();
    final int MAX_COMPLEXITY = 1000;

    public int maximumClusterNumber() {
        return clusters.maximumClusterNumber();
    }

    public void addSentence(Sentence s) {
        sentences.put(s.nr, s);
    }

    public boolean addLink(int nr1, int nr2) {
        Sentence v1 = sentences.get(nr1);
        Sentence v2 = sentences.get(nr2);

        if ((v1 != null) && (v2 != null)) {
            Cluster c1 = v1.cluster;
            Cluster c2 = v2.cluster;
            if ((c1 == null) && (c2 == null)) {

                Cluster c = new Cluster();
                clusterCount++;
                c.nr = clusterCount;

                clusters.put(c.nr, c);
                c.sentences.clear();
                c.add(v1);
                c.add(v2);
                v1.cluster = c;
                v2.cluster = c;

            } else if ((c2 == null) && (c1 != null)) {

                c1.add(v2);
                v2.cluster = c1;

            } else if ((c2 != null) && (c1 == null)) {

                c2.add(v1);
                v1.cluster = c2;

            } else // c1!=null and c2!=null
            if (c1.nr != c2.nr) {

                // put vertices of c2 in c1 and remove c2
                for (Sentence v : c2.sentences) {
                    c1.add(v);
                    v.cluster = c1;
                }

                clusters.remove(c2.nr);

            }
            return true;
        }
        return false;
    }

    /* when any of the parameters in the selection Frame are changed (i.e. source/target language, tags, pattern)
     'selectClustersByParameters' sets 'selectedByParameters' but also
     calculates the complexity of these selected clusters and creates a cumulative frequency in the array 'complexityFreq'.
     countSelectedClusters() can then very quickly return the number of selected clusters when the complexity sliders are moved.
     To actually select the clusters (upon 'Apply'), selectClusters must be called.
     */
    private void calculateComplexity() {
        // complexity is the average length of the sentences in the cluster
        // only calculated for clusters already selected by language!!
        complexityFreq.clear();
        for (int i = 0; i <= MAX_COMPLEXITY; i++) {
            complexityFreq.add(i, 0);
        }

        boolean pclusters;
                        if (clusters.values().size() > 100) {
                    System.out.println("too many clusters to print");pclusters=false;
                } else {pclusters=true;}
                            
                        
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (Cluster c : clusters.values()) {
            if (c.selectedByParameters) {

                if (pclusters==true) {
                    System.out.println("cluster");
                    for (String tag : c.tags) {
                        System.out.print("\u0009");
                        System.out.print(tag);
                    }
                    System.out.println();
                    for (Sentence s : c.sentences) {
                        if (s.comment) {
                            System.out.print("[" + s.language + "]");
                        } else {
                            System.out.print(s.language);
                        }
                        System.out.print("\u0009");
                        System.out.print(s.sentence);
                        System.out.println(" [complexity="+s.sentence.length()+"]");
                    }
                }

                int complexity = 0;
                for (Sentence s : c.sentences) {
                    complexity = complexity + s.complexity;
                }
                c.complexity = complexity / c.sentences.size();
                if (c.complexity > MAX_COMPLEXITY) {
                    c.complexity = MAX_COMPLEXITY;
                }
                complexityFreq.set(c.complexity, complexityFreq.get(c.complexity) + 1);

                if (c.complexity < min) {
                    min = c.complexity;
                }
                if (c.complexity > max) {
                    max = c.complexity;
                }
            }
        }
        minComplexity = min;
        maxComplexity = max;

        // change frequency to cumulative frequency
        int count = 0;
        for (int i = 0; i <= MAX_COMPLEXITY; i++) {
            count = count + complexityFreq.get(i);
            complexityFreq.set(i, count);
        }
    }

    public void selectClustersByParameters(
            HashSet<String> sourceLanguages,
            HashSet<String> targetLanguages,
            HashSet<String> selectedTags,
            String sourcePattern,
            String targetPattern) {

        Pattern pattern;
        Matcher matcher;

        for (Cluster c : clusters.values()) {
            boolean sourceOK = false;
            boolean targetOK = false;

            if (sourceLanguages.isEmpty()) {
                sourceOK = true;
            }
            if (targetLanguages.isEmpty()) {
                targetOK = true;
            }

            for (Sentence s : c.sentences) {
                if (sourceLanguages.contains(s.language)) {
                    sourceOK = true;
                }
                if (targetLanguages.contains(s.language)) {
                    targetOK = true;
                }
            }
            c.selectedByParameters = sourceOK && targetOK;

            if (c.selectedByParameters) {
                // eliminate the cluster if there are selection tags and the cluster does not have them all
                if (!selectedTags.isEmpty()) {
                    if (!c.tags.containsAll(selectedTags)) {
                        c.selectedByParameters = false;
                    }
                }
            }

            if (c.selectedByParameters && !sourcePattern.equals("")) {
                // unselect the cluster if there is a source pattern and no source sentence matches this pattern
                pattern = Pattern.compile(sourcePattern);
                if (pattern != null) {
                    boolean match = false;
                    for (Sentence s : c.sentences) {
                        if (sourceLanguages.contains(s.language)) {
                            matcher = pattern.matcher(s.sentence);
                            if (matcher.find()) {
                                match = true;
                            }
                        }
                    }
                    if (!match) {
                        c.selectedByParameters = false;
                    }
                }
            }

            if (c.selectedByParameters && !targetPattern.equals("")) {
                // unselect the cluster if there is a target pattern and no target sentence matches this pattern
                pattern = Pattern.compile(targetPattern);
                if (pattern != null) {
                    boolean match = false;
                    for (Sentence s : c.sentences) {
                        if (targetLanguages.contains(s.language)) {
                            matcher = pattern.matcher(s.sentence);
                            if (matcher.find()) {
                                System.out.println("true");
                                match = true;
                            }
                        }
                    }
                    if (!match) {
                        c.selectedByParameters = false;
                    }
                }
            }

        }

        calculateComplexity();
    }

    public void selectClustersByComplexity(float minFraction, float maxFraction, boolean countOnly) {

        // cumulative frequency of complexity is only counting the clusters selected by language
        int cmin = Math.max(0, Math.round(maxComplexity * minFraction));
        int cmax = Math.min(MAX_COMPLEXITY, Math.round(maxComplexity * maxFraction));

        if (complexityFreq.isEmpty()) { // called before any cluster is read
            selectedClusterCount = 0;
        } else if (complexityFreq.get(MAX_COMPLEXITY) == 0) { // called with no clusters selected
            selectedClusterCount = 0;
        } else {
            
 //           System.out.println("cmin "+cmin+" cmax "+cmax);
            if (cmin == 0) {
                selectedClusterCount = complexityFreq.get(cmax);
            } else {
                selectedClusterCount = complexityFreq.get(cmax) - complexityFreq.get(cmin - 1);
            };
            if (!countOnly) {
                for (Cluster c : clusters.values()) {
                    c.selectedByComplexity = (c.complexity <= cmax) && (c.complexity >= cmin);
                }
            }
        }
    }

    public void selectClusters() {
        // selectClustersByLanguage and selectClustersByComplexity are supposed to be called already
        for (Cluster c : clusters.values()) {
            c.selected = c.selectedByParameters && c.selectedByComplexity;
        }
    }

    public void displayClusters(GenericTextFrame frame, String which, SelectionFrame selectionFrame) {
        frame.erase();
        for (Cluster c : clusters.values()) {
            if ((which.equals("selected") && c.selected)
                    || (which.equals("unsaved") && c.unsaved)) {
                for (Sentence s : c.sentences) {
                    if (selectionFrame.sourceLanguages.contains(s.language)) {
                        frame.write(s.language + ">  " + s.sentence);
                    }
                }
                for (Sentence s : c.sentences) {
                    if (selectionFrame.targetLanguages.contains(s.language)) {
                        frame.write(s.language + ">  " + s.sentence);
                    }
                }
                frame.write("");
            }
        }
    }

    public int unsavedClusters() {
        int i = 0;
        for (Cluster c : clusters.values()) {
            if (c.unsaved) {
                i++;
            }
        }
        return i;
    }

    class LanguageMatrix {

        HashMap<String, Integer> ll = new HashMap<>();

        public int value(String language1, String language2) {
            String key = language1 + ":" + language2;
            Integer i = ll.get(key);
            if (i == null) {
                return 0;
            } else {
                return i;
            }
        }

        public void increment(String language1, String language2) {
            String key1 = language1 + ":" + language2;
            Integer value;

            value = ll.get(key1);
            if (value == null) {
                value = 0;
            }
            //           System.out.println(key1+" "+(value+1));
            ll.put(key1, value + 1);
        }

        public void generate() {

            ArrayList<String> languages = new ArrayList<>();

            ll.clear();

            for (Cluster c : clusters.values()) {
                languages.clear();
                for (Sentence s : c.sentences) {
                    if (!languages.contains(s.language)) {
                        languages.add(s.language);
                    }
                }

                for (String l1 : languages) {
                    for (String l2 : languages) {
                        increment(l1, l2);
                    }
                }

            }
        }

    }

    public void generateLanguageMatrix() {
        languageMatrix = new LanguageMatrix();
    }

}
