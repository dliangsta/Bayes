import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

public class TreeAugmentedNetwork extends BayesianNetwork
{
   /**
    * Pairs of nodes, organized in the following manner: 0: 1 2 3 4 5 6 7 8 9 1: 2 3 4 5 6 7 8 9 ... 7: 8 9 8: 9
    */
   public NodePair[][] nodePairs;
   public TANNode root;
   public int[] classCounts;
   public int total;

   public TreeAugmentedNetwork(ARFF arff)
   {
      super(arff, false);
      classCounts = new int[Bayes.NUM_CLASSES];
      for (Instance inst : arff.instances)
      {
         classCounts[inst.classification]++;
         total++;
      }
      // "edges" between nodes
      nodePairs = new NodePair[nodes.length - 1][];

      // create pairs
      for (int i = 0; i < nodePairs.length; i++)
      {
         nodePairs[i] = new NodePair[nodePairs.length - i];
         for (int j = 0; j < nodePairs[i].length; j++)
         {
            nodePairs[i][j] = new NodePair((TANNode) nodes[i], (TANNode) nodes[i + j + 1]);
         }
      }
      for (Instance inst : arff.instances)
      {
         // for each unique pairing of features in this instance, increment the count
         for (int i = 0; i < nodePairs.length; i++)
         {
            for (int j = 0; j < nodePairs[i].length; j++)
            {
               nodePairs[i][j].increment(inst.classification, inst.features[i], inst.features[i + j + 1]);
            }
         }
      }

      // create a PQ and compute the information gain for each pair
      PriorityQueue<NodePair> pq = new PriorityQueue<>();
      for (int i = 0; i < nodePairs.length; i++)
      {
         for (int j = 0; j < nodePairs[i].length; j++)
         {
            nodePairs[i][j].computeInformationGain();
            pq.add(nodePairs[i][j]);
         }
      }

      HashSet<TANNode> vertices = new HashSet<>();
      vertices.add((TANNode) nodes[0]);
      ArrayList<NodePair> edges = new ArrayList<>();
      ArrayList<NodePair> buffer = new ArrayList<>();
      while (!pq.isEmpty() && vertices.size() < nodes.length)
      {
         NodePair np = pq.poll();
         boolean i = vertices.contains(np.i);
         boolean j = vertices.contains(np.j);
         if (i ^ j)
         {
            if (i)
            {
               vertices.add(np.j);
            }
            else
            {
               vertices.add(np.i);
            }
            edges.add(np);
            pq.addAll(buffer);
            buffer.clear();
         }
         else
         {

            buffer.add(np);
         }
      }
      root = (TANNode) nodes[0];
      root.claimEdges(edges);
      root.computeCPT(arff);
      for (int i = 0; i < Bayes.featureMap.size(); i++)
      {
         root.print(i);
      }
      System.out.println();
   }

   @Override
   public int classify(Instance inst)
   {
      int maxIndex = -1;
      double maxProbability = 0;
      double totalProbability = 0;
      for (int i = 0; i < classValues.size(); i++)
      {
         double probability = (classCounts[i] + 1) / ((double) total + classCounts.length) * root.classify(i, inst);
         if (probability > maxProbability)
         {
            maxProbability = probability;
            maxIndex = i;
         }
         totalProbability += probability;
      }

      System.out.printf(classValues.get(maxIndex) + " " + classValues.get(inst.classification) + " " + "%.12f\n", maxProbability / totalProbability);
      return maxIndex;
   }
}
