package parsing;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class CreateDLFile {
   public static ArrayList<String> gene_names;

   public static void create(String inputMatrix, String outputFile) {
      try {
         BufferedReader br = new BufferedReader(new FileReader(inputMatrix));
         BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
         String line = br.readLine();
         bw.write(line + "\n");

         for(line = br.readLine(); line != null; line = br.readLine()) {
            int indexTab = line.indexOf("\t");
            int id = Integer.parseInt(line.substring(0, indexTab));
            bw.write((String)gene_names.get(id) + "\t" + line.substring(indexTab + 1) + "\n");
         }

         br.close();
         bw.close();
      } catch (IOException var7) {
         var7.printStackTrace();
      }

   }

   public static void getGenesFromJSON(String JSONFile) {
      gene_names = new ArrayList<>();

      try {
         Gson gson = new Gson();
         JsonReader reader = new JsonReader(new FileReader(JSONFile));
         String[][] listGenes = (String[][])gson.fromJson((JsonReader)reader, (Type)String[][].class);

         for(int i = 0; i < listGenes.length; ++i) {
            String list = listGenes[i][0] + "|" + listGenes[i][1] + "|" + listGenes[i][2];
            if (listGenes[i].length <= 3) {
               list = list + "|";
            } else {
               list = list + "|" + listGenes[i][3];
            }

            gene_names.add(list);
         }

         reader.close();
      } catch (FileNotFoundException var6) {
         System.err.println("The JSON gene list was not found at the given path: " + JSONFile + "\nStopping program...");
         System.exit(-1);
      } catch (Exception var7) {
         System.out.println(var7);
         System.err.println("Problem detected when reading the JSON gene list. Stopping program...");
         System.exit(-1);
      }

   }
}
