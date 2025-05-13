package model;

import db.DBManager;
import dim_reduction.model.Model;
import java.io.File;
import parsing.model.ColumnName;

public class Parameters {
   public static String organism_S = null;
   public static String outputFolder = null;
   public static String outputFile = null;
   public static String fileName = null;
   public static String fitModel = null;
   public static String erccFile = null;
   public static int organism = 1;
   public static int taxon = -1;
   public static float pcKept = -1.0F;
   public static Model dimReducModel = null;
   public static int perplexity = -1;
   public static filtering.model.Model filtModel = null;
   public static int nbCountsPerCell = -1;
   public static int nbCellsDetected = -1;
   public static String JSONFileName = null;
   public static int nbRepeat = -1;
   public static int randomSeed = 42;
   public static boolean isSilent = false;
   public static String adjMethod = "fdr";
   public static double probaCutoff = -1.0D;
   public static enrichment.model.Model enrichModel = null;
   public static int maxGenesInPathway = 500;
   public static int minGenesInPathway = 15;
   public static String pathwayFile = null;
   public static String backgroundFile = null;
   public static String listGenesFile = null;
   public static boolean has_header = true;
   public static int skip_line = 0;
   public static ColumnName name_column;
   public static String delimiter;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$model$Mode;

   static {
      name_column = ColumnName.FIRST;
      delimiter = "\t";
   }

   public static void load(String[] args, Mode m) {
      if (args.length == 0) {
         printHelp(m);
         System.exit(0);
      }

      switch($SWITCH_TABLE$model$Mode()[m.ordinal()]) {
      case 1:
         loadParsing(args);
         break;
      case 2:
         loadRegenerateNewOrganism(args);
         break;
      case 3:
         loadCreateDLFile(args);
      case 4:
      default:
         break;
      case 5:
         loadCreateEnrichmentDB(args);
         break;
      case 6:
         loadCreateEnsemblDB(args);
         break;
      case 7:
         loadEnrichment(args);
         break;
      case 8:
         loadFiltering(args);
         break;
      case 9:
         loadDimensionReduction(args);
      }

   }

