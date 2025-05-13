package tools;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Utils {
   private static Random rand = new Random();
   private static DecimalFormat df = new DecimalFormat("#.###");

   public static void setSeed(int seed) {
      rand = new Random((long)seed);
   }

   public static String format(double n) {
      return df.format(n);
   }

   public static double[][] t(double[][] matrix) {
      double[][] output = new double[matrix[0].length][matrix.length];

      for(int i = 0; i < matrix.length; ++i) {
         for(int j = 0; j < matrix[i].length; ++j) {
            output[j][i] = matrix[i][j];
         }
      }

      return output;
   }

   public static void listdirs(String directoryName, ArrayList<File> files) {
      File directory = new File(directoryName);
      File[] fList = directory.listFiles();
      File[] var7 = fList;
      int var6 = fList.length;

      for(int var5 = 0; var5 < var6; ++var5) {
         File file = var7[var5];
         if (file.isDirectory()) {
            files.add(file);
         }
      }

   }

   public static void listfiles(String directoryName, ArrayList<File> files) {
      File directory = new File(directoryName);
      File[] fList = directory.listFiles();
      File[] var7 = fList;
      int var6 = fList.length;

      for(int var5 = 0; var5 < var6; ++var5) {
         File file = var7[var5];
         if (file.isFile()) {
            files.add(file);
         }
      }

   }

   public static Random getRandomGenerator() {
      return rand;
   }

   public static double mean(double[] data) {
      double sum = 0.0D;
      double[] var7 = data;
      int var6 = data.length;

      for(int var5 = 0; var5 < var6; ++var5) {
         double a = var7[var5];
         sum += a;
      }

      return sum / (double)data.length;
   }

   public static double mean(ArrayList<Integer> data) {
      double sum = 0.0D;

      Integer a;
      for(Iterator var4 = data.iterator(); var4.hasNext(); sum += (double)a) {
         a = (Integer)var4.next();
      }

      return sum / (double)data.size();
   }

   public static int sum(int[] array) {
      int sum = 0;
      int[] var5 = array;
      int var4 = array.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         int i = var5[var3];
         sum += i;
      }

      return sum;
   }

   public static double mean(int[] data) {
      double sum = 0.0D;
      int[] var7 = data;
      int var6 = data.length;

      for(int var5 = 0; var5 < var6; ++var5) {
         double a = (double)var7[var5];
         sum += a;
      }

      return sum / (double)data.length;
   }

   public static double median(int[] data) {
      Arrays.sort(data);
      return data.length % 2 == 0 ? ((double)data[data.length / 2] + (double)data[data.length / 2 - 1]) / 2.0D : (double)data[data.length / 2];
   }

   public static double median(ArrayList<Double> data) {
      Collections.sort(data);
      return data.size() % 2 == 0 ? ((Double)data.get(data.size() / 2) + (Double)data.get(data.size() / 2 - 1)) / 2.0D : (Double)data.get(data.size() / 2);
   }

   public static Double quartile(double[] data, float quartile) {
      Arrays.sort(data);
      if (!(quartile < 0.0F) && !(quartile > 1.0F)) {
         int q = Math.round((float)data.length * quartile);
         if (q >= data.length) {
            q = data.length - 1;
         }

         return data[q];
      } else {
         return null;
      }
   }

   public static double var(double[] data, double mean) {
      double var = 0.0D;
      double[] var9 = data;
      int var8 = data.length;

      for(int var7 = 0; var7 < var8; ++var7) {
         double a = var9[var7];
         var += (a - mean) * (a - mean);
      }

      return var / (double)(data.length - 1);
   }

   public static double var(int[] data, double mean) {
      double var = 0.0D;
      int[] var9 = data;
      int var8 = data.length;

      for(int var7 = 0; var7 < var8; ++var7) {
         double a = (double)var9[var7];
         var += (a - mean) * (a - mean);
      }

      return var / (double)(data.length - 1);
   }

   public static double sd(double[] data, double mean) {
      double var = var(data, mean);
      return Math.sqrt(var);
   }

   public static double sd(int[] data, double mean) {
      double var = var(data, mean);
      return Math.sqrt(var);
   }

   public static double var(double[] data) {
      double mean = 0.0D;
      double M2 = 0.0D;
      if (data.length < 2) {
         return 0.0D;
      } else {
         for(int i = 0; i < data.length; ++i) {
            double delta = data[i] - mean;
            mean += delta / (double)(i + 1);
            M2 += delta * (data[i] - mean);
         }

         return M2 / (double)(data.length - 1);
      }
   }

   public static double sd(double[] data) {
      double var = var(data);
      return Math.sqrt(var);
   }

   public static double cv(double[] data) {
      double mu = mean(data);
      double theta = sd(data);
      return theta / mu;
   }

   public static double[] toArray(ArrayList<Double> data) {
      double[] res = new double[data.size()];

      for(int i = 0; i < res.length; ++i) {
         res[i] = (Double)data.get(i);
      }

      return res;
   }

   public static double cov(double[] x, double[] y) {
      double result = 0.0D;
      int length = x.length;
      double xMean = mean(x);
      double yMean = mean(y);

      for(int i = 0; i < length; ++i) {
         double xDev = x[i] - xMean;
         double yDev = y[i] - yMean;
         result += (xDev * yDev - result) / (double)(i + 1);
      }

      return result * ((double)length / (double)(length - 1));
   }

   public static double cov(double[] x, double[] y, double xMean, double yMean) {
      double result = 0.0D;
      int length = x.length;

      for(int i = 0; i < length; ++i) {
         double xDev = x[i] - xMean;
         double yDev = y[i] - yMean;
         result += (xDev * yDev - result) / (double)(i + 1);
      }

      return result * ((double)length / (double)(length - 1));
   }

   public static String[] sortD(Map<String, Double> map) {
      List<Double> values = new ArrayList(map.values());
      Collections.sort(values, Collections.reverseOrder());
      String[] sortedIndexes = new String[values.size()];
      Iterator var4 = map.keySet().iterator();

      while(var4.hasNext()) {
         String key = (String)var4.next();
         int index = values.indexOf(map.get(key));
         sortedIndexes[index] = key;
         values.set(index, (Double)null);
      }

      return sortedIndexes;
   }

   public static boolean contains(String[] array, String value) {
      String[] var5 = array;
      int var4 = array.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         String val = var5[var3];
         if (val.equals(value)) {
            return true;
         }
      }

      return false;
   }

   public static String[] sortI(Map<String, Integer> map) {
      List<Integer> values = new ArrayList(map.values());
      Collections.sort(values, Collections.reverseOrder());
      String[] sortedIndexes = new String[values.size()];
      Iterator var4 = map.keySet().iterator();

      while(var4.hasNext()) {
         String key = (String)var4.next();
         int index = values.indexOf(map.get(key));
         sortedIndexes[index] = key;
         values.set(index, (Integer)null);
      }

      return sortedIndexes;
   }

   public static String[] sortL(Map<String, Long> map) {
      List<Long> values = new ArrayList(map.values());
      Collections.sort(values, Collections.reverseOrder());
      String[] sortedIndexes = new String[values.size()];
      Iterator var4 = map.keySet().iterator();

      while(var4.hasNext()) {
         String key = (String)var4.next();
         int index = values.indexOf(map.get(key));
         sortedIndexes[index] = key;
         values.set(index, (Long)null);
      }

      return sortedIndexes;
   }

   public static String[] sortKeys(Set<String> keySet) {
      String[] keys = (String[])keySet.toArray(new String[keySet.size()]);
      Arrays.sort(keys);
      return keys;
   }

   public static String toReadableTime(long ms) {
      if (ms < 1000L) {
         return ms + " ms";
      } else {
         long s = ms / 1000L;
         ms %= 1000L;
         if (s < 60L) {
            return s + " s " + ms + " ms";
         } else {
            long mn = s / 60L;
            s %= 60L;
            if (mn < 60L) {
               return mn + " mn " + s + " s " + ms + " ms";
            } else {
               long h = mn / 60L;
               mn %= 60L;
               if (h < 24L) {
                  return h + " h " + mn + " mn " + s + " s " + ms + " ms";
               } else {
                  long d = h / 24L;
                  h %= 24L;
                  return d + " d " + h + " h " + mn + " mn " + s + " s " + ms + " ms";
               }
            }
         }
      }
   }
}
