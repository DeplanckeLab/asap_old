package db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import tools.Utils;

public class GODatabase {
   private static HashMap<String, GOTerm> termsBP = null;
   private static HashMap<String, GOTerm> termsMF = null;
   private static HashMap<String, GOTerm> termsCC = null;

   public static void main(String[] args) throws IOException {
      ArrayList<Specie> species = fetchSpecies();
      System.out.println(species.size());
      BufferedWriter bw = new BufferedWriter(new FileWriter("go_species.txt"));
      bw.write("id_species\tcommon_name\tncbi_taxa_id\n");
      Iterator<Specie> spec = species.iterator();

      while(spec.hasNext()) {
         Specie s = spec.next();
         bw.write(s.genus + "_" + s.species + "\t" + s.common_name + "\t" + s.ncbi_taxa_id + "\n");
         System.out.println(s.genus + "_" + s.species + "\t" + s.common_name + "\t" + s.ncbi_taxa_id);
      }

      DBManager.disconnect();
      bw.close();
      System.out.println(DBManager.species.length);
   }

   public static ArrayList<Integer> getTaxons() {
	  ArrayList<Integer> res = new ArrayList<Integer>();
	  BufferedReader br = null;
	  try 
      {
         br = new BufferedReader(new FileReader("go_species.txt"));
         String line = br.readLine();

         for(line = br.readLine(); line != null; line = br.readLine()) {
            String[] tokens = line.split("\t");
            res.add(Integer.parseInt(tokens[2]));
         }

         br.close();
      } 
      catch (IOException var4) 
      {
         var4.printStackTrace();
      } 
      finally
      {
    	  try { if(br != null) br.close(); } catch (IOException e) { e.printStackTrace(); }
      }

      return res;
   }

   public static ArrayList<Specie> fetchSpecies() {
      DBManager.JDBC_DRIVER = "com.mysql.jdbc.Driver";
      DBManager.URL = "jdbc:mysql://mysql.ebi.ac.uk:4085/go_latest?user=go_select&password=amigo";
      DBManager.connect();
      ArrayList<Specie> species = new ArrayList<Specie>();
      Statement stmt = null;
      HashSet<String> toCheck = new HashSet<String>();
      String[] var6;
      int var5 = (var6 = DBManager.species).length;

      String s;
      int var4;
      for(var4 = 0; var4 < var5; ++var4) {
         s = var6[var4];
         toCheck.add(s.toLowerCase());
      }

      try {
         stmt = DBManager.conn.createStatement();
         s = "SELECT DISTINCT * FROM species";
         ResultSet rs = stmt.executeQuery(s);

         while(rs.next()) {
            Specie spec = new Specie();
            spec.species = rs.getString("species");
            if (spec.species != null) {
            	spec.species = spec.species.toLowerCase().replaceAll(" ", "_");
            }

            spec.common_name = rs.getString("common_name");
            if (spec.common_name != null) {
            	spec.common_name = spec.common_name;
            }

            spec.genus = rs.getString("genus");
            if (spec.genus != null) {
            	spec.genus = spec.genus.toLowerCase().replaceAll(" ", "_");
            }

            spec.ncbi_taxa_id = rs.getString("ncbi_taxa_id");
            spec.taxonomic_rank = rs.getString("taxonomic_rank");
            if (spec.ncbi_taxa_id.equals("1868482")) {
            	spec.genus = "tarsius";
            }

            if (spec.ncbi_taxa_id.equals("9615")) {
            	spec.species = "familiaris";
            }

            if (toCheck.contains(spec.genus + "_" + spec.species)) {
               species.add(spec);
            }
         }

         rs.close();
      } catch (Exception var16) {
         var16.printStackTrace();
      } finally {
         try {
            if (stmt != null) {
               stmt.close();
            }
         } catch (SQLException var15) {
         }

      }

      DBManager.disconnect();
      var5 = (var6 = DBManager.species).length;

      for(var4 = 0; var4 < var5; ++var4) {
         s = var6[var4];
         boolean found = false;
         Iterator<Specie> it = species.iterator();

         while(it.hasNext()) {
            Specie sp = it.next();
            if (s.equals(sp.genus + "_" + sp.species)) {
               found = true;
            }
         }

         if (!found) {
            System.err.println(s + " IS NOT FOUND!!");
         }
      }

      return species;
   }

