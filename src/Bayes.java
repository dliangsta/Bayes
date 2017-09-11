import java.util.ArrayList;
import java.util.LinkedHashMap;

// driver class
public class Bayes
{
   public static int NUM_CLASSES = 2;
   public static int NUM_SAMPLES = 4;
   public static ArrayList<String> classValues;
   public static LinkedHashMap<String, ArrayList<String>> featureMap;

   public static void main(String[] args)
   {
      if (args.length != 3)
      {
         System.err.println("Usage: bayes <train-set-file> <test-set-file> <n|t>");
      }

      ARFF train = new ARFF(args[0]);
      // train.reduceInstances(NUM_SAMPLES);
      ARFF test = new ARFF(args[1]);
      NUM_CLASSES = train.classValues.size();
      classValues = train.classValues;
      featureMap = train.featureMap;

      if (args[2].equals("n"))
      {
         NaiveBayesianNetwork NBN = new NaiveBayesianNetwork(train);
         evaluate(NBN, test);
      }
      else if (args[2].equals("t"))
      {
         TreeAugmentedNetwork TAN = new TreeAugmentedNetwork(train);
         evaluate(TAN, test);
      }
      else
      {
         System.err.println("Invalid tree type!");
         System.exit(1);
      }
   }

   public static void evaluate(BayesianNetwork BN, ARFF test)
   {
      double correct = 0;
      for (Instance inst : test.instances)
      {
         String classification = classValues.get(BN.classify(inst));
         if (classification.equals(classValues.get(inst.classification)))
         {
            correct++;
         }
      }
      System.out.println("\n" + (int) correct);
   }

   public static void evaluate(BayesianNetwork BN, ArrayList<Instance> instances)
   {
      double correct = 0;
      for (Instance inst : instances)
      {
         String classification = classValues.get(BN.classify(inst));
         if (classification.equals(classValues.get(inst.classification)))
         {
            correct++;
         }
      }
      System.out.println("\n" + (int) correct);
   }
}
