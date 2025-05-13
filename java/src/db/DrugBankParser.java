package db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DrugBankParser {
   public static HashSet<String> accepted_organisms;
   public static HashMap<String, String> drug_description = new HashMap<String, String>();

   public static void setOrganism(String organism) {
      accepted_organisms = new HashSet<String>();
      switch(organism.hashCode()) {
      case 103606:
         if (organism.equals("hsa")) {
            accepted_organisms.add("Human");
            accepted_organisms.add("human");
            return;
         }
         break;
      case 108245:
         if (organism.equals("mmu")) {
            accepted_organisms.add("Mouse");
            return;
         }
         break;
      case 112677:
         if (organism.equals("rat")) {
            accepted_organisms.add("Rat");
            return;
         }
         break;
      case 96335054:
         if (organism.equals("ecoli")) {
            accepted_organisms.add("Escherichia coli");
            accepted_organisms.add("Escherichia coli (strain K12)");
            return;
         }
      }

      System.err.println("This organism does not exist");
      System.exit(-1);
   }

   public static void parse(String organism, String associationFile, String outputGMTFile) throws IOException {
      setOrganism(organism);
      BufferedWriter bw = new BufferedWriter(new FileWriter(outputGMTFile));
      HashMap<String, ArrayList<String>> assoc = loadDataAssoc(associationFile);
      Iterator<String> it = assoc.keySet().iterator();

      while(true) {
         String geneset;
         ArrayList<String> genes;
         do {
            if (!it.hasNext()) {
               bw.close();
               return;
            }

            geneset = it.next();
            genes = assoc.get(geneset);
         } while(genes.size() <= 0);

         bw.write(geneset + "\t" + (String)drug_description.get(geneset) + "\thttp://www.drugbank.ca/drugs/" + geneset);
         Iterator<String> it2 = genes.iterator();

         while(it2.hasNext()) {
            String gene = it2.next();
            bw.write("\t" + gene);
         }

         bw.write("\n");
      }
   }

   private static HashMap<String, ArrayList<String>> loadDataAssoc(String filename) {
      if (filename == null) {
         return null;
      } else {
         HashMap<String, ArrayList<String>> data_assoc = new HashMap<String, ArrayList<String>>();

         try {
            File xmlFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList drugs = doc.getFirstChild().getChildNodes();

            for(int d = 0; d < drugs.getLength(); ++d) {
               Node dNode = drugs.item(d);
               if (dNode.getNodeName().equals("drug") && dNode.getNodeType() == 1) {
                  ArrayList<String> genes = new ArrayList<String>();
                  Element drug = (Element)dNode;
                  String drugId = drug.getElementsByTagName("drugbank-id").item(0).getTextContent();
                  drug_description.put(drugId, drug.getElementsByTagName("name").item(0).getTextContent());
                  NodeList subnodes = drug.getChildNodes();

                  for(int s = 0; s < subnodes.getLength(); ++s) {
                     Node sNode = subnodes.item(s);
                     if (sNode.getNodeName().equals("targets") && sNode.getNodeType() == 1) {
                        Element targets = (Element)sNode;
                        NodeList targetSet = targets.getChildNodes();

                        for(int t = 0; t < targetSet.getLength(); ++t) {
                           Node tNode = targetSet.item(t);
                           if (tNode.getNodeName().equals("target") && tNode.getNodeType() == 1) {
                              Element target = (Element)tNode;
                              String organism = target.getElementsByTagName("organism").item(0).getTextContent();
                              if (accepted_organisms.contains(organism.trim())) {
                                 NodeList nodes = target.getChildNodes();

                                 for(int tt = 0; tt < nodes.getLength(); ++tt) {
                                    Node n = nodes.item(tt);
                                    if (n.getNodeName().equals("polypeptide") && n.getNodeType() == 1) {
                                       Element polypeptide = (Element)n;
                                       String geneId = polypeptide.getElementsByTagName("gene-name").item(0).getTextContent().trim();
                                       if (!genes.contains(geneId) && !geneId.equals("")) {
                                          genes.add(geneId);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }

                  data_assoc.put(drugId, genes);
               }
            }
         } catch (FileNotFoundException var26) {
            var26.printStackTrace();
            System.exit(-1);
         } catch (ParserConfigurationException var27) {
            var27.printStackTrace();
            System.exit(-1);
         } catch (SAXException var28) {
            var28.printStackTrace();
            System.exit(-1);
         } catch (IOException var29) {
            var29.printStackTrace();
            System.exit(-1);
         }

         return data_assoc;
      }
   }
}
