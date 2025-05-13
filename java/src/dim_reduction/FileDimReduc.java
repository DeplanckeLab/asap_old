package dim_reduction;

import com.jujutsu.tsne.barneshut.BHTSne;
import com.jujutsu.tsne.barneshut.BarnesHutTSne;
import com.jujutsu.tsne.barneshut.ParallelBHTsne;
import com.jujutsu.tsne.barneshut.TSneConfiguration;
import com.jujutsu.utils.MatrixUtils;
import com.jujutsu.utils.TSneUtils;
import dim_reduction.model.Model;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import model.DimReducJSON;
import model.ErrorJSON;
import model.Parameters;
import tools.Utils;

public class FileDimReduc {
   public static DimReducJSON dimReducJSON = null;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$dim_reduction$model$Model;

   public static void reduceDimension() {
      dimReducJSON = new DimReducJSON();
      System.out.println("Reducing dimension of file : " + Parameters.fileName);

      try {
         switch($SWITCH_TABLE$dim_reduction$model$Model()[Parameters.dimReducModel.ordinal()]) {
         case 2:
            reduceTSNE();
            break;
         default:
            System.err.println("Not implemented yet");
         }
      } catch (IOException var3) {
         IOException ioe = var3;
         System.err.println(var3.getMessage());

         try {
            new ErrorJSON(ioe.getMessage());
            BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + "output.json"));
            bw.write("{\"displayed_error\":\"" + ioe.getMessage() + "\"}");
            bw.close();
         } catch (IOException var2) {
            System.err.println(var2.getMessage());
         }

         System.exit(-1);
      }

      dimReducJSON.writeJSON();
   }

   public static void reduceTSNE() throws IOException {
      System.out.println("Reading file...");
      double[][] matrix = MatrixUtils.simpleRead2DMatrix(new File(Parameters.fileName), "\t");
      System.out.println("Read!");
      System.gc();
      System.gc();
      matrix = Utils.t(matrix);
      System.out.println("Transposed is computed!");
      System.gc();
      System.gc();
      boolean parallel = true;
      Object tsne;
      if (parallel) {
         tsne = new ParallelBHTsne();
      } else {
         tsne = new BHTSne();
      }

      TSneConfiguration config = TSneUtils.buildConfig(matrix, 2, 55, (double)Parameters.perplexity, 2000, false, 0.5D, false, true);
      System.out.println("Run tSNE!");
      double[][] Y = ((BarnesHutTSne)tsne).tsne(config);
      System.out.println(Y.length);
      System.out.println(Y[0].length);
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$dim_reduction$model$Model() {
      int[] var10000 = $SWITCH_TABLE$dim_reduction$model$Model;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[Model.values().length];

         try {
            var0[Model.MDS.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[Model.PCA.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[Model.TSNE.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[Model.ZIFA.ordinal()] = 4;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$dim_reduction$model$Model = var0;
         return var0;
      }
   }
}
