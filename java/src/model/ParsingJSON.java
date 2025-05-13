package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import parsing.FileParser;

public class ParsingJSON {
   public int nber_cells = 0;
   public int nber_genes = 0;
   public int nber_not_found_genes = 0;
   public int nber_ercc = 0;
   public boolean is_count_table = true;
   public long nber_zeros = 0L;
   public int nber_duplicated_genes = 0;
   public int nber_unique_genes = 0;
   public int nber_all_duplicated_genes = 0;
   public Batch batch_file = null;
   public long nber_total_biotypes = 0L;
   public long bio_protein_coding = 0L;
   public long bio_rRNA = 0L;
   public long total_chrs = 0L;
   public long chr_MT = 0L;
   public String[] cell_names_parsed = null;
   public HashMap<String, Integer> reads_per_cell = new HashMap();
   public HashMap<String, HashMap<String, Integer>> biotypes = new HashMap();
   public HashMap<String, HashMap<String, Integer>> chrs = new HashMap();
   private static final TypeAdapter<Boolean> booleanAsIntAdapter = new TypeAdapter<Boolean>() {
      // $FF: synthetic field
      private static int[] $SWITCH_TABLE$com$google$gson$stream$JsonToken;

      public void write(JsonWriter out, Boolean value) throws IOException {
         if (value == null) {
            out.nullValue();
         } else {
            out.value(value);
         }

      }

      public Boolean read(JsonReader in) throws IOException {
         JsonToken peek = in.peek();
         switch($SWITCH_TABLE$com$google$gson$stream$JsonToken()[peek.ordinal()]) {
         case 6:
            return Boolean.parseBoolean(in.nextString());
         case 7:
            if (in.nextInt() != 0) {
               return true;
            }

            return false;
         case 8:
            return in.nextBoolean();
         case 9:
            in.nextNull();
            return null;
         default:
            throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
         }
      }

      // $FF: synthetic method
      static int[] $SWITCH_TABLE$com$google$gson$stream$JsonToken() {
         int[] var10000 = $SWITCH_TABLE$com$google$gson$stream$JsonToken;
         if (var10000 != null) {
            return var10000;
         } else {
            int[] var0 = new int[JsonToken.values().length];

            try {
               var0[JsonToken.BEGIN_ARRAY.ordinal()] = 1;
            } catch (NoSuchFieldError var10) {
            }

            try {
               var0[JsonToken.BEGIN_OBJECT.ordinal()] = 3;
            } catch (NoSuchFieldError var9) {
            }

            try {
               var0[JsonToken.BOOLEAN.ordinal()] = 8;
            } catch (NoSuchFieldError var8) {
            }

            try {
               var0[JsonToken.END_ARRAY.ordinal()] = 2;
            } catch (NoSuchFieldError var7) {
            }

            try {
               var0[JsonToken.END_DOCUMENT.ordinal()] = 10;
            } catch (NoSuchFieldError var6) {
            }

            try {
               var0[JsonToken.END_OBJECT.ordinal()] = 4;
            } catch (NoSuchFieldError var5) {
            }

            try {
               var0[JsonToken.NAME.ordinal()] = 5;
            } catch (NoSuchFieldError var4) {
            }

            try {
               var0[JsonToken.NULL.ordinal()] = 9;
            } catch (NoSuchFieldError var3) {
            }

            try {
               var0[JsonToken.NUMBER.ordinal()] = 7;
            } catch (NoSuchFieldError var2) {
            }

            try {
               var0[JsonToken.STRING.ordinal()] = 6;
            } catch (NoSuchFieldError var1) {
            }

            $SWITCH_TABLE$com$google$gson$stream$JsonToken = var0;
            return var0;
         }
      }
   };

   public void setChrs() {
      this.total_chrs = 0L;
      this.chr_MT = 0L;

      try {
         BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + "chrs.tab"));
         if (this.chrs.size() > 0 && this.is_count_table) {
            String[] var5;
            int var4 = (var5 = this.cell_names_parsed).length;

            String chr;
            for(int var3 = 0; var3 < var4; ++var3) {
               chr = var5[var3];
               bw.write("\t" + chr);
            }

            bw.write("\n");
            Iterator var10 = this.chrs.keySet().iterator();

            while(var10.hasNext()) {
               chr = (String)var10.next();
               bw.write(chr);
               HashMap<String, Integer> cells = (HashMap)this.chrs.get(chr);
               String[] var8;
               int var7 = (var8 = this.cell_names_parsed).length;

               for(int var6 = 0; var6 < var7; ++var6) {
                  String cell = var8[var6];
                  bw.write("\t" + cells.get(cell));
                  if (chr.equals("MT")) {
                     this.chr_MT += (long)(Integer)cells.get(cell);
                  }

                  this.total_chrs += (long)(Integer)cells.get(cell);
               }

               bw.write("\n");
            }
         }

