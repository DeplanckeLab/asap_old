package enrichment;

class ResultSet {
   double[] p_value;
   double[] adj_p_value;
   double[] OR;
   String[] pathways;
   String[] descriptions;
   String[] urls;
   String warning;

   public void init(int length) {
      this.p_value = new double[length];
      this.adj_p_value = new double[length];
      this.descriptions = new String[length];
      this.pathways = new String[length];
      this.urls = new String[length];
      this.OR = new double[length];
      this.warning = null;
   }

   public void clone(ResultSet res, int res_index, int this_index) {
      this.p_value[this_index] = res.p_value[res_index];
      this.adj_p_value[this_index] = res.adj_p_value[res_index];
      this.descriptions[this_index] = res.descriptions[res_index];
      this.pathways[this_index] = res.pathways[res_index];
      this.urls[this_index] = res.urls[res_index];
      this.OR[this_index] = res.OR[res_index];
      this.warning = res.warning;
   }
}
