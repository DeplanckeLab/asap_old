package db;

import java.awt.Component;
import javax.swing.ProgressMonitor;

class ProgressBar {
   public ProgressMonitor progressBar;
   public int max;
   public int step;

   public ProgressBar(String dbname, int max) {
      this.max = max;
      this.step = 0;
      this.progressBar = new ProgressMonitor((Component)null, "Fetching database " + dbname, (String)null, 0, max);
   }

   public void increment() {
      ++this.step;
      this.progressBar.setProgress(this.step);
      this.progressBar.setNote(this.step + " GOCC Terms fetched over " + this.max);
      if (this.progressBar.isCanceled()) {
         this.close();
      }

   }

   public void close() {
      this.progressBar.close();
   }
}