   public static void generateGODBv2(String outputGMTFolder, String species) throws IOException {
      int taxonId = 0;
      if (species.equals("hsa")) {
         taxonId = 9606;
      }

      if (species.equals("mmu")) {
         taxonId = 10090;
      }

      if (!outputGMTFolder.endsWith("/")) {
         outputGMTFolder = outputGMTFolder + "/";
      }

      DBManager.connect();
      fetchTerms();
      System.out.println(termsBP.size() + " GO BP terms were fetched.");
      System.out.println(termsMF.size() + " GO MF terms were fetched.");
      System.out.println(termsCC.size() + " GO CC terms were fetched.");
      long t1 = System.currentTimeMillis();
      ProgressBar p = new ProgressBar("GO Cellular Component", termsCC.size());
      Iterator<String> it = termsCC.keySet().iterator();

      while(it.hasNext()) {
         String goterm = it.next();
         GOTerm go = (GOTerm)termsCC.get(goterm);
         go.genes = fetchGOGenesDirect(go.id, taxonId);
         go.descendants = fetchDescendant(go.id);
         p.increment();
      }

      p.close();
      System.out.println("GO CC processing time: " + Utils.toReadableTime(System.currentTimeMillis() - t1));
   }

   public static void generateGODB(String outputGMTFolder, int taxonId) {
      try {
         System.out.println("Computing " + DBManager.species[DBManager.findTaxon(taxonId)] + " (" + taxonId + ")...");
         if (!outputGMTFolder.endsWith("/")) {
            outputGMTFolder = outputGMTFolder + "/";
         }

         fetchTerms();
         System.out.println(termsBP.size() + " GO BP terms were fetched.");
         System.out.println(termsMF.size() + " GO MF terms were fetched.");
         System.out.println(termsCC.size() + " GO CC terms were fetched.");
         long t1 = System.currentTimeMillis();
         ProgressBar p = new ProgressBar("GO Cellular Component", termsCC.size());
         BufferedWriter bw = new BufferedWriter(new FileWriter(outputGMTFolder + "GO_CC_" + taxonId + ".gmt"));

         String goterm;
         Iterator<String> it;
         GOTerm go;
         ArrayList<String> genes;
         String gene;
         for(it = termsCC.keySet().iterator(); it.hasNext(); p.increment()) {
            goterm = it.next();
            go = (GOTerm)termsCC.get(goterm);
            genes = fetchGOGenesRecursively(go.id, taxonId);
            if (genes.size() > 0) {
               bw.write(go.id + "\t" + go.description + "\t" + "http://amigo.geneontology.org/amigo/term/" + go.id);
               Iterator<String> it2 = genes.iterator();

               while(it2.hasNext()) {
                  gene = it2.next();
                  bw.write("\t" + gene);
               }

               bw.write("\n");
            }
         }

         p.close();
         bw.close();
         System.out.println("GO CC processing time: " + Utils.toReadableTime(System.currentTimeMillis() - t1));
         t1 = System.currentTimeMillis();
         p = new ProgressBar("GO Molecular Function", termsMF.size());
         bw = new BufferedWriter(new FileWriter(outputGMTFolder + "GO_MF_" + taxonId + ".gmt"));

         for(it = termsMF.keySet().iterator(); it.hasNext(); p.increment()) {
            goterm = it.next();
            go = (GOTerm)termsMF.get(goterm);
            genes = fetchGOGenesRecursively(go.id, taxonId);
            if (genes.size() > 0) {
               bw.write(go.id + "\t" + go.description + "\t" + "http://amigo.geneontology.org/amigo/term/" + go.id);
               Iterator<String> it2 = genes.iterator();

               while(it2.hasNext()) {
                  gene = it2.next();
                  bw.write("\t" + gene);
               }

               bw.write("\n");
            }
         }

         p.close();
         bw.close();
         System.out.println("GO MF processing time: " + Utils.toReadableTime(System.currentTimeMillis() - t1));
         t1 = System.currentTimeMillis();
         p = new ProgressBar("GO Biological Process", termsBP.size());
         bw = new BufferedWriter(new FileWriter(outputGMTFolder + "GO_BP_" + taxonId + ".gmt"));

         for(it = termsBP.keySet().iterator(); it.hasNext(); p.increment()) {
            goterm = it.next();
            go = (GOTerm)termsBP.get(goterm);
            genes = fetchGOGenesRecursively(go.id, taxonId);
            if (genes.size() > 0) {
               bw.write(go.id + "\t" + go.description + "\t" + "http://amigo.geneontology.org/amigo/term/" + go.id);
               Iterator<String> it2 = genes.iterator();

               while(it2.hasNext()) {
                  gene = it2.next();
                  bw.write("\t" + gene);
               }

               bw.write("\n");
            }
         }

         p.close();
         bw.close();
         System.out.println("GO BP processing time: " + Utils.toReadableTime(System.currentTimeMillis() - t1));
      } catch (IOException var12) {
         var12.printStackTrace();
      }

   }

