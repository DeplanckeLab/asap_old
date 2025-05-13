package enrichment;

import java.util.HashSet;

class Pathway {
   public String id;
   public String url;
   public String description;
   public HashSet<String> listGenes = new HashSet<String>();
}
