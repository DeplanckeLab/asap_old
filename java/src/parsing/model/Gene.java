package parsing.model;

public class Gene {
   public String ensembl_id;
   public String name;
   public String biotype;
   public long sum_exon_length;
   public long gene_length;
   public String chr;
   public String alt_names;

   public String toString() {
      return this.ensembl_id + "\t" + this.name;
   }
}
