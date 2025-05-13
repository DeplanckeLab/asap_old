import db.Config;
import db.DBManager;
import db.EnsemblDB;
import db.GODatabase;
import dim_reduction.FileDimReduc;
import enrichment.Enrichment;
import filtering.FileFilter;
import model.Mode;
import model.Parameters;
import parsing.CreateDLFile;
import parsing.FileParser;
import parsing.RegenerateNewOrganism;
import tools.Utils;

public class ASAP {
   public static Mode m = null;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$model$Mode;

   public static void main(String[] args) {
      DBManager.JDBC_DRIVER = Config.driver;
      DBManager.URL = Config.ConfigMAIN().getURL("asap");
      String[] args2 = readMode(args);
      Parameters.load(args2, m);
      long t;
      switch($SWITCH_TABLE$model$Mode()[m.ordinal()]) {
      case 1:
         t = System.currentTimeMillis();
         FileParser.dbGenes = DBManager.getGenesInDB();
         System.out.println("Accessing DB time: " + Utils.toReadableTime(System.currentTimeMillis() - t));
         t = System.currentTimeMillis();
         FileParser.parse();
         System.out.println("Parsing time: " + Utils.toReadableTime(System.currentTimeMillis() - t));
         break;
      case 2:
         RegenerateNewOrganism.regenerateJSON();
         break;
      case 3:
         CreateDLFile.getGenesFromJSON(Parameters.JSONFileName);
         CreateDLFile.create(Parameters.fileName, Parameters.outputFile);
         break;
      case 4:
         DBManager.createDB();
         break;
      case 5:
         DBManager.JDBC_DRIVER = "com.mysql.jdbc.Driver";
         DBManager.URL = "jdbc:mysql://mysql.ebi.ac.uk:4085/go_latest?user=go_select&password=amigo";
         if (Parameters.taxon == -1) {
            System.out.println("No Taxon entered. Computing everything.");
            int[] var5;
            int var4 = (var5 = DBManager.taxons).length;

            for(int var3 = 0; var3 < var4; ++var3) {
               int taxon = var5[var3];
               DBManager.connect();
               GODatabase.generateGODB(Parameters.outputFolder, taxon);
               DBManager.disconnect();
            }

            return;
         } else {
            DBManager.connect();
            GODatabase.generateGODB(Parameters.outputFolder, Parameters.taxon);
            DBManager.disconnect();
            break;
         }
      case 6:
         EnsemblDB.createEnsemblDB();
         break;
      case 7:
         Enrichment.readFiles();
         Enrichment.runEnrichment();
         break;
      case 8:
         t = System.currentTimeMillis();
         FileFilter.filter();
         System.out.println("Filtering time: " + Utils.toReadableTime(System.currentTimeMillis() - t));
         break;
      case 9:
         t = System.currentTimeMillis();
         FileDimReduc.reduceDimension();
         System.out.println("Dimension reduction time: " + Utils.toReadableTime(System.currentTimeMillis() - t));
      }

   }

   public static String[] readMode(String[] args) {
      String[] args2 = null;
      if (args.length >= 2) {
         args2 = new String[args.length - 2];
         int j = 0;

         for(int i = 0; i < args.length; ++i) {
            String arg = args[i];
            switch(arg.hashCode()) {
            case 1479:
               if (arg.equals("-T")) {
                  ++i;
                  String mode = args[i];
                  switch(mode.hashCode()) {
                  case -1663577139:
                     if (mode.equals("DimensionReduction")) {
                        m = Mode.DimensionReduction;
                        continue;
                     }
                     break;
                  case -1329636288:
                     if (mode.equals("CreateGenesDB")) {
                        m = Mode.CreateGenesDB;
                        continue;
                     }
                     break;
                  case -369117475:
                     if (mode.equals("CreateEnrichmentDB")) {
                        m = Mode.CreateEnrichmentDB;
                        continue;
                     }
                     break;
                  case 83093150:
                     if (mode.equals("CreateEnsemblDB")) {
                        m = Mode.CreateEnsemblDB;
                        continue;
                     }
                     break;
                  case 309996714:
                     if (mode.equals("RegenerateNewOrganism")) {
                        m = Mode.RegenerateNewOrganism;
                        continue;
                     }
                     break;
                  case 818143235:
                     if (mode.equals("Enrichment")) {
                        m = Mode.Enrichment;
                        continue;
                     }
                     break;
                  case 871689872:
                     if (mode.equals("Parsing")) {
                        m = Mode.Parsing;
                        continue;
                     }
                     break;
                  case 1122605386:
                     if (mode.equals("Filtering")) {
                        m = Mode.Filtering;
                        continue;
                     }
                     break;
                  case 1232418368:
                     if (mode.equals("CreateDLFile")) {
                        m = Mode.CreateDLFile;
                        continue;
                     }
                  }

                  System.err.println("Mode (-T) " + mode + " does not exist!");
                  System.out.println("-T %s \t\tMode to run ASAP [Parsing, RegenerateOutput, CreateEnrichmentDB, CreateGenesDB, createEnsemblDB, Enrichment].");
                  System.exit(-1);
                  break;
               }
            default:
               args2[j] = arg;
               ++j;
            }
         }
      }

      if (m == null || args.length < 2) {
         System.out.println("Argument -T is mandatory:");
         System.out.println("-T %s \t\tMode to run ASAP [Parsing, Filtering, RegenerateOutput, CreateEnrichmentDB, CreateGenesDB, CreateEnsemblDB, Enrichment].");
         System.exit(-1);
      }

      return args2;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$model$Mode() {
      int[] var10000 = $SWITCH_TABLE$model$Mode;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[Mode.values().length];

         try {
            var0[Mode.CreateDLFile.ordinal()] = 3;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[Mode.CreateEnrichmentDB.ordinal()] = 5;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[Mode.CreateEnsemblDB.ordinal()] = 6;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[Mode.CreateGenesDB.ordinal()] = 4;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[Mode.DimensionReduction.ordinal()] = 9;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[Mode.Enrichment.ordinal()] = 7;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[Mode.Filtering.ordinal()] = 8;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[Mode.Parsing.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[Mode.RegenerateNewOrganism.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$model$Mode = var0;
         return var0;
      }
   }
}
