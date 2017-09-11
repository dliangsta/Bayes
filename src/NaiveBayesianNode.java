import java.util.ArrayList;

public class NaiveBayesianNode extends BayesianNode
{
   // total number of trainng instances
   public int total;
   // each array for each class value and each value of this node's feature
   public int[][] totals;

   public int[] classCounts;
   // computed laplace probabilities
   public double[][] probabilities;

   public NaiveBayesianNode(String feature, ArrayList<String> featureValues)
   {
      super(feature, featureValues);
      this.total = 0;
      this.totals = new int[Bayes.NUM_CLASSES][featureValues.size()];
      this.classCounts = new int[Bayes.NUM_CLASSES];
      this.probabilities = new double[Bayes.NUM_CLASSES][featureValues.size()];
   }

   public void print()
   {
      System.out.println(feature + " class ");
   }

   public void increment(int classification, String featureValue)
   {
      int feature = featureValueMap.get(featureValue);
      totals[classification][feature]++;
      classCounts[classification]++;
      total++;
   }

   public double probability(int classification, String featureValue)
   {
      return probabilities[classification][featureValueMap.get(featureValue)];
   }

   public void computeProbabilities()
   {
      for (int i = 0; i < Bayes.NUM_CLASSES; i++)
      {
         for (int j = 0; j < totals[i].length; j++)
         {
            probabilities[i][j] = (double) (totals[i][j] + 1) / (classCounts[i] + totals[i].length);
         }
      }
   }

}