   public static void loadEnrichment(String[] args) {
      for(int i = 0; i < args.length; ++i) {
         String arg = args[i];
         if (arg.startsWith("-")) {
            switch(arg.hashCode()) {
            case -590626533:
               if (arg.equals("-background")) {
                  ++i;
                  backgroundFile = args[i];
                  continue;
               }
               break;
            case 1504:
               if (arg.equals("-m")) {
                  ++i;
                  String var4;
                  switch((var4 = args[i]).hashCode()) {
                  case -114316955:
                     if (var4.equals("hypergeo")) {
                        enrichModel = enrichment.model.Model.HyperGeometric;
                        continue;
                     }
                     break;
                  case 101269:
                     if (var4.equals("fet")) {
                        enrichModel = enrichment.model.Model.FET;
                        continue;
                     }
                     break;
                  case 3182216:
                     if (var4.equals("gsea")) {
                        enrichModel = enrichment.model.Model.GSEA;
                        continue;
                     }
                  }

                  System.err.println("The entered model, " + args[i] + ", does not exist!\nIt should be one of the following: [gsea, hypergeo, fet]");
                  System.exit(-1);
                  continue;
               }
               break;
            case 1505:
               if (arg.equals("-n")) {
                  ++i;

                  try {
                     nbRepeat = Integer.parseInt(args[i]);
                  } catch (NumberFormatException var9) {
                     System.err.println("The '-n' option should be followed by an Integer. You entered " + args[i]);
                     System.exit(-1);
                  }
                  continue;
               }
               break;
            case 1506:
               if (arg.equals("-o")) {
                  ++i;
                  outputFolder = args[i];
                  outputFolder = outputFolder.replaceAll("\\\\", "/");
                  if (!outputFolder.endsWith("/")) {
                     outputFolder = outputFolder + "/";
                  }

                  (new File(outputFolder)).mkdirs();
                  continue;
               }
               break;
            case 1507:
               if (arg.equals("-p")) {
                  ++i;

                  try {
                     probaCutoff = Double.parseDouble(args[i]);
                     if (!(probaCutoff < 0.0D) && !(probaCutoff > 1.0D)) {
                        continue;
                     }

                     throw new NumberFormatException();
                  } catch (NumberFormatException var10) {
                     System.err.println("The '-p' option should be followed by an Double value in [0, 1]. You entered " + args[i]);
                     System.exit(-1);
                     continue;
                  }
               }
               break;
            case 1510:
               if (arg.equals("-s")) {
                  ++i;

                  try {
                     randomSeed = Integer.parseInt(args[i]);
                  } catch (NumberFormatException var8) {
                     System.err.println("The '-s' option should be followed by an Integer value. You entered " + args[i]);
                     System.exit(-1);
                  }
                  continue;
               }
               break;
            case 1437018:
               if (arg.equals("-adj")) {
                  ++i;
                  adjMethod = args[i];
                  if (!adjMethod.equals("bonferroni") && !adjMethod.equals("fdr") && !adjMethod.equals("none")) {
                     System.err.println("The '-adj' option should be followed by one of those values: [bonferroni, fdr, none]. You entered " + args[i]);
                     System.exit(-1);
                  }
                  continue;
               }
               break;
            case 1448471:
               if (arg.equals("-max")) {
                  ++i;

                  try {
                     maxGenesInPathway = Integer.parseInt(args[i]);
                     if (maxGenesInPathway < 0) {
                        throw new NumberFormatException();
                     }
                  } catch (NumberFormatException var7) {
                     System.err.println("The '-max' option should be followed by an positive Integer value. You entered " + args[i]);
                     System.exit(-1);
                  }
                  continue;
               }
               break;
            case 1448709:
               if (arg.equals("-min")) {
                  ++i;

                  try {
                     minGenesInPathway = Integer.parseInt(args[i]);
                     if (minGenesInPathway < 0) {
                        throw new NumberFormatException();
                     }
                  } catch (NumberFormatException var6) {
                     System.err.println("The '-min' option should be followed by an positive Integer value. You entered " + args[i]);
                     System.exit(-1);
                  }
                  continue;
               }
               break;
            case 44991954:
               if (arg.equals("-path")) {
                  ++i;
                  pathwayFile = args[i];
                  continue;
               }
               break;
            case 45114943:
               if (arg.equals("-test")) {
                  ++i;
                  listGenesFile = args[i];
                  continue;
               }
               break;
            case 380632770:
               if (arg.equals("-silent")) {
                  isSilent = true;
                  continue;
               }
            }

            System.err.println("Unused argument: " + arg);
         }
      }

      if (enrichModel == null || pathwayFile == null || outputFolder == null || backgroundFile == null || listGenesFile == null) {
         if (enrichModel == null) {
            System.err.println("No model is specified, please choose a model by using the '-m' option.");
         }

         if (pathwayFile == null) {
            System.err.println("No pathway file is specified, please choose a data file by using the '-path' option.");
         }

         if (outputFolder == null) {
            System.err.println("No output folder is specified, please choose an output file by using the '-o' option.");
         }

         if (backgroundFile == null) {
            System.err.println("No background file is specified, please choose a background file by using the '-background' option.");
         }

         if (listGenesFile == null) {
            System.err.println("No gene list file is specified, please choose a gene list file by using the '-test' option.");
         }

         System.out.println();
         if (!isSilent) {
            System.err.println("Note: Enrichment cannot be run because files are missing.");
         }

         System.exit(-1);
      }

      (new File(outputFolder)).mkdirs();
      if (enrichModel == enrichment.model.Model.GSEA && nbRepeat == -1) {
         System.out.println("The model '" + enrichModel + "' is using a permutation resampling, but you did not specify the number of permutation to perform using the '-n' option. Using default number: 10000.");
         nbRepeat = 10000;
      }

      if ((enrichModel == enrichment.model.Model.FET || enrichModel == enrichment.model.Model.HyperGeometric) && probaCutoff == -1.0D) {
         System.out.println("The model '" + enrichModel + "' requires a probability cutoff for considering a gene as deregulated, but you did not specify this number by using the '-p' option. Using default value: 0.05.");
         probaCutoff = 0.05D;
      }

   }

