package model;

import java.util.HashSet;
import java.util.Iterator;

import org.apache.sis.measure.Range;
import org.apache.sis.util.collection.RangeSet;

public class GeneInfo {
   public String gene_id;
   public String gene_name;
   public HashSet<String> alternate_names = new HashSet<String>();
   public String biotype = null;
   public long start;
   public long end;
   public String chr;
   public long sumExonLength = 0L;
   public RangeSet<Long> exon_id = RangeSet.create(Long.class, true, true);

   public long getLength() {
      long sum = 0L;

      Range<Long> r;
      for(Iterator<Range<Long>> var4 = this.exon_id.iterator(); var4.hasNext(); sum += (Long)r.getMaxValue() - (Long)r.getMinValue() + 1L) {
         r = var4.next();
      }

      return sum;
   }

   public void setGeneLengthToExons() {
      this.start = Long.MAX_VALUE;
      this.end = Long.MIN_VALUE;
      Iterator<Range<Long>> var2 = this.exon_id.iterator();

      while(var2.hasNext()) {
         Range<Long> r = var2.next();
         if ((Long)r.getMaxValue() > this.end) {
            this.end = (Long)r.getMaxValue();
         }

         if ((Long)r.getMinValue() < this.start) {
            this.start = (Long)r.getMinValue();
         }
      }

   }
}
