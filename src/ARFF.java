import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class ARFF
{
   public String relation;
   public ArrayList<Instance> instances;
   public ArrayList<String> classValues;
   public LinkedHashMap<String, ArrayList<String>> featureMap;

   public ARFF(String filename)
   {
      Scanner in = null;
      try
      {
         in = new Scanner(new File(filename));
      }
      catch (FileNotFoundException e)
      {
         e.printStackTrace();
      }
      instances = new ArrayList<>();
      featureMap = new LinkedHashMap<>();

      boolean readingData = false;

      while (in.hasNextLine())
      {
         String s = in.nextLine().toLowerCase();

         for (int i = 0; i < s.length() - 1; i++)
         {
            if (s.charAt(i) == ' ' && s.charAt(i + 1) == ' ')
            {
               s = s.substring(0, i) + s.substring(i-- + 1, s.length());
            }
         }

         if (s.charAt(0) == '%')
         {
            continue;
         }
         else if (s.charAt(0) == '@')
         {
            if (s.charAt(1) == 'r')
            {
               if (relation == null)
               {
                  relation = s.split(" ")[1];
               }
               else
               {
                  System.err.println("Multiple relations defined!");
                  continue;
               }
            }
            else if (s.charAt(1) == 'a')
            {
               String[] split = s.split(" ");
               // remove quotations around attribute
               if (split[1].charAt(0) == '\'')
               {
                  split[1] = split[1].substring(1, split[1].length() - 1);
               }

               int initial = 2;
               if (split[2].equals("{"))
               {
                  initial = 3;

                  int last = split.length - 1;
                  // remove the '}' from the end of the last string if its there
                  if (split[last].charAt(split[last].length() - 1) == '}')
                  {
                     split[last] = split[last].substring(0, split[last].length() - 1);
                  }
               }

               ArrayList<String> featureValues = new ArrayList<>();

               for (int i = initial; i < split.length; i++)
               {
                  if (split[i].length() > 1)
                  {
                     if (split[i].charAt(split[i].length() - 1) == ',')
                     {
                        split[i] = split[i].substring(0, split[i].length() - 1);
                     }
                  }
                  if (split[i].length() > 0)
                  {
                     featureValues.add(split[i]);
                  }
               }
               if (!split[1].equals("class"))
               {
                  featureMap.put(split[1], featureValues);
               }
               else
               {
                  classValues = featureValues;
               }
            }
            else if (s.substring(1, 5).equals("data"))
            {
               readingData = true;
            }
            else
            {
               System.err.println("Unable to understand this line: " + s);
            }
         }
         else
         {
            if (!readingData)
            {
               System.out.println("@data flag not yet reached...");
               continue;
            }
            String[] split = s.split(",");
            String classification = split[split.length - 1];
            String[] features = null;
            features = s.substring(0, s.length() - classification.length()).split(",");

            instances.add(new Instance(classValues.indexOf(classification), features));
         }
      }
      in.close();

   }

   public void reduceInstances(int numSamples)
   {
      if (numSamples > instances.size())
      {
         System.err.println("Requested number of samples is greater than number of training instances.");
         return;
      }

      int size = instances.size() / numSamples;

      ArrayList<Instance> newInstances = new ArrayList<>();

      for (int i = 0; i < size; i++)
      {
         Instance inst = instances.get((int) Math.floor(Math.random() * instances.size()));
         newInstances.add(inst);
      }

      this.instances = newInstances;
   }
}