   public static void loadCreateEnsemblDB(String[] args) {
      boolean found = false;

      label56:
      for(int i = 0; i < args.length; ++i) {
         String arg = args[i];
         if (arg.startsWith("-")) {
            switch(arg.hashCode()) {
            case 1506:
               if (arg.equals("-o")) {
                  ++i;
                  outputFolder = args[i];
                  outputFolder = outputFolder.replaceAll("\\\\", "/");
                  if (!outputFolder.endsWith("/")) {
                     outputFolder = outputFolder + "/";
                  }

                  (new File(outputFolder)).mkdirs();
                  continue;
               }
               break;
            case 1585316863:
               if (arg.equals("-organism")) {
                  ++i;
                  organism_S = args[i];
                  String[] var8;
                  int var7 = (var8 = DBManager.species).length;
                  int var6 = 0;

                  while(true) {
                     if (var6 >= var7) {
                        continue label56;
                     }

                     String spe = var8[var6];
                     if (spe.equals(organism_S)) {
                        found = true;
                        continue label56;
                     }

                     ++var6;
                  }
               }
            }

            System.err.println("Unused argument: " + arg);
         }
      }

      if (outputFolder == null || organism_S == null) {
         printHelp(Mode.CreateEnsemblDB);
         System.exit(-1);
      }

      if (!found) {
         System.err.println("This organism (" + organism_S + ") was not found in our database.");
         System.exit(-1);
      }

   }

   public static void loadCreateEnrichmentDB(String[] args) {
      for(int i = 0; i < args.length; ++i) {
         String arg = args[i];
         if (arg.startsWith("-")) {
            switch(arg.hashCode()) {
            case 1506:
               if (arg.equals("-o")) {
                  ++i;
                  outputFolder = args[i];
                  outputFolder = outputFolder.replaceAll("\\\\", "/");
                  if (!outputFolder.endsWith("/")) {
                     outputFolder = outputFolder + "/";
                  }

                  (new File(outputFolder)).mkdirs();
                  continue;
               }
               break;
            case 1398448829:
               if (arg.equals("-taxon")) {
                  ++i;

                  try {
                     taxon = Integer.parseInt(args[i]);
                     if (DBManager.findTaxon(taxon) == -1) {
                        System.err.println("This taxon: " + taxon + " is not in the database.");
                        System.exit(-1);
                     }
                  } catch (NumberFormatException var5) {
                     System.err.println("The '-taxon' option should be followed by an Integer. You entered " + args[i]);
                     System.exit(-1);
                  }
                  continue;
               }
            }

            System.err.println("Unused argument: " + arg);
         }
      }

      if (outputFolder == null) {
         printHelp(Mode.CreateEnrichmentDB);
         System.exit(-1);
      }

   }

   public static void loadCreateDLFile(String[] args) {
      for(int i = 0; i < args.length; ++i) {
         String arg = args[i];
         if (arg.startsWith("-")) {
            switch(arg.hashCode()) {
            case 1497:
               if (arg.equals("-f")) {
                  ++i;
                  fileName = args[i];
                  fileName = fileName.replaceAll("\\\\", "/");
                  continue;
               }
               break;
            case 1501:
               if (arg.equals("-j")) {
                  ++i;
                  JSONFileName = args[i];
                  JSONFileName = JSONFileName.replaceAll("\\\\", "/");
                  continue;
               }
               break;
            case 1506:
               if (arg.equals("-o")) {
                  ++i;
                  outputFile = args[i];
                  outputFile = outputFile.replaceAll("\\\\", "/");
                  continue;
               }
            }

            System.err.println("Unused argument: " + arg);
         }
      }

      if (outputFile == null || fileName == null || JSONFileName == null) {
         printHelp(Mode.CreateDLFile);
         System.exit(-1);
      }

   }

