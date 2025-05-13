package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FilterJSON {
   public int nber_cells = 0;
   public int nber_genes = 0;
   public int nber_filtered_genes = 0;
   public long nber_zeros = 0L;
   public ArrayList<DataPlots> list_plots = new ArrayList<>();
   public ArrayList<DataWarnings> warnings = new ArrayList<>();
   public String info = "";

   public void writeJSON() {
      if (this.nber_genes == this.nber_filtered_genes) {
         new ErrorJSON("The output matrix has no more genes. You should put less stringent thresholds.");
      }

      try {
         BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + "output.json"));
         bw.write("{\"nber_genes\":" + (this.nber_genes - this.nber_filtered_genes) + ",");
         bw.write("\"nber_cells\":" + this.nber_cells + ",");
         bw.write("\"nber_filtered_genes\":" + this.nber_filtered_genes + ",");
         bw.write("\"nber_zeros\":" + this.nber_zeros + ",");
         if (!this.info.equals("")) {
            bw.write("\"info\":\"" + this.info + "\",");
         }

         bw.write("\"list_plots\":" + DataPlots.toString(this.list_plots) + ",");
         bw.write("\"warnings\":" + DataWarnings.toString(this.warnings));
         bw.write("}");
         bw.close();
      } catch (IOException var2) {
         System.err.println(var2.getMessage());
         System.exit(-1);
      }

   }

   public void addPlot(String name, String description) {
      DataPlots d = new DataPlots();
      d.description = description;
      d.name = name;
      this.list_plots.add(d);
   }

   public void addWarning(String message) {
      DataWarnings w = new DataWarnings();
      w.message = message;
      this.warnings.add(w);
   }
}
