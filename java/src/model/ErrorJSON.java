package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ErrorJSON {
   public String displayed_error = "Error";

   public ErrorJSON(String errorMessage) {
      this.displayed_error = errorMessage;
      this.writeJSON();
      System.err.println(this.displayed_error);
      System.exit(-1);
   }

   private void writeJSON() {
      try {
         BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + "output.json"));
         bw.write("{\"displayed_error\":\"" + this.displayed_error + "\"}");
         bw.close();
      } catch (IOException var2) {
         System.err.println(var2.getMessage());
         System.exit(-1);
      }

   }
}