   public static void loadRegenerateNewOrganism(String[] args) {
      for(int i = 0; i < args.length; ++i) {
         String arg = args[i];
         if (arg.startsWith("-")) {
            switch(arg.hashCode()) {
            case 1501:
               if (arg.equals("-j")) {
                  ++i;
                  JSONFileName = args[i];
                  JSONFileName = JSONFileName.replaceAll("\\\\", "/");
                  continue;
               }
               break;
            case 1506:
               if (arg.equals("-o")) {
                  ++i;
                  outputFolder = args[i];
                  outputFolder = outputFolder.replaceAll("\\\\", "/");
                  if (!outputFolder.endsWith("/")) {
                     outputFolder = outputFolder + "/";
                  }

                  (new File(outputFolder)).mkdirs();
                  continue;
               }
               break;
            case 1585316863:
               if (arg.equals("-organism")) {
                  ++i;

                  try {
                     organism = Integer.parseInt(args[i]);
                  } catch (NumberFormatException var5) {
                     System.err.println("The '-organism' option should be followed by an Integer. You entered " + args[i]);
                     System.exit(-1);
                  }
                  continue;
               }
            }

            System.err.println("Unused argument: " + arg);
         }
      }

      if (JSONFileName == null || outputFolder == null) {
         printHelp(Mode.RegenerateNewOrganism);
         System.exit(-1);
      }

   }

   public static void loadParsing(String[] args) {
      for(int i = 0; i < args.length; ++i) {
         String arg = args[i];
         if (arg.startsWith("-")) {
            switch(arg.hashCode()) {
            case 1495:
               if (arg.equals("-d")) {
                  ++i;
                  delimiter = args[i];
                  continue;
               }
               break;
            case 1497:
               if (arg.equals("-f")) {
                  ++i;
                  fileName = args[i];
                  fileName = fileName.replaceAll("\\\\", "/");
                  continue;
               }
               break;
            case 1506:
               if (arg.equals("-o")) {
                  ++i;
                  outputFolder = args[i];
                  outputFolder = outputFolder.replaceAll("\\\\", "/");
                  if (!outputFolder.endsWith("/")) {
                     outputFolder = outputFolder + "/";
                  }

                  (new File(outputFolder)).mkdirs();
                  continue;
               }
               break;
            case 1439283:
               if (arg.equals("-col")) {
                  ++i;
                  String var4;
                  switch((var4 = args[i]).hashCode()) {
                  case 3314326:
                     if (var4.equals("last")) {
                        name_column = ColumnName.LAST;
                        continue;
                     }
                     break;
                  case 3387192:
                     if (var4.equals("none")) {
                        name_column = ColumnName.NONE;
                        continue;
                     }
                     break;
                  case 97440432:
                     if (var4.equals("first")) {
                        name_column = ColumnName.FIRST;
                        continue;
                     }
                  }

                  System.err.println("The '-col' option should be followed by [last, first, none]. You entered " + args[i]);
                  System.exit(-1);
                  continue;
               }
               break;
            case 45090604:
               if (arg.equals("-skip")) {
                  ++i;

                  try {
                     skip_line = Integer.parseInt(args[i]);
                  } catch (NumberFormatException var6) {
                     System.err.println("The '-s' option should be followed by an Integer. You entered " + args[i]);
                     System.exit(-1);
                  }
                  continue;
               }
               break;
            case 61689082:
               if (arg.equals("-header")) {
                  ++i;
                  has_header = Boolean.parseBoolean(args[i]);
                  continue;
               }
               break;
            case 1585316863:
               if (arg.equals("-organism")) {
                  ++i;

                  try {
                     organism = Integer.parseInt(args[i]);
                  } catch (NumberFormatException var5) {
                     System.err.println("The '-organism' option should be followed by an Integer. You entered " + args[i]);
                     System.exit(-1);
                  }
                  continue;
               }
            }

            System.err.println("Unused argument: " + arg);
         }
      }

      if (outputFolder == null || fileName == null) {
         printHelp(Mode.Parsing);
         System.exit(-1);
      }

   }

