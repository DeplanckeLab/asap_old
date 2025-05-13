package filtering;

import filtering.model.Model;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import model.ErrorJSON;
import model.FilterJSON;
import model.Parameters;
import model.ParsingJSON;
import tools.Stats;
import tools.Utils;

public class FileFilter {
   public static ParsingJSON parsJSON = null;
   public static FilterJSON filtJSON = null;
   public static BufferedWriter bw = null;
   public static int[] expressedGenesPerSample = null;
   public static double[] colSum = null;
   public static double[] rowSum = null;
   public static double[] rowVar = null;
   public static double[] rowCoeffOfVar = null;
   public static double[] sizeFactors = null;
   public static String[] geneNames = null;
   public static String[] cellNames = null;
   public static double[] loggeomeans = null;
   public static String yCol = "Nb Expressed Genes [count > 0]";
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$filtering$model$Model;

   public static void filter() {
      parsJSON = ParsingJSON.loadJSON(Parameters.JSONFileName);
      filtJSON = new FilterJSON();
      System.out.println("Filtering file : " + Parameters.fileName);

      try {
         FileFilter.bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + "output.tab"));
         switch($SWITCH_TABLE$filtering$model$Model()[Parameters.filtModel.ordinal()]) {
         case 1:
            filterNONE();
            filtJSON.addPlot("boxplot.png", "Gene expression distribution in each sample after filtering.");
            if (parsJSON.is_count_table) {
               filtJSON.addPlot("barplot.txt", "Number of genes detected as expressed in each sample.");
               filtJSON.addPlot("expressed.png", "Cumulative number of expressed genes when taking cells as bulk [ranked by increasing order].");
            }
            break;
         case 2:
            filterEXPRESSED();
            filtJSON.addPlot("boxplot.png", "Gene expression distribution in each sample after filtering.");
            if (parsJSON.is_count_table) {
               filtJSON.addPlot("barplot.txt", "Number of genes detected as expressed in each sample.");
               filtJSON.addPlot("expressed.png", "Cumulative number of expressed genes when taking cells as bulk [ranked by increasing order].");
            }
            break;
         case 3:
            filterCOEFFOFVAR();
            filtJSON.addPlot("boxplot.png", "Gene expression distribution in each sample after filtering.");
            if (parsJSON.is_count_table) {
               filtJSON.addPlot("barplot.txt", "Number of genes detected as expressed in each sample.");
               filtJSON.addPlot("expressed.png", "Cumulative number of expressed genes when taking cells as bulk [ranked by increasing order].");
            }
            break;
         case 4:
            filterVAR();
            filtJSON.addPlot("boxplot.png", "Gene expression distribution in each sample after filtering.");
            if (parsJSON.is_count_table) {
               filtJSON.addPlot("barplot.txt", "Number of genes detected as expressed in each sample.");
               filtJSON.addPlot("expressed.png", "Cumulative number of expressed genes when taking cells as bulk [ranked by increasing order].");
            }
            break;
         case 5:
         case 6:
         default:
            System.err.println("Not implemented yet");
            break;
         case 7:
            if (!parsJSON.is_count_table) {
               new ErrorJSON("Parsed file is not a count matrix. Cannot compute CPM.");
            }

            filterCPM();
            filtJSON.addPlot("boxplot.png", "Gene expression distribution in each sample after filtering.");
            filtJSON.addPlot("barplot.txt", "Number of genes detected as expressed in each sample.");
            filtJSON.addPlot("expressed.png", "Cumulative number of expressed genes when taking cells as bulk [ranked by increasing order].");
            break;
         case 8:
            if (!parsJSON.is_count_table) {
               new ErrorJSON("Parsed file is not a count matrix. Cannot use scLVM.");
            }

            filterSCLVM();
            filtJSON.addPlot("boxplot.png", "Gene expression distribution in each sample after filtering.");
            filtJSON.addPlot("barplot.txt", "Number of genes detected as expressed in each sample.");
            filtJSON.addPlot("expressed.png", "Cumulative number of expressed genes when taking cells as bulk [ranked by increasing order].");
         }

