import java.util.ArrayList;
import java.util.LinkedHashMap;

public abstract class BayesianNode {
    // feature of this node
    public String feature;
    // maps a feature name to its index
    public LinkedHashMap<String, Integer> featureValueMap;

    public BayesianNode(String feature, ArrayList<String> featureValues) {
        this.feature = feature;
        this.featureValueMap = new LinkedHashMap<>();
        // map feature values to indices
        for (int i = 0; i < featureValues.size(); i++) {
            featureValueMap.put(featureValues.get(i), i);
        }
    }
}