   public static void loadFiltering(String[] args) {
      for(int i = 0; i < args.length; ++i) {
         String arg = args[i];
         if (arg.startsWith("-")) {
            switch(arg.hashCode()) {
            case 1497:
               if (arg.equals("-f")) {
                  ++i;
                  fileName = args[i];
                  fileName = fileName.replaceAll("\\\\", "/");
                  continue;
               }
               break;
            case 1504:
               if (arg.equals("-m")) {
                  ++i;
                  String var4;
                  switch((var4 = args[i]).hashCode()) {
                  case -2046831209:
                     if (var4.equals("coeffofvar")) {
                        filtModel = filtering.model.Model.COEFFOFVAR;
                        ++i;

                        try {
                           pcKept = Float.parseFloat(args[i]) / 100.0F;
                        } catch (NumberFormatException var13) {
                           System.err.println("The '-m coeffofvar' model should be followed by a Float: 'pcKept'. The value you entered is not a FLoat: " + args[i]);
                           System.exit(-1);
                        } catch (ArrayIndexOutOfBoundsException var14) {
                           new ErrorJSON("The '-m coeffofvar' model should be followed by a Float: 'pcKept'. This parameter is missing!");
                        }

                        if (pcKept <= 0.0F || pcKept > 1.0F) {
                           new ErrorJSON("The '-m coeffofvar' model should be followed by a Float in ]0, 100]. You entered '" + pcKept * 100.0F + "'.");
                        }
                        continue;
                     }
                     break;
                  case -995742506:
                     if (var4.equals("pagoda")) {
                        filtModel = filtering.model.Model.PAGODA;
                        continue;
                     }
                     break;
                  case 98720:
                     if (var4.equals("cpm")) {
                        filtModel = filtering.model.Model.CPM;
                        ++i;

                        try {
                           nbCountsPerCell = Integer.parseInt(args[i]);
                        } catch (NumberFormatException var9) {
                           new ErrorJSON("The '-m cpm' model should be followed by two Integers: 'nbCountsPerCell nbCellsDetected'. The first one you entered is not an Integer: " + args[i]);
                        } catch (ArrayIndexOutOfBoundsException var10) {
                           new ErrorJSON("The '-m cpm' model should be followed by two Integers: 'nbCountsPerCell nbCellsDetected'. The first one is missing!");
                        }

                        ++i;

                        try {
                           nbCellsDetected = Integer.parseInt(args[i]);
                        } catch (NumberFormatException var7) {
                           new ErrorJSON("The '-m cpm' model should be followed by two Integers: 'nbCountsPerCell nbCellsDetected'. The second one you entered is not an Integer: " + args[i]);
                        } catch (ArrayIndexOutOfBoundsException var8) {
                           new ErrorJSON("The '-m cpm' model should be followed by two Integers: 'nbCountsPerCell nbCellsDetected'. The second one is missing!");
                        }

                        if (nbCountsPerCell < 0 || nbCellsDetected < 0) {
                           new ErrorJSON("The '-m cpm' model should be followed by two positive Integers. You entered '" + nbCountsPerCell + " " + nbCellsDetected + "'.");
                        }
                        continue;
                     }
                     break;
                  case 116519:
                     if (var4.equals("var")) {
                        filtModel = filtering.model.Model.VAR;
                        ++i;

                        try {
                           pcKept = Float.parseFloat(args[i]) / 100.0F;
                        } catch (NumberFormatException var11) {
                           System.err.println("The '-m var' model should be followed by a Float: 'pcKept'. The value you entered is not a FLoat: " + args[i]);
                           System.exit(-1);
                        } catch (ArrayIndexOutOfBoundsException var12) {
                           new ErrorJSON("The '-m var' model should be followed by a Float: 'pcKept'. This parameter is missing!");
                        }

                        if (pcKept <= 0.0F || pcKept > 1.0F) {
                           new ErrorJSON("The '-m var' model should be followed by a Float in ]0, 100]. You entered '" + pcKept * 100.0F + "'.");
                        }
                        continue;
                     }
                     break;
                  case 3387192:
                     if (var4.equals("none")) {
                        filtModel = filtering.model.Model.NONE;
                        continue;
                     }
                     break;
                  case 109230003:
                     if (var4.equals("scLVM")) {
                        filtModel = filtering.model.Model.SCLVM;
                        ++i;

                        try {
                           fitModel = args[i];
                        } catch (ArrayIndexOutOfBoundsException var6) {
                           new ErrorJSON("The '-m scLVM' model should be followed by a fit model: 'log' or 'logvar'. This parameter is missing!");
                        }

                        if (!fitModel.equals("log") && !fitModel.equals("logvar")) {
                           new ErrorJSON("The '-m scLVM' model should be followed by a fit model: 'log' or 'logvar'. You entered something different: " + args[i]);
                        }

                        ++i;
                        erccFile = args[i];
                        erccFile = erccFile.replaceAll("\\\\", "/");
                        if (!(new File(erccFile)).exists() && !erccFile.equals("null")) {
                           new ErrorJSON("The ERCC file you specified for the '-m scLVM' model does not exist . You entered '" + erccFile + "'. If no ERCC, please put 'null'");
                        }

                        if (erccFile.equals("null")) {
                           erccFile = null;
                        }
                        continue;
                     }
                     break;
                  case 496271375:
                     if (var4.equals("expressed")) {
                        filtModel = filtering.model.Model.EXPRESSED;
                        ++i;

                        try {
                           pcKept = Float.parseFloat(args[i]) / 100.0F;
                        } catch (NumberFormatException var15) {
                           System.err.println("The '-m expressed' model should be followed by a Float: 'pcKept'. The value you entered is not a FLoat: " + args[i]);
                           System.exit(-1);
                        } catch (ArrayIndexOutOfBoundsException var16) {
                           new ErrorJSON("The '-m expressed' model should be followed by a Float: 'pcKept'. This parameter is missing!");
                        }

                        if (pcKept <= 0.0F || pcKept > 1.0F) {
                           new ErrorJSON("The '-m expressed' model should be followed by a Float in ]0, 100]. You entered '" + pcKept * 100.0F + "'.");
                        }
                        continue;
                     }
                     break;
                  case 1910968715:
                     if (var4.equals("scanupc")) {
                        filtModel = filtering.model.Model.SCANUPC;
                        continue;
                     }
                  }

                  new ErrorJSON("The entered model, " + args[i] + ", does not exist!\nIt should be one of the following: [none, expressed, coeffofvar, var, pagoda, scanupc, cpm, scLVM]");
                  continue;
               }
               break;
            case 1506:
               if (arg.equals("-o")) {
                  ++i;
                  outputFolder = args[i];
                  outputFolder = outputFolder.replaceAll("\\\\", "/");
                  if (!outputFolder.endsWith("/")) {
                     outputFolder = outputFolder + "/";
                  }

                  (new File(outputFolder)).mkdirs();
                  continue;
               }
               break;
            case 44830357:
               if (arg.equals("-json")) {
                  ++i;
                  JSONFileName = args[i];
                  JSONFileName = JSONFileName.replaceAll("\\\\", "/");
                  continue;
               }
            }

            System.err.println("Unused argument: " + arg);
         }
      }

      if (filtModel == null || outputFolder == null || fileName == null || JSONFileName == null) {
         printHelp(Mode.Filtering);
         String error = "Filtering cannot be run because parameters are missing:\n";
         if (filtModel == null) {
            error = error + "No model is specified, please choose a model by using the '-m' option.\n";
         }

         if (fileName == null) {
            error = error + "No file is specified, please choose a data file by using the '-f' option.\n";
         }

         if (outputFolder == null) {
            error = error + "No output folder is specified, please choose an output file by using the '-o' option.\n";
         }

         if (JSONFileName == null) {
            error = error + "No JSON input file is specified, please choose a JSON file by using the '-json' option.\n";
         }

         new ErrorJSON(error);
      }

      (new File(outputFolder)).mkdirs();
      if (filtModel == filtering.model.Model.CPM && (nbCountsPerCell == -1 || nbCellsDetected == -1)) {
         new ErrorJSON("The '-m cpm' model should be followed by two Integers: 'nbCountsPerCell nbCellsDetected'.");
      }

      if (filtModel == filtering.model.Model.EXPRESSED && pcKept == -1.0F) {
         new ErrorJSON("The '-m expressed' model should be followed by one float: 'pcKept'.");
      }

      if (filtModel == filtering.model.Model.VAR && pcKept == -1.0F) {
         new ErrorJSON("The '-m var' model should be followed by one float: 'pcKept'.");
      }

      if (filtModel == filtering.model.Model.COEFFOFVAR && pcKept == -1.0F) {
         new ErrorJSON("The '-m coeffofvar' model should be followed by one float: 'pcKept'.");
      }

      if (filtModel == filtering.model.Model.SCLVM && fitModel == null) {
         new ErrorJSON("The '-m scLVM' model should be followed by the 2 following arguments: 'fitmodel' [log, logvar] and 'erccFile' ['file path' or 'null'].");
      }

   }

