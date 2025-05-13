package tools;

import model.Parameters;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

public class Stats {
   private static Rengine re = null;

   public static void startRHandle(boolean showConsole) {
      if (!Rengine.versionCheck()) {
         System.err.println("** Version mismatch - Java files don't match library version.");
      }

      if (re == null) {
         if (showConsole) {
            re = new Rengine(new String[]{"--vanilla"}, false, new Console());
         } else {
            re = new Rengine(new String[]{"--vanilla"}, false, (RMainLoopCallbacks)null);
         }

         if (!re.waitForR()) {
            System.err.println("Cannot load R");
         } else {
            System.out.println("R loaded");
         }
      } else {
         System.err.println("R is already running");
      }

   }

   public static void scLVM(double[][] normalizedDataset) {
      long t = System.currentTimeMillis();
      assignDoubleMatrix(normalizedDataset, "data.parsed.sf");
      System.out.println("Assign Double Matrix: " + Utils.toReadableTime(System.currentTimeMillis() - t));
      re.eval("require(scLVM)");
      re.eval("png(" + Parameters.outputFolder + "\"tech.noise.fit.png\", width=500, height=600, type=\"cairo\")");
      re.eval("data.tech.noise <- fitTechnicalNoise(data.parsed.sf, use_ERCC = F, fit_type = \"" + Parameters.fitModel + "\")");
      re.eval("dev.off()");
   }

   public static void stopRHandle() {
      re.end();
      System.out.println("R unloaded");
   }

   private static REXP assignDoubleMatrix(double[][] matrix, String nameToAssignOn) {
      re.assign(nameToAssignOn, matrix[0]);
      REXP resultMatrix = re.eval(nameToAssignOn + " <- matrix( " + nameToAssignOn + " ,nr=1)");

      for(int i = 1; i < matrix.length; ++i) {
         re.assign("temp", matrix[i]);
         resultMatrix = re.eval(nameToAssignOn + " <- rbind(" + nameToAssignOn + ",matrix(temp,nr=1))");
      }

      return resultMatrix;
   }
}
