import java.util.LinkedHashMap;
import java.util.ArrayList;

public abstract class BayesianNetwork {
    // maps a feature name to a node index
    public String relation;
    public ArrayList<String> classValues;
    public BayesianNode[] nodes;

    public BayesianNetwork(ARFF arff, boolean naive) {
        this.relation = arff.relation;
        this.classValues = arff.classValues;
        this.nodes = new BayesianNode[arff.featureMap.size()];

        ArrayList<String> features = new ArrayList<>(arff.featureMap.keySet());

        for (int i = 0; i < features.size(); i++) {
            String feature = features.get(i);
            if (naive) {
                nodes[i] = new NaiveBayesianNode(feature, arff.featureMap.get(feature));
            } else {
                nodes[i] = new TANNode(i, feature, arff.featureMap.get(feature));
            }
        }
    }

    public abstract int classify(Instance inst);
}
