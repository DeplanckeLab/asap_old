package model;

class Batch {
   public int nber_lines_parsed;
   public int nber_lines_unparsed;
   public int nber_groups;

   public String toString() {
      return "{\"nber_lines_parsed\":" + this.nber_lines_parsed + ",\"nber_lines_unparsed\":" + this.nber_lines_unparsed + ",\"nber_groups\":" + this.nber_groups + "}";
   }
}
