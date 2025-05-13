package tools;

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

class Console implements RMainLoopCallbacks {
   public void rBusy(Rengine arg0, int arg1) {
   }

   public String rChooseFile(Rengine arg0, int arg1) {
      return null;
   }

   public void rFlushConsole(Rengine arg0) {
   }

   public void rLoadHistory(Rengine arg0, String arg1) {
   }

   public String rReadConsole(Rengine arg0, String arg1, int arg2) {
      return null;
   }

   public void rSaveHistory(Rengine arg0, String arg1) {
   }

   public void rShowMessage(Rengine arg0, String arg1) {
   }

   public void rWriteConsole(Rengine arg0, String arg1, int arg2) {
      System.out.println(arg1);
   }
}
