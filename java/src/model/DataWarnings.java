package model;

import java.util.ArrayList;
import java.util.Iterator;

class DataWarnings {
   String message;

   public static String toString(ArrayList<DataWarnings> warnings) {
      String res = "[";

      DataWarnings w;
      for(Iterator<DataWarnings> var3 = warnings.iterator(); var3.hasNext(); res = res + "{\"message\":\"" + w.message + "\"},") {
         w = var3.next();
      }

      if (!res.equals("[")) {
         res = res.substring(0, res.length() - 1);
      }

      return res + "]";
   }
}
