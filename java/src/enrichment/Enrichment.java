package enrichment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import enrichment.model.Model;
import jsc.contingencytables.ContingencyTable2x2;
import jsc.contingencytables.FishersExactTest;
import model.Parameters;
import tools.Utils;

public class Enrichment {
   private static HashMap<String, Pathway> data_pathways = new HashMap<String, Pathway>();
   private static HashMap<String, Boolean> backgroundGenes = new HashMap<String, Boolean>();
   private static HashSet<String> genesToEnrich = new HashSet<String>();
   private static String warningMess = null;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$enrichment$model$Model;

   public static void readFiles() {
      long t1 = System.currentTimeMillis();
      if (!Parameters.isSilent) {
         System.out.println("Reading the Background file...");
      }

      loadBackgroundGenes(Parameters.backgroundFile);
      if (!Parameters.isSilent) {
         System.out.println("Background file provided: " + backgroundGenes.size() + " IDs are found.");
      }

      if (!Parameters.isSilent) {
         System.out.println("Reading the Gene-Pathway mapping file...");
      }

      loadDataPathways(Parameters.pathwayFile);
      if (!Parameters.isSilent) {
         System.out.println("Pathway Map provided: " + data_pathways.keySet().size() + " pathways.");
      }

      String[] copy = (String[])backgroundGenes.keySet().toArray(new String[backgroundGenes.size()]);
      String[] var6 = copy;
      int var5 = copy.length;

      for(int var4 = 0; var4 < var5; ++var4) {
         String gene = var6[var4];
         if (!(Boolean)backgroundGenes.get(gene)) {
            backgroundGenes.remove(gene);
         } else {
            backgroundGenes.put(gene, false);
         }
      }

      if (!Parameters.isSilent) {
         System.out.println("Background file provided: " + backgroundGenes.size() + " IDs are remaining.");
      }

      if (!Parameters.isSilent) {
         System.out.println("Reading the Gene list to enrich [JSON]...");
      }

      loadGeneListJSON(Parameters.listGenesFile);
      if (!Parameters.isSilent) {
         System.out.println("Gene list to enrich [JSON] provided: " + genesToEnrich.size() + " genes.");
      }

      warningMess = "After filtering out genes not present in pathway file: " + genesToEnrich.size() + " genes remain to be enriched.";
      if (!Parameters.isSilent) {
         System.out.println("Loading file time: " + Utils.toReadableTime(System.currentTimeMillis() - t1));
      }

   }

   public static void runEnrichment() {
      long t1 = System.currentTimeMillis();
      File folder = new File(Parameters.outputFolder);
      if (folder.exists() && !folder.isDirectory()) {
         System.err.println("The Output folder specified is not a folder.\nStopping program...");
         System.exit(-1);
      }

      if (!Parameters.isSilent) {
         System.out.println("\n" + Parameters.enrichModel.toString() + " model is used.\n");
      }

      ResultSet res = new ResultSet();
      res.init(data_pathways.size());
      int k = 0;
      if (!Parameters.isSilent) {
         System.out.println("Computing the scores for each pathway...");
      }

      int i;
      label85:
      switch($SWITCH_TABLE$enrichment$model$Model()[Parameters.enrichModel.ordinal()]) {
      case 2:
         Iterator<String> var6 = data_pathways.keySet().iterator();

         while(true) {
            if (!var6.hasNext()) {
               break label85;
            }

            String path = var6.next();
            Pathway p = (Pathway)data_pathways.get(path);
            res.pathways[k] = p.id;
            res.descriptions[k] = p.description;
            res.urls[k] = p.url;
            i = 0;
            Iterator<String> var10 = p.listGenes.iterator();

            while(var10.hasNext()) {
               String gene = var10.next();
               Iterator<String> var12 = genesToEnrich.iterator();

               while(var12.hasNext()) {
                  String g = var12.next();
                  if (gene.equals(g)) {
                     ++i;
                  }
               }
            }

            double[] resFet = doFExactTest(i, p.listGenes.size() - i, genesToEnrich.size() - i, backgroundGenes.size() - p.listGenes.size() - genesToEnrich.size() + i);
            res.p_value[k] = resFet[0];
            res.OR[k] = resFet[1];
            ++k;
         }
      default:
         System.err.println("This model is not yet implemented.");
         System.exit(-1);
      }

      res.adj_p_value = p_adjust(res.p_value, Parameters.adjMethod);
      res.warning = warningMess;
      int filtered = 0;
      ResultSet res_filtered = new ResultSet();

      int index;
      for(index = 0; index < data_pathways.size(); ++index) {
         if (res.adj_p_value[index] <= Parameters.probaCutoff) {
            ++filtered;
         }
      }

      System.out.println(filtered + " values passed the " + Parameters.probaCutoff * 100.0D + "% threshold after p-value adjustment");
      res_filtered.init(filtered);
      index = 0;

      for(i = 0; i < data_pathways.size(); ++i) {
         if (res.adj_p_value[i] <= Parameters.probaCutoff) {
            res_filtered.clone(res, i, index);
            ++index;
         }
      }

      if (!Parameters.isSilent) {
         System.out.println("Writing the result file...");
      }

      try {
         Writer writer = new FileWriter(Parameters.outputFolder + "output.json");
         Gson gson = (new GsonBuilder()).create();
         gson.toJson((Object)res_filtered, (Appendable)writer);
         writer.close();
      } catch (Exception var13) {
         System.err.println("Problem detected when writing the JSON result file. Stopping program...");
         System.exit(-1);
      }

      if (!Parameters.isSilent) {
         System.out.println("Computation Done!");
      }

      if (!Parameters.isSilent) {
         System.out.println("Score & Writing computation time: " + Utils.toReadableTime(System.currentTimeMillis() - t1));
      }

   }

