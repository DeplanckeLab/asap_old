require 'yaml'

begin
  # Explicitly allow YAML aliases by setting aliases: true
  config = YAML.safe_load(File.read('src/config/database.yml'), aliases: true)

  puts "YAML parsed successfully:"
  puts config

rescue Psych::BadAlias => e
  puts "Alias error: #{e.message}"
  puts e.backtrace

rescue Psych::Exception => e
  puts "YAML Error: #{e.message}"
  puts e.backtrace

rescue StandardError => e
  puts "General Error: #{e.message}"
  puts e.backtrace
end
