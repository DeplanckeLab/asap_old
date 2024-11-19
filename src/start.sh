#!/bin/bash

rm -f ./tmp/pids/server.pid

# Install node_modules if doesn't exist in container
#if [ ! -d "node_modules" ]; then
#    npm i
#fi

#gem update --system 3.3.22
bundle install
#rm -rf ./solr/pids/development/sunspot-solr-development.pid
#bundle exec rake sunspot:solr:start &

#npm run build & npm run build:css &
#./bin/rails server -b "0.0.0.0" && fg 

bundle exec unicorn -c config/unicorn.rb -E $RAILS_ENV -p 3000