         bw.close();
      } catch (IOException var9) {
         System.err.println(var9.getMessage());
         System.exit(-1);
      }

   }

   public void setBiotypes() {
      this.nber_total_biotypes = 0L;
      this.bio_protein_coding = 0L;
      this.bio_rRNA = 0L;

      try {
         BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + "biotypes.tab"));
         if (this.biotypes.size() > 0 && this.is_count_table) {
            String[] var5;
            int var4 = (var5 = this.cell_names_parsed).length;

            String bio;
            for(int var3 = 0; var3 < var4; ++var3) {
               bio = var5[var3];
               bw.write("\t" + bio);
            }

            bw.write("\n");
            Iterator var10 = this.biotypes.keySet().iterator();

            while(var10.hasNext()) {
               bio = (String)var10.next();
               bw.write(bio);
               HashMap<String, Integer> cells = (HashMap)this.biotypes.get(bio);
               String[] var8;
               int var7 = (var8 = this.cell_names_parsed).length;

               for(int var6 = 0; var6 < var7; ++var6) {
                  String cell = var8[var6];
                  bw.write("\t" + cells.get(cell));
                  if (bio.equals("protein_coding")) {
                     this.bio_protein_coding += (long)(Integer)cells.get(cell);
                  }

                  if (bio.equals("rRNA") || bio.equals("Mt_rRNA") || bio.equals("rRNA_pseudogene")) {
                     this.bio_rRNA += (long)(Integer)cells.get(cell);
                  }

                  this.nber_total_biotypes += (long)(Integer)cells.get(cell);
               }

               bw.write("\n");
            }
         }

         bw.close();
      } catch (IOException var9) {
         System.err.println(var9.getMessage());
         System.exit(-1);
      }

   }

   public void writeOutputJSON() {
      String empty_columns = "[";
      ArrayList<String> emptyCells = new ArrayList();
      if (this.is_count_table) {
         Iterator var4 = this.reads_per_cell.keySet().iterator();

         while(var4.hasNext()) {
            String cell = (String)var4.next();
            if ((Integer)this.reads_per_cell.get(cell) == 0) {
               emptyCells.add(cell);
               empty_columns = empty_columns + "\"" + cell + "\",";
            }
         }
      }

      if (!emptyCells.isEmpty()) {
         empty_columns = empty_columns.substring(0, empty_columns.length() - 1) + "]";
         this.nber_cells -= emptyCells.size();
         this.nber_zeros -= (long)(emptyCells.size() * this.nber_genes);
         FileParser.parseAndExclude(emptyCells, "output");
         FileParser.parseAndExclude(emptyCells, "dl_output");
         FileParser.parseAndExclude(emptyCells, "ercc");
      } else {
         empty_columns = null;
      }

      try {
         BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + "output.json"));
         bw.write("{\"nber_genes\":" + this.nber_genes + ",");
         bw.write("\"nber_cells\":" + this.nber_cells + ",");
         bw.write("\"nber_not_found_genes\":" + this.nber_not_found_genes + ",");
         bw.write("\"nber_duplicated_genes\":" + this.nber_duplicated_genes + ",");
         bw.write("\"nber_all_duplicated_genes\":" + this.nber_all_duplicated_genes + ",");
         bw.write("\"nber_zeros\":" + this.nber_zeros + ",");
         bw.write("\"nber_ercc\":" + this.nber_ercc + ",");
         bw.write("\"nber_unique_genes\":" + this.nber_unique_genes + ",");
         if (this.is_count_table) {
            bw.write("\"nber_total_biotypes\":" + this.nber_total_biotypes + ",");
            bw.write("\"nber_protein_coding\":" + this.bio_protein_coding + ",");
            bw.write("\"nber_rRNA\":" + this.bio_rRNA + ",");
            bw.write("\"nber_total_chr\":" + this.total_chrs + ",");
            bw.write("\"nber_MT\":" + this.chr_MT + ",");
            if (empty_columns != null) {
               bw.write("\"empty_columns\":" + empty_columns + ",");
            }
         }

         bw.write("\"is_count_table\":" + (this.is_count_table ? 1 : 0) + ",");
         bw.write("\"batch_file\":" + this.batch_file + "}");
         bw.close();
      } catch (IOException var5) {
         System.err.println(var5.getMessage());
         System.exit(-1);
      }

   }

   public static ParsingJSON loadJSON(String jsonFile) {
      ParsingJSON res = null;

      try {
         Gson gson = (new GsonBuilder()).registerTypeAdapter(Boolean.class, booleanAsIntAdapter).registerTypeAdapter(Boolean.TYPE, booleanAsIntAdapter).create();
         JsonReader reader = new JsonReader(new FileReader(jsonFile));
         res = (ParsingJSON)gson.fromJson((JsonReader)reader, (Type)ParsingJSON.class);
         reader.close();
      } catch (FileNotFoundException var4) {
         System.err.println("The JSON gene list was not found at the given path: " + Parameters.outputFolder + "output.json" + "\nStopping program...");
         System.exit(-1);
      } catch (Exception var5) {
         System.out.println(var5);
         System.err.println("Problem detected when reading the JSON gene list. Stopping program...");
         System.exit(-1);
      }

      return res;
   }
}
