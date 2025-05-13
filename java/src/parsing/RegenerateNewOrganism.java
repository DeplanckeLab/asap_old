package parsing;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import db.DBManager;
import model.Parameters;
import model.ParsingJSON;
import parsing.model.Gene;
import tools.Utils;

public class RegenerateNewOrganism {
   public static ParsingJSON json = null;
   public static HashMap<String, ArrayList<Gene>> dbGenes = null;
   public static BufferedWriter bw_NF = null;
   public static ArrayList<String> genes = null;
   public static HashMap<String, Integer> geneDups = null;

   public static void regenerateJSON() {
      long t = System.currentTimeMillis();
      dbGenes = DBManager.getGenesInDB();
      System.out.println("Accessing DB time: " + Utils.toReadableTime(System.currentTimeMillis() - t));
      t = System.currentTimeMillis();
      if (dbGenes.size() > 0) {
         try {
            geneDups = new HashMap<String, Integer>();
            json = ParsingJSON.loadJSON(Parameters.outputFolder + "output.json");
            System.out.println("There was " + json.nber_not_found_genes + " not found genes in output.json.");
            json.nber_not_found_genes = 0;
            System.out.println("There was " + json.nber_duplicated_genes + " duplicated genes [unique] in output.json.");
            json.nber_duplicated_genes = 0;
            System.out.println("There was " + json.nber_all_duplicated_genes + " duplicated genes in output.json.");
            json.nber_all_duplicated_genes = 0;
            genes = getGenesFromJSON();
            System.out.println("Find " + genes.size() + " genes in JSON.");
            bw_NF = new BufferedWriter(new FileWriter(Parameters.outputFolder + "not_found_genes.txt"));

            for(int i = 0; i < genes.size(); ++i) {
               genes.set(i, generateName((String)genes.get(i)));
            }

            bw_NF.close();
            System.out.println("Now, there is " + json.nber_not_found_genes + " not found genes in output.json.");
            System.out.println("Now, there is " + json.nber_duplicated_genes + " duplicated genes [unique] in output.json.");
            System.out.println("Now, there is " + json.nber_all_duplicated_genes + " duplicated genes in output.json.");
            json.nber_unique_genes = geneDups.size();
            Iterator<String> var3 = geneDups.keySet().iterator();

            while(var3.hasNext()) {
               String gene = var3.next();
               if ((Integer)geneDups.get(gene) != 1) {
                  ++json.nber_duplicated_genes;
               }
            }

            json.writeOutputJSON();
            writeGeneNamesJSON();
            writeDuplicatedGenes(geneDups);
         } catch (IOException var4) {
            var4.printStackTrace();
         }
      } else {
         System.err.println("No gene found in database for this organism. Aborted.");
      }

   }

   public static void writeDuplicatedGenes(HashMap<String, Integer> geneDups) {
      try {
         BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + "duplicated_genes.txt"));
         Iterator<String> var3 = geneDups.keySet().iterator();

         while(var3.hasNext()) {
            String geneKey = var3.next();
            Integer nb = geneDups.get(geneKey);
            if (nb > 1) {
               bw.write(geneKey + "\t" + nb + "\n");
            }
         }

         bw.close();
      } catch (IOException var5) {
         System.err.println(var5.getMessage());
         System.exit(-1);
      }

   }

   public static String generateName(String gene) throws IOException {
      ArrayList<Gene> dbHit = dbGenes.get(gene.toUpperCase());
      String ensIdList = "";
      String geneIdList = "";
      if (dbHit == null) {
         bw_NF.write(gene + "\n");
         ++json.nber_not_found_genes;
      } else {
         Gene gHit;
         for(Iterator<Gene> var5 = dbHit.iterator(); var5.hasNext(); geneIdList = geneIdList + gHit.name + ",") {
            gHit = var5.next();
            ensIdList = ensIdList + gHit.ensembl_id + ",";
         }

         ensIdList = ensIdList.substring(0, ensIdList.length() - 1);
         geneIdList = geneIdList.substring(0, geneIdList.length() - 1);
      }

      String res = "[\"" + gene + "\",\"" + ensIdList + "\",\"" + geneIdList + "\"]";
      Integer count = (Integer)geneDups.get(res);
      if (count == null) {
         geneDups.put(res, 1);
      } else {
         count = count + 1;
         ++json.nber_all_duplicated_genes;
         geneDups.put(res, count);
         res = "[\"" + gene + "," + count + "\",\"" + ensIdList + "\",\"" + geneIdList + "\"]";
      }

      return res;
   }

   public static void writeGeneNamesJSON() {
      try {
         BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + "gene_names.json"));
         if (genes.size() == 0) {
            bw.write("{}");
         } else {
            bw.write("[" + (String)genes.get(0));
            int i = 1;

            while(true) {
               if (i >= genes.size()) {
                  bw.write("]");
                  break;
               }

               bw.write("," + (String)genes.get(i));
               ++i;
            }
         }

         bw.close();
      } catch (IOException var2) {
         System.err.println(var2.getMessage());
         System.exit(-1);
      }

   }

   public static ArrayList<String> getGenesFromJSON() {
      ArrayList<String> res = new ArrayList<String>();

      try {
         Gson gson = new Gson();
         JsonReader reader = new JsonReader(new FileReader(Parameters.JSONFileName));
         String[][] listGenes = (String[][])gson.fromJson((JsonReader)reader, (Type)String[][].class);

         for(int i = 0; i < listGenes.length; ++i) {
            String originalName = listGenes[i][0];
            int index = originalName.indexOf(",");
            if (index != -1) {
               originalName = originalName.substring(0, index);
            }

            res.add(originalName);
         }

         reader.close();
      } catch (FileNotFoundException var7) {
         System.err.println("The JSON gene list was not found at the given path: " + Parameters.JSONFileName + "\nStopping program...");
         System.exit(-1);
      } catch (Exception var8) {
         System.out.println(var8);
         System.err.println("Problem detected when reading the JSON gene list. Stopping program...");
         System.exit(-1);
      }

      return res;
   }
}