   private static double[] doFExactTest(int a, int b, int c, int d) {
      if (a == 0) {
         return new double[]{1.0D, 0.0D};
      } else if (b != 0 && c != 0) {
         if (d == 0) {
            return new double[]{1.0D, 0.0D};
         } else {
            ContingencyTable2x2 table = new ContingencyTable2x2(a, b, c, d);
            FishersExactTest test = new FishersExactTest(table);
            double twotailedP = test.getOppositeTailProb() + test.getOneTailedSP();
            double OR = (double)(a * d) / (double)(b * c);
            return new double[]{twotailedP, OR};
         }
      } else {
         return new double[]{0.0D, Double.MAX_VALUE};
      }
   }

   private static double[] p_adjust(final double[] pvalues, String adjMethod) {
      if (!adjMethod.equals("BH") && !adjMethod.equals("fdr") && !adjMethod.equals("bonferroni") && !adjMethod.equals("none")) {
         System.out.println("This adjustment method is not implemented.");
         System.exit(0);
      }

      if (adjMethod.equals("fdr")) {
         adjMethod = "BH";
      }

      if (adjMethod.equals("none")) {
         return pvalues;
      } else {
         Integer[] idx = new Integer[pvalues.length];

         for(int i = 0; i < idx.length; ++i) {
            idx[i] = i;
         }

         double[] adj_p_value = new double[pvalues.length];
         Arrays.sort(idx, new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
               return Double.compare(pvalues[o2], pvalues[o1]);
            }
         });
         int[] index = new int[idx.length];

         int i;
         for(i = 0; i < index.length; index[idx[i]] = i++) {
         }

         for(i = 0; i < pvalues.length; ++i) {
            if (adjMethod.equals("BH")) {
               adj_p_value[i] = (double)pvalues.length / ((double)pvalues.length - (double)index[i]) * pvalues[i];
            }

            if (adjMethod.equals("bonferroni")) {
               adj_p_value[i] = (double)pvalues.length * pvalues[i];
            }
         }

         double min = Double.MAX_VALUE;

         for(int j = 0; j < index.length; ++j) {
            double adjP = adj_p_value[idx[j]];
            if (adjP < min) min = adjP;
            else adjP = min;
            adj_p_value[idx[i]] = Math.min(1.0D, adjP);
         }