   public static void loadDimensionReduction(String[] args) {
      for(int i = 0; i < args.length; ++i) {
         String arg = args[i];
         if (arg.startsWith("-")) {
            switch(arg.hashCode()) {
            case 1497:
               if (arg.equals("-f")) {
                  ++i;
                  fileName = args[i];
                  fileName = fileName.replaceAll("\\\\", "/");
                  continue;
               }
               break;
            case 1504:
               if (arg.equals("-m")) {
                  ++i;
                  String var4;
                  switch((var4 = args[i]).hashCode()) {
                  case 107964:
                     if (var4.equals("mds")) {
                        dimReducModel = Model.MDS;
                        continue;
                     }
                     break;
                  case 110798:
                     if (var4.equals("pca")) {
                        dimReducModel = Model.PCA;
                        continue;
                     }
                     break;
                  case 3569782:
                     if (var4.equals("tsne")) {
                        dimReducModel = Model.TSNE;
                        ++i;

                        try {
                           perplexity = Integer.parseInt(args[i]);
                        } catch (NumberFormatException var6) {
                           System.err.println("The '-m tsne' model should be followed by an Integer: 'perplexity'. The value you entered is not an Integer: " + args[i]);
                           System.exit(-1);
                        } catch (ArrayIndexOutOfBoundsException var7) {
                           new ErrorJSON("The '-m tsne' model should be followed by an Integer: 'perplexity'. This parameter is missing!");
                        }

                        if (perplexity <= 0) {
                           new ErrorJSON("The '-m tsne' model should be followed by an Integer: 'perplexity' greater than 0. You entered '" + perplexity + "'.");
                        }
                        continue;
                     }
                     break;
                  case 3738666:
                     if (var4.equals("zifa")) {
                        dimReducModel = Model.ZIFA;
                        continue;
                     }
                  }

                  new ErrorJSON("The entered model, " + args[i] + ", does not exist!\nIt should be one of the following: [pca, mds, tsne, zifa]");
                  continue;
               }
               break;
            case 1506:
               if (arg.equals("-o")) {
                  ++i;
                  outputFolder = args[i];
                  outputFolder = outputFolder.replaceAll("\\\\", "/");
                  if (!outputFolder.endsWith("/")) {
                     outputFolder = outputFolder + "/";
                  }

                  (new File(outputFolder)).mkdirs();
                  continue;
               }
            }

            System.err.println("Unused argument: " + arg);
         }
      }

      if (dimReducModel == null || outputFolder == null || fileName == null) {
         printHelp(Mode.DimensionReduction);
         String error = "Filtering cannot be run because parameters are missing:\n";
         if (dimReducModel == null) {
            error = error + "No model is specified, please choose a model by using the '-m' option.\n";
         }

         if (fileName == null) {
            error = error + "No file is specified, please choose a data file by using the '-f' option.\n";
         }

         if (outputFolder == null) {
            error = error + "No output folder is specified, please choose an output file by using the '-o' option.\n";
         }

         new ErrorJSON(error);
      }

      (new File(outputFolder)).mkdirs();
      if (dimReducModel == Model.TSNE && perplexity == -1) {
         new ErrorJSON("The '-m tsne' model should be followed by an Integer: 'perplexity'. This parameter is missing!");
      }

   }

