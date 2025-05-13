package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import model.Parameters;
import parsing.model.Gene;
import tools.Utils;

public class DBManager {
   public static String JDBC_DRIVER = null;
   public static String URL = null;
   public static Connection conn = null;
   public static final String[] species = new String[]{"ailuropoda_melanoleuca", "anas_platyrhynchos", "anolis_carolinensis", "astyanax_mexicanus", "bos_taurus", "caenorhabditis_elegans", "callithrix_jacchus", "canis_familiaris", "cavia_porcellus", "chlorocebus_sabaeus", "choloepus_hoffmanni", "ciona_intestinalis", "ciona_savignyi", "danio_rerio", "dasypus_novemcinctus", "dipodomys_ordii", "drosophila_melanogaster", "echinops_telfairi", "equus_caballus", "erinaceus_europaeus", "felis_catus", "ficedula_albicollis", "gadus_morhua", "gallus_gallus", "gasterosteus_aculeatus", "gorilla_gorilla", "homo_sapiens", "ictidomys_tridecemlineatus", "latimeria_chalumnae", "lepisosteus_oculatus", "loxodonta_africana", "macaca_mulatta", "macropus_eugenii", "meleagris_gallopavo", "microcebus_murinus", "monodelphis_domestica", "mus_musculus", "mustela_putorius_furo", "myotis_lucifugus", "nomascus_leucogenys", "ochotona_princeps", "oreochromis_niloticus", "ornithorhynchus_anatinus", "oryctolagus_cuniculus", "oryzias_latipes", "otolemur_garnettii", "ovis_aries", "pan_troglodytes", "papio_anubis", "pelodiscus_sinensis", "petromyzon_marinus", "poecilia_formosa", "pongo_abelii", "procavia_capensis", "pteropus_vampyrus", "rattus_norvegicus", "saccharomyces_cerevisiae", "sarcophilus_harrisii", "sorex_araneus", "sus_scrofa", "taeniopygia_guttata", "takifugu_rubripes", "tarsius_syrichta", "tetraodon_nigroviridis", "tupaia_belangeri", "tursiops_truncatus", "vicugna_pacos", "xenopus_tropicalis", "xiphophorus_maculatus"};
   public static final int[] taxons = new int[]{9646, 8839, 28377, 7994, 9913, 6239, 9483, 9615, 10141, 60711, 9358, 7719, 51511, 7955, 9361, 10020, 7227, 9371, 9796, 9365, 9685, 59894, 8049, 9031, 69293, 9593, 9606, 43179, 7897, 7918, 9785, 9544, 9315, 9103, 30608, 13616, 10090, 9669, 59463, 61853, 9978, 8128, 9258, 9986, 8090, 30611, 9940, 9598, 9555, 13735, 7757, 48698, 9601, 9813, 132908, 10116, 4932, 9305, 42254, 9823, 59729, 31033, 1868482, 99883, 37347, 9739, 30538, 8364, 8083};

   public static void generateEnrichmentDB() {
      GODatabase.generateGODB(Parameters.outputFolder, Parameters.taxon);
   }

   public static int findTaxon(int taxon) {
      for(int i = 0; i < taxons.length; ++i) {
         if (taxons[i] == taxon) {
            return i;
         }
      }

      return -1;
   }

   public static void createDB() {
      long t = System.currentTimeMillis();
      connect();
      System.out.print("Running: DROP TABLE genes...");
      dropTable("genes");
      System.out.println("DONE!");
      System.out.print("Running: CREATE TABLE genes...");
      createGeneTable();
      System.out.println("DONE!");
      disconnect();
      System.out.println("Creating DB took " + Utils.toReadableTime(System.currentTimeMillis() - t));
   }

   public static void createGeneTable() {
      Statement stmt = null;

      try {
         stmt = conn.createStatement();
         String sql = "CREATE TABLE genes (id INTEGER NOT NULL AUTO_INCREMENT,  ensembl_id TEXT,  name TEXT,  alternate_names TEXT,  organism_id INTEGER DEFAULT NULL,  biotype TEXT,  sum_exon_length INTEGER,  gene_length INTEGER,  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  PRIMARY KEY ( id ))";
         stmt.executeUpdate(sql);
         sql = "CREATE TABLE gene_names (id INTEGER NOT NULL AUTO_INCREMENT,  gene_id int references genes,  value TEXT,  PRIMARY KEY ( id ))";
         stmt.executeUpdate(sql);
      } catch (Exception var10) {
         var10.printStackTrace();
      } finally {
         try {
            if (stmt != null) {
               stmt.close();
            }
         } catch (SQLException var9) {
         }

      }

   }

