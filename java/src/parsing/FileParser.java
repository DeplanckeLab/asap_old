package parsing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import model.Parameters;
import model.ParsingJSON;
import parsing.model.ColumnName;
import parsing.model.Gene;

public class FileParser {
   public static ParsingJSON json = null;
   public static String header = "";
   public static HashMap<String, ArrayList<Gene>> dbGenes = null;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$parsing$model$ColumnName;

   public static void parse() {
      json = new ParsingJSON();
      HashSet<String> ERCCs = new HashSet<String>();
      ArrayList<String> genes = new ArrayList<String>();
      HashMap<String, Integer> geneDups = new HashMap<String, Integer>();
      ArrayList<String> cell_names = new ArrayList<String>();
      System.out.println("Parsing file : " + Parameters.fileName);

      try {
         int current_line = 0;
         int num_columns = -1;
         int num_columns_header = -1;
         boolean headerWritten = false;
         BufferedReader br = null;
         if (Parameters.fileName.endsWith(".gz")) {
            br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(Parameters.fileName))));
         } else if (Parameters.fileName.endsWith(".zip")) {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(Parameters.fileName));
            zis.getNextEntry();
            br = new BufferedReader(new InputStreamReader(zis));
         } else {
            br = new BufferedReader(new FileReader(Parameters.fileName));
         }

         BufferedWriter bw_tab = new BufferedWriter(new FileWriter(Parameters.outputFolder + "output.tab"));
         BufferedWriter bw_dl_tab = new BufferedWriter(new FileWriter(Parameters.outputFolder + "dl_output.tab"));
         BufferedWriter bw_ercc = new BufferedWriter(new FileWriter(Parameters.outputFolder + "ercc.tab"));
         BufferedWriter bw_NF = new BufferedWriter(new FileWriter(Parameters.outputFolder + "not_found_genes.txt"));
         BufferedWriter bw_exonLength = new BufferedWriter(new FileWriter(Parameters.outputFolder + "sum_exon_length.txt"));

         String line;
         for(line = br.readLine(); current_line < Parameters.skip_line; ++current_line) {
            line = br.readLine();
         }

         String[] tokens;
         int col;
         int l;
         if (Parameters.has_header) {
            tokens = line.split(Parameters.delimiter);
            String[] var19 = tokens;
            l = tokens.length;
            col = 0;

            while(true) {
               if (col >= l) {
                  if (cell_names.size() == 0) {
                     Parameters.has_header = false;
                  } else {
                     num_columns_header = cell_names.size();
                  }

                  line = br.readLine();
                  ++current_line;
                  break;
               }

               String t = var19[col];
               String name = t.trim();
               if (name.startsWith("\"") || name.startsWith("'")) {
                  name = name.substring(1);
               }

               if (name.endsWith("\"") || name.endsWith("'")) {
                  name = name.substring(0, name.length() - 1);
               }

               if (!name.equals("")) {
                  cell_names.add(name);
               }

               ++col;
            }
         }

         for(; line != null; line = br.readLine()) {
            ++current_line;
            tokens = line.split(Parameters.delimiter);
            ArrayList<String> rowValues = new ArrayList<String>();
            col = 0;
            String[] var21 = tokens;
            int var54 = tokens.length;

            String gene;
            for(int var51 = 0; var51 < var54; ++var51) {
               String t = var21[var51];
               gene = t.trim();
               if (gene.startsWith("\"") || gene.startsWith("'")) {
                  gene = gene.substring(1);
               }

               if (gene.endsWith("\"") || gene.endsWith("'")) {
                  gene = gene.substring(0, gene.length() - 1);
               }

               if (gene.equals("NA")) {
                  error("Value at line " + current_line + " col " + col + " is NA, but this is not allowed in the current version of ASAP.");
               }

               if (!gene.equals("")) {
                  rowValues.add(gene);
               }

               ++col;
            }

            l = rowValues.size();
            if (rowValues.size() != 0) {
               if (num_columns == -1) {
                  num_columns = l;
               }

               if (num_columns != l) {
                  error("Row " + current_line + " contains a different number of values (" + rowValues.size() + ") than the other rows (" + num_columns + ")");
               }

               if (num_columns_header != -1 && num_columns != num_columns_header && num_columns != num_columns_header + 1) {
                  error("Row " + current_line + " contains a different number of values (" + rowValues.size() + ") than the header (" + num_columns_header + ")");
               }

               if (!headerWritten) {
                  createHeader(cell_names, num_columns, num_columns_header);
                  bw_tab.write(header + "\n");
                  bw_dl_tab.write(header + "\n");
                  bw_ercc.write(header + "\n");
                  headerWritten = true;
               }

               int start = 0;
               boolean isERCC = false;
               int end = num_columns;
               gene = null;
               String biotype = null;
               String chr = null;
               switch($SWITCH_TABLE$parsing$model$ColumnName()[Parameters.name_column.ordinal()]) {
               case 1:
                  gene = "Gene_" + (json.nber_genes + 1);
                  break;
               case 2:
                  gene = (String)rowValues.get(0);
                  start = 1;
                  break;
               case 3:
                  gene = (String)rowValues.get(num_columns - 1);
                  end = num_columns - 1;
               }

               String altIdList;
               if (gene.startsWith("ERCC-")) {
                  if (ERCCs.contains(gene)) {
                     error("Duplicated ERCCs are not allowed (at line " + current_line + ")");
                  }

                  ERCCs.add(gene);
                  bw_ercc.write(gene);
                  isERCC = true;
                  ++json.nber_ercc;
               } else {
                  ArrayList<Gene> dbHit = dbGenes.get(gene.toUpperCase());
                  String ensIdList = "";
                  String geneIdList = "";
                  altIdList = "";
                  long sumExonLength = -1L;
                  if (dbHit == null) {
                     bw_NF.write(gene + "\t" + current_line + "\n");
                     ++json.nber_not_found_genes;
                  } else {
                     ArrayList<String> unique_ensembl_list = new ArrayList<String>();
                     ArrayList<String> unique_gene_list = new ArrayList<String>();
                     ArrayList<String> unique_alt_list = new ArrayList<String>();
                     Iterator var35 = dbHit.iterator();

                     while(true) {
                        if (!var35.hasNext()) {
                           String alt;
                           for(var35 = unique_ensembl_list.iterator(); var35.hasNext(); ensIdList = ensIdList + alt + ",") {
                              alt = (String)var35.next();
                           }

                           for(var35 = unique_gene_list.iterator(); var35.hasNext(); geneIdList = geneIdList + alt + ",") {
                              alt = (String)var35.next();
                           }

                           for(var35 = unique_alt_list.iterator(); var35.hasNext(); altIdList = altIdList + alt + ",") {
                              alt = (String)var35.next();
                           }

                           ensIdList = ensIdList.substring(0, ensIdList.length() - 1);
                           geneIdList = geneIdList.substring(0, geneIdList.length() - 1);
                           altIdList = altIdList.substring(0, altIdList.length() - 1);
                           break;
                        }

                        Gene gHit = (Gene)var35.next();
                        String[] list = gHit.ensembl_id.split(",");
                        String[] var40 = list;
                        int var39 = list.length;

                        String t;
                        int var38;
                        for(var38 = 0; var38 < var39; ++var38) {
                           t = var40[var38];
                           if (!unique_ensembl_list.contains(t)) {
                              unique_ensembl_list.add(t);
                           }
                        }

                        list = gHit.name.split(",");
                        var40 = list;
                        var39 = list.length;

                        for(var38 = 0; var38 < var39; ++var38) {
                           t = var40[var38];
                           if (!unique_gene_list.contains(t)) {
                              unique_gene_list.add(t);
                           }
                        }

                        list = gHit.alt_names.split(",");
                        var40 = list;
                        var39 = list.length;

                        for(var38 = 0; var38 < var39; ++var38) {
                           t = var40[var38];
                           if (!unique_alt_list.contains(t)) {
                              unique_alt_list.add(t);
                           }
                        }

                        biotype = gHit.biotype;
                        chr = gHit.chr;
                        sumExonLength = gHit.sum_exon_length;
                     }
                  }

                  String res = gene + "|" + ensIdList + "|" + geneIdList + "|" + altIdList;
                  Integer count = (Integer)geneDups.get(res);
                  if (count == null) {
                     geneDups.put(res, 1);
                  } else {
                     count = count + 1;
                     ++json.nber_all_duplicated_genes;
                     geneDups.put(res, count);
                     res = gene + "," + count + "|" + ensIdList + "|" + geneIdList + "|" + altIdList;
                  }

                  genes.add(res);
                  bw_exonLength.write(res + "\t" + sumExonLength + "\n");
                  bw_dl_tab.write(res);
                  bw_tab.write("" + json.nber_genes);
                  ++json.nber_genes;
               }

               String[] cnp = header.split("\t");
               json.cell_names_parsed = new String[cnp.length - 1];

               int gIndex;
               for(gIndex = 1; gIndex < cnp.length; ++gIndex) {
                  json.cell_names_parsed[gIndex - 1] = cnp[gIndex];
               }

               gIndex = -1;

               for(int i = start; i < end; ++i) {
                  ++gIndex;
                  altIdList = ((String)rowValues.get(i)).replaceAll(",", ".");

                  try {
                     double v = Double.parseDouble(altIdList);
                     double rv = (double)Math.round(v);
                     if (Math.abs(rv - v) < 1.0E-5D) {
                        v = rv;
                        Integer t = (Integer)json.reads_per_cell.get(json.cell_names_parsed[gIndex]);
                        if (t == null) {
                           t = 0;
                        }

                        json.reads_per_cell.put(json.cell_names_parsed[gIndex], t + (int)rv);
                        if (!isERCC) {
                           HashMap<String, Integer> map;
                           Integer c;
                           if (biotype != null) {
                              map = json.biotypes.get(biotype);
                              if (map == null) {
                                 map = new HashMap<String, Integer>();
                              }

                              c = (Integer)map.get(json.cell_names_parsed[gIndex]);
                              if (c == null) {
                                 c = 0;
                              }

                              map.put(json.cell_names_parsed[gIndex], c + (int)rv);
                              json.biotypes.put(biotype, map);
                           }

                           if (chr != null) {
                              map = json.chrs.get(chr);
                              if (map == null) {
                                 map = new HashMap<String, Integer>();
                              }

                              c = (Integer)map.get(json.cell_names_parsed[gIndex]);
                              if (c == null) {
                                 c = 0;
                              }

                              map.put(json.cell_names_parsed[gIndex], c + (int)rv);
                              json.chrs.put(chr, map);
                           }
                        }
                     } else {
                        json.is_count_table = false;
                     }

                     if (v == 0.0D) {
                        ++json.nber_zeros;
                     }

                     if (isERCC) {
                        if (json.is_count_table) {
                           bw_ercc.write("\t" + (int)v);
                        } else {
                           bw_ercc.write("\t" + v);
                        }
                     } else if (json.is_count_table) {
                        bw_dl_tab.write("\t" + (int)v);
                        bw_tab.write("\t" + (int)v);
                     } else {
                        bw_dl_tab.write("\t" + v);
                        bw_tab.write("\t" + v);
                     }
                  } catch (NumberFormatException var42) {
                     error("Value '" + altIdList + "' at line " + current_line + " col " + (i + 1) + " is not a number.");
                  }
               }

               if (isERCC) {
                  bw_ercc.write("\n");
               } else {
                  bw_dl_tab.write("\n");
                  bw_tab.write("\n");
               }
            }
         }

         bw_tab.close();
         bw_ercc.close();
         bw_NF.close();
         bw_dl_tab.close();
         bw_exonLength.close();
         br.close();
      } catch (IOException var43) {
         IOException ioe = var43;
         System.err.println(var43.getMessage());

         try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + "output.json"));
            bw.write("{\"displayed_error\":\"" + ioe.getMessage() + "\"}");
            bw.close();
         } catch (IOException var41) {
            System.err.println(var41.getMessage());
         }

         System.exit(-1);
      }

      writeDuplicatedGenes(geneDups);
      writeGeneNamesJSON(genes);
      json.nber_unique_genes = geneDups.size();
      Iterator<String> var47 = geneDups.keySet().iterator();

      while(var47.hasNext()) {
         String gene = var47.next();
         if ((Integer)geneDups.get(gene) != 1) {
            ++json.nber_duplicated_genes;
         }
      }

      if (json.nber_ercc == 0) {
         (new File(Parameters.outputFolder + "ercc.tab")).delete();
      }

      json.setBiotypes();
      json.setChrs();
      json.writeOutputJSON();
   }

   public static void parseAndExclude(List<String> emptyCells, String filename) {
      try {
         File in = new File(Parameters.outputFolder + filename + ".tab");
         File tmp = new File(Parameters.outputFolder + filename + ".tmp.tab");
         if (in.exists()) {
            BufferedReader br_tab = new BufferedReader(new FileReader(in));
            BufferedWriter bw_tab = new BufferedWriter(new FileWriter(tmp));
            String[] header = br_tab.readLine().split("\t");
            bw_tab.write("Genes");
            HashMap<Integer, Boolean> toBeRemoved = new HashMap<>();

            for(int i = 1; i < header.length; ++i) {
               if (emptyCells.contains(header[i])) {
                  toBeRemoved.put(i, true);
               } else {
                  bw_tab.write("\t" + header[i]);
               }
            }

            bw_tab.write("\n");

            for(String line = br_tab.readLine(); line != null; line = br_tab.readLine()) {
               String[] tokens = line.split("\t");
               bw_tab.write(tokens[0]);

               for(int i = 1; i < tokens.length; ++i) {
                  if (toBeRemoved.get(i) == null) {
                     bw_tab.write("\t" + tokens[i]);
                  }
               }

               bw_tab.write("\n");
            }

            bw_tab.close();
            br_tab.close();
            in.delete();
            tmp.renameTo(in);
         }
      } catch (IOException var12) {
         IOException ioe = var12;
         System.err.println(var12.getMessage());

         try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + "output.json"));
            bw.write("{\"displayed_error\":\"[Occured when excluding cells with 0 reads]:" + ioe.getMessage() + "\"}");
            bw.close();
         } catch (IOException var11) {
            System.err.println(var11.getMessage());
         }

         System.exit(-1);
      }

   }

   public static void createHeader(ArrayList<String> cell_names, int num_columns, int num_columns_header) throws IOException {
      header = "Genes";
      int i;
      if (Parameters.has_header) {
         if (num_columns == num_columns_header) {
            switch($SWITCH_TABLE$parsing$model$ColumnName()[Parameters.name_column.ordinal()]) {
            case 1:
               for(i = 0; i < cell_names.size(); ++i) {
                  header = header + "\t" + (String)cell_names.get(i);
               }

               json.nber_cells = num_columns;
               break;
            case 2:
               for(i = 1; i < cell_names.size(); ++i) {
                  header = header + "\t" + (String)cell_names.get(i);
               }

               json.nber_cells = num_columns - 1;
               break;
            case 3:
               for(i = 0; i < cell_names.size() - 1; ++i) {
                  header = header + "\t" + (String)cell_names.get(i);
               }

               json.nber_cells = num_columns - 1;
            }
         } else {
            switch($SWITCH_TABLE$parsing$model$ColumnName()[Parameters.name_column.ordinal()]) {
            case 1:
               error("You stated that there are no Gene Names, but header is missing one column ( " + num_columns_header + " ) as compared to first line ( " + num_columns + " ).");
               break;
            case 2:
            case 3:
               for(i = 0; i < cell_names.size(); ++i) {
                  header = header + "\t" + (String)cell_names.get(i);
               }

               json.nber_cells = num_columns - 1;
            }
         }
      } else {
         switch($SWITCH_TABLE$parsing$model$ColumnName()[Parameters.name_column.ordinal()]) {
         case 1:
            json.nber_cells = num_columns;
            break;
         case 2:
         case 3:
            json.nber_cells = num_columns - 1;
         }

         for(i = 1; i <= json.nber_cells; ++i) {
            header = header + "\tCell_" + i;
         }
      }

   }

   public static void writeDuplicatedGenes(HashMap<String, Integer> geneDups) {
      try {
         BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + "duplicated_genes.txt"));
         Iterator<String> var3 = geneDups.keySet().iterator();

         while(var3.hasNext()) {
            String geneKey = var3.next();
            Integer nb = (Integer)geneDups.get(geneKey);
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

   public static void writeGeneNamesJSON(ArrayList<String> genes) {
      try {
         BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + "gene_names.json"));
         if (genes.size() == 0) {
            bw.write("{}");
         } else {
            bw.write("[" + geneNameToJSON((String)genes.get(0)));
            int i = 1;

            while(true) {
               if (i >= genes.size()) {
                  bw.write("]");
                  break;
               }

               bw.write("," + geneNameToJSON((String)genes.get(i)));
               ++i;
            }
         }

         bw.close();
      } catch (IOException var3) {
         System.err.println(var3.getMessage());
         System.exit(-1);
      }

   }

   public static String geneNameToJSON(String name) {
      int i1 = name.indexOf("|");
      int i2 = name.indexOf("|", i1 + 1);
      int i3 = name.indexOf("|", i2 + 1);
      String originalGene = name.substring(0, i1);
      String ensGene = name.substring(i1 + 1, i2);
      String otherGenes = name.substring(i2 + 1, i3);
      String altGenes = name.substring(i3 + 1, name.length());
      return "[\"" + originalGene + "\",\"" + ensGene + "\",\"" + otherGenes + "\",\"" + altGenes + "\"]";
   }

   public static void error(String error) {
      try {
         BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + "output.json"));
         bw.write("{\"displayed_error\":\"" + error + "\"}");
         bw.close();
      } catch (IOException var2) {
         System.err.println(var2.getMessage());
      }

      System.exit(-1);
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$parsing$model$ColumnName() {
      int[] var10000 = $SWITCH_TABLE$parsing$model$ColumnName;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[ColumnName.values().length];

         try {
            var0[ColumnName.FIRST.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[ColumnName.LAST.ordinal()] = 3;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[ColumnName.NONE.ordinal()] = 1;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$parsing$model$ColumnName = var0;
         return var0;
      }
   }
}