         return adj_p_value;
      }
   }

   private static void loadGeneListJSON(String fileName) {
      try {
         Gson gson = new Gson();
         JsonReader reader = new JsonReader(new FileReader(fileName));
         String[][] listGenes = (String[][])gson.fromJson((JsonReader)reader, (Type)String[][].class);

         for(int i = 0; i < listGenes.length; ++i) {
            for(int j = 0; j < 3; ++j) {
               String[] gene = listGenes[i][j].split(",");
               String[] var10 = gene;
               int var9 = gene.length;

               for(int var8 = 0; var8 < var9; ++var8) {
                  String g = var10[var8];
                  String ge = g.toUpperCase();
                  if (backgroundGenes.get(ge) != null) {
                     backgroundGenes.put(ge, true);
                     genesToEnrich.add(ge);
                  }
               }
            }
         }

         System.out.println(genesToEnrich.size() + " genes were matching other lists to be enriched.");
         reader.close();
      } catch (FileNotFoundException var12) {
         System.err.println("The JSON gene list was not found at the given path: " + fileName + "\nStopping program...");
         System.exit(-1);
      } catch (Exception var13) {
         System.out.println(var13);
         System.err.println("Problem detected when reading the JSON gene list. Stopping program...");
         System.exit(-1);
      }

   }

   private static void loadDataPathways(String fileName) {
      try {
         BufferedReader br = new BufferedReader(new FileReader(fileName));

         for(String line = br.readLine(); line != null; line = br.readLine()) {
            String[] tokens = line.split("\t");
            Pathway p = new Pathway();
            p.id = tokens[0];
            p.description = tokens[1];
            p.url = tokens[2];

            for(int i = 3; i < tokens.length; ++i) {
               String gene = tokens[i].toUpperCase();
               Boolean inMatrix = (Boolean)backgroundGenes.get(gene);
               if (inMatrix != null) {
                  backgroundGenes.put(gene, true);
                  p.listGenes.add(gene);
               }
            }

            if (p.listGenes.size() <= Parameters.maxGenesInPathway && p.listGenes.size() >= Parameters.minGenesInPathway) {
               data_pathways.put(p.id, p);
            }
         }

         br.close();
      } catch (FileNotFoundException var8) {
         System.err.println("The Pathway file was not found at the given path: " + fileName + "\nStopping program...");
         System.exit(-1);
      } catch (Exception var9) {
         System.err.println("Problem detected when reading the Pathway file. Stopping program...");
         System.exit(-1);
      }

   }

   private static void loadBackgroundGenes(String filename) {
      try {
         BufferedReader br = new BufferedReader(new FileReader(filename));
         String line = br.readLine();

         for(line = br.readLine(); line != null; line = br.readLine()) {
            String[] vals = line.substring(0, line.indexOf(9)).split("\\|");
            String gene;
            int var5;
            int var6;
            String[] var7;
            if (vals.length > 0) {
               var6 = (var7 = vals[0].split(",")).length;

               for(var5 = 0; var5 < var6; ++var5) {
                  gene = var7[var5];
                  backgroundGenes.put(gene.toUpperCase(), false);
               }
            }

            if (vals.length > 1) {
               var6 = (var7 = vals[1].split(",")).length;

               for(var5 = 0; var5 < var6; ++var5) {
                  gene = var7[var5];
                  backgroundGenes.put(gene.toUpperCase(), false);
               }
            }

            if (vals.length > 2) {
               var6 = (var7 = vals[2].split(",")).length;

               for(var5 = 0; var5 < var6; ++var5) {
                  gene = var7[var5];
                  backgroundGenes.put(gene.toUpperCase(), false);
               }
            }
         }

         br.close();
      } catch (FileNotFoundException var8) {
         System.err.println("The Background file was not found at the given path: " + filename + "\nStopping program...");
         System.exit(-1);
      } catch (Exception var9) {
         System.err.println("Problem detected when reading the Background file. Stopping program...");
         var9.printStackTrace();
         System.exit(-1);
      }
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$enrichment$model$Model() {
      int[] var10000 = $SWITCH_TABLE$enrichment$model$Model;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[Model.values().length];

         try {
            var0[Model.FET.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[Model.GSEA.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[Model.HyperGeometric.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$enrichment$model$Model = var0;
         return var0;
      }
   }
}
