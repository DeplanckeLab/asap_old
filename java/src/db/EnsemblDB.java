package db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.sis.util.collection.RangeSet;

import model.GeneInfo;
import model.Parameters;

public class EnsemblDB {
   public static FTPClient f = null;
   public static HashMap<String, HashMap<Integer, String>> urls = new HashMap<String, HashMap<Integer, String>>();
   public static HashMap<String, GeneInfo> geneInfo = new HashMap<String, GeneInfo>();

   public static void main(String[] args) throws Exception {
      generateSpeciesURLFromEnsembl();
   }

   public static void readSpecies() {
	    try (BufferedReader br = new BufferedReader(new FileReader("species.txt"))) {
	        String line = br.readLine();
	        HashMap<Integer, Integer> lineToRelease = new HashMap<>();
	        String[] header = line.split("\t");

	        for (int i = 1; i < header.length; ++i) {
	            lineToRelease.put(i, Integer.parseInt(header[i]));
	        }

	        while ((line = br.readLine()) != null) {
	            String[] tokens = line.split("\t");
	            HashMap<Integer, String> urlss = new HashMap<>();

	            for (int i = 1; i < tokens.length; ++i) {
	                urlss.put(lineToRelease.get(i), tokens[i]);
	            }

	            urls.put(tokens[0], urlss);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}


   public static void createEnsemblDB() {
	    try {
	        readSpecies();
	        System.out.println("Computing gene info for species " + Parameters.organism_S + " ...");
	        HashMap<Integer, String> toLoad = urls.get(Parameters.organism_S);

	        String gene_id;
	        Iterator<String> it;
	        GeneInfo g;
	        for (int i = 43; i <= 87; ++i) {
	            String url = toLoad.get(i);
	            if (url.equals("NA")) {
	                System.out.println("Release " + i + " does not exist for species " + Parameters.organism_S);
	            } else {
	                downloadEnsembl(url);
	                it = geneInfo.keySet().iterator();

	                while (it.hasNext()) {
	                    gene_id = it.next();
	                    g = geneInfo.get(gene_id);
	                    long l = g.getLength();
	                    if (l != 0L) {
	                        g.sumExonLength = g.getLength();
	                    }
	                }

	                System.out.println("Release " + i + ": " + geneInfo.size() + " genes now in database.");
	            }
	        }

	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + Parameters.organism_S + ".txt"))) {
	            bw.write("Ensembl\tName\tAltNames\tBiotype\tGeneLength\tSumExonLength\tChr\n");

	            ArrayList<String> tmp = new ArrayList<>();
	            it = geneInfo.keySet().iterator();
	            while (it.hasNext()) {
	                gene_id = it.next();
	                tmp.add(gene_id);
	            }

	            Collections.sort(tmp);

	            for (String id : tmp) {
	                g = geneInfo.get(id);
	                if (g.chr.equals("dmel_mitochondrion_genome")) {
	                    g.chr = "MT";
	                }
	                bw.write(id + "\t" + g.gene_name + "\t" +
	                         buildAltNamesString(g.alternate_names, g.gene_id.toUpperCase(), g.gene_name.toUpperCase()) + "\t" +
	                         g.biotype + "\t" +
	                         (g.end - g.start + 1L) + "\t" +
	                         g.sumExonLength + "\t" +
	                         g.chr + "\n");
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}


   private static String buildAltNamesString(HashSet<String> names, String ensNameUp, String geneNameUp) {
      HashSet<String> unique = new HashSet<String>();
      String res = "";
      Iterator<String> it = names.iterator();

      while(it.hasNext()) {
         String n = it.next();
         String nUp = n.toUpperCase();
         if (!unique.contains(nUp)) {
            if (!nUp.equals(ensNameUp) && !nUp.equals(geneNameUp)) {
               res = res + n + ",";
            }

            unique.add(nUp);
         }
      }

      if (res.endsWith(",")) {
         res = res.substring(0, res.length() - 1);
      }

      return res;
   }

   public static void downloadEnsembl(String path) 
   {
	    boolean foundGeneAnnotation = false;
	    try ( InputStream is = URI.create("ftp://ftp.ensembl.org" + path).toURL().openStream();
    	      GZIPInputStream gzipStream = new GZIPInputStream(is);
    	      BufferedReader br = new BufferedReader(new InputStreamReader(gzipStream)) ) 
	    {
	        // Initialize exon_id field
	        for (String key : geneInfo.keySet())
	        {
	            GeneInfo g = geneInfo.get(key);
	            g.exon_id = RangeSet.create(Long.class, true, true);  // Assuming RangeSet is a custom utility
	        }

	        String line;
	        while ((line = br.readLine()) != null) 
	        {
	            if (line.startsWith("#")) continue;

	            String[] tokens = line.split("\t");
	            if (tokens.length < 9) continue; // avoid ArrayIndexOutOfBounds

	            String chr = tokens[0];
	            String type = tokens[2];
	            long start = Long.parseLong(tokens[3]);
	            long end = Long.parseLong(tokens[4]);

	            String[] params = tokens[8].split(";");
	            String gene_name = null;
	            String gene_id = null;
	            String biotype = null;

	            for (String param : params) 
	            {
	                int startIdx = param.indexOf("\"");
	                int endIdx = param.lastIndexOf("\"");
	                if (startIdx < 0 || endIdx <= startIdx) continue;

	                String value = param.substring(startIdx + 1, endIdx);
	                if (param.contains("gene_name")) gene_name = value;
	                else if (param.contains("gene_id")) gene_id = value;
	                else if (param.contains("gene_biotype")) biotype = value;
	            }

	            if (gene_id == null) continue;
	            if (gene_name == null) gene_name = gene_id;
	            if (biotype == null) biotype = tokens[1];

	            if (gene_id.startsWith("ENS") || gene_id.startsWith("FB")) 
	            {
	                GeneInfo g = geneInfo.getOrDefault(gene_id, new GeneInfo());
	                g.gene_id = gene_id;

	                if (!gene_name.equals(g.gene_name)) 
	                {
	                    if (g.gene_name != null && !g.gene_name.startsWith("ENS") && !g.alternate_names.contains(g.gene_name)) 
	                    {
	                        g.alternate_names.add(g.gene_name);
	                    }
	                    if (g.gene_name != null && g.alternate_names.contains(gene_name)) 
	                    {
	                        g.alternate_names.remove(gene_name);
	                    }
	                    g.gene_name = gene_name;
	                }

	                g.chr = chr;
	                g.biotype = biotype;

	                if (type.equals("gene")) 
	                {
	                    foundGeneAnnotation = true;
	                    g.start = start;
	                    g.end = end;
	                } else if (type.equals("exon")) 
	                {
	                    g.exon_id.add(start, end);
	                }

	                geneInfo.put(g.gene_id, g);
	            }
	        }

	        // If no gene annotations found, set lengths based on exon data
	        if (!foundGeneAnnotation) 
	        {
	            for (GeneInfo g : geneInfo.values()) 
	            {
	                if (g.exon_id != null && g.exon_id.size() > 0) 
	                {
	                    g.setGeneLengthToExons();
	                }
	            }
	        }

	    } 
	    catch (IOException e) 
	    {
	        e.printStackTrace(); // Consider using a logger in production
	    }
	}


   private static void generateSpeciesURLFromEnsembl() throws Exception {
      f = new FTPClient();
      f.connect("ftp.ensembl.org");
      f.login("anonymous", "");

      for(int i = 43; i <= 87; ++i) {
         getGTF(i);
      }

      ArrayList<String> species = new ArrayList<String>();
      Iterator<String> it = urls.keySet().iterator();

      while(it.hasNext()) {
         String s = it.next();
         species.add(s);
      }

      Collections.sort(species);
      BufferedWriter bw = new BufferedWriter(new FileWriter("species.txt"));
      bw.write("species");

      for(int i = 43; i <= 87; ++i) {
         bw.write("\t" + i);
      }

      bw.write("\n");
      Iterator<String> it2 = species.iterator();

      while(it2.hasNext()) {
         String s = it2.next();
         bw.write(s);
         HashMap<Integer, String> releases = urls.get(s);

         for(int i = 43; i <= 87; ++i) {
            String url = releases.get(i);
            if (url == null) {
               url = "NA";
            }

            bw.write("\t" + url);
         }

         bw.write("\n");
      }

      bw.close();
   }

   private static void getGTF(int release) throws Exception {
      if (release >= 48) {
         getGTF_48_87(release);
      } else if (release == 47) {
         getGTF_47(release);
      } else if (release >= 43) {
         getGTF_43_46(release);
      } else {
         System.exit(-1);
      }

   }

   private static void getGTF_48_87(int release) throws Exception {
      FTPFile[] files = f.listDirectories("/pub/release-" + release + "/gtf/");
      FTPFile[] var5 = files;
      int var4 = files.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         FTPFile fi = var5[var3];
         String url = "/pub/release-" + release + "/gtf/" + fi.getName() + "/";
         FTPFile[] subfiles = f.listFiles(url);
         FTPFile[] var11 = subfiles;
         int var10 = subfiles.length;

         for(int var9 = 0; var9 < var10; ++var9) {
            FTPFile fo = var11[var9];
            if (fo.getName().endsWith("gtf.gz") && fo.getName().indexOf("abinitio") == -1 && fo.getName().indexOf(".chr") == -1) {
               HashMap<Integer, String> releases = urls.get(fi.getName());
               if (releases == null) {
                  releases = new HashMap<Integer, String>();
               }

               releases.put(release, url + fo.getName());
               urls.put(fi.getName(), releases);
            }
         }
      }

   }

   private static void getGTF_47(int release) throws Exception {
      String url = "/pub/release-" + release + "/gtf/";
      FTPFile[] files = f.listFiles(url);
      FTPFile[] var6 = files;
      int var5 = files.length;

      for(int var4 = 0; var4 < var5; ++var4) {
         FTPFile fi = var6[var4];
         String name = fi.getName();
         name = name.substring(0, name.indexOf(".")).toLowerCase();
         HashMap<Integer, String> releases = urls.get(name);
         if (releases == null) {
            releases = new HashMap<Integer, String>();
         }

         releases.put(release, url + fi.getName());
         urls.put(name, releases);
      }

   }

   private static void getGTF_43_46(int release) throws Exception {
      FTPFile[] files = f.listDirectories("/pub/release-" + release);
      FTPFile[] var5 = files;
      int var4 = files.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         FTPFile fi = var5[var3];
         String url = "/pub/release-" + release + "/" + fi.getName() + "/data/gtf/";
         FTPFile[] subfiles = f.listFiles(url);
         FTPFile[] var11 = subfiles;
         int var10 = subfiles.length;

         for(int var9 = 0; var9 < var10; ++var9) {
            FTPFile fo = var11[var9];
            String name = fi.getName();
            name = name.substring(0, name.indexOf("_", name.indexOf("_") + 1)).toLowerCase();
            HashMap<Integer, String> releases = urls.get(name);
            if (releases == null) {
               releases = new HashMap<Integer, String>();
            }

            releases.put(release, url + fo.getName());
            urls.put(name, releases);
         }
      }

   }
}
