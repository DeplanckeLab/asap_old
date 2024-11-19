desc '####################### Get jobs'
task get_jobs: :environment do
  puts 'Executing...'

  h_steps = {}
Step.all.map{|s| h_steps[s.id] =s}

  h_methods = {
    "lib/filtering.R" => 5,
    "lib/de.R" => 5,
    "lib/visualization.R" => 5,
    "lib/clustering.R" => 5,
    "lib/normalization.R" => 5
  }

  h_data = {}

  output_file = Pathname.new(APP_CONFIG[:data_dir]) + "benchmark_data_full.json"
  
  Job.all.each do |j|
    
    h = {
      :job_id => j.id,
      :step_id => j.step_id,
      :step => h_steps[j.step_id].label,
      :command_line => j.command_line,
      :duration => j.duration
    }

    tab_command = []
    
    if j.command_line
      tab_command = j.command_line.split(/\s+/)
      
      if m = j.command_line.match(/([^ ]+?\.R)\s+([^ ]+?)\s+/)
        h[:script_name] = m[1]
        h[:input_file] = m[2]
        h[:method] = tab_command[h_methods[h[:script_name]]] if h_methods[h[:script_name]]
      end   

      if h[:step] == 'Cell clusters' and tab_command[6] == '0'
       h[:method] += ' auto'
      end
      
      if h[:step] == 'Visualisation' 
        if ['pca', 'tsne', 'mds', 'zifa'].include? h[:method]
          h[:step]= 'Dimension reduction'
        elsif h[:method] == 'heatmap'
           h[:step]= 'Heatmap'
        elsif h[:method] == 'trajectory'
           h[:step]= 'Trajectory'
        end
      end
      
      if h[:input_file] and File.exist? h[:input_file]
        if  h[:input_file].match(/\.json/)
          h[:nber_rows] = 3
          h_json =  JSON.parse(File.read(h[:input_file]))
	  if  h_json['text']
            h[:nber_cols] = h_json['text'].size
          elsif h_json['nber_genes'] and h_json['nber_cells']
	   h[:nber_cols] =  h_json['nber_cells']
            h[:nber_rows] = h_json['nber_genes']
	    else	  
	  puts h.to_json
            puts h_json.to_json
	    exit
          end
        else
         # puts h[:command_line] if h[:input_file].match(/\.json/)
         # puts "wc -l #{h[:input_file]}"
          h[:nber_rows] = `wc -l #{h[:input_file]}`.to_i - 1
          h[:nber_cols] = `head -1 #{h[:input_file]} | wc -w`.to_i - 1
        end
      else
        next
      end

     
      
      if ! h_methods[h[:script_name]]    
        puts h[:script_name]
        puts h.to_json
        exit
      end

#      puts h.to_json

      h_data[h[:step]] ||= {}
      h_data[h[:step]][h[:method]] ||= []

      if j.duration
        h_data[h[:step]][h[:method]].push({
                                            :job_id => h[:job_id],
                                            :nber_rows => h[:nber_rows],
                                            :nber_cols => h[:nber_cols],
                                            :duration => j.duration
                                          })
      end
      
    end
#    puts h.to_json    
#exit

    File.open(output_file, 'w') do |f|
      f.write h_data.to_json
    end

  end
  
end