   public static void printHelp(Mode m) {
      switch($SWITCH_TABLE$model$Mode()[m.ordinal()]) {
      case 1:
         System.out.println("Parsing Mode\n\nOptions:");
         System.out.println("-col %s \tName Column [none, first, last].");
         System.out.println("-o %s \t\tOutput folder.");
         System.out.println("-f %s \t\tFile to parse.");
         System.out.println("-organism %i \tId of the organism.");
         System.out.println("-header %b \tThe file has a header [true, false].");
         System.out.println("-d %s \t\tDelimiter.");
         System.out.println("-skip %i \tNumber of lines to skip at the beginning of the file.");
         break;
      case 2:
         System.out.println("RegenerateNewOrganism Mode\n\nOptions:");
         System.out.println("-organism %i \tId of the organism.");
         System.out.println("-o %s \t\tOutput folder where are the 'not_found_genes.txt', 'output.json' and 'gene_names.json' files to modify.");
         System.out.println("-j %s \t\tThe JSON file containing the gene names.");
         break;
      case 3:
         System.out.println("CreateDLFile Mode\n\nOptions:");
         System.out.println("-f %s \t\tThe matrix file with id of genes.");
         System.out.println("-o %s \t\tThe output file to create.");
         System.out.println("-j %s \t\tThe JSON file containing the gene names.");
      case 4:
      default:
         break;
      case 5:
         System.out.println("CreateEnrichmentDB\n\nOptions:");
         System.out.println("-o %s \tOutput folder.");
         System.out.println("-organism %i \tId of the organism.");
         break;
      case 6:
         System.out.println("CreateEnsemblDB\n\nOptions:");
         System.out.println("-o %s \tOutput folder.");
         System.out.println("-organism %s \tEnsembl name of the organism [mus_musculus, homo_sapiens, ...].");
         break;
      case 7:
         System.out.println("Enrichment Mode\n\nOptions:");
         System.out.println("-m %s \t\tChoose a model among [gsea, hypergeo, fet].");
         System.out.println("-o %s \t\tOutput folder");
         System.out.println("-path %s \tPathway/Gene Mapping file.");
         System.out.println("-background %s \tBackground file [matrix].");
         System.out.println("-test %s \tList genes to enrich file [JSON].");
         System.out.println("-n %i \t\tNumber of permutation resampling to perform for models [gsea].");
         System.out.println("-p %f \t\tProbability threshold for considering a gene as deregulated for models [fet, hypergeo].");
         System.out.println("-s %i \t\tRandom seed for the generator of pseudo-random numbers [default = 42].");
         System.out.println("-adj %s \tStatitical adjustment method for multiple comparision [bonferroni, fdr, or none, default = fdr].");
         System.out.println("-min %i \tMinimum number of genes in a pathway for being taken into consideration [default = 15].");
         System.out.println("-max %i \tMaximum number of genes in a pathway for being taken into consideration [default = 500].");
         System.out.println("-silent \tDo not print message in the standard output.");
         break;
      case 8:
         System.out.println("Filtering Mode\n\nOptions:");
         System.out.println("-json %s \t\tInput JSON file from parsing step.");
         System.out.println("-o %s \t\tOutput folder.");
         System.out.println("-f %s \t\tFile to parse.");
         System.out.println("-m %s \t\tModel to use for filtering. It should be one of the following: [none, expressed, coeffofvar, var, pagoda, scanupc, cpm, scLVM]");
         break;
      case 9:
         System.out.println("Dimension Reduction Mode\n\nOptions:");
         System.out.println("-o %s \t\tOutput folder.");
         System.out.println("-f %s \t\tFile to parse.");
         System.out.println("-m %s \t\tModel to use for dimension reduction. It should be one of the following: [pca, mds, tsne, zifa]");
      }

      System.out.println();
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
