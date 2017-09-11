public class NaiveBayesianNetwork extends BayesianNetwork
{
   public int total;
   public int[] classCounts;

   public NaiveBayesianNetwork(ARFF arff)
   {
      super(arff, true);
      this.total = 0;
      this.classCounts = new int[arff.classValues.size()];
      for (int i = 0; i < arff.instances.size(); i++)
      {
         trainOnInstance(arff.instances.get(i));
      }

      for (int i = 0; i < nodes.length; i++)
      {
         ((NaiveBayesianNode) nodes[i]).computeProbabilities();
      }

      for (int i = 0; i < Bayes.featureMap.size(); i++)
      {
         ((NaiveBayesianNode) nodes[i]).print();
      }
      System.out.println();
   }

   public void trainOnInstance(Instance inst)
   {
      int classification = inst.classification;

      classCounts[inst.classification]++;

      for (int i = 0; i < inst.features.length; i++)
      {
         ((NaiveBayesianNode) nodes[i]).increment(classification, inst.features[i]);
      }

      total++;
   }

   @Override
   public int classify(Instance inst)
   {
      if (inst.features.length != nodes.length)
      {
         System.err.println("Invalid classification instance...");
         return -1;
      }

      double[] probabilities = new double[classValues.size()];
      double totalProbability = 0;

      for (int i = 0; i < classValues.size(); i++)
      {
         probabilities[i] = (double) (classCounts[i] + 1) / (total + Bayes.NUM_CLASSES);

         for (int j = 0; j < nodes.length; j++)
         {
            probabilities[i] *= ((NaiveBayesianNode) nodes[j]).probability(i, inst.features[j]);
         }
         totalProbability += probabilities[i];
      }

      double maxProbability = 0;
      int maxIndex = -1;

      for (int i = 0; i < classValues.size(); i++)
      {
         probabilities[i] /= totalProbability;
         if (probabilities[i] > maxProbability)
         {
            maxProbability = probabilities[i];
            maxIndex = i;
         }
      }

      System.out.printf(classValues.get(maxIndex) + " " + classValues.get(inst.classification) + " %.12f\n", maxProbability);

      return maxIndex;
   }
}
