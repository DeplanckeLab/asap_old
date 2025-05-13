package model;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

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

    public HashMap<String, Integer> reads_per_cell = new HashMap<>();
    public HashMap<String, HashMap<String, Integer>> biotypes = new HashMap<>();
    public HashMap<String, HashMap<String, Integer>> chrs = new HashMap<>();

    private static final TypeAdapter<Boolean> booleanAsIntAdapter = new TypeAdapter<>() {
        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value);
            }
        }

        @Override
        public Boolean read(JsonReader in) throws IOException {
            JsonToken token = in.peek();
            switch (token) {
                case STRING:
                    return Boolean.parseBoolean(in.nextString());
                case NUMBER:
                    return in.nextInt() != 0;
                case BOOLEAN:
                    return in.nextBoolean();
                case NULL:
                    in.nextNull();
                    return null;
                default:
                    throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + token);
            }
        }
    };

    public void setChrs() {
        total_chrs = 0L;
        chr_MT = 0L;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + "chrs.tab"))) {
            if (!chrs.isEmpty() && is_count_table) {
                // Write header
                for (String cell : cell_names_parsed) {
                    bw.write("\t" + cell);
                }
                bw.write("\n");

                for (Map.Entry<String, HashMap<String, Integer>> entry : chrs.entrySet()) {
                    String chr = entry.getKey();
                    bw.write(chr);
                    for (String cell : cell_names_parsed) {
                        int count = entry.getValue().get(cell);
                        bw.write("\t" + count);
                        total_chrs += count;
                        if ("MT".equals(chr)) {
                            chr_MT += count;
                        }
                    }
                    bw.write("\n");
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void setBiotypes() {
        nber_total_biotypes = 0L;
        bio_protein_coding = 0L;
        bio_rRNA = 0L;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + "biotypes.tab"))) {
            if (!biotypes.isEmpty() && is_count_table) {
                for (String cell : cell_names_parsed) {
                    bw.write("\t" + cell);
                }
                bw.write("\n");

                for (Map.Entry<String, HashMap<String, Integer>> entry : biotypes.entrySet()) {
                    String bio = entry.getKey();
                    bw.write(bio);
                    for (String cell : cell_names_parsed) {
                        int count = entry.getValue().get(cell);
                        bw.write("\t" + count);
                        nber_total_biotypes += count;

                        if ("protein_coding".equals(bio)) {
                            bio_protein_coding += count;
                        }

                        if (bio.equals("rRNA") || bio.equals("Mt_rRNA") || bio.equals("rRNA_pseudogene")) {
                            bio_rRNA += count;
                        }
                    }
                    bw.write("\n");
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void writeOutputJSON() {
        List<String> emptyCells = new ArrayList<>();
        StringBuilder emptyColumnsJson = new StringBuilder("[");

        if (is_count_table) {
            for (Map.Entry<String, Integer> entry : reads_per_cell.entrySet()) {
                if (entry.getValue() == 0) {
                    emptyCells.add(entry.getKey());
                    emptyColumnsJson.append("\"").append(entry.getKey()).append("\",");
                }
            }
        }

        String empty_columns = null;
        if (!emptyCells.isEmpty()) {
            empty_columns = emptyColumnsJson.substring(0, emptyColumnsJson.length() - 1) + "]";
            nber_cells -= emptyCells.size();
            nber_zeros -= (long) emptyCells.size() * nber_genes;
            FileParser.parseAndExclude(emptyCells, "output");
            FileParser.parseAndExclude(emptyCells, "dl_output");
            FileParser.parseAndExclude(emptyCells, "ercc");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(Parameters.outputFolder + "output.json"))) {
            bw.write("{");
            bw.write("\"nber_genes\":" + nber_genes + ",");
            bw.write("\"nber_cells\":" + nber_cells + ",");
            bw.write("\"nber_not_found_genes\":" + nber_not_found_genes + ",");
            bw.write("\"nber_duplicated_genes\":" + nber_duplicated_genes + ",");
            bw.write("\"nber_all_duplicated_genes\":" + nber_all_duplicated_genes + ",");
            bw.write("\"nber_zeros\":" + nber_zeros + ",");
            bw.write("\"nber_ercc\":" + nber_ercc + ",");
            bw.write("\"nber_unique_genes\":" + nber_unique_genes + ",");

            if (is_count_table) {
                bw.write("\"nber_total_biotypes\":" + nber_total_biotypes + ",");
                bw.write("\"nber_protein_coding\":" + bio_protein_coding + ",");
                bw.write("\"nber_rRNA\":" + bio_rRNA + ",");
                bw.write("\"nber_total_chr\":" + total_chrs + ",");
                bw.write("\"nber_MT\":" + chr_MT + ",");
                if (empty_columns != null) {
                    bw.write("\"empty_columns\":" + empty_columns + ",");
                }
            }

            bw.write("\"is_count_table\":" + (is_count_table ? 1 : 0) + ",");
            bw.write("\"batch_file\":" + batch_file + "}");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static ParsingJSON loadJSON(String jsonFile) {
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
                    .registerTypeAdapter(Boolean.TYPE, booleanAsIntAdapter)
                    .create();

            JsonReader reader = new JsonReader(new FileReader(jsonFile));
            ParsingJSON result = gson.fromJson(reader, ParsingJSON.class);
            reader.close();
            return result;

        } catch (FileNotFoundException e) {
            System.err.println("The JSON gene list was not found at the given path: " + Parameters.outputFolder + "output.json\nStopping program...");
            System.exit(-1);
        } catch (Exception e) {
            System.out.println(e);
            System.err.println("Problem detected when reading the JSON gene list. Stopping program...");
            System.exit(-1);
        }

        return null;
    }
}