   public static void dropTable(String name) {
      Statement stmt = null;

      try {
         stmt = conn.createStatement();
         stmt.executeUpdate("DROP TABLE " + name);
      } catch (Exception var11) {
         var11.printStackTrace();
      } finally {
         try {
            if (stmt != null) {
               stmt.close();
            }
         } catch (SQLException var10) {
         }

      }

   }

   public static void insertGene(String ensembl_id, String name, String alternateNames, int organism_id, String biotype, int exon_length, int full_length) {
      Statement stmt = null;

      try {
         stmt = conn.createStatement();
         String sql = "INSERT INTO genes (ensembl_id, name, alternate_names, organism_id, biotype, exon_length, full_length) VALUES ('" + ensembl_id + "','" + name + "','" + alternateNames + "'," + organism_id + ",'" + biotype + "'," + exon_length + "," + full_length + ")";
         stmt.executeUpdate(sql);
      } catch (Exception var17) {
         var17.printStackTrace();
      } finally {
         try {
            if (stmt != null) {
               stmt.close();
            }
         } catch (SQLException var16) {
         }

      }

   }

   public static HashMap<String, ArrayList<Gene>> getGenesInDB() {
      HashMap<String, ArrayList<Gene>> genes = new HashMap<String, ArrayList<Gene>>();
      connect();
      Statement stmt = null;

      try {
         stmt = conn.createStatement();
         String sql = "SELECT * FROM genes WHERE organism_id=" + Parameters.organism;
         ResultSet rs = stmt.executeQuery(sql);

         label127:
         while(true) {
            Gene g;
            ArrayList<Gene> gene_list;
            do {
               if (!rs.next()) {
                  rs.close();
                  break label127;
               }

               g = new Gene();
               g.ensembl_id = rs.getString("ensembl_id");
               g.name = rs.getString("name");
               g.biotype = rs.getString("biotype");
               g.gene_length = (long)rs.getInt("gene_length");
               g.sum_exon_length = (long)rs.getInt("sum_exon_length");
               g.chr = rs.getString("chr");
               g.alt_names = rs.getString("alt_names");
               String ensUp = g.ensembl_id.toUpperCase();
               String nameUp = g.name.toUpperCase();
               gene_list = (ArrayList<Gene>)genes.get(ensUp);
               if (gene_list == null) {
                  gene_list = new ArrayList<Gene>();
               }

               gene_list.add(g);
               genes.put(ensUp, gene_list);
               gene_list = genes.get(nameUp);
               if (gene_list == null) {
                  gene_list = new ArrayList<Gene>();
               }

               gene_list.add(g);
               genes.put(nameUp, gene_list);
            } while(g.alt_names == null);

            String[] tokens = g.alt_names.split(",");
            String[] var12 = tokens;
            int var11 = tokens.length;

            for(int var10 = 0; var10 < var11; ++var10) {
               String gene = var12[var10];
               String geneUp = gene.toUpperCase();
               gene_list = genes.get(geneUp);
               if (gene_list == null) {
                  gene_list = new ArrayList<Gene>();
               }

               gene_list.add(g);
               genes.put(geneUp, gene_list);
            }
         }
      } catch (Exception var22) {
         var22.printStackTrace();
      } finally {
         try {
            if (stmt != null) {
               stmt.close();
            }
         } catch (SQLException var21) {
         }

      }

      disconnect();
      return genes;
   }

   public static void connect() {
      try {
         Class.forName(JDBC_DRIVER);
         System.out.print("Connecting to database..., URL = " + URL);
         conn = DriverManager.getConnection(URL);
         System.out.println("Connected!");
      } catch (Exception var1) {
         var1.printStackTrace();
      }

   }

   public static void disconnect() {
      try {
         if (conn != null) {
            conn.close();
         }
      } catch (SQLException var1) {
         var1.printStackTrace();
      }

   }
}
