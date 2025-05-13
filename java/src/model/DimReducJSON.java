package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DimReducJSON {
   public ArrayList<DataWarnings> warnings = new ArrayList();

   public void writeJSON() {
      try {
         BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + "output.json"));
         bw.write("{\"text\": TODO ,");
         bw.write("{\"PC1\": TODO ,");
         bw.write("{\"PC2\": TODO ,");
         bw.write("{\"PC3\": TODO ,");
         bw.write("{\"PC4\": TODO ,");
         bw.write("{\"PC5\": TODO ,");
         bw.write("\"warnings\":" + DataWarnings.toString(this.warnings));
         bw.write("}");
         bw.close();
      } catch (IOException var2) {
         System.err.println(var2.getMessage());
         System.exit(-1);
      }

   }

   public void addWarning(String message) {
      DataWarnings w = new DataWarnings();
      w.message = message;
      this.warnings.add(w);
   }
}
