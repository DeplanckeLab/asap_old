require 'json'
require 'fileutils'

filename = ARGV[0]

puts "Analyzing #{filename}..."

header_fields = []
proteins = []
h_protein_idx_by_gene = {}
genes = []
protein_matrix = []
gene_matrix = []
meta_idx = []
protein_meta = []
gene_meta = []
samples = []
comparisons = []
#groups = []
#conditions = []
nber_cols_before_comparisons = 15

i = 0
File.open(filename, 'r') do |f|
	header_fields = f.gets.chomp.split("\t").map{|e| e.gsub(".", " ")}

	#identify the list of comparisons and the list of samples
	comparisons = header_fields.map{|e| (m = e.match(/^(.+?)_logFC$/)) ? m[1] : nil}.compact
	puts comparisons.to_json
	start_sample_i = nber_cols_before_comparisons + (comparisons.size * 5) 
	samples = (start_sample_i .. header_fields.size-1).to_a.map{|j| header_fields[j]}
	meta_idx = (1 .. start_sample_i-1).to_a

	while(l = f.gets) do
 		t = l.chomp.split("\t")
 		proteins.push t[0]
 		row = (start_sample_i .. header_fields.size-1).to_a.map{|j| t[j]}
 		protein_matrix.push row
 		meta_row = (1 .. start_sample_i-1).to_a.map{|j| t[j]}
 		protein_meta.push meta_row
 		h_protein_idx_by_gene[t[3]]||=[]
        h_protein_idx_by_gene[t[3]].push i


        	
    	i+=1
	end
end
 
genes = h_protein_idx_by_gene.keys

puts "Number genes: #{genes.size}"
puts "Number proteins: #{proteins.size}"

h_protein_idx_by_gene.keys.select{|gene| h_protein_idx_by_gene[gene].size > 1}.each do |gene|
 puts "#{gene}: #{h_protein_idx_by_gene[gene].map{|i| proteins[i]}.join(", ")}"
end

## write files
filename_without_ext = filename.gsub(/\..{2,4}$/, '')
File.open("#{filename_without_ext}.matrix.txt", 'w') do |fw|
	puts samples.to_json
  fw.write (["Accession"] + samples).join("\t") + "\n"
#  fw.write (0 .. protein_matrix.size-1).to_a.map{|i| ([proteins[i]] + protein_matrix[i]).join("\t")}.join("\n")
  fw.write (0 .. protein_matrix.size-1).to_a.map{|i| ([(protein_meta[i][2] != '') ? protein_meta[i][2] : '_NA_'] + protein_matrix[i]).join("\t")}.join("\n")
end
	
File.open("#{filename}.metadata", 'w') do |fw|
  fw.write (["Accession"] + meta_idx.map{|i| header_fields[i]}).join("\t") + "\n"
  fw.write (0 .. protein_meta.size-1).to_a.map{|i| ([proteins[i]] + protein_meta[i]).join("\t")}.join("\n")
end

#move original_file
FileUtils.move filename, filename + ".ori"
FileUtils.move "#{filename_without_ext}.matrix.txt", filename