         BufferedWriter bw_expressed = new BufferedWriter(new FileWriter(Parameters.outputFolder + "barplot.txt"));
         bw_expressed.write("sample\tnbExpressedGenesPerSample\n");

         for(int i = 0; i < cellNames.length; ++i) {
            bw_expressed.write(cellNames[i] + "\t" + expressedGenesPerSample[i] + "\n");
         }

         bw_expressed.close();
         FileFilter.bw.close();
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

      filtJSON.writeJSON();
   }

   public static void filterNONE() throws IOException {
      BufferedWriter bw_filt = new BufferedWriter(new FileWriter(Parameters.outputFolder + "filtered.genes.txt"));
      filtJSON.nber_filtered_genes = 0;
      filtJSON.nber_zeros = 0L;
      BufferedReader br = new BufferedReader(new FileReader(Parameters.fileName));
      String line = br.readLine();
      readHeader(line);
      expressedGenesPerSample = new int[filtJSON.nber_cells];
      geneNames = new String[parsJSON.nber_genes];
      int nbGenes = 0;

      for(line = br.readLine(); line != null; line = br.readLine()) {
         boolean detect = false;
         String[] tokens = line.split("\t");
         geneNames[nbGenes] = tokens[0];
         int nbZeros = 0;
         if (parsJSON.is_count_table) {
            for(int i = 1; i < tokens.length; ++i) {
               int val = Integer.parseInt(tokens[i]);
               if (val > 0) {
                  detect = true;
                  int var10002 = expressedGenesPerSample[i - 1]++;
               } else {
                  ++nbZeros;
               }
            }
         } else {
            detect = true;
         }

         if (detect) {
            FilterJSON var10000 = filtJSON;
            var10000.nber_zeros += (long)nbZeros;
            bw.write(line + "\n");
         } else {
            bw_filt.write(geneNames[nbGenes] + "\n");
            ++filtJSON.nber_filtered_genes;
         }

         ++nbGenes;
      }

      filtJSON.nber_genes = nbGenes;
      if (filtJSON.nber_genes != parsJSON.nber_genes) {
         new ErrorJSON("Detected different number of genes between parsingJSON(" + parsJSON.nber_genes + ") and Data Matrix(" + filtJSON.nber_genes + ")");
      }

      if (parsJSON.is_count_table && filtJSON.nber_filtered_genes > 0) {
         filtJSON.info = "Since dataset is a count matrix ASAP filtered out " + filtJSON.nber_filtered_genes + " genes which expression was 0 for all samples.";
      }

      yCol = "Nb Expressed Genes [reads > 0]";
      bw_filt.close();
      br.close();
   }

   public static void readHeader(String header) throws IOException {
      String[] cells = header.split("\t");
      filtJSON.nber_cells = cells.length - 1;
      if (filtJSON.nber_cells != parsJSON.nber_cells) {
         new ErrorJSON("Detected different number of cells between parsingJSON(" + parsJSON.nber_cells + ") and Header(" + filtJSON.nber_cells + ")");
      }

      bw.write(header + "\n");
      cellNames = new String[filtJSON.nber_cells];

      for(int i = 1; i < cells.length; ++i) {
         cellNames[i - 1] = cells[i];
      }

   }

   public static void filterCPM() throws IOException {
      BufferedWriter bw_filt = new BufferedWriter(new FileWriter(Parameters.outputFolder + "filtered.genes.txt"));
      filtJSON.nber_filtered_genes = 0;
      filtJSON.nber_zeros = 0L;
      getStatsOnFile(false);
      BufferedReader br = new BufferedReader(new FileReader(Parameters.fileName));
      expressedGenesPerSample = new int[filtJSON.nber_cells];
      String line = br.readLine();
      line = br.readLine();

      for(int nbGenes = 0; line != null; line = br.readLine()) {
         int[] arrayDetected = new int[filtJSON.nber_cells];
         String[] tokens = line.split("\t");
         int detected = 0;
         int nbZeros = 0;

         int i;
         for(i = 1; i < tokens.length; ++i) {
            int val = Integer.parseInt(tokens[i]);
            double cpm = (double)val / colSum[i - 1] * 1000000.0D;
            if (val == 0) {
               ++nbZeros;
            }

            if (cpm > (double)Parameters.nbCountsPerCell) {
               ++arrayDetected[i - 1];
               ++detected;
            }
         }

         if (detected < Parameters.nbCellsDetected) {
            bw_filt.write(geneNames[nbGenes] + "\n");
            ++filtJSON.nber_filtered_genes;
         } else {
            FilterJSON var10000 = filtJSON;
            var10000.nber_zeros += (long)nbZeros;

            for(i = 0; i < filtJSON.nber_cells; ++i) {
               int[] var12 = expressedGenesPerSample;
               var12[i] += arrayDetected[i];
            }

            bw.write(line + "\n");
         }

         ++nbGenes;
      }

      yCol = "Nb Expressed Genes [CPM > " + Parameters.nbCountsPerCell + "]";
      bw_filt.close();
      br.close();
   }

   public static void filterEXPRESSED() throws IOException {
      BufferedWriter bw_filt = new BufferedWriter(new FileWriter(Parameters.outputFolder + "filtered.genes.txt"));
      filtJSON.nber_filtered_genes = 0;
      filtJSON.nber_zeros = 0L;
      getStatsOnFile(false);
      expressedGenesPerSample = new int[filtJSON.nber_cells];
      double threshold = Utils.quartile(rowSum, Parameters.pcKept);
      BufferedReader br = new BufferedReader(new FileReader(Parameters.fileName));
      String line = br.readLine();
      line = br.readLine();

      for(int nbGenes = 0; line != null; line = br.readLine()) {
         int[] arrayDetected = new int[filtJSON.nber_cells];
         String[] tokens = line.split("\t");
         double sum = 0.0D;
         int nbZeros = 0;

         int i;
         for(i = 1; i < tokens.length; ++i) {
            double val = Double.parseDouble(tokens[i]);
            if (val == 0.0D) {
               ++nbZeros;
            } else {
               arrayDetected[i - 1] = 1;
            }

            sum += val;
         }

         if (!(sum > threshold)) {
            bw_filt.write(geneNames[nbGenes] + "\n");
            ++filtJSON.nber_filtered_genes;
         } else {
            if (parsJSON.is_count_table) {
               for(i = 0; i < tokens.length - 1; ++i) {
                  int[] var10000 = expressedGenesPerSample;
                  var10000[i] += arrayDetected[i];
               }
            } else {
               for(i = 0; i < tokens.length - 1; ++i) {
                  int var10002 = expressedGenesPerSample[i]++;
               }
            }

            FilterJSON var14 = filtJSON;
            var14.nber_zeros += (long)nbZeros;
            bw.write(line + "\n");
         }

         ++nbGenes;
      }

      yCol = "Nb Expressed Genes [reads > 0]";
      filtJSON.info = filtJSON.nber_filtered_genes + " genes where sum of expression <= " + Utils.format(threshold) + " across " + filtJSON.nber_cells + " cells [mean <= " + Utils.format(threshold / (double)filtJSON.nber_cells) + "] were filtered out.";
      bw_filt.close();
      br.close();
   }

   public static void filterVAR() throws IOException {
      BufferedWriter bw_filt = new BufferedWriter(new FileWriter(Parameters.outputFolder + "filtered.genes.txt"));
      filtJSON.nber_filtered_genes = 0;
      filtJSON.nber_zeros = 0L;
      getStatsOnFile(false);
      expressedGenesPerSample = new int[filtJSON.nber_cells];
      double threshold = Utils.quartile(rowVar, Parameters.pcKept);
      BufferedReader br = new BufferedReader(new FileReader(Parameters.fileName));
      String line = br.readLine();
      line = br.readLine();

      for(int nbGenes = 0; line != null; line = br.readLine()) {
         int[] arrayDetected = new int[filtJSON.nber_cells];
         String[] tokens = line.split("\t");
         int nbZeros = 0;
         double mean = 0.0D;
         double M2 = 0.0D;

         for(int i = 1; i < tokens.length; ++i) {
            double val = Double.parseDouble(tokens[i]);
            if (val == 0.0D) {
               ++nbZeros;
            } else {
               arrayDetected[i - 1] = 1;
            }

            double delta = val - mean;
            mean += delta / (double)i;
            M2 += delta * (val - mean);
         }

         double var = M2 / (double)(tokens.length - 2);
         if (!(var > threshold)) {
            bw_filt.write(geneNames[nbGenes] + "\n");
            ++filtJSON.nber_filtered_genes;
         } else {
            int i;
            if (parsJSON.is_count_table) {
               for(i = 0; i < tokens.length - 1; ++i) {
                  int[] var10000 = expressedGenesPerSample;
                  var10000[i] += arrayDetected[i];
               }
            } else {
               for(i = 0; i < tokens.length - 1; ++i) {
                  int var10002 = expressedGenesPerSample[i]++;
               }
            }

            FilterJSON var19 = filtJSON;
            var19.nber_zeros += (long)nbZeros;
            bw.write(line + "\n");
         }

         ++nbGenes;
      }

      yCol = "Nb Expressed Genes [reads > 0]";
      filtJSON.info = filtJSON.nber_filtered_genes + " genes where variance <= " + Utils.format(threshold) + " across " + filtJSON.nber_cells + " cells were filtered out.";
      bw_filt.close();
      br.close();
   }

   public static void filterCOEFFOFVAR() throws IOException {
      BufferedWriter bw_filt = new BufferedWriter(new FileWriter(Parameters.outputFolder + "filtered.genes.txt"));
      filtJSON.nber_filtered_genes = 0;
      filtJSON.nber_zeros = 0L;
      getStatsOnFile(false);
      expressedGenesPerSample = new int[filtJSON.nber_cells];
      double threshold = Utils.quartile(rowCoeffOfVar, Parameters.pcKept);
      BufferedReader br = new BufferedReader(new FileReader(Parameters.fileName));
      String line = br.readLine();
      line = br.readLine();

      for(int nbGenes = 0; line != null; line = br.readLine()) {
         int[] arrayDetected = new int[filtJSON.nber_cells];
         String[] tokens = line.split("\t");
         int nbZeros = 0;
         double mean = 0.0D;
         double M2 = 0.0D;

         for(int i = 1; i < tokens.length; ++i) {
            double val = Double.parseDouble(tokens[i]);
            if (val == 0.0D) {
               ++nbZeros;
            } else {
               arrayDetected[i - 1] = 1;
            }

            double delta = val - mean;
            mean += delta / (double)i;
            M2 += delta * (val - mean);
         }

         double ecartType = Math.sqrt(M2 / (double)(tokens.length - 2));
         double coeffOfVar = ecartType / mean;
         if (mean == 0.0D) {
            coeffOfVar = 0.0D;
         }

         if (!(coeffOfVar > threshold)) {
            bw_filt.write(geneNames[nbGenes] + "\n");
            ++filtJSON.nber_filtered_genes;
         } else {
            int i;
            if (parsJSON.is_count_table) {
               for(i = 0; i < tokens.length - 1; ++i) {
                  int[] var10000 = expressedGenesPerSample;
                  var10000[i] += arrayDetected[i];
               }
            } else {
               for(i = 0; i < tokens.length - 1; ++i) {
                  int var10002 = expressedGenesPerSample[i]++;
               }
            }

            FilterJSON var19 = filtJSON;
            var19.nber_zeros += (long)nbZeros;
            bw.write(line + "\n");
         }

         ++nbGenes;
      }

      yCol = "Nb Expressed Genes [reads > 0]";
      filtJSON.info = filtJSON.nber_filtered_genes + " genes where coefficient of variation <= " + Utils.format(threshold) + " across " + filtJSON.nber_cells + " cells were filtered out.";
      bw_filt.close();
      br.close();
   }

   public static void filterSCLVM() throws IOException {
      BufferedWriter bw_filt = new BufferedWriter(new FileWriter(Parameters.outputFolder + "filtered.genes.txt"));
      filtJSON.nber_filtered_genes = 0;
      filtJSON.nber_zeros = 0L;
      double[][] dataset = getStatsOnFile(true);
      double[] sizeFactors = new double[filtJSON.nber_cells];

      for(int i = 0; i < filtJSON.nber_cells; ++i) {
         ArrayList<Double> sub = new ArrayList();

         int j;
         for(j = 0; j < parsJSON.nber_genes; ++j) {
            if (Double.isFinite(loggeomeans[j]) && dataset[i][j] > 0.0D) {
               sub.add(Math.log(dataset[i][j]) - loggeomeans[j]);
            }
         }

         sizeFactors[i] = Math.exp(Utils.median(sub));

         for(j = 0; j < parsJSON.nber_genes; ++j) {
            dataset[i][j] /= sizeFactors[i];
         }
      }

      System.out.println();
      Stats.startRHandle(true);
      Stats.scLVM(dataset);
      Stats.stopRHandle();
      yCol = "Nb Expressed Genes [reads > 0]";
      bw_filt.close();
   }

   public static double[][] getStatsOnFile(boolean getdataset) throws IOException {
      BufferedReader br = new BufferedReader(new FileReader(Parameters.fileName));
      String line = br.readLine();
      readHeader(line);
      double[][] dataset = null;
      if (Parameters.nbCellsDetected > filtJSON.nber_cells) {
         new ErrorJSON("'Min Detected' should be smaller than the total number of cells/samples i.e. <=" + filtJSON.nber_cells);
      }

      if (getdataset) {
         dataset = new double[filtJSON.nber_cells][parsJSON.nber_genes];
      }

      geneNames = new String[parsJSON.nber_genes];
      colSum = new double[filtJSON.nber_cells];
      rowSum = new double[parsJSON.nber_genes];
      rowVar = new double[parsJSON.nber_genes];
      rowCoeffOfVar = new double[parsJSON.nber_genes];
      loggeomeans = new double[parsJSON.nber_genes];
      sizeFactors = new double[filtJSON.nber_cells];
      int nbGenes = 0;

      for(line = br.readLine(); line != null; line = br.readLine()) {
         String[] tokens = line.split("\t");
         double mean = 0.0D;
         double M2 = 0.0D;
         geneNames[nbGenes] = tokens[0];

         double[] var10000;
         for(int i = 1; i < tokens.length; ++i) {
            double val = Double.parseDouble(tokens[i]);
            double delta = val - mean;
            mean += delta / (double)i;
            M2 += delta * (val - mean);
            var10000 = colSum;
            var10000[i - 1] += val;
            var10000 = rowSum;
            var10000[nbGenes] += val;
            var10000 = loggeomeans;
            var10000[nbGenes] += Math.log(val);
            if (getdataset) {
               dataset[i - 1][nbGenes] = val;
            }
         }

         var10000 = loggeomeans;
         var10000[nbGenes] /= (double)(tokens.length - 1);
         rowVar[nbGenes] = M2 / (double)(tokens.length - 2);
         if (mean == 0.0D) {
            rowCoeffOfVar[nbGenes] = 0.0D;
         } else {
            rowCoeffOfVar[nbGenes] = Math.sqrt(rowVar[nbGenes]) / mean;
         }

         ++nbGenes;
      }

      br.close();
      filtJSON.nber_genes = nbGenes;
      if (filtJSON.nber_genes != parsJSON.nber_genes) {
         new ErrorJSON("Detected different number of genes between parsingJSON(" + parsJSON.nber_genes + ") and Data Matrix(" + filtJSON.nber_genes + ")");
      }

      return dataset;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$filtering$model$Model() {
      int[] var10000 = $SWITCH_TABLE$filtering$model$Model;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[Model.values().length];

         try {
            var0[Model.COEFFOFVAR.ordinal()] = 3;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[Model.CPM.ordinal()] = 7;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[Model.EXPRESSED.ordinal()] = 2;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[Model.NONE.ordinal()] = 1;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[Model.PAGODA.ordinal()] = 5;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[Model.SCANUPC.ordinal()] = 6;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[Model.SCLVM.ordinal()] = 8;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[Model.VAR.ordinal()] = 4;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$filtering$model$Model = var0;
         return var0;
      }
   }
}
