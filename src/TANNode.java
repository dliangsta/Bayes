import java.util.ArrayList;

public class TANNode extends BayesianNode
{
   public int featureIndex;
   public TANNode parent;
   public ArrayList<TANNode> children;
   public double[][][] CPT;
   public double[][] PT;

   public TANNode(int featureIndex, String feature, ArrayList<String> featureValues)
   {
      super(feature, featureValues);
      this.featureIndex = featureIndex;
   }

   public void print(int featureIndex)
   {
      if (featureIndex == this.featureIndex)
      {
         if (parent == null)
         {
            System.out.println(feature + " class");
         }
         else
         {
            System.out.println(feature + " " + parent.feature + " class ");
         }
         return;
      }

      for (TANNode tan : children)
      {
         tan.print(featureIndex);
      }
   }

   public void printCPT(int featureIndex)
   {
      if (featureIndex == this.featureIndex)
      {
         System.out.println("CPT of Attribute " + featureIndex);
         if (parent == null)
         {
            for (int k = 0; k < PT.length; k++)
            {
               for (int i = 0; i < PT[k].length; i++)
               {
                  System.out.println("Pr(" + featureIndex + "=" + i + " | " + 18 + "=" + k + ") = " + PT[k][i]);
               }
            }
         }
         else
         {

            for (int k = 0; k < CPT.length; k++)
            {
               for (int i = 0; i < CPT[k].length; i++)
               {
                  for (int j = 0; j < CPT[k][i].length; j++)
                  {
                     System.out.println("Pr(" + featureIndex + "=" + j + " | " + parent.featureIndex + "=" + i + "," + 18 + "=" + k + ") = " + CPT[k][i][j]);
                  }
               }
            }
         }
         System.out.println();
         return;
      }

      for (TANNode tan : children)
      {
         tan.printCPT(featureIndex);
      }
   }

   public double classify(int classification, Instance inst)
   {
      double probability = 0;

      if (parent == null)
      {
         probability = PT[classification][featureValueMap.get(inst.features[featureIndex])];
      }
      else
      {
         probability = CPT[classification][parent.featureValueMap.get(inst.features[parent.featureIndex])][featureValueMap.get(inst.features[featureIndex])];
      }

      if (children != null)
      {
         for (TANNode child : children)
         {
            probability *= child.classify(classification, inst);
         }
      }

      return probability;
   }

   public void computeCPT(ARFF arff)
   {
      if (parent == null)
      {
         int[][] counts = new int[Bayes.NUM_CLASSES][featureValueMap.size()];
         int[] classCounts = new int[Bayes.NUM_CLASSES];
         for (Instance inst : arff.instances)
         {
            classCounts[inst.classification]++;
            counts[inst.classification][featureValueMap.get(inst.features[featureIndex])]++;
         }
         PT = new double[Bayes.NUM_CLASSES][featureValueMap.size()];
         for (int k = 0; k < PT.length; k++)
         {
            double total = 0;
            for (int i = 0; i < PT[k].length; i++)
            {
               PT[k][i] = (counts[k][i] + 1.0) / ((double) classCounts[k] + classCounts.length);
               total += PT[k][i];
            }
            for (int i = 0; i < PT[k].length; i++)
            {
               PT[k][i] /= total;
            }
         }
      }
      else
      {
         int[][][] counts = new int[Bayes.NUM_CLASSES][parent.featureValueMap.size()][featureValueMap.size()];
         int[][] parentClassCounts = new int[Bayes.NUM_CLASSES][parent.featureValueMap.size()];
         for (Instance inst : arff.instances)
         {
            parentClassCounts[inst.classification][parent.featureValueMap.get(inst.features[parent.featureIndex])]++;
            counts[inst.classification][parent.featureValueMap.get(inst.features[parent.featureIndex])][featureValueMap.get(inst.features[featureIndex])]++;
         }
         CPT = new double[counts.length][counts[0].length][counts[0][0].length];
         for (int k = 0; k < counts.length; k++)
         {
            for (int i = 0; i < counts[k].length; i++)
            {
               double total = 0;
               for (int j = 0; j < counts[k][i].length; j++)
               {
                  CPT[k][i][j] = (counts[k][i][j] + 1.0) / ((double) parentClassCounts[k][i] + counts[k][i].length);
                  total += CPT[k][i][j];
               }
               for (int j = 1; j < counts[k][i].length; j++)
               {
                  CPT[k][i][j] /= total;
               }
            }
         }
      }
      if (children != null)
      {
         for (TANNode child : children)
         {
            child.computeCPT(arff);
         }
      }
   }

   public void claimEdges(ArrayList<NodePair> edges)
   {
      this.children = new ArrayList<>();
      for (int i = 0; i < edges.size() && i >= 0; i++)
      {
         NodePair edge = edges.get(i);
         if (edge.i == this)
         {
            edge.j.parent = this;
            children.add(edge.j);
            edges.remove(i--);
         }
         else if (edge.j == this)
         {
            edge.i.parent = this;
            children.add(edge.i);
            edges.remove(i--);
         }
      }

      for (TANNode child : children)
      {
         child.claimEdges(edges);
      }
   }
}
