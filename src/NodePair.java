
public class NodePair implements Comparable<NodePair> {
    public TANNode i;
    public TANNode j;
    public int total;
    public int[][][] counts;
    public int[] classCounts;
    public double informationGain;

    public NodePair(TANNode i, TANNode j) {
        this.i = i;
        this.j = j;
        this.counts = new int[Bayes.NUM_CLASSES][i.featureValueMap.size()][j.featureValueMap.size()];
        this.classCounts = new int[Bayes.NUM_CLASSES];
        this.informationGain = 0;
    }

    public void increment(int classification, String fi, String fj) {
        ++counts[classification][i.featureValueMap.get(fi)][j.featureValueMap.get(fj)];
        ++classCounts[classification];
        ++total;
    }

    public void computeInformationGain() {
        informationGain = 0;
        for (int k = 0; k < Bayes.NUM_CLASSES; k++) {
            for (int i = 0; i < counts[k].length; i++) {
                for (int j = 0; j < counts[k][i].length; j++) {
                }
            }
        }
        for (int k = 0; k < counts.length; k++) {
            for (int i = 0; i < counts[k].length; i++) {
                for (int j = 0; j < counts[k][i].length; j++) {
                    double pIJK = (double) (counts[k][i][j] + 1) / (total + counts.length * counts[k].length * counts[k][i].length);
                    double pIJgivenK = doubleConditional(k, i, j);
                    double pIgivenK = conditional(k, i, -1);
                    double pJgivenK = conditional(k, -1, j);
                    informationGain += pIJK * Math.log(pIJgivenK / pIgivenK / pJgivenK) / Math.log(2);
                }
            }
        }
    }

    public double conditional(int classification, int i, int j) {
        double conditional = 0;
        if (i == -1) {
            // sum out i
            for (int k = 0; k < counts[classification].length; k++) {
                conditional += counts[classification][k][j];
            }
            return (conditional + 1) / (classCounts[classification] + counts[classification][0].length);
        } else {
            // sum out j
            for (int k = 0; k < counts[classification][i].length; k++) {
                conditional += counts[classification][i][k];
            }
            return (conditional + 1) / (classCounts[classification] + counts[classification].length);
        }

    }

    public double doubleConditional(int classification, int i, int j) {
        return (double) (counts[classification][i][j] + 1) / (classCounts[classification] + counts[classification].length * counts[classification][0].length);
    }

    @Override
    public int compareTo(NodePair arg0) {
        return (int) Math.signum(arg0.informationGain - this.informationGain);
    }
}