   public static void fetchTerms() {
      termsBP = new HashMap<String, GOTerm>();
      termsMF = new HashMap<String, GOTerm>();
      termsCC = new HashMap<String, GOTerm>();
      Statement stmt = null;

      try {
         stmt = DBManager.conn.createStatement();
         String sql = "SELECT * FROM term";
         ResultSet rs = stmt.executeQuery(sql);

         while(rs.next()) {
            GOTerm go = new GOTerm();
            if (rs.getInt("is_obsolete") == 0) {
               go.id = rs.getString("acc");
               go.description = rs.getString("name");
               String term_type = rs.getString("term_type");
               if (term_type.equals("biological_process")) {
                  termsBP.put(go.id, go);
               } else if (term_type.equals("molecular_function")) {
                  termsMF.put(go.id, go);
               } else if (term_type.equals("cellular_component")) {
                  termsCC.put(go.id, go);
               }
            }
         }

         rs.close();
      } catch (Exception var13) {
         var13.printStackTrace();
      } finally {
         try {
            if (stmt != null) {
               stmt.close();
            }
         } catch (SQLException var12) {
         }

      }

   }

   public static ArrayList<String> fetchGOGenesRecursively(String goId, int taxon) {
      ArrayList<String> genes = new ArrayList<String>();
      Statement stmt = null;

      try {
         stmt = DBManager.conn.createStatement();
         String sql = "SELECT DISTINCT gene_product.symbol FROM term INNER JOIN graph_path ON (term.id=graph_path.term1_id) INNER JOIN association ON (graph_path.term2_id=association.term_id) INNER JOIN gene_product ON (association.gene_product_id=gene_product.id) INNER JOIN species ON (gene_product.species_id=species.id) INNER JOIN dbxref ON (gene_product.dbxref_id=dbxref.id) WHERE term.acc = '" + goId + "' AND  ncbi_taxa_id = " + taxon;
         ResultSet rs = stmt.executeQuery(sql);

         while(rs.next()) {
            genes.add(rs.getString("symbol").toUpperCase().trim());
         }

         rs.close();
      } catch (Exception var14) {
         var14.printStackTrace();
      } finally {
         try {
            if (stmt != null) {
               stmt.close();
            }
         } catch (SQLException var13) {
         }

      }

      return genes;
   }

   public static ArrayList<String> fetchGOGenesDirect(String goId, int taxon) {
      ArrayList<String> genes = new ArrayList<String>();
      Statement stmt = null;

      try {
         stmt = DBManager.conn.createStatement();
         String sql = "SELECT DISTINCT gene_product.symbol FROM term INNER JOIN association ON (term.id=association.term_id) INNER JOIN gene_product ON (association.gene_product_id=gene_product.id) INNER JOIN species ON (gene_product.species_id=species.id) INNER JOIN dbxref ON (gene_product.dbxref_id=dbxref.id) WHERE dbxref.xref_dbname = 'UniProtKB' AND term.acc = '" + goId + "' AND  ncbi_taxa_id = " + taxon;
         ResultSet rs = stmt.executeQuery(sql);

         while(rs.next()) {
            genes.add(rs.getString("gene_product.symbol").toUpperCase().trim());
         }

         rs.close();
      } catch (Exception var14) {
         var14.printStackTrace();
      } finally {
         try {
            if (stmt != null) {
               stmt.close();
            }
         } catch (SQLException var13) {
         }

      }

      return genes;
   }

   public static ArrayList<String> fetchDescendant(String goId) {
      ArrayList<String> go = new ArrayList<String>();
      Statement stmt = null;

      try {
         stmt = DBManager.conn.createStatement();
         String sql = "SELECT DISTINCT descendant.acc FROM term INNER JOIN graph_path ON (term.id=graph_path.term1_id) INNER JOIN term AS descendant ON (descendant.id=graph_path.term2_id) WHERE term.acc = '" + goId + "' AND distance <> 0";
         ResultSet rs = stmt.executeQuery(sql);

         while(rs.next()) {
            go.add(rs.getString("descendant.acc").trim());
         }

         rs.close();
      } catch (Exception var13) {
         var13.printStackTrace();
      } finally {
         try {
            if (stmt != null) {
               stmt.close();
            }
         } catch (SQLException var12) {
         }

      }

      return go;
   }
}
