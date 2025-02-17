require_relative 'boot'

require 'rails/all'
require File.dirname(__FILE__) + '/../app/lib/custom_logger.rb'

# Require the gems listed in Gemfile, including any gems
# you've limited to :test, :development, or :production.
Bundler.require(*Rails.groups)

module Asap
  class Application < Rails::Application
    # Settings in config/environments/* take precedence over those specified here.
    # Application configuration should go into files in config/initializers
    # -- all .rb files in that directory are automatically loaded.    
    config.autoload_paths += %W( #{config.root}/lib )
    config.eager_load_paths += %W( #{config.root}/lib )
    
#    config.middleware.swap Rails::Rack::Logger, CustomLogger, :silenced => ["/projects/get_pipeline"]

    config.action_dispatch.default_headers = {
      'X-Frame-Options' => 'ALLOW-FROM http://net-kiosk.com'
    }

  end
end
