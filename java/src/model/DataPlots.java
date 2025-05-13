package model;

import java.util.ArrayList;
import java.util.Iterator;

class DataPlots {
   String name;
   String description;

   public static String toString(ArrayList<DataPlots> plots) {
      String res = "[";

      DataPlots p;
      for(Iterator<DataPlots> var3 = plots.iterator(); var3.hasNext(); res = res + "{\"name\":\"" + p.name + "\",\"description\":\"" + p.description + "\"},") {
         p = var3.next();
      }

      if (!res.equals("[")) {
         res = res.substring(0, res.length() - 1);
      }

      return res + "]";
   }
}
