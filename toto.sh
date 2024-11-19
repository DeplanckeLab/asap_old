require 'yaml'

begin
  config = YAML.load_file("src/config/database.yml")

  puts "Development configuration:"
  puts config["development"]

  puts "Test configuration:"
  puts config["test"]

  puts "Production configuration:"
  puts config["production"]

rescue Psych::BadAlias => e
  puts "Alias error: #{e.message}"
rescue StandardError => e
  puts "Error: #{e.message}"
end
